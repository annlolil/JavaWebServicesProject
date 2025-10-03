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
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
class AdminControllerWorkoutServiceIntegrationTest {

    private AdminController adminController;
    private WorkoutRepository workoutRepository;
    private GymBookingRepository gymBookingRepository;
    private InstructorRepository instructorRepository;

    @Autowired
    public AdminControllerWorkoutServiceIntegrationTest(AdminController adminController, WorkoutRepository workoutRepository, GymBookingRepository gymBookingRepository, InstructorRepository instructorRepository) {
        this.adminController = adminController;
        this.workoutRepository = workoutRepository;
        this.gymBookingRepository = gymBookingRepository;
        this.instructorRepository = instructorRepository;
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void addInstructor() {
    }

    @Test
    void addWorkout_shouldAddWorkoutAndReturnStatusCodeCreated() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Spinning");
        Workout workout = new Workout();
        workout.setWorkoutName("Workout1");
        workout.setType("Spinning");
        workout.setMaxNrOfParticipants(5);
        workout.setInstructor(instructor);
        workout.setPriceInSEK(500.0);
        instructorRepository.save(instructor);

        //When
        ResponseEntity<Workout> response = adminController.addWorkout(workout);

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.equals(response.getBody(), workout));
    }

    @Test
    void addWorkout_shouldReturnExceptionWhenInvalidData() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Spinning");
        Workout workout = new Workout();
        workout.setWorkoutName("");
        workout.setType("Spinning");
        workout.setMaxNrOfParticipants(5);
        workout.setInstructor(instructor);
        workout.setPriceInSEK(500.0);
        instructorRepository.save(instructor);

        //When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> adminController.addWorkout(workout));

        //Then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("Workout name is required");
    }

    @Test
    void updateWorkout_shouldUpdateWorkoutAndReturnStatusCodeOk() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Strength");
        Workout workout = new Workout("Strength90", "Group", 10, 500.0, instructor);
        instructorRepository.save(instructor);
        workoutRepository.save(workout);
        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(workout.getId());
        updateWorkoutDTO.setWorkoutName("Yoga");

        //When
        ResponseEntity<Workout> response = adminController.updateWorkout(updateWorkoutDTO);

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(workout.getWorkoutName()).isEqualTo("Yoga");
    }

    @Test
    void deleteWorkout_shouldDeleteWorkoutAndReturnStatusCodeOk() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Strength");
        Workout workoutToDelete = new Workout("Strength90", "Group", 10, 500.0, instructor);
        instructorRepository.save(instructor);
        workoutRepository.save(workoutToDelete);

        //When
        ResponseEntity<String> response = adminController.deleteWorkout(workoutToDelete.getId());

        //Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertThat(workoutRepository.findById(workoutToDelete.getId())).isEmpty();
    }

    @Test
    void deleteWorkout_shouldSoftDeleteWorkoutWhenInPastBookings() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Strength");
        Workout workoutToDelete = new Workout("Strength90", "Group", 10, 500.0, instructor);
        GymBooking gymBooking = new GymBooking(LocalDate.of(2021,1,1), 0.0, 500.0, 45.0, "Anna", workoutToDelete, false);
        instructorRepository.save(instructor);
        workoutRepository.save(workoutToDelete);
        gymBookingRepository.save(gymBooking);

        //When
        ResponseEntity<String> response = adminController.deleteWorkout(workoutToDelete.getId());

        //Then
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo("Workout deleted");
        assertTrue(workoutToDelete.isDeleted());
    }

    @Test
    void deleteWorkout_shouldThrowExceptionWhenNotFound() {

        //Given/When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    adminController.deleteWorkout(99L));

        //Then
        assertThat(exception.getReason()).isEqualTo("Workout not found");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteWorkout_shouldThrowExceptionWhenInUpcomingBooking() {

        //Given
        Instructor instructor = new Instructor("Lars", "Larsson", "Strength");
        Workout workoutToDelete = new Workout("Strength90", "Group", 10, 500.0, instructor);
        GymBooking gymBooking = new GymBooking(LocalDate.now(), 0.0, 500.0, 45.0, "Anna", workoutToDelete, true);
        instructorRepository.save(instructor);
        workoutRepository.save(workoutToDelete);
        gymBookingRepository.save(gymBooking);

        //When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                adminController.deleteWorkout(workoutToDelete.getId()));

        //Then
        assertThat(exception.getReason()).isEqualTo("You can not delete workout in upcoming bookings");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getCanceledBookings() {
    }

    @Test
    void getUpcomingBookings() {
    }

    @Test
    void getPastBookings() {
    }
}