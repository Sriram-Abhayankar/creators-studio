package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "Purchase_date")
    private LocalDateTime purchaseDate;

    @CreationTimestamp
    @Column(name = "Created_at", updatable = false)
    private LocalDateTime createdAt;

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
