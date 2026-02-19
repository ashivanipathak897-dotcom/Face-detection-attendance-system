package com.example.faceattendance.service;

import com.example.faceattendance.model.User;
import com.example.faceattendance.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FaceRecognitionService {
    @Autowired UserRepository userRepository;
    
    // Simple Euclidean distance matching (Threshold 0.6 usually good for face-api.js)
    public Optional<User> identifyUser(List<Double> liveDescriptor) {
        List<User> allUsers = userRepository.findAll();
        
        for(User user : allUsers) {
            if(user.getFaceEncoding() == null) continue;
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Double> storedDescriptor = mapper.readValue(user.getFaceEncoding(), List.class);
                
                double distance = calculateDistance(liveDescriptor, storedDescriptor);
                if(distance < 0.6) { // Match found
                    return Optional.of(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
    
    private double calculateDistance(List<Double> d1, List<Double> d2) {
        double sum = 0;
        for(int i=0; i<d1.size(); i++) {
            sum += Math.pow(d1.get(i) - d2.get(i), 2);
        }
        return Math.sqrt(sum);
    }
}
