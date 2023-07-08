package io.syscall.hsw.study.apiserver.example.model

import io.syscall.commons.entityid.LongEntityId
import io.syscall.commons.entityid.LongEntityIdFactoryWithString
import java.io.Serial
import java.io.Serializable

@JvmInline
public value class PersonId private constructor(override val value: Long) : LongEntityId, Serializable {

    public companion object : LongEntityIdFactoryWithString<PersonId> {

        @JvmStatic
        public val EVERYONE: PersonId = PersonId(-1)

        public override fun create(value: Long): PersonId {
            require(value > 0)
            return PersonId(value)
        }

        public override fun create(value: String): PersonId {
            return create(value.toLong())
        }

        @Serial
        private const val serialVersionUID: Long = 0x1L
    }
}
