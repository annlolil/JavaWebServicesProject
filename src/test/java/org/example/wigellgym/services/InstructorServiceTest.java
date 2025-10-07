package org.example.wigellgym.services;

import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.repositories.InstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private InstructorService instructorService;

    private Instructor instructor1;
    private Instructor instructor2;

    @BeforeEach
    void setUp() {
        instructor1 = new Instructor("Lars", "Larsson", "Strength");
        instructor2 = new Instructor("Karl", "Karlsson", "Running");
    }

    @Test
    void getAllInstructors_ShouldReturnAllInstructors() {

        List<Instructor> instructors = Arrays.asList(instructor1, instructor2);

        when(instructorRepository.findAll()).thenReturn(instructors);

        assertEquals(instructors, instructorService.getAllInstructors());
    }

    @Test
    void addInstructor_ShouldAddInstructor() {

        when(instructorRepository.save(instructor1)).thenReturn(instructor1);

        Instructor addedInstructor = instructorService.addInstructor(instructor1);

        assertEquals(instructor1, addedInstructor);
    }

    @Test
    void addInstructor_ShouldThrowException_WhenInvalidFirstName() {

        instructor1.setFirstName("");

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () ->
                instructorService.addInstructor(instructor1));

        assertThat(responseStatusException.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        assertThat(responseStatusException.getMessage().contains("Firstname cannot be empty."));
    }

    @Test
    void addInstructor_ShouldThrowException_WhenInvalidLastName() {

        instructor1.setLastName("");

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () ->
                instructorService.addInstructor(instructor1));

        assertThat(responseStatusException.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        assertThat(responseStatusException.getMessage().contains("Lastname cannot be empty."));
    }

    @Test
    void addInstructor_ShouldThrowException_WhenInvalidSpeciality() {

        instructor1.setSpecialty("");

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () ->
                instructorService.addInstructor(instructor1));

        assertThat(responseStatusException.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        assertThat(responseStatusException.getMessage().contains("Specialty cannot be empty."));
    }
}