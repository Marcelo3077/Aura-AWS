package com.example.aura.Entity.Admin.Domain;

import com.example.aura.Entity.Certification.Domain.Certification;
import com.example.aura.Entity.Superuser.Domain.Superuser;
import com.example.aura.Entity.SupportTicket.Domain.SupportTicket;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "admin_id")
public class Admin extends Superuser {

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", length = 20)
    private AccessLevel accessLevel;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SupportTicket> tickets = new ArrayList<>();

    @ManyToMany(mappedBy = "admins")
    @JsonIgnore
    private List<Certification> verifiedCertifications = new ArrayList<>();
}