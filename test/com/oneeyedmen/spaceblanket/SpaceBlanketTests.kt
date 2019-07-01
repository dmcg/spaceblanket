package com.oneeyedmen.spaceblanket

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class SpaceBlanketTests {

    val thing = Thing(42, "banana")
    val thingAsMap = thing.asPropertyMap()

    val propertyNames = sortedSetOf("anInt", "aString", "aNull")
    val compareByString = Comparator<Any?> { t1, t2 -> t1.toString().compareTo(t2.toString()) }

    @Test fun properties_to_map_entries() {
        assertEquals(thing.anInt, thingAsMap["anInt"])
        assertEquals(thing.aString, thingAsMap["aString"])
        assertEquals(thing.aNull, thingAsMap["aNull"])
        assertEquals(null, thingAsMap["not there"])
    }

    @Test fun keys() {
        assertEquals(propertyNames, thingAsMap.keys.toSortedSet())
    }

    @Test fun values() {
        assertEquals<Set<Any?>>(
            setOf(42, "banana", null).toSortedSet(compareByString),
            thingAsMap.values.toSortedSet(compareByString))
    }

    @Test fun size() {
        assertEquals(3, thingAsMap.size)
    }

    @Test fun contains_key() {
        assertTrue(thingAsMap.containsKey("anInt"))
        assertTrue(thingAsMap.containsKey("aString"))
        assertFalse(thingAsMap.containsKey("aKumquat"))
    }

    @Test fun contains_value() {
        assertTrue(thingAsMap.containsValue(42))
        assertTrue(thingAsMap.containsValue(null))
        assertFalse(thingAsMap.containsValue("aKumquat"))
    }

    @Test fun respects_mutation() {
        assertEquals("banana", thingAsMap["aString"])
        thing.aString = "kumquat"
        assertEquals("kumquat", thingAsMap["aString"])
        assertFalse(thingAsMap.containsValue("banana"))
        assertTrue(thingAsMap.containsValue("kumquat"))
    }

    @Test fun equals_another_map() {
        assertEquals(thingAsMap, mapOf("anInt" to 42, "aString" to "banana", "aNull" to null))
        assertEquals(mapOf("anInt" to 42, "aString" to "banana", "aNull" to null), thingAsMap)

        assertEquals(mapOf("anInt" to 42, "aString" to "banana", "aNull" to null).hashCode(), thingAsMap.hashCode())
        assertEquals(thingAsMap.toString(), mapOf("anInt" to 42, "aString" to "banana", "aNull" to null).toString())
    }

    @Test fun equals_another_spaceBlanket() {
        assertEquals(thingAsMap, Thing(42, "banana").asPropertyMap())
        assertEquals(Thing(42, "banana").asPropertyMap(), thingAsMap)
    }

    @Test fun respects_privacy_for_listing() {
        assertEquals(emptyMap(), PrivateThing("secret").asPropertyMap())
    }

    @Test fun throws_for_private_lookup() {
        assertThrows<IllegalCallableAccessException> {
            PrivateThing("secret").asPropertyMap()["aString"]
        }
    }

    open class SuperThing {
        val aNull = null
    }

    class Thing(val anInt: Int, var aString: String) : SuperThing()

    @Suppress("unused")
    class PrivateThing(private val aString: String)
}

