package com.example.aura.Entity.Certification.Domain;

import com.example.aura.Entity.Admin.Domain.Admin;
import com.example.aura.Entity.CertificationImage.Domain.CertificationImage;
import com.example.aura.Entity.Technician.Domain.Technician;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "certification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long id;

    @JoinColumn(name = "technician_id")
    @ManyToOne
    private Technician technician;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "validated")
    private Boolean validated;


    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificationImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "certification_admin",
        joinColumns = @JoinColumn(name = "certification_id"),
        inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private List<Admin> admins = new ArrayList<>();
}
