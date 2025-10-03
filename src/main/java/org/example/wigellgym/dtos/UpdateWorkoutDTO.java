package org.example.wigellgym.dtos;

import org.example.wigellgym.entities.Instructor;

public class UpdateWorkoutDTO {

    private Long id;
    private String workoutName;
    private String type;
    private Integer maxNrOfParticipants;
    private double priceInSEK;
    private Instructor instructor;

    public UpdateWorkoutDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPriceInSEK() {
        return priceInSEK;
    }

    public void setPriceInSEK(double priceInSEK) {
        this.priceInSEK = priceInSEK;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}
