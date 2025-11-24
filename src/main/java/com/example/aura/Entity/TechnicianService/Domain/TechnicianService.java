package com.example.aura.Entity.TechnicianService.Domain;

import com.example.aura.Entity.Reservation.Domain.Reservation;
import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Technician.Domain.Technician;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "technician_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianService {

    @EmbeddedId
    private TechnicianServiceId id;

    @Column(name = "base_rate")
    private Double baseRate;

    @ManyToOne(optional = false)
    @MapsId("technicianId")
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;

    @ManyToOne(optional = false)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @OneToMany(mappedBy = "technicianService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
