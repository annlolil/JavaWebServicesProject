package org.example.wigellgym.services;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.repositories.GymBookingRepository;
import org.example.wigellgym.repositories.InstructorRepository;
import org.example.wigellgym.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkoutService implements WorkoutServiceInterface {

    private final WorkoutRepository workoutRepository;
    private final InstructorRepository instructorRepository;
    private final GymBookingRepository gymBookingRepository;
    private final Logger LOGGER = LogManager.getLogger("useranalyze");

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, InstructorRepository instructorRepository, GymBookingRepository gymBookingRepository) {
        this.workoutRepository = workoutRepository;
        this.instructorRepository = instructorRepository;
        this.gymBookingRepository = gymBookingRepository;
    }

    @Override
    public List<Workout> getAllWorkouts() {

        return workoutRepository.streamWorkoutByDeleted(false);
    }

    @Override
    public Workout addWorkout(Workout workout) {

        workout.setId(null);
        workout.setDeleted(false);
        validWorkout(workout);

        Instructor instructor = instructorRepository.findById(workout.getInstructor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));
        workout.setInstructor(instructor);

        workoutRepository.save(workout);

        LOGGER.info("Admin added a new workout with ID: {} and the instructor ID: {}.",
                workout.getId(), workout.getInstructor().getId());

        return workout;
    }

    @Override
    public Workout updateWorkout(UpdateWorkoutDTO updateWorkout) {

        Workout existingWorkout = workoutRepository.findById(updateWorkout.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        List<GymBooking> existingBookings = gymBookingRepository.findByWorkout(existingWorkout);
        boolean hasUpcomingBookings = existingBookings.stream()
                .anyMatch(b->b.isActive() && (b.getDate().isAfter(LocalDate.now()) || b.getDate().isEqual(LocalDate.now())));

        if(hasUpcomingBookings) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not change workouts in upcoming bookings");
        }

        if(updateWorkout.getWorkoutName() != null) {
            existingWorkout.setWorkoutName(updateWorkout.getWorkoutName());
        }
        if(updateWorkout.getType() != null) {
            existingWorkout.setType(updateWorkout.getType());
        }
        if(updateWorkout.getMaxNrOfParticipants() != null) {
            if (updateWorkout.getMaxNrOfParticipants() > 0) {
                existingWorkout.setMaxNrOfParticipants(updateWorkout.getMaxNrOfParticipants());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max number of participants must be greater than 0");
            }
        }
        if(updateWorkout.getInstructor() != null) {
            if (updateWorkout.getInstructor().getId() != null) {
                Instructor newInstructor = instructorRepository.findById(updateWorkout.getInstructor().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));
                existingWorkout.setInstructor(newInstructor);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor ID is required");
            }
        }

        workoutRepository.save(existingWorkout);

        LOGGER.info("Admin updated workout with ID: {}", existingWorkout.getId());

        return existingWorkout;
    }

    @Override
    @Transactional
    public void deleteWorkout(Long id) {

        Workout workoutToDelete = workoutRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found")
        );

        List<GymBooking> existingGymBookings = gymBookingRepository.findByWorkout(workoutToDelete);

        boolean upcomingBookings = existingGymBookings.stream().anyMatch(b ->
                b.isActive() && (b.getDate().isAfter(LocalDate.now()) || b.getDate().isEqual(LocalDate.now())));
        if(upcomingBookings) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not delete workout in upcoming bookings");
        }

        if(!existingGymBookings.isEmpty()) {
            workoutToDelete.setDeleted(true);
            workoutRepository.save(workoutToDelete);
            LOGGER.info("Admin made a soft delete of workout with ID: {}", workoutToDelete.getId());
        }
        else {
            workoutRepository.delete(workoutToDelete);
            LOGGER.info("Admin deleted a workout with id {}.", id);
        }
    }

    private static void validWorkout(Workout workout) {

        if(workout.getWorkoutName() == null || workout.getWorkoutName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout name is required");
        }
        if(workout.getMaxNrOfParticipants() <=0 || workout.getMaxNrOfParticipants() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Max number of participants must be greater than 0");
        }
        if(workout.getType() == null || workout.getType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type is required");
        }
        if(workout.getPriceInSEK() <= 0 || workout.getPriceInSEK() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
        }
        if(workout.getInstructor() == null || workout.getInstructor().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructor ID is required");
        }
    }
}
