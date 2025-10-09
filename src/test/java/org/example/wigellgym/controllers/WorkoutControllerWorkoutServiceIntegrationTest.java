package org.example.wigellgym.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerWorkoutServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutRepository workoutRepository;

    @MockitoBean
    private InstructorRepository instructorRepository;

    @MockitoBean
    private GymBookingRepository gymBookingRepository;

    private Instructor instructor;
    private Workout workout;
    private GymBooking gymBooking;

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setId(1L);
        instructor.setFirstName("Lars");
        instructor.setLastName("Larsson");
        instructor.setSpecialty("Spinning");

        workout = new Workout();
        workout.setId(1L);
        workout.setWorkoutName("Spinning90");
        workout.setType("Group");
        workout.setMaxNrOfParticipants(5);
        workout.setPriceInSEK(500.0);
        workout.setInstructor(instructor);

        gymBooking = new GymBooking();
        gymBooking.setWorkout(workout);
        gymBooking.setCustomer("Anna");
        gymBooking.setFirstBookingDiscountPercentage(10.0);
        gymBooking.setTotalPriceSEK(500.0);
        gymBooking.setTotalPriceEuro(45.0);
        gymBooking.setDate(LocalDate.of(2099, 1, 1));
        gymBooking.setActive(true);
    }

    @Test
    void addWorkout_ShouldAddWorkoutAndReturnStatusCodeCreated() throws Exception {

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        mockMvc.perform(post("/api/wigellgym/addworkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workout)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workoutName").value("Spinning90"))
                .andExpect(jsonPath("$.instructor.firstName").value("Lars"));

        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void addWorkout_ShouldReturnBadRequest_WhenWorkoutNameIsMissing() throws Exception {
        workout.setWorkoutName("");

        mockMvc.perform(post("/api/wigellgym/addworkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workout)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Workout name is required"));
    }

    @Test
    void updateWorkout_ShouldUpdateWorkoutAndReturnStatusCodeOk_WhenFound() throws Exception {

        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(1L);
        updateWorkoutDTO.setWorkoutName("New workout name");

        Workout updatedWorkout = new Workout();
        updatedWorkout.setId(1L);
        updatedWorkout.setWorkoutName(updateWorkoutDTO.getWorkoutName());
        updatedWorkout.setType("Group");
        updatedWorkout.setMaxNrOfParticipants(5);
        updatedWorkout.setPriceInSEK(500.0);
        updatedWorkout.setInstructor(instructor);

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        mockMvc.perform(put("/api/wigellgym/updateworkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateWorkoutDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workoutName").value("New workout name"))
                .andExpect(jsonPath("$.type").value("Group"))
                .andExpect(jsonPath("$.maxNrOfParticipants").value(5))
                .andExpect(jsonPath("$.priceInSEK").value(500.0))
                .andExpect(jsonPath("$.instructor.firstName").value("Lars"))
                .andExpect(jsonPath("$.instructor.lastName").value("Larsson"))
                .andExpect(jsonPath("$.instructor.specialty").value("Spinning"))
                .andExpect(jsonPath("$.deleted").value(false));

        verify(workoutRepository, times(1)).findById(1L);
    }

    @Test
    void updateWorkout_ShouldReturnConflict_WhenWorkoutHasFutureBookings() throws Exception {

        UpdateWorkoutDTO updateWorkoutDTO = new UpdateWorkoutDTO();
        updateWorkoutDTO.setId(1L);
        updateWorkoutDTO.setWorkoutName("Conflict Name");

        when(gymBookingRepository.findByWorkout(any())).thenReturn(List.of(gymBooking));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        mockMvc.perform(put("/api/wigellgym/updateworkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateWorkoutDTO)))
                .andExpect(status().isConflict())
                .andExpect(status().reason("You can not change workouts in upcoming bookings"));

        verify(workoutRepository).findById(1L);
        verify(gymBookingRepository).findByWorkout(workout);
    }

    @Test
    void deleteWorkout_ShouldDeleteWorkoutAndReturnStatusCodeOk_WhenFound() throws Exception {

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        mockMvc.perform(delete("/api/wigellgym/remworkout/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Workout deleted"));

        verify(workoutRepository, times(1)).findById(1L);
    }

    @Test
    void deleteWorkout_ShouldReturnConflict_WhenInUpcomingBooking() throws Exception {

        when(workoutRepository.findById(workout.getId())).thenReturn(Optional.of(workout));
        when(gymBookingRepository.findByWorkout(workout)).thenReturn(List.of(gymBooking));

        mockMvc.perform(delete("/api/wigellgym/remworkout/{id}", workout.getId()))
                .andExpect(status().isConflict())
                .andExpect(status().reason("You can not delete workout in upcoming bookings"));

        verify(workoutRepository, times(1)).findById(workout.getId());
        verify(gymBookingRepository, times(1)).findByWorkout(workout);

    }

    @Test
    void getAllWorkouts_ShouldReturnOnlyNotSoftDeletedWorkouts() throws Exception {

        workout.setDeleted(false);

        Workout workout2 = new Workout();
        workout2.setWorkoutName("Spinning60");
        workout2.setDeleted(false);

        when(workoutRepository.streamWorkoutByDeleted(false)).thenReturn(List.of(workout, workout2));

        mockMvc.perform(get("/api/wigellgym/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].workoutName").value("Spinning90"))
                .andExpect(jsonPath("$[1].workoutName").value("Spinning60"));

        verify(workoutRepository, times(1)).streamWorkoutByDeleted(false);
    }
}