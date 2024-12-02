package io.springboot.java.meetingscheduler.dto;

import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MeetingDTO {

    private String username;
    private int menteeId;
    private int mentorId;
    private LocalDate meetingDate;
    private LocalTime startTime; // 06:00 PM - 09:00
    private LocalTime endTime;
    private MeetingStatus status;
}