package com.example.aura.Entity.CertificationImage.Domain;

import com.example.aura.Entity.Certification.Domain.Certification;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "certification_image")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CertificationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_image_id")
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "file")
    private String file;
}
