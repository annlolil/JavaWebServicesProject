package org.example.wigellgym.services;

import org.example.wigellgym.entities.Instructor;

import java.util.List;

public interface InstructorServiceInterface {

    List<Instructor> getAllInstructors();

    Instructor addInstructor(Instructor instructor);
}
