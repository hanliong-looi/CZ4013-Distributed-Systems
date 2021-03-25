package cz4013.shared.request;

import java.util.ArrayList;

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
