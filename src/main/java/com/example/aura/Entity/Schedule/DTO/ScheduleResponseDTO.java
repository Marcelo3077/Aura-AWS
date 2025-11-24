package com.example.aura.Entity.Schedule.DTO;

import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleStatus status;
    private Integer totalTechnicians;
}
