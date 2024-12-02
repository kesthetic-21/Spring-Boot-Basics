package io.springboot.java.meetingscheduler.controllers;

import io.springboot.java.meetingscheduler.dto.MeetingDTO;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import io.springboot.java.meetingscheduler.enums.UserRoles;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import io.springboot.java.meetingscheduler.services.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/meetingScheduler")
public class MeetingsController {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/meeting")
    public String bookMeeting(@ModelAttribute MeetingDTO meetingDTO, Model model) {
        try {
            System.out.println("Received MeetingDTO: " + meetingDTO);

            meetingService.saveMeeting(meetingDTO);
            model.addAttribute("success", "Meeting successfully booked!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            System.err.println("Error: " + e.getMessage());
        }
        return "redirect:/meetingScheduler/mentee/dashboard";
    }

    @GetMapping("/meeting")
    public String meetingForm(Model model) {
        List<User> mentors = userRepository.findByRole(UserRoles.MENTOR);
        List<User> mentees = userRepository.findByRole(UserRoles.MENTEE);
        model.addAttribute("mentors", mentors);
        model.addAttribute("mentees", mentees);
        model.addAttribute("meetingDTO", new MeetingDTO());
        return "meeting";
    }

    @PostMapping("/mentor/meetings/accept")
    public String acceptMeeting(@RequestParam("meetingId") int meetingId) {
        meetingService.updateMeetingStatus(meetingId, MeetingStatus.APPROVED);
        return "redirect:/meetingScheduler/mentor/dashboard";
    }

    @PostMapping("/mentor/meetings/reject")
    public String rejectMeeting(@RequestParam("meetingId") int meetingId) {
        meetingService.updateMeetingStatus(meetingId, MeetingStatus.REJECTED);
        return "redirect:/meetingScheduler/mentor/dashboard";
    }
}
