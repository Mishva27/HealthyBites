// CoachModel.java
package com.example.healthybites.models;

// This model class is used to map the data from your Firestore 'coaches' collection.
public class CoachModel {

    // These variable names MUST match the field names in your Firestore documents exactly.
    // For example, 'name' in Firestore maps to 'name' here.
    private String name;
    private String specialization;
    private String experience;
    private String bio;
    private double rating;
    private String imageUrl;
    private String contact;

    // A public no-argument constructor is required for Firestore's toObject() method.
    public CoachModel() {
        // Default constructor required for calls to DataSnapshot.getValue(CoachModel.class)
    }

    // You also need public getters and setters for each field.
    // For Firestore, the naming convention should be 'getFieldName' and 'setFieldName'.
    // If you prefer to use different variable names (e.g., 'coachName' instead of 'name'),
    // you must use the @PropertyName annotation, but matching the names is simpler.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
