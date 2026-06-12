package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Fabric_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Fabric_id")
    @JsonBackReference
    private Fabric fabric;

    @Column(name = "colour", nullable = false, length = 100)
    private String colour;

    @Column(name = "GSM", nullable = false)
    private Integer gsm;

    @Column(name = "Weight", nullable = false)
    private BigDecimal weight;

    @Column(name = "Rib", nullable = false)
    private BigDecimal rib;

    @Column(name = "Price_per_kg", nullable = false)
    private BigDecimal pricePerKg;

    @Column(name = "Row_total", nullable = false)
    private BigDecimal rowTotal;
}
