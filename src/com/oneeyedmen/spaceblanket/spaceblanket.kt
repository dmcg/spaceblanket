package com.oneeyedmen.spaceblanket

import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties


fun Any.asPropertyMap(): Map<String, Any?> = SpaceBlanket(this)

internal class SpaceBlanket(private val thing: Any) : Map<String, Any?> {

    override val entries: Set<Map.Entry<String, Any?>>
        get() = keys.map { AbstractMap.SimpleImmutableEntry(it, get(it)) }.toSet()

    override val keys by lazy { properties.filter { it.visibility == KVisibility.PUBLIC }.map { it.name }.toSet() }

    override val size: Int get() = keys.size

    override val values: Collection<Any?>
        get() = keys.map { this.get(it) }

    override fun containsKey(key: String) = keys.contains(key)

    override fun containsValue(value: Any?) = values.contains(value)

    override fun get(key: String): Any? = properties.filter { it.name == key }.firstOrNull()?.get(thing)

    override fun isEmpty() = size == 0

    override fun equals(other: Any?) = when (other) {
        is SpaceBlanket -> this.entries == other.entries
        is Map<*, *> -> other == this
        else -> false
    }

    // copied from AbstractMap
    override fun hashCode(): Int {
        var h = 0
        val i = entries.iterator()
        while (i.hasNext())
            h += i.next().hashCode()
        return h
    }

    override fun toString() = HashMap(this).toString()

    @Suppress("UNCHECKED_CAST")
    private val properties by lazy {
        thing::class.memberProperties as Collection<KProperty1<Any, Any?>> // nasty cast due to out variance of thing::class
    }
}