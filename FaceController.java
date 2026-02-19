package com.example.faceattendance.controller;

import com.example.faceattendance.model.Attendance;
import com.example.faceattendance.model.User;
import com.example.faceattendance.payload.request.MarkAttendanceRequest;
import com.example.faceattendance.repository.AttendanceRepository;
import com.example.faceattendance.repository.UserRepository;
import com.example.faceattendance.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/face")
public class FaceController {
    @Autowired UserRepository userRepository;
    @Autowired AttendanceRepository attendanceRepository;
    @Autowired FaceRecognitionService faceRecognitionService;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequest request) {
        // Logic: Use request.getFaceDescriptor() to match against DB
        Optional<User> userOpt = faceRecognitionService.identifyUser(request.getFaceDescriptor());
        
        if(userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Face not recognized");
        }
        
        User user = userOpt.get();
        // Check if already marked today
        if(attendanceRepository.existsByUserAndDate(user, LocalDate.now())) {
            return ResponseEntity.badRequest().body("Attendance already marked for today");
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        attendanceRepository.save(attendance);
        
        return ResponseEntity.ok("Attendance marked for " + user.getName());
    }
    
    @GetMapping("/attendance/today")
    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findByDate(LocalDate.now());
    }
}
