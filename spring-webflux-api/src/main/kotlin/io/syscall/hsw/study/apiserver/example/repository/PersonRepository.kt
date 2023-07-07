package io.syscall.hsw.study.apiserver.example.repository

import io.syscall.hsw.study.apiserver.example.entity.PersonEntity
import io.syscall.hsw.study.apiserver.example.model.PersonId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
public interface PersonRepository : JpaRepository<PersonEntity, PersonId>
