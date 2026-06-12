package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Accessory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accessory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "Accessory_name", nullable = false, length = 100)
    private String accessoryName;

    @Column(name = "Total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToOne(mappedBy = "accessory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference(value = "accessory-cone")
    private Cone cone;

    @OneToOne(mappedBy = "accessory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference(value = "accessory-sizePattern")
    private SizePattern sizePattern;

    @OneToOne(mappedBy = "accessory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference(value = "accessory-others")
    private Others others;
}
