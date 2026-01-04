package io.syscall.hsw.study.sampledomain.persistence.repository

import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.model.PersonId
import org.springframework.data.jpa.repository.JpaRepository

public interface PersonRepository : JpaRepository<PersonEntity, PersonId>
