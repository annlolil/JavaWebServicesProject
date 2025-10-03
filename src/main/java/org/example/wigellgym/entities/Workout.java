package org.example.wigellgym.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String workoutName;

    @Column(length = 20, nullable = false)
    private String type;

    @Column(length = 10, nullable = false)
    private Integer maxNrOfParticipants;

    @Column(length = 20, nullable = false)
    private Double priceInSEK;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @Column(length = 10, nullable = false)
    private boolean deleted = false;

    public Workout() {}

    public Workout(String workoutName, String type, Integer maxNrOfParticipants,
                   Double priceInSEK, Instructor instructor) {
        this.workoutName = workoutName;
        this.type = type;
        this.maxNrOfParticipants = maxNrOfParticipants;
        this.priceInSEK = priceInSEK;
        this.instructor = instructor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMaxNrOfParticipants() {
        return maxNrOfParticipants;
    }

    public void setMaxNrOfParticipants(Integer maxNrOfParticipants) {
        this.maxNrOfParticipants = maxNrOfParticipants;
    }


    public Double getPriceInSEK() {
        return priceInSEK;
    }

    public void setPriceInSEK(Double priceInSEK) {
        this.priceInSEK = priceInSEK;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
