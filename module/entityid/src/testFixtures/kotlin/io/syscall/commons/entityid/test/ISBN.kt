package io.syscall.commons.entityid.test

import io.syscall.commons.entityid.StringEntityId
import io.syscall.commons.entityid.StringEntityIdFactory
import java.io.Serial
import java.io.Serializable

@JvmInline
value class ISBN private constructor(override val value: String) : StringEntityId, Serializable {

    companion object : StringEntityIdFactory<ISBN> {

        @Serial
        private const val serialVersionUID: Long = 1L

        val PATTERN = Regex("[0-9]{13}")

        @JvmStatic
        val SAMPLE_1: ISBN = ISBN("978-3-16-148410-0")

        @JvmStatic
        override fun create(value: String): ISBN {
            val canonical = value.replace(Regex("[-]"), "")
            require(canonical.matches(PATTERN))
            return ISBN(canonical)
        }
    }
}
