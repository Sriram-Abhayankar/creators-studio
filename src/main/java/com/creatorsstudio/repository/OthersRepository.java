package com.creatorsstudio.repository;

import com.creatorsstudio.entity.Others;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OthersRepository extends JpaRepository<Others, Long> {

    Optional<Others> findByAccessoryId(Long accessoryId);
}
