package io.springboot.java.meetingscheduler.services;

import io.springboot.java.meetingscheduler.dto.MeetingDTO;
import io.springboot.java.meetingscheduler.entities.Meeting;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import io.springboot.java.meetingscheduler.repository.MeetingRepository;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeetingService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MeetingRepository meetingRepository;

    // Save a new meeting
    public String saveMeeting(MeetingDTO meetingDTO) {

        System.out.println("Saving meeting: " + meetingDTO);

        try{
            User mentor = userRepository.findById(meetingDTO.getMentorId())
                    .orElseThrow(() -> new RuntimeException("Mentor not found!"));

            User mentee = userRepository.findById(meetingDTO.getMenteeId())
                    .orElseThrow(() -> new RuntimeException("Mentee not found!"));

            Meeting meeting = new Meeting();
            meeting.setUsername(meetingDTO.getUsername());
            meeting.setMenteeId(mentee);
            meeting.setMentorId(mentor);
            meeting.setMeetingDate(meetingDTO.getMeetingDate());
            meeting.setStartTime(meetingDTO.getStartTime());
            meeting.setEndTime(meetingDTO.getEndTime());
            meeting.setStatus(MeetingStatus.PENDING);

            // Convert LocalDate to LocalDateTime with time set to 00:00:00
            LocalDate dateTime = LocalDate.from(meetingDTO.getMeetingDate().atStartOfDay()); // Convert to start of the day
            meeting.setMeetingDate(dateTime);

            meetingRepository.save(meeting);
        } catch (Exception e) {
            System.err.println("Error saving meeting: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/meetingScheduler/mentee/dashboard";
    }

    public void updateMeetingStatus(int meetingId, MeetingStatus status) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found!"));
        meeting.setStatus(status);
        meetingRepository.save(meeting);
    }
}