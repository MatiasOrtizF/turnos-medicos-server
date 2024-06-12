package com.turnosmedicos.turnosmedicos.models;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentResponse {
    private List<LocalTime> hour;
    private String day;
    private Integer dayNumber;
}
