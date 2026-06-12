package com.creatorsstudio.repository;

import com.creatorsstudio.entity.Cone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConeRepository extends JpaRepository<Cone, Long> {

    Optional<Cone> findByAccessoryId(Long accessoryId);
}
