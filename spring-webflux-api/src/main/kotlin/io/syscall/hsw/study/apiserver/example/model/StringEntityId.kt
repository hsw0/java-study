package io.syscall.hsw.study.apiserver.example.model

import io.syscall.hsw.study.apiserver.example.model.StringEntityId.StringEntityIdFactory
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

public interface StringEntityId : EntityId<String> {

    public fun interface StringEntityIdFactory<E : StringEntityId> : EntityIdFactory<String, E>

    public companion object {

        @JvmStatic
        public fun <E : StringEntityId> factory(type: KClass<E>): StringEntityIdFactory<E> {
            val ctor: KFunction<E> = EntityIdFactory.initializeConstructor(type, "dummy")
            return StringEntityIdFactory { it: String ->
                EntityIdFactory.invokeConstructor(ctor, it)
            }
        }

        @JvmStatic
        public fun <E : StringEntityId> factory(type: Class<E>): StringEntityIdFactory<E> {
            return factory(type.kotlin)
        }

        public inline fun <reified E : StringEntityId> factory(): StringEntityIdFactory<E> {
            return factory(E::class)
        }
    }
}
