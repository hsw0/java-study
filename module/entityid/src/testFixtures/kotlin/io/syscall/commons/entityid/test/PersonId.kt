package io.syscall.commons.entityid.test

import io.syscall.commons.entityid.LongEntityId
import io.syscall.commons.entityid.LongEntityIdFactoryWithString
import java.io.Serial
import java.io.Serializable
import kotlin.reflect.full.memberProperties

@JvmInline
value class PersonId private constructor(override val value: Long) : LongEntityId, Serializable {

    companion object : LongEntityIdFactoryWithString<PersonId> {

        @JvmStatic
        val EVERYONE = PersonId(-1)

        @JvmStatic
        val ALICE = PersonId(1000_0001)

        @JvmStatic
        val BOB = PersonId(1000_0002)

        @JvmStatic
        var CHARLIE = PersonId(1000_0003)

        @JvmStatic
        val MALLORY: PersonId = PersonId(1000_0004)

        @JvmStatic
        override fun create(value: Long): PersonId {
            require(value > 0)
            return PersonId(value)
        }

        @JvmStatic
        override fun create(value: String): PersonId {
            for (prop in Companion::class.memberProperties) {
                if (prop.name == value) {
                    return prop.get(Companion) as PersonId
                }
            }
            return create(value.toLong())
        }


        @Serial
        private const val serialVersionUID: Long = 1L
    }
}
