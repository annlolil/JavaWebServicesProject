package org.example.wigellgym.controllers;

import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.services.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
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

    @GetMapping("/workouts")
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return new ResponseEntity<>(workoutService.getAllWorkouts(), HttpStatus.OK);
    }
}
