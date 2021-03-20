package cz4013.server.entity;

import java.util.ArrayList;

/**
 * Details about a facility stored in DB.
 */
public class FacilityDetail {
    public String name;
    public String operatingHours;
    public String address;
    public ArrayList<String> reviews;

    /**
     * Constructs an entry for a facility in DB.
     *
     * @param name     name of the facility
     * @param reviews  list of reviews for the facility
     */
    public FacilityDetail(String name, String operatingHours, String address, ArrayList<String> reviews) {
        this.name = name;
        this.operatingHours = operatingHours;
        this.address = address;
        this.reviews = reviews;
    }

    public void addReview(String review){
        this.reviews.add(review);
    }

    @Override
    public String toString() {
        String str = "FacilityDetail(Name: " + name + ", Operating Hours: " + operatingHours + ", Address: " + address + ", Reviews: \n";
        for(int i = 0; i < reviews.size(); i++){
            str = str + reviews.get(i) + ",\n";
        }
        return str;
    }
}
