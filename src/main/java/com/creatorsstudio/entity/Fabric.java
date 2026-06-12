package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Fabric")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fabric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "Fabric_name", nullable = false, length = 100)
    private String fabricName;

    @Column(name = "Fabric_type", nullable = false, length = 100)
    private String fabricType;

    @Column(name = "Total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "Purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "fabric", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<FabricItem> fabricItems = new ArrayList<>();
}
