package org.example.wigellgym.repositories;

import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GymBookingRepository extends JpaRepository<GymBooking, Long> {

    List<GymBooking> findByWorkout(Workout workout);
}
