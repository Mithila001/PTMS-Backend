package com.tritonptms.ptms.feature.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByNic(String nic);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNic(String nic);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);

    @Query("select u.authVersion from User u where u.id = :id")
    Optional<Long> findAuthVersionById(@Param("id") Long id);

    @Query("select case when count(u) > 0 then true else false end from User u join u.roles r where r.name = :roleName")
    boolean existsWithRole(@Param("roleName") String roleName);
}
