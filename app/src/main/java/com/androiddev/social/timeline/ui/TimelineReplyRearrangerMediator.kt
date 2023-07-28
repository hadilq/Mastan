package com.androiddev.social.timeline.ui

import com.androiddev.social.UserScope
import com.androiddev.social.timeline.data.StatusDB
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface TimelineReplyRearrangerMediator {

    fun rearrangeTimeline(
        statuses: List<StatusDB>,
    ): Sequence<StatusDB>
}

@ContributesBinding(UserScope::class, boundType = TimelineReplyRearrangerMediator::class)
class RealTimelineReplyRearrangerMediator @Inject constructor() : TimelineReplyRearrangerMediator {

    private fun addIndentionToDescendantStatus(
        after: List<StatusDB>,
        repliesGraph: Map<String, List<StatusDB>>,
        levelCounter: Int,
        alreadyEmittedSet: MutableSet<String>,
    ): Sequence<StatusDB> = after.asSequence().flatMap { status ->
        sequence {
            if (repliesGraph.containsKey(status.remoteId)) {
                yieldAll(
                    addIndentionToDescendantStatus(
                        repliesGraph[status.remoteId]!!,
                        repliesGraph, levelCounter + 1,
                        alreadyEmittedSet
                    )
                )
                emitStatus(status, levelCounter, alreadyEmittedSet)
            } else {
                emitStatus(status, levelCounter, alreadyEmittedSet)
            }
        }
    }

    override fun rearrangeTimeline(
        statuses: List<StatusDB>,
    ): Sequence<StatusDB> {
        val wrappedStatues = statuses.sortedBy { it.dbOrder }.asSequence()

        val repliesGraph = wrappedStatues
            .filter { !it.inReplyTo.isNullOrEmpty() }
            .groupBy { it.inReplyTo!! }

        val associateStatuses = wrappedStatues.associateBy { it.remoteId }
        val orderQueue = wrappedStatues.map { it.dbOrder }.toMutableList()
        orderQueue.reverse()
        val alreadyEmittedSet = mutableSetOf<String>()

        return wrappedStatues
            .flatMap { status ->
                var inReplyTo = status.inReplyTo
                val ascendants = mutableListOf<StatusDB>()
                while (!inReplyTo.isNullOrEmpty()) {
                    val parent = associateStatuses[inReplyTo]
                    parent?.let { ascendants.add(it) }
                    inReplyTo = parent?.inReplyTo
                }

                sequence {
                    var levelCounter = kotlin.math.max(0, ascendants.size - 1)
                    if (repliesGraph.containsKey(status.remoteId)) {
                        val replies = repliesGraph[status.remoteId]!!
                        yieldAll(
                            addIndentionToDescendantStatus(
                                replies,
                                repliesGraph,
                                levelCounter + 1,
                                alreadyEmittedSet,
                            )
                        )
                        emitStatus(status, levelCounter, alreadyEmittedSet)
                    } else {
                        emitStatus(status, levelCounter, alreadyEmittedSet)
                    }
                    ascendants.forEach { parent ->
                        emitStatus(parent, levelCounter, alreadyEmittedSet)
                        levelCounter--
                    }
                }
            }
            .map {
                it.copy(dbOrder = orderQueue.removeLast())
            }
    }

    private suspend fun SequenceScope<StatusDB>.emitStatus(
        status: StatusDB,
        levelCounter: Int,
        alreadyEmittedSet: MutableSet<String>,
    ) {
        if (!alreadyEmittedSet.contains(status.remoteId)) {
            alreadyEmittedSet.add(status.remoteId)
            yield(status.copy(replyIndention = levelCounter))
        }
    }
}
