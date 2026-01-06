package io.syscall.commons.module.persistence.jpa

import org.hibernate.proxy.HibernateProxy

public object EntityExtensions {

    @JvmStatic
    public val Any.effectiveClass: Class<*>
        get() = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
}
