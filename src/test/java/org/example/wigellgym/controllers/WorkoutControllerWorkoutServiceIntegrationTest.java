package org.example.wigellgym.controllers;

import jakarta.transaction.Transactional;
import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.repositories.GymBookingRepository;
import org.example.wigellgym.repositories.InstructorRepository;
import org.example.wigellgym.repositories.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class WorkoutControllerWorkoutServiceIntegrationTest {

    private WorkoutRepository workoutRepository;
    private GymBookingRepository gymBookingRepository;
    private InstructorRepository instructorRepository;
    private WorkoutController workoutController;

    private Workout workout;
    private Instructor instructor;

    @Autowired
    public WorkoutControllerWorkoutServiceIntegrationTest(WorkoutRepository workoutRepository, GymBookingRepository gymBookingRepository, InstructorRepository instructorRepository, WorkoutController workoutController) {
        this.workoutRepository = workoutRepository;
        this.gymBookingRepository = gymBookingRepository;
        this.instructorRepository = instructorRepository;
        this.workoutController = workoutController;
    }

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setFirstName("Lars");
        instructor.setLastName("Larsson");
        instructor.setSpecialty("Spinning");
        instructorRepository.save(instructor);

        workout = new Workout();
        workout.setWorkoutName("Spinning90");
        workout.setType("Spinning");
        workout.setMaxNrOfParticipants(5);
        workout.setInstructor(instructor);
        workout.setPriceInSEK(500.0);
    }

    @Test
    void addWorkout_ShouldAddWorkoutAndReturnStatusCodeCreated_WhenValidInput() {

        ResponseEntity<Workout> response = workoutController.addWorkout(workout);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void addWorkout_ShouldReturnStatusCodeBadRequest_WhenInvalidInput() {

        workout.setWorkoutName("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> workoutController.addWorkout(workout));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("Workout name is required");
    }

    @Test
    void addWorkout_ShouldReturnStatusCodeNotFound_WhenInstructorNotFound() {

        Instructor missingInstructor = new Instructor();
        missingInstructor.setId(2L);
        workout.setInstructor(missingInstructor);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> workoutController.addWorkout(workout));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Instructor not found");
    }

    @Test
    void updateWorkout_ShouldUpdateWorkoutAndReturnStatusCodeOk_WhenFound() {

        workoutRepository.save(workout);
        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(workout.getId());
        updateWorkoutDTO.setWorkoutName("New workout name");

        ResponseEntity<Workout> response = workoutController.updateWorkout(updateWorkoutDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("New workout name", Objects.requireNonNull(response.getBody()).getWorkoutName());
    }

    @Test
    void updateWorkout_ShouldReturnStatusCodeNotFound_WhenWorkoutNotFound() {

        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(1L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> workoutController.updateWorkout(updateWorkoutDTO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Workout not found");
    }

    @Test
    void updateWorkout_ShouldReturnStatusCodeConflict_WhenWorkoutInUpcomingBooking() {

        workoutRepository.save(workout);
        GymBooking gymBooking = new GymBooking();
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setDate(LocalDate.of(2099, 1, 1));
        gymBooking.setActive(true);
        gymBookingRepository.save(gymBooking);
        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(workout.getId());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> workoutController.updateWorkout(updateWorkoutDTO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getReason()).isEqualTo("You can not change workouts in upcoming bookings");
    }

    @Test
    void deleteWorkout_ShouldDeleteWorkoutAndReturnStatusCodeOk_WhenFound() {

        workoutRepository.save(workout);

        ResponseEntity<String> response = workoutController.deleteWorkout(workout.getId());

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertThat(workoutRepository.findById(workout.getId())).isEmpty();
    }

    @Test
    void deleteWorkout_ShouldSoftDeleteWorkoutAndReturnStatusCodeOk_WhenInPastBookings() {

        workoutRepository.save(workout);
        GymBooking gymBooking = new GymBooking();
        gymBooking.setDate(LocalDate.of(2021,1,1));
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setActive(false);
        gymBookingRepository.save(gymBooking);

        ResponseEntity<String> response = workoutController.deleteWorkout(workout.getId());

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertTrue(workout.isDeleted());
    }

    @Test
    void deleteWorkout_ShouldReturnStatusCodeNotFound_WhenNotFound() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    workoutController.deleteWorkout(99L));

        assertThat(exception.getReason()).isEqualTo("Workout not found");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteWorkout_ShouldReturnStatusCodeConflict_WhenInUpcomingBooking() {

        workoutRepository.save(workout);
        GymBooking gymBooking = new GymBooking();
        gymBooking.setDate(LocalDate.of(2099,1,1));
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setActive(true);
        gymBookingRepository.save(gymBooking);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                workoutController.deleteWorkout(workout.getId()));

        assertThat(exception.getReason()).isEqualTo("You can not delete workout in upcoming bookings");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAllWorkouts() {

        workoutRepository.save(workout);

        ResponseEntity<List<Workout>> response = workoutController.getAllWorkouts();

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isNotNull();
    }
}