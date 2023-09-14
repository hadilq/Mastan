package com.hadilq.mastan.di

import com.hadilq.mastan.BuildConfig
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.SkeletonScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(SkeletonScope::class)
@SingleIn(SkeletonScope::class)
class RealBuildConfigDetails @Inject constructor() : BuildConfigDetails {
    override val debug: Boolean
        get() = BuildConfig.DEBUG
}
