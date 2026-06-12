package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "others")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Others {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Accessory_id")
    @JsonBackReference(value = "accessory-others")
    private Accessory accessory;

    @Column(name = "Items_name", nullable = false, length = 100)
    private String itemsName;

    @Column(name = "unit", nullable = false)
    private BigDecimal unit;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
