package com.example.aura.Entity.Schedule.Domain;

import com.example.aura.Entity.Technician.Domain.Technician;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ScheduleStatus status;

    @ManyToMany(mappedBy = "schedules")
    private List<Technician> technicians = new ArrayList<>();
}
