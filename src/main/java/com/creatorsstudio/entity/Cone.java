package com.creatorsstudio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Cone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Accessory_id")
    @JsonBackReference(value = "accessory-cone")
    private Accessory accessory;

    @OneToMany(mappedBy = "cone", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ConeItem> coneItems = new ArrayList<>();
}
