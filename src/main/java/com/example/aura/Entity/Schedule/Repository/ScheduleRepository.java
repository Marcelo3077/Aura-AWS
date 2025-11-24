package com.example.aura.Entity.Schedule.Repository;

import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.Schedule;
import com.example.aura.Entity.Schedule.Domain.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<Schedule> findByStatus(ScheduleStatus status);
}
