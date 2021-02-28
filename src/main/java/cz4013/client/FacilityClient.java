package cz4013.client;

import cz4013.shared.request.*;
import cz4013.shared.response.*;

import java.time.Duration;
import java.util.Optional;

/**
 * This class provides client side service calls.
 */
public class FacilityClient {
    private Client client;

    public FacilityClient(Client client) {
        this.client = client;
    }

    /**
    * Sends a request to server to view facility availability.
    */
    public void runViewFacilityAvailability() {
        System.out.println("Please input the following information");
        String name = askFacilityName();
        String days = askDays();
        ViewFacilityAvailabilityResponse resp = client.request(
            "viewFacility",
            new ViewFacilityAvailabilityRequest(name, days),
            new Response<ViewFacilityAvailabilityResponse>() {}
        );
        System.out.printf("Facility %s is available on %s", resp.facilityName, resp.availability);
    }

    private String askFacilityName() {
        System.out.print("Facility Name = ");
        return Util.readLine();
    }
    
    private String askDays() {
        System.out.print("Facility Name = ");
        return Util.readLine();
    }
}
