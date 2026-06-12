package com.creatorsstudio.repository;

import com.creatorsstudio.entity.ConeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConeItemRepository extends JpaRepository<ConeItem, Long> {

    List<ConeItem> findByConeId(Long coneId);
}
