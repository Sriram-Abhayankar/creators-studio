package com.creatorsstudio.repository;

import com.creatorsstudio.entity.Fabric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FabricRepository extends JpaRepository<Fabric, Long> {
    List<Fabric> findAllByOrderByPurchaseDateDesc();

    @Query("SELECT COALESCE(SUM(f.totalPrice), 0) FROM Fabric f")
    BigDecimal sumTotalPrice();

    @Query("SELECT f FROM Fabric f " +
           "WHERE (:name IS NULL OR LOWER(f.fabricName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:type IS NULL OR f.fabricType = :type) " +
           "AND (:startDate IS NULL OR f.purchaseDate >= :startDate) " +
           "AND (:endDate IS NULL OR f.purchaseDate <= :endDate)")
    List<Fabric> searchFabrics(
            @Param("name") String name,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            org.springframework.data.domain.Sort sort
    );

    @Query("SELECT f.fabricType, COALESCE(SUM(f.totalPrice), 0) FROM Fabric f GROUP BY f.fabricType")
    List<Object[]> getFabricExpensesGroupedByType();

    @Query("SELECT YEAR(f.purchaseDate), MONTH(f.purchaseDate), COALESCE(SUM(f.totalPrice), 0) FROM Fabric f GROUP BY YEAR(f.purchaseDate), MONTH(f.purchaseDate)")
    List<Object[]> getFabricExpensesGroupedByMonth();
}
