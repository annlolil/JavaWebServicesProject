package org.example.wigellgym.controllers;

import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.services.GymBookingService;
import org.example.wigellgym.services.InstructorService;
import org.example.wigellgym.services.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class AdminController {

    private final WorkoutService workoutService;
    private final InstructorService instructorService;
    private final GymBookingService gymBookingService;

    @Autowired
    public AdminController(WorkoutService workoutService, InstructorService instructorService, GymBookingService gymBookingService) {
        this.workoutService = workoutService;
        this.instructorService = instructorService;
        this.gymBookingService = gymBookingService;
    }

    @PostMapping("/addinstructor")
    public ResponseEntity<Instructor> addInstructor(@RequestBody Instructor instructor) {
        return new ResponseEntity<>(instructorService.addInstructor(instructor),HttpStatus.CREATED);
    }

    @PostMapping("/addworkout")
    public ResponseEntity<Workout> addWorkout(@RequestBody Workout workout) {
        return new ResponseEntity<>(workoutService.addWorkout(workout), HttpStatus.CREATED);
    }

    @PutMapping("/updateworkout")
    public ResponseEntity<Workout> updateWorkout(@RequestBody UpdateWorkoutDTO updateWorkoutDTO) {
        return new ResponseEntity<>(workoutService.updateWorkout(updateWorkoutDTO), HttpStatus.OK);
    }

    @DeleteMapping("/remworkout/{id}")
    public ResponseEntity<String> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return new ResponseEntity<>("Workout deleted", HttpStatus.OK);
    }

    @GetMapping("/listcanceled")
    public ResponseEntity<List<GymBooking>> getCanceledBookings() {
        return new ResponseEntity<>(gymBookingService.getCanceledBookings(), HttpStatus.OK);
    }

    @GetMapping("/listupcoming")
    public ResponseEntity<List<GymBooking>> getUpcomingBookings() {
        return new ResponseEntity<>(gymBookingService.getUpcomingBookings(), HttpStatus.OK);
    }

    @GetMapping("/listpast")
    public ResponseEntity<List<GymBooking>> getPastBookings() {
        return new ResponseEntity<>(gymBookingService.getPastBookings(), HttpStatus.OK);
    }
}
