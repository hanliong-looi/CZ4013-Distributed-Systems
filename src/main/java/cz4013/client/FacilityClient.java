package cz4013.client;

import cz4013.shared.request.*;
import cz4013.shared.response.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

/**
 * This class provides client side service calls.
 */
public class FacilityClient {
    private Client client;
    private HashMap<String, ArrayList<ArrayList<Integer>>> bookingsMade = new HashMap<String, ArrayList<ArrayList<Integer>>>();

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
        System.out.printf("Facility %s availability: \n", name);
        // System.out.println("Size of booking list response: " + Integer.toString(resp.bookingList.size()));
        // System.out.println("Size of booking list 2nd array response: " + Integer.toString(resp.bookingList.get(0).size()));
        for(int i=0; i<resp.bookingList.size(); i++){
            //First get(0) returns the list of bookings for that day
            //Second get(0) returns the particular booking of that day
            //Third get(0) returns the attribute of that booking 0 = startHour, 1 = startMin, 2 = endHour, 3 = endMin
            System.out.printf("%s: %s \n", convertIntToDay(days.get(i)), resp.bookingList.get(0).get(0).get(0));
        }
    }

    /**
    * Sends a request to server to modify facility booking.
    */
    public void runModifyFacilityBooking(){
        if(bookingsMade.isEmpty()){
            System.out.println("You have not made any bookings yet.");
            return;
        }
        int length = 0;
        ArrayList<String> facNameList = new ArrayList<String>();
        ArrayList<Integer> dayList = new ArrayList<Integer>();
        ArrayList<Integer> idList = new ArrayList<Integer>();
        for (Entry<String, ArrayList<ArrayList<Integer>>> entry : bookingsMade.entrySet()) {
            String facName = entry.getKey();
            ArrayList<ArrayList<Integer>> bookingsMadeList = entry.getValue();
            for(int i = 0; i < bookingsMadeList.size(); i++){
                facNameList.add(facName);
                dayList.add(bookingsMadeList.get(i).get(0));
                idList.add(bookingsMadeList.get(i).get(0));
                length ++;
            }
        }
        ViewPersonalBookingsResponse vresp = client.request(
            "viewPersonalBookings",
            new ViewPersonalBookingsRequest(length, facNameList, dayList, idList),
            new Response<ViewPersonalBookingsResponse>() {}
        );
        String str = String.format("[1-%d]", vresp.bookingsMade.size());
        System.out.printf("Please select the booking to be modified " + str + ": ");
        for(int i = 0; i < vresp.bookingsMade.size(); i++){
            String facName = vresp.bookingsMade.get(i).get(0);
            String dayStr = convertIntToDay(Integer.valueOf(vresp.bookingsMade.get(i).get(2)));
            String startTime = vresp.bookingsMade.get(i).get(3) + vresp.bookingsMade.get(i).get(4);
            String endTime = vresp.bookingsMade.get(i).get(5) + vresp.bookingsMade.get(i).get(6);
            System.out.println("[" + Integer.toString(i+1) + "]: " + facName + " " + dayStr + " " + startTime + "-" + endTime);
        }
        while(true){
            int choice = Util.safeReadInt();
            if(choice < 1 || choice > vresp.bookingsMade.size()){
                System.out.println("Invalid choice!");
            }
            else{
                break;
            }
            
        }
    }

    /**
    * Sends a request to server to view facility details.
    */
    public void runViewFacilityDetail(){
        System.out.println("Please input the following information");
        String name = askFacilityName();
        ViewFacilityDetailResponse resp = client.request(
            "viewFacilityDetail",
            new ViewFacilityDetailRequest(name),
            new Response<ViewFacilityDetailResponse>() {}
        );
        System.out.println("Facility Name: " + resp.name);
        System.out.println("Operating Hours: " + resp.operatingHours);
        System.out.println("Address: " + resp.address);
        System.out.println("Reviews: ");
        for(int i = 0; i < resp.reviews.size(); i++){
            System.out.println("[" + Integer.toString(i+1) + "] " + resp.reviews.get(i));
        }
    }

    /**
    * Sends a request to server to add a review for a  facility.
    */
    public void runAddFacilityReview(){
        System.out.println("Please input the following information");
        String name = askFacilityName();
        String review = askReview();
        AddFacilityReviewResponse resp = client.request(
            "addFacilityReview",
            new AddFacilityReviewRequest(name, review),
            new Response<AddFacilityReviewResponse>() {}
        );
        if(resp.success){
            System.out.println("Review successfully added!");
        }
        else{
            System.out.println("Error adding review");
        }
    }

    private String convertIntToDay(int i){
        switch(i){
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            case 7:
                return "Sun";
            default:
                return "Default: day not found";
        }
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
        ArrayList<Integer> days = new ArrayList<Integer>();
        System.out.println("Please choose date (1-7 for Mon-Sun, 8 to cancel) = ");
        while(choice!=8){
            choice = Util.safeReadInt();
            if(choice < 1 || choice > 8){
                System.out.println("Choice must be 1-8!");
                continue;
            }
            if(choice != 8){
                days.add(choice);
            }
        }
        Collections.sort(days);
        return days;
    }

    private String askReview(){
        System.out.println("Please enter a review for this facility: ");
        String review = Util.readLine();
        return review;
    }
}
