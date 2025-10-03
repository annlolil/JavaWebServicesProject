package org.example.wigellgym.services;

import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.Workout;

import java.util.List;

public interface WorkoutServiceInterface {

    List<Workout> getAllWorkouts();

    Workout addWorkout(Workout workout);

    Workout updateWorkout(UpdateWorkoutDTO updateWorkoutDTO);

    void deleteWorkout(Long id);
}
