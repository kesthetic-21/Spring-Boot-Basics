package io.springboot.java.meetingscheduler.repository;

import io.springboot.java.meetingscheduler.entities.OtpEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/* Repository (interface that contains DB operations)
with custom queries */

@Repository
public interface OTPRepository extends JpaRepository<OtpEntity, Long> {

    //Query to find the OTP by email
    @Query("SELECT otp FROM OtpEntity otp WHERE otp.user_email = :email ORDER BY otp.otp_generation_time DESC")
    Optional<OtpEntity> findLatestByUserEmail(@Param("email") String email);

    //Query to check OTP expiration
    @Query("SELECT otp FROM OtpEntity otp WHERE otp.user_email = :user_email AND otp.isAlreadyUsed = false AND otp.isVerified = false AND otp.otp_expiration_time > :current_time")
    Optional<OtpEntity> findValidOTPByEmailAndCurrentTime(@Param("user_email") String user_email, @Param("current_time") LocalDateTime current_time);
    
    // This method will get only OtpEntity by using userEmail
    @Query("SELECT otp FROM OtpEntity otp WHERE otp.user_email = :user_email")
    Optional<OtpEntity> findByOTPUserEmail(String user_email);

    //Query to check if the OTP is already used
    @Query("SELECT otp FROM OtpEntity otp WHERE otp.user_email = :user_email AND otp.isAlreadyUsed = false")
    Boolean existsByOtp(@Param("user_email") String user_email);

    //Query to check the retry count
    @Query("SELECT otp FROM OtpEntity otp WHERE otp.user_email = :user_email AND otp.retryCount < 3")
    Optional<OtpEntity> findOtpByRetryCount(@Param("user_email") String user_email);

    //Query to increment retry count
    @Modifying
    @Transactional
    @Query("UPDATE OtpEntity otp SET otp.retryCount = otp.retryCount + 1 WHERE otp.user_email = :user_email")
    void incrementRetryCount(@Param("user_email") String user_email);

    //Query to set the OTP as already used
    @Modifying
    @Transactional
    @Query("UPDATE OtpEntity otp SET otp.isAlreadyUsed = true WHERE otp.user_email = :user_email")
    void setAlreadyUsed(@Param("user_email") String user_email);

    //Query to delete the old OTPs
    @Modifying
    @Query("DELETE FROM OtpEntity otp WHERE otp.user_email = :email")
    void deleteByUserEmail(String email);
}