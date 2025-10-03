package org.example.wigellgym.controllers;

import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.services.GymBookingService;
import org.example.wigellgym.services.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class CustomerController {

    private final WorkoutService workoutService;
    private final GymBookingService gymBookingService;

    @Autowired
    public CustomerController(WorkoutService workoutService, GymBookingService gymBookingService) {
        this.workoutService = workoutService;
        this.gymBookingService = gymBookingService;
    }

    @GetMapping("/workouts")
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return new ResponseEntity<>(workoutService.getAllWorkouts(), HttpStatus.OK);
    }

    @PostMapping("/bookworkout")
    public ResponseEntity<GymBooking> bookWorkout(@RequestParam Long id, @RequestParam String date, Authentication user) {
        return new ResponseEntity<>(gymBookingService.createBooking(id, date, user), HttpStatus.CREATED);
    }

    @PutMapping("/cancelworkout")
    public ResponseEntity<String> cancelWorkout(@RequestParam Long id, Authentication user) {
        return new ResponseEntity<>(gymBookingService.cancelBooking(id, user), HttpStatus.OK);
    }

    @GetMapping("/mybookings")
    public ResponseEntity<List<GymBooking>> getMyBookings(Authentication user) {
        return new ResponseEntity<>(gymBookingService.getMyBookings(user), HttpStatus.OK);
    }
}
