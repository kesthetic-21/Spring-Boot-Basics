package io.springboot.java.meetingscheduler.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentorAvailability")
@Data
@NoArgsConstructor
public class MentorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "mentorId", referencedColumnName = "id")
    private User mentorId; //Foreign Key

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
