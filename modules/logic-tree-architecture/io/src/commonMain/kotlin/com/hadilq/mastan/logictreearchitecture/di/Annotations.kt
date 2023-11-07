package com.hadilq.mastan.logictreearchitecture.di

/**
 * Each IO, `io` Graddle module, can have one interface that defines everything that module provides
 * for other components on the UI tree.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class UiIoDefinition

/**
 * Each IO, `io` Gradle module, can have one interface that defines everything that module provides
 * for other components on the logic tree.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class LogicIoDefinition

/**
 * Each implementation, `impl` module, has its own dependencies interface that should be
 * annotated with this annotation, to be easily available for following checks:
 * - It must be interface.
 * - Only other [ImplDependencies] interfaces defined in this interface can be `single`, which means
 *     only other [Dependencies] can use [logicTreeArchitecture.singleWithNoRace], or
 *     [logicTreeArchitecture.single] on their definition.
 * - Other dependencies must be only provider functions, so Compose functions only keep/remember
 *     them.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ImplDependencies

