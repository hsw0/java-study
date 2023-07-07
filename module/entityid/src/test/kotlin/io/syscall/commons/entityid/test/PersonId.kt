package io.syscall.commons.entityid.test

import io.syscall.commons.entityid.LongEntityId
import java.io.Serial
import java.io.Serializable

@JvmInline
value class PersonId private constructor(override val value: Long) : LongEntityId, Serializable {

    companion object : LongEntityId.LongEntityIdFactoryWithString<PersonId> {

        @Serial
        private const val serialVersionUID: Long = 1L

        @JvmStatic
        val EVERYONE: PersonId = PersonId(-1)

        @JvmStatic
        override fun create(value: Long): PersonId {
            require(value > 0)
            return PersonId(value)
        }

        @JvmStatic
        override fun create(value: String): PersonId {
            return create(value.toLong())
        }
    }
}
