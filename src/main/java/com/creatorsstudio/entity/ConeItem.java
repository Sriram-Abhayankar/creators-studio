package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Cone_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Cone_id")
    @JsonBackReference
    private Cone cone;

    @Column(name = "colour_name", nullable = false, length = 100)
    private String colourName;

    @Column(name = "Colour_code", nullable = false, length = 100)
    private String colourCode;

    @Column(name = "unit", nullable = false)
    private BigDecimal unit;

    @Column(name = "Price_per_unit", nullable = false)
    private BigDecimal pricePerUnit;

    @Column(name = "Row_total", nullable = false)
    private BigDecimal rowTotal;
}
