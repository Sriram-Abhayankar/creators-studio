package com.creatorsstudio.repository;

import com.creatorsstudio.entity.SizePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizePatternRepository extends JpaRepository<SizePattern, Long> {

    Optional<SizePattern> findByAccessoryId(Long accessoryId);
}
