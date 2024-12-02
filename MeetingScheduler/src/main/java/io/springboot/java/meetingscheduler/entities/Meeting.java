package io.springboot.java.meetingscheduler.entities;

import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table (name = "meeting")
@Data
@NoArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "mentorId", referencedColumnName = "id")
    private User mentorId;

    @ManyToOne
    @JoinColumn(name = "menteeId", referencedColumnName = "id")
    private User menteeId;

    private String username;

    private LocalDate meetingDate;

    private LocalTime startTime; // 06:00 PM - 09:00
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;
}