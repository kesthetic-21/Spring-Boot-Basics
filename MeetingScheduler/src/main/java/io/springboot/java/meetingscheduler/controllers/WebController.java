package io.springboot.java.meetingscheduler.controllers;

import io.springboot.java.meetingscheduler.dto.MeetingDTO;
import io.springboot.java.meetingscheduler.entities.Meeting;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import io.springboot.java.meetingscheduler.enums.UserRoles;
import io.springboot.java.meetingscheduler.repository.MeetingRepository;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import io.springboot.java.meetingscheduler.services.MeetingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/meetingScheduler")
public class WebController {

    @Autowired
    private MeetingRepository meetingRepository;

    //Home Page
    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("pageTitle", "Meeting Scheduler");
        return "home"; // Refers to `home.html`
    }

    //Mentor's Dashboard
    @GetMapping("/mentor/dashboard")
    public String mentorDashboard(Model model, HttpSession session) {

        User mentor = (User) session.getAttribute("loggedInUser");

        if (mentor == null) {
            model.addAttribute("error", "No user is logged in.");
            return "redirect:/meetingScheduler/login"; // Redirect to login if not logged in
        }

        List<Meeting> meetings = meetingRepository.findByMentorId_Id(mentor.getId());

        // Add meetings to the model
        model.addAttribute("meetings", meetings);

        model.addAttribute("pageTitle", "Mentor Dashboard - Meeting Scheduler");
        return "mentorDashboard";
    }

    //Mentee's Dashboard
    @GetMapping("/mentee/dashboard")
    public String menteeDashboard(Model model, HttpSession session) {

        User mentee = (User) session.getAttribute("loggedInUser");

        if (mentee == null) {
            model.addAttribute("error", "No user is logged in.");
            return "redirect:/meetingScheduler/login"; // Redirect to login if not logged in
        }

        List<Meeting> meetings = meetingRepository.findByMenteeId_Id(mentee.getId());

        // Add meetings to the model
        model.addAttribute("meetings", meetings);

        model.addAttribute("pageTitle", "Mentee Dashboard - Meeting Scheduler");
        return "menteeDashboard";
    }

    @GetMapping("/getDetails")
    public void getDetails(@RequestParam int menteeId) {
        List<Meeting> meetings = meetingRepository.findByMenteeId_Id(menteeId);
        System.out.println("Details" + meetings);
        System.out.println("MenteeId" + menteeId);

    }
}