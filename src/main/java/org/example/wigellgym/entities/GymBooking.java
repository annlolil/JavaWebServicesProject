package org.example.wigellgym.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Bookings")
public class GymBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private LocalDate date;

    @Column(length = 20)
    private double firstBookingDiscountPercentage;

    @Column(length = 20, nullable = false)
    private double totalPriceSEK;

    @Column(length = 20, nullable = false)
    private double totalPriceEuro;

    @Column(length = 20, nullable = false)
    private String customer;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(length = 10, nullable = false)
    private boolean active;

    public GymBooking() {}

    public GymBooking(LocalDate date, double firstBookingDiscountPercentage, double totalPriceSEK,
                      double totalPriceEuro, String customer, Workout workout, boolean active) {
        this.date = date;
        this.firstBookingDiscountPercentage = firstBookingDiscountPercentage;
        this.totalPriceSEK = totalPriceSEK;
        this.totalPriceEuro = totalPriceEuro;
        this.customer = customer;
        this.workout = workout;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getFirstBookingDiscountPercentage() {
        return firstBookingDiscountPercentage;
    }

    public void setFirstBookingDiscountPercentage(double firstBookingDiscountPercentage) {
        this.firstBookingDiscountPercentage = firstBookingDiscountPercentage;
    }

    public double getTotalPriceSEK() {
        return totalPriceSEK;
    }

    public void setTotalPriceSEK(double totalPriceSEK) {
        this.totalPriceSEK = totalPriceSEK;
    }

    public double getTotalPriceEuro() {
        return totalPriceEuro;
    }

    public void setTotalPriceEuro(double totalPriceEuro) {
        this.totalPriceEuro = totalPriceEuro;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
