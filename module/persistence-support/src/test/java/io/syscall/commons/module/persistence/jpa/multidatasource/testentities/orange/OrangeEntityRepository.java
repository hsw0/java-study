package io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for OrangeEntity, uses orange datasource. */
public interface OrangeEntityRepository extends JpaRepository<OrangeEntity, Long> {

    Optional<OrangeEntity> findByName(String name);
}
