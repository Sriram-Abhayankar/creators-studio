package com.creatorsstudio.repository;

import com.creatorsstudio.entity.FabricItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FabricItemRepository extends JpaRepository<FabricItem, Long> {

    List<FabricItem> findByFabricId(Long fabricId);
}
