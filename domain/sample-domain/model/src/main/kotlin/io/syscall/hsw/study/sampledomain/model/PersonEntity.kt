package io.syscall.hsw.study.sampledomain.model

import io.syscall.commons.module.persistence.jpa.EntityExtensions.effectiveClass
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "person")
public class PersonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public var id: PersonId? = null,

    @Column
    public var name: String,

    @Column
    public var age: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        if (other.effectiveClass != this.effectiveClass) return false
        val that: PersonEntity = other as PersonEntity
        return this.id != null && this.id == that.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
