package cz4013.shared.request;

import java.util.ArrayList;

/**
 * The request to view availability of a facility.
 */
public class ViewFacilityAvailabilityRequest {
    public String facName;
    public ArrayList<Integer> days;

    public ViewFacilityAvailabilityRequest() {
    }

    public ViewFacilityAvailabilityRequest(String facName, ArrayList<Integer> days){
        super();
        this.facName = facName;
        this.days = days;
    }

    @Override
    public String toString() {
        String str = "ViewFacilityAvailabilityRequest(" + facName + ", ";
        for(int i = 0; i < days.size(); i++){
            str = str + Integer.toString(days.get(i)) + ",\n";
        }
        return str;
    }
}
