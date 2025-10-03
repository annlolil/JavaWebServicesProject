package org.example.wigellgym.repositories;

import org.example.wigellgym.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> streamWorkoutByDeleted(boolean deleted);
}
