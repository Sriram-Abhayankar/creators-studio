package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Size_pattern")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizePattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Accessory_id")
    @JsonBackReference(value = "accessory-sizePattern")
    private Accessory accessory;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "style_number", nullable = false)
    private Integer styleNumber;

    @Column(name = "Style_name", nullable = false, length = 100)
    private String styleName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
