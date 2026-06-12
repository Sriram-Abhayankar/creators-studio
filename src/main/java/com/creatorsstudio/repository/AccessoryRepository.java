package com.creatorsstudio.repository;

import com.creatorsstudio.entity.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
    List<Accessory> findAllByOrderByIdDesc();

    @Query("SELECT COALESCE(SUM(a.totalPrice), 0) FROM Accessory a")
    BigDecimal sumTotalPrice();

    @Query("SELECT DISTINCT a FROM Accessory a " +
           "LEFT JOIN a.cone c " +
           "LEFT JOIN a.sizePattern sp " +
           "LEFT JOIN a.others o " +
           "WHERE (:name IS NULL OR LOWER(a.accessoryName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:type IS NULL OR (:type = 'CONE' AND c IS NOT NULL) OR (:type = 'SIZE_PATTERN' AND sp IS NOT NULL) OR (:type = 'OTHERS' AND o IS NOT NULL)) " +
           "AND (:startDate IS NULL OR (c IS NOT NULL AND c.purchaseDate >= :startDate)) " +
           "AND (:endDate IS NULL OR (c IS NOT NULL AND c.purchaseDate <= :endDate)) " +
           "ORDER BY a.id DESC")
    List<Accessory> searchAccessoriesOrderByIdDesc(
            @Param("name") String name,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT a FROM Accessory a " +
           "LEFT JOIN a.cone c " +
           "LEFT JOIN a.sizePattern sp " +
           "LEFT JOIN a.others o " +
           "WHERE (:name IS NULL OR LOWER(a.accessoryName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:type IS NULL OR (:type = 'CONE' AND c IS NOT NULL) OR (:type = 'SIZE_PATTERN' AND sp IS NOT NULL) OR (:type = 'OTHERS' AND o IS NOT NULL)) " +
           "AND (:startDate IS NULL OR (c IS NOT NULL AND c.purchaseDate >= :startDate)) " +
           "AND (:endDate IS NULL OR (c IS NOT NULL AND c.purchaseDate <= :endDate)) " +
           "ORDER BY a.id ASC")
    List<Accessory> searchAccessoriesOrderByIdAsc(
            @Param("name") String name,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT a FROM Accessory a " +
           "JOIN a.cone c " +
           "WHERE (:name IS NULL OR LOWER(a.accessoryName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:startDate IS NULL OR c.purchaseDate >= :startDate) " +
           "AND (:endDate IS NULL OR c.purchaseDate <= :endDate) " +
           "ORDER BY c.purchaseDate DESC")
    List<Accessory> searchAccessoriesConeOrderByPurchaseDateDesc(
            @Param("name") String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT a FROM Accessory a " +
           "JOIN a.cone c " +
           "WHERE (:name IS NULL OR LOWER(a.accessoryName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:startDate IS NULL OR c.purchaseDate >= :startDate) " +
           "AND (:endDate IS NULL OR c.purchaseDate <= :endDate) " +
           "ORDER BY c.purchaseDate ASC")
    List<Accessory> searchAccessoriesConeOrderByPurchaseDateAsc(
            @Param("name") String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(a.totalPrice), 0) FROM Accessory a WHERE a.cone IS NOT NULL")
    BigDecimal sumConeExpenses();

    @Query("SELECT COALESCE(SUM(a.totalPrice), 0) FROM Accessory a WHERE a.sizePattern IS NOT NULL")
    BigDecimal sumSizePatternExpenses();

    @Query("SELECT COALESCE(SUM(a.totalPrice), 0) FROM Accessory a WHERE a.others IS NOT NULL")
    BigDecimal sumOthersExpenses();

    @Query("SELECT YEAR(c.purchaseDate), MONTH(c.purchaseDate), COALESCE(SUM(a.totalPrice), 0) " +
           "FROM Accessory a JOIN a.cone c " +
           "GROUP BY YEAR(c.purchaseDate), MONTH(c.purchaseDate)")
    List<Object[]> getConeExpensesGroupedByMonth();
}
