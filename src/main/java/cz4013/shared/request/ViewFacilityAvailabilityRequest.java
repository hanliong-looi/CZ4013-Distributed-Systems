package cz4013.shared.request;

/**
 * The request to view availability of a facility.
 */
public class ViewFacilityAvailabilityRequest {
    public String facName;
    public String days;

    public ViewFacilityAvailabilityRequest() {
    }

    public ViewFacilityAvailabilityRequest(String facName, String days){
        super();
        this.facName = facName;
        this.days = days;
    }

    @Override
    public String toString() {
        return "DepositRequest(" + facName + ", " + days + ")";
    }
}
