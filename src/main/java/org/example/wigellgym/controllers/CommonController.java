package org.example.wigellgym.controllers;

import org.example.wigellgym.entities.Instructor;
import org.example.wigellgym.services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class CommonController {

    private final InstructorService instructorService;

    @Autowired
    public CommonController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        return new ResponseEntity<>(instructorService.getAllInstructors(), HttpStatus.OK);
    }
}
