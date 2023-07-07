package io.syscall.hsw.study.apiserver.example.model

import java.io.Serial

@JvmInline
public value class PersonId private constructor(override val value: Long) : LongEntityId {

    public companion object : LongEntityId.LongEntityIdFactoryWithString<PersonId> {

        @Serial
        private const val serialVersionUID: Long = 0x1111_2222_3333_4444

        @JvmStatic
        public val EVERYONE: PersonId = PersonId(-1)

        @JvmStatic
        public override fun create(value: Long): PersonId {
            require(value > 0)
            return PersonId(value)
        }

        @JvmStatic
        public override fun create(value: String): PersonId {
            return create(value.toLong())
        }
    }
}
