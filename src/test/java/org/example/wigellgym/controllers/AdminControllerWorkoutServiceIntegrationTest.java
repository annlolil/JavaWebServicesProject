package org.example.wigellgym.controllers;

import jakarta.transaction.Transactional;
import org.example.wigellgym.dtos.UpdateWorkoutDTO;
import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.repositories.GymBookingRepository;
import org.example.wigellgym.repositories.InstructorRepository;
import org.example.wigellgym.repositories.WorkoutRepository;
import org.example.wigellgym.services.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class AdminControllerWorkoutServiceIntegrationTest {

    private WorkoutRepository workoutRepository;
    private GymBookingRepository gymBookingRepository;
    private InstructorRepository instructorRepository;
    private AdminController adminController;

    private Workout workout;
    private Instructor instructor;

    @Autowired
    public AdminControllerWorkoutServiceIntegrationTest(AdminController adminController, WorkoutRepository workoutRepository, GymBookingRepository gymBookingRepository, InstructorRepository instructorRepository) {
        this.adminController = adminController;
        this.workoutRepository = workoutRepository;
        this.gymBookingRepository = gymBookingRepository;
        this.instructorRepository = instructorRepository;
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
    void addWorkout_ShouldAddWorkoutAndReturnStatusCodeCreated() {

        ResponseEntity<Workout> response = adminController.addWorkout(workout);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(workout);
    }

    @Test
    void addWorkout_ShouldReturnException_WhenInvalidData() {

        workout.setWorkoutName("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adminController.addWorkout(workout));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("Workout name is required");
    }

    @Test
    void addWorkout_ShouldReturnException_WhenInstructorNotFound() {

        Instructor missingInstructor = new Instructor();
        missingInstructor.setId(2L);
        workout.setInstructor(missingInstructor);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adminController.addWorkout(workout));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Instructor not found");
    }

    @Test
    void updateWorkout_ShouldUpdateWorkoutAndReturnStatusCodeOk() {

        workoutRepository.save(workout);
        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(workout.getId());
        updateWorkoutDTO.setWorkoutName(workout.getWorkoutName() + " updated");

        ResponseEntity<Workout> response = adminController.updateWorkout(updateWorkoutDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(workout);
    }

    @Test
    void updateWorkout_ShouldThrowException_WhenWorkoutNotFound() {

        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(1L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adminController.updateWorkout(updateWorkoutDTO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Workout not found");
    }

    @Test
    void updateWorkout_ShouldThrowException_WhenWorkoutInUpcomingBooking() {

        workoutRepository.save(workout);
        GymBooking gymBooking = new GymBooking();
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setDate(LocalDate.now());
        gymBooking.setActive(true);
        gymBookingRepository.save(gymBooking);
        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(workout.getId());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adminController.updateWorkout(updateWorkoutDTO));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getReason()).isEqualTo("You can not change workouts in upcoming bookings");
    }

    @Test
    void deleteWorkout_ShouldDeleteWorkoutAndReturnStatusCodeOk() {

        workoutRepository.save(workout);

        ResponseEntity<String> response = adminController.deleteWorkout(workout.getId());

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertThat(workoutRepository.findById(workout.getId())).isEmpty();
    }

    @Test
    void deleteWorkout_ShouldSoftDeleteWorkout_WhenInPastBookings() {

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

        ResponseEntity<String> response = adminController.deleteWorkout(workout.getId());

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertTrue(workout.isDeleted());
    }

    @Test
    void deleteWorkout_ShouldThrowException_WhenNotFound() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    adminController.deleteWorkout(99L));

        assertThat(exception.getReason()).isEqualTo("Workout not found");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteWorkout_ShouldThrowException_WhenInUpcomingBooking() {

        workoutRepository.save(workout);
        GymBooking gymBooking = new GymBooking();
        gymBooking.setDate(LocalDate.now());
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setActive(true);
        gymBookingRepository.save(gymBooking);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                adminController.deleteWorkout(workout.getId()));

        assertThat(exception.getReason()).isEqualTo("You can not delete workout in upcoming bookings");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}