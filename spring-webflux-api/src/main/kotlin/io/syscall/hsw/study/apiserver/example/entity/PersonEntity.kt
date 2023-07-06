package io.syscall.hsw.study.apiserver.example.entity

import io.syscall.hsw.study.apiserver.example.model.PersonId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(/*schema = "example",*/ name = "example")
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
        if (javaClass != other?.javaClass) return false

        return id == (other as PersonEntity).id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
