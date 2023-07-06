package io.syscall.hsw.study.apiserver.example.model

import io.syscall.hsw.study.apiserver.example.model.StringEntityId.StringEntityIdFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


public interface StringEntityId : EntityId<String> {

    public fun interface StringEntityIdFactory<T : StringEntityId> : EntityIdFactory<String, T> {
        public override fun create(value: String): T
    }

    public companion object {

        @JvmStatic
        public fun <T : StringEntityId> factory(type: KClass<T>): StringEntityIdFactory<T> {
            val ctor = type.primaryConstructor!!
            return StringEntityIdFactory { it: String -> ctor.call(it) }
        }

        @JvmStatic
        public fun <T : StringEntityId> factory(type: Class<T>): StringEntityIdFactory<T> {
            return factory(type.kotlin)
        }
    }
}
