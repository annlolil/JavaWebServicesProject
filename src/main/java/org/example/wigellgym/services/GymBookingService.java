package org.example.wigellgym.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.wigellgym.dtos.CurrencyConverterDTO;
import org.example.wigellgym.entities.GymBooking;
import org.example.wigellgym.entities.Workout;
import org.example.wigellgym.repositories.GymBookingRepository;
import org.example.wigellgym.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GymBookingService implements GymBookingServiceInterface {

    private final GymBookingRepository gymBookingRepository;
    private final WorkoutRepository workoutRepository;
    private static final Logger LOGGER = LogManager.getLogger("useranalyze");
    private final RestClient restClient;

    @Value("${currency-converter.api.key}")
    private String apiKey;

    @Autowired
    public GymBookingService(GymBookingRepository gymBookingRepository, WorkoutRepository workoutRepository, RestClient.Builder restClientBuilder) {
        this.gymBookingRepository = gymBookingRepository;
        this.workoutRepository = workoutRepository;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public GymBooking createBooking(Long id, String date, Authentication user) {

        LocalDate bookedDate = validDateFormat(date);

        Workout workout = workoutRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));

        if(workout.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout deleted");
        }

        if (bookedDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date");
        }

        int nrOfParticipants = countNrOfParticipants(bookedDate, workout);
        if (nrOfParticipants == workout.getMaxNrOfParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max number of booked participants exceeded");
        }

        GymBooking newGymBooking = new GymBooking();
        String customer = user.getName();
        double totalPriceInSEK;
        double totalPriceInEuro;

        //Check if customer books for the first time and therefor gets a discount
        if(getMyBookings(user).isEmpty()) {
            newGymBooking.setFirstBookingDiscountPercentage(10.0);
            totalPriceInSEK = workout.getPriceInSEK()-
                    (workout.getPriceInSEK()*newGymBooking.getFirstBookingDiscountPercentage()*0.01);
        }
        else {
            totalPriceInSEK = workout.getPriceInSEK();
        }
        totalPriceInEuro = getConvertedPrice(totalPriceInSEK);

        newGymBooking.setCustomer(customer);
        newGymBooking.setWorkout(workout);
        newGymBooking.setTotalPriceSEK(totalPriceInSEK);
        newGymBooking.setTotalPriceEuro(totalPriceInEuro);
        newGymBooking.setActive(true);
        newGymBooking.setDate(bookedDate);

        gymBookingRepository.save(newGymBooking);

        LOGGER.info("User created a new booking for {} with ID: {} at {}",
                workout.getWorkoutName(), workout.getId(), newGymBooking.getDate());

        return newGymBooking;
    }

    @Override
    public String cancelBooking(Long id, Authentication user) {

        GymBooking gymBookingToCancel = gymBookingRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!gymBookingToCancel.getCustomer().equals(user.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!gymBookingToCancel.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already canceled");
        }

        if (!gymBookingToCancel.getDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too late to cancel");
        }

        gymBookingToCancel.setActive(false);

        gymBookingRepository.save(gymBookingToCancel);

        LOGGER.info("User canceled the booking to {} with ID: {} at {}.",
                gymBookingToCancel.getWorkout().getWorkoutName(), gymBookingToCancel.getWorkout().getId(), gymBookingToCancel.getDate());

        return "Booking canceled";
    }

    @Override
    public List<GymBooking> getMyBookings(Authentication user) {

        return gymBookingRepository.findAll().stream().filter(b ->
                b.getCustomer().equals(user.getName())).collect(Collectors.toList());
    }

    @Override
    public List<GymBooking> getCanceledBookings() {

        List<GymBooking> canceledGymBookings = new ArrayList<>();

        for (GymBooking gymBooking : gymBookingRepository.findAll()) {
            if (!gymBooking.isActive()) {
                canceledGymBookings.add(gymBooking);
            }
        }
        return canceledGymBookings;
    }

    @Override
    public List<GymBooking> getUpcomingBookings() {

        List<GymBooking> upcomingGymBookings = new ArrayList<>();

        for (GymBooking gymBooking : gymBookingRepository.findAll()) {
            if (gymBooking.isActive() &&
                    (gymBooking.getDate().isAfter(LocalDate.now()) ||
                    gymBooking.getDate().isEqual(LocalDate.now()))) {
                upcomingGymBookings.add(gymBooking);
            }
        }
        return upcomingGymBookings;
    }

    @Override
    public List<GymBooking> getPastBookings() {

        List<GymBooking> pastGymBookings = new ArrayList<>();

        for (GymBooking gymBooking : gymBookingRepository.findAll()) {
            if (gymBooking.getDate().isBefore(LocalDate.now())) {
                pastGymBookings.add(gymBooking);
            }
        }
        return pastGymBookings;
    }

    private int countNrOfParticipants(LocalDate date, Workout workout) {

        List<GymBooking> existingBookings = new ArrayList<>();
        int nrOfParticipants;

        if (!getUpcomingBookings().isEmpty()) {
            existingBookings = getUpcomingBookings().stream().filter(booking ->
                    booking.getDate().equals(date)).filter(booking ->
                    booking.getWorkout().getId().equals(workout.getId())).toList();
        }
        nrOfParticipants = existingBookings.size();
        return nrOfParticipants;
    }

    private double getConvertedPrice(double priceInSEK) {

        String amount = String.valueOf(priceInSEK);
        CurrencyConverterDTO currencyConverterResponse = restClient.get()
                .uri("https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/SEK/Eur/" + amount)
                .retrieve()
                .body(new ParameterizedTypeReference<CurrencyConverterDTO>() {
                });
        if (currencyConverterResponse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Something went wrong with the currency converter");
        }
        return currencyConverterResponse.getConversionResult();
    }

    private LocalDate validDateFormat(String date) {

        try {
            LocalDate validDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return validDate;
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date format, it should be yyyy-mm-dd");
        }
    }
}
