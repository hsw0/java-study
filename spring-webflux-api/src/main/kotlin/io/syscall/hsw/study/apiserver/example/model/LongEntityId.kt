package io.syscall.hsw.study.apiserver.example.model

import io.syscall.hsw.study.apiserver.example.model.LongEntityId.LongEntityIdFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


public interface LongEntityId : EntityId<Long> {

    public fun interface LongEntityIdFactory<T : LongEntityId> : EntityIdFactory<Long, T> {
        public override fun create(value: Long): T
    }

    public companion object {

        @JvmStatic
        public fun <T : LongEntityId> factory(type: KClass<T>): LongEntityIdFactory<T> {
            val ctor = type.primaryConstructor!!
            return LongEntityIdFactory { it: Long -> ctor.call(it) }
        }

        @JvmStatic
        public fun <T : LongEntityId> factory(type: Class<T>): LongEntityIdFactory<T> {
            return factory(type.kotlin)
        }
    }
}

