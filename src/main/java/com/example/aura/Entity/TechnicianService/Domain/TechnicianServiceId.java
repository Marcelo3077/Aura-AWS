package com.example.aura.Entity.TechnicianService.Domain;

import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Technician.Domain.Technician;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class TechnicianServiceId implements Serializable {

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(name = "service_id")
    private Long serviceId;

    public TechnicianServiceId(Technician technician, Service service) {
        this.technicianId = technician.getId();
        this.serviceId = service.getId();
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TechnicianServiceId that)) return false;
        return Objects.equals(technicianId, that.technicianId) &&
               Objects.equals(serviceId, that.serviceId);
    }

     @Override
    public int hashCode() {
        return Objects.hash(technicianId, serviceId);
    }
}
