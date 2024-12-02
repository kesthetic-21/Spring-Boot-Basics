package io.springboot.java.meetingscheduler.repository;

import io.springboot.java.meetingscheduler.entities.Meeting;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.enums.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    @Query("SELECT meeting FROM Meeting meeting WHERE meeting.mentorId = :mentorId AND meeting.status = :status")
    List<Meeting> findByMentorIdAndStatus(@Param("mentorId") int mentorId, @Param("status") MeetingStatus status);


    List<Meeting> findByMenteeId_Id(int menteeId);
    List<Meeting> findByMentorId_Id(int mentorId);
}
