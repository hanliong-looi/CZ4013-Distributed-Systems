package cz4013.shared.request;

import java.util.ArrayList;

/**
 * The request to view availability of a facility in array form.
 */
public class ViewFacilityAvailabilityArrayRequest {
    public String facName;
    public ArrayList<Integer> days;

    public ViewFacilityAvailabilityArrayRequest(){

    }

    public ViewFacilityAvailabilityArrayRequest(String facName, ArrayList<Integer>days){
        this.facName = facName;
        this.days = days;
    }
}
