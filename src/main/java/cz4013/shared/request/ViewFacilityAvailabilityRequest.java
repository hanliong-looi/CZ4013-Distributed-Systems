package cz4013.shared.request;

/**
 * The request to view availability of a facility.
 */
public class ViewFacilityAvailabilityRequest {
    public String facilityName;
    public String days;

    public ViewFacilityAvailabilityRequest(){

    }

    public ViewFacilityAvailabilityRequest(String facilityName, String days){
        super();
        this.facilityName = facilityName;
        this.days = days;
    }

    @Override
    public String toString() {
        return "DepositRequest(" + facilityName + ", " + days + ")";
    }
}
