package dev.itswin11.greenland.util

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun <T> emptyImmutableList(): ImmutableList<T> = persistentListOf()

inline fun <T, R> Iterable<T>.mapImmutable(transform: (T) -> R): ImmutableList<R>
    = map { transform(it) }.toImmutableList()

inline fun <T, R> Iterable<T>.flatMapImmutable(transform: (T) -> Iterable<R>): ImmutableList<R>
    = flatMap { transform(it) }.toImmutableList()

fun <T> ImmutableList<T>.plus(iterable: Iterable<T>): ImmutableList<T>
    = (this + iterable).toImmutableList()