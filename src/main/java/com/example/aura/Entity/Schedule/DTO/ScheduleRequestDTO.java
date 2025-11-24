package com.example.aura.Entity.Schedule.DTO;

import com.example.aura.Entity.Schedule.Domain.DayOfWeek;
import com.example.aura.Entity.Schedule.Domain.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Status is required")
    private ScheduleStatus status;
}