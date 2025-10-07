package org.example.wigellgym.controllers;

import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.services.GymBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wigellgym")
public class GymBookingController {

    private final GymBookingService gymBookingService;

    @Autowired
    public GymBookingController(GymBookingService gymBookingService) {
        this.gymBookingService = gymBookingService;
    }

    @PostMapping("/bookworkout")
    public ResponseEntity<GymBooking> bookWorkout(@RequestParam Long id, @RequestParam String date, Authentication user) {
        return new ResponseEntity<>(gymBookingService.createBooking(id, date, user), HttpStatus.CREATED);
    }

    @PutMapping("/cancelworkout")
    public ResponseEntity<String> cancelWorkout(@RequestParam Long id, Authentication user) {
        return new ResponseEntity<>(gymBookingService.cancelBooking(id, user), HttpStatus.OK);
    }

    @GetMapping("/mybookings")
    public ResponseEntity<List<GymBooking>> getMyBookings(Authentication user) {
        return new ResponseEntity<>(gymBookingService.getMyBookings(user), HttpStatus.OK);
    }

    @GetMapping("/listcanceled")
    public ResponseEntity<List<GymBooking>> getCanceledBookings() {
        return new ResponseEntity<>(gymBookingService.getCanceledBookings(), HttpStatus.OK);
    }

    @GetMapping("/listupcoming")
    public ResponseEntity<List<GymBooking>> getUpcomingBookings() {
        return new ResponseEntity<>(gymBookingService.getUpcomingBookings(), HttpStatus.OK);
    }

    @GetMapping("/listpast")
    public ResponseEntity<List<GymBooking>> getPastBookings() {
        return new ResponseEntity<>(gymBookingService.getPastBookings(), HttpStatus.OK);
    }
}
