package io.syscall.commons.entityid.test

import io.syscall.commons.entityid.LongEntityId
import io.syscall.commons.entityid.LongEntityIdFactoryWithString
import io.syscall.commons.entityid.WellKnownValueSupport
import java.io.Serial
import java.io.Serializable

@JvmInline
value class PersonId private constructor(override val value: Long) : LongEntityId, Serializable {

    override fun asString(): String {
        val named = wellKnownValues[this]
        return named ?: super.asString()
    }

    companion object : LongEntityIdFactoryWithString<PersonId> {

        private val wellKnownValues = WellKnownValueSupport(::PersonId)

        @JvmStatic
        val EVERYONE: PersonId = wellKnownValues.declare("EVERYONE", -1)

        @JvmStatic
        val ALICE = wellKnownValues.declare("ALICE", 1000_0001)

        @JvmStatic
        val BOB = wellKnownValues.declare("BOB", 1000_0002)

        @JvmStatic
        var CHARLIE = wellKnownValues.declare("CHARLIE", 1000_0003)

        @JvmStatic
        val MALLORY = wellKnownValues.declare("MALLORY", 1000_0004)

        override fun create(value: Long): PersonId {
            require(value > 0)
            return wellKnownValues.internIfExists(PersonId(value))
        }

        override fun create(value: String) = wellKnownValues[value] ?: create(value.toLong())

        @Serial
        private const val serialVersionUID: Long = 1L
    }


}
