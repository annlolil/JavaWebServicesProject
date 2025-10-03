package org.example.wigellgym.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.repositories.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InstructorService implements InstructorServiceInterface {

    private final InstructorRepository instructorRepository;
    private final Logger LOGGER = LogManager.getLogger("useranalyze");

    @Autowired
    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public Instructor addInstructor(Instructor instructor) {

        instructor.setId(null);
        validInstructor(instructor);

        instructorRepository.save(instructor);

        LOGGER.info("Admin added a new instructor with the name {} {} and ID: {}.",
                instructor.getFirstName(), instructor.getLastName(), instructor.getId());

        return instructor;
    }

    private static void validInstructor(Instructor instructor) {

        if (instructor.getFirstName() == null || instructor.getFirstName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firstname cannot be empty");
        }
        if(instructor.getLastName() == null || instructor.getLastName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lastname cannot be empty");
        }
        if(instructor.getSpecialty() == null || instructor.getSpecialty().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialty cannot be empty");
        }
    }
}
