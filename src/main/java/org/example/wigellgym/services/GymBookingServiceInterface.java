package org.example.wigellgym.services;

import org.example.wigellgym.entities.GymBooking;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface GymBookingServiceInterface {

    GymBooking createBooking(Long id, String date, Authentication user);

    String cancelBooking(Long id, Authentication user);

    List<GymBooking> getMyBookings(Authentication user);

    List<GymBooking> getCanceledBookings();

    List<GymBooking> getUpcomingBookings();

    List<GymBooking> getPastBookings();
}
