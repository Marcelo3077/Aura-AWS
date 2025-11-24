package com.example.aura.Entity.Technician.Domain;

import com.example.aura.Entity.Certification.Domain.Certification;
import com.example.aura.Entity.Schedule.Domain.Schedule;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.example.aura.Entity.TechnicianService.Domain.TechnicianService;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "technician")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@PrimaryKeyJoinColumn(name = "technician_id")
public class Technician extends Superuser {

    @Column(name = "description", length = 500)
    private String description;

    @ElementCollection(targetClass = ServiceCategory.class)
    @CollectionTable(name = "technician_specialties", joinColumns = @JoinColumn(name = "technician_technician_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialties")
    private List<ServiceCategory> specialties = new ArrayList<>();

    // --- Relaciones ---

    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TechnicianService> technicianServices = new ArrayList<>();

    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Certification> certifications = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "technician_schedule",
            joinColumns = @JoinColumn(name = "technician_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id")
    )
    @JsonIgnore
    private List<Schedule> schedules = new ArrayList<>();
}