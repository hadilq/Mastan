/**
 * Copyright 2023 Hadi Lashkari Ghouchani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hadilq.mastan.timeline.ui

import com.hadilq.mastan.UserScope
import com.hadilq.mastan.timeline.data.StatusDB
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
