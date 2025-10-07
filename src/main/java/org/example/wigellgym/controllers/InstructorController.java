package org.example.wigellgym.controllers;

import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping("/addinstructor")
    public ResponseEntity<Instructor> addInstructor(@RequestBody Instructor instructor) {
        return new ResponseEntity<>(instructorService.addInstructor(instructor), HttpStatus.CREATED);
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        return new ResponseEntity<>(instructorService.getAllInstructors(), HttpStatus.OK);
    }
}
