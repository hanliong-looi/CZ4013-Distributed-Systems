package cz4013.client;

import cz4013.shared.request.*;
import cz4013.shared.response.*;

import java.time.Duration;
import java.util.ArrayList;
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
        ArrayList<Integer> days = askDays();
        ViewFacilityAvailabilityResponse resp = client.request(
            "viewFacilityAvailability",
            new ViewFacilityAvailabilityRequest(name, days),
            new Response<ViewFacilityAvailabilityResponse>() {}
        );
        System.out.printf("Facility %s availability: \n", resp.facilityName);
        // for(int i=0; i<resp.bookingDetails.size(); i++){
        //     System.out.printf("%s %d: \n", resp.bookingDetails.get(i), resp.bookingDetails.get(i).get(i));
        // }
    }

    private String askFacilityName() {
        int choice = 0;
        String str = "";
        System.out.print("----------------------------------------------------------------\n" +
        "Please choose a Facility\n" +
        "1: North Hill Gym\n");
        choice = Util.safeReadInt();
        if(choice == 1){
            str = "North Hill Gym";
        } 
        return str;
    }
    
    private ArrayList<Integer> askDays() {
        int choice = 0;
        int i = 0;
        ArrayList<Integer> days = new ArrayList<Integer>();
        System.out.println("Please choose date (1-7 for Mon-Sun, 8 to cancel) = ");
        while(choice!=8){
            if(choice < 1 || choice > 8){
                System.out.println("Choice must be 1-8!");
                continue;
            }
            int chosenDay = Util.safeReadInt();
            days.set(i, chosenDay);
            i++;
        }
        return days;
    }
}
