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
import java.util.regex.Pattern;

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
        //get facility name
        String name = askFacilityName();

        //get days selected
        ArrayList<Integer> days = askDays();

        ViewFacilityAvailabilityResponse resp = client.request(
            "viewFacilityAvailability",
            new ViewFacilityAvailabilityRequest(name, days),
            new Response<ViewFacilityAvailabilityResponse>() {}
        );

        ViewFacilityAvailabilityArrayResponse arrayResp = client.request(
            "viewFacilityAvailabilityArray",
            new ViewFacilityAvailabilityArrayRequest(name, days),
            new Response<ViewFacilityAvailabilityArrayResponse>() {}
        );

        System.out.printf("Facility %s availability: \n", name);
        
        // for table design
        System.out.format("\n");
        System.out.format("+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
        System.out.format("\n");
        
        // to print time
        System.out.format("|      | ");
        System.out.format("09:00 | 09:30 | 10:00 | 10:30 | 11:00 | 11:30 | 12:00 | 12:30 | 13:00 | 13:30 | 14:00 | 14:30 | 15:00 | 15:30 | 16:00 | 16:30 | 17:00 |");
        System.out.print("\n");

        // for table design
        System.out.format("+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
        System.out.format("\n");
        
        float duration;
        int count;
        float countHour = 9;
        float countMin = 0;

        //loop through the days selected
        for (int i = 0; i < days.size(); i++) {
            
            //print days in first column
            System.out.print("| " + convertIntToDay(days.get(i)) + "  |");
                
                //if the day contains booking(s)
                if(!resp.bookingList.get(i).isEmpty())
                {   
                    //loop through the bookings on that day
                    for(int p = 0; p < resp.bookingList.get(i).size(); p++){
                        //loop through the timeslots
                        for(int j = 0; j <=17; j++){
                            //getting duration of the booking
                            duration = getDuration(resp.bookingList, i, p);
                            //get number of slots booked, based on duration. 1 slot = 0.5 hour
                            count = (int)(duration/0.5);
                            //print 'x' if slot is booked
                            //while looping through timeslots, if timeslot matches the booking start hour
                            if((Math.floor(countHour) == Float.parseFloat(resp.bookingList.get(i).get(p).get(0))))
                            {
                                //check if start min is 00 and that the current timeslot min is 00
                                if(resp.bookingList.get(i).get(p).get(1).equals("0") && j%2==0)
                                {
                                    //printing 'x' for the number of slots booked
                                    for(int k=0; k < count; k++)
                                        System.out.print("  hi   |");
                                    j+=count;
                                }   
                                //check if start min is 30 and that the current timeslot min is 30
                                else if(resp.bookingList.get(i).get(p).get(1).equals("30")  && j%2!=0)
                                {
                                    //printing 'x' for the number of slots booked
                                    for(int k=0; k < count; k++)
                                        System.out.print("  hi   |");
                                    j+=count;
                                }  
                                else
                                    //if not match, just leave it empty
                                    System.out.print("       |");
                            }
                            // else if((Math.floor(countHour) == Float.parseFloat(bookingList.get(i).get(0).get(0))) && countMinThirty.equals(bookingList.get(i).get(0).get(1)))
                            // {
                            //     System.out.print("     hi      |");
                            // }
                            else
                            {
                                System.out.print("       |");
                            }
                            countMin++;
                            countHour+=0.5;
                        }
                    }    
                    // for table design
                    System.out.print("\n");
                    System.out.format("+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
                    System.out.format("\n");
                    
                }
                else
                {
                    // for table design
                    System.out.print("\n");
                    System.out.format("+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
                    System.out.format("\n");
                }
        }
        System.out.print("\n");
        
        // System.out.println("Size of booking list response: " + Integer.toString(resp.bookingList.size()));
        // System.out.println("Size of booking list 2nd array response: " + Integer.toString(resp.bookingList.get(0).size()));
        // for(int i=0; i<resp.bookingList.size(); i++){
        //     //First get(0) returns the list of bookings for that day
        //     //Second get(0) returns the particular booking of that day
        //     //Third get(0) returns the attribute of that booking 0 = startHour, 1 = startMin, 2 = endHour, 3 = endMin
            
        //     System.out.printf("%s: %s \n", convertIntToDay(days.get(i)), resp.bookingList.get(0).get(0).get(0));
            
            
        //     //for testing
        //     System.out.printf("First get: %s \n", resp.bookingList.get(0));
        //     System.out.printf("Second get: %s \n", resp.bookingList.get(0).get(0));
        //     System.out.printf("Third get: %s \n", resp.bookingList.get(0).get(0).get(0));
        // }
    }

    /**
    * Sends a request to server to add facility booking.
    */
    public void runAddFacilityBooking() {
        System.out.println("Please input the following information");
        String name = askFacilityName();
        //choose a day
        int bookingDay = askBookingDay();
        //choose timing, start time, end time
        String startTime = askStartTime();
        String endTime = askEndTime();
        //need check that end time is not earlier than start time
        while(true){
            if(Integer.parseInt(endTime) < Integer.parseInt(startTime))
            {
                System.out.println("End time must be later than start time!");
                startTime = askStartTime();
                endTime = askEndTime();
            }
            break;
        }

        //convert start time and end time into integers
        int startHour = Integer.parseInt(startTime.substring(0,2));
        int startMin = Integer.parseInt(startTime.substring(2,4));
        int endHour = Integer.parseInt(endTime.substring(0,2));
        int endMin = Integer.parseInt(endTime.substring(2,4));
        
        
        AddFacilityBookingResponse resp = client.request(
            "addFacilityBooking",
            new AddFacilityBookingRequest(name, bookingDay, startHour, startMin, endHour, endMin),
            new Response<AddFacilityBookingResponse>() {}
        );

        //get back unique confirmation ID
        //need check for availability, return error msg if unavailable
        if(resp.success){
            System.out.printf("Your confirmation ID is: %d\n", resp.bookingId);
            System.out.println(resp.errorMessage);
            if(bookingsMade.containsKey(name)){
                bookingsMade.get(name).get(bookingDay-1).add(resp.bookingId);
            }
            else{
                bookingsMade.put(name, new ArrayList<ArrayList<Integer>>());
                for(int i = 0; i < 7; i++){
                    bookingsMade.get(name).add(new ArrayList<Integer>());
                }
                bookingsMade.get(name).get(bookingDay-1).add(resp.bookingId);
            }
        }
        else{
            System.out.println(resp.errorMessage);
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
                if(bookingsMadeList.get(i).size()==0){
                    continue;
                }
                for(int j = 0; j < bookingsMadeList.get(i).size(); j++){
                    facNameList.add(facName);
                    dayList.add(i+1);
                    idList.add(bookingsMadeList.get(i).get(j));
                    length ++;
                }
            }
        }
        ViewPersonalBookingsResponse vresp = client.request(
            "viewPersonalBookings",
            new ViewPersonalBookingsRequest(length, facNameList, dayList, idList),
            new Response<ViewPersonalBookingsResponse>() {}
        );
        String str = String.format("[1-%d]", vresp.bookingsMade.size());
        System.out.printf("Please select the booking to be modified " + str + ": \n");
        for(int i = 0; i < vresp.bookingsMade.size(); i++){
            String facName = vresp.bookingsMade.get(i).get(0);
            String dayStr = convertIntToDay(Integer.valueOf(vresp.bookingsMade.get(i).get(2)));
            String startHour = vresp.bookingsMade.get(i).get(3);
            if(startHour.length()==1){
                startHour = "0" + startHour;
            }
            String startMin = vresp.bookingsMade.get(i).get(4);
            if(startMin.length()==1){
                startMin = "0" + startMin;
            }
            String startTime = startHour + startMin;
            String endHour = vresp.bookingsMade.get(i).get(5);
            if(endHour.length()==1){
                endHour = "0" + endHour;
            }
            String endMin = vresp.bookingsMade.get(i).get(6);
            if(endMin.length()==1){
                endMin = "0" + endMin;
            }
            String endTime = endHour + endMin;
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
        while(true){
            System.out.print("----------------------------------------------------------------\n" +
            "Please choose a Facility\n" +
            "1: North Hill Gym\n");
            choice = Util.safeReadInt();
            if(choice == 1){
                str = "North Hill Gym";
                break;
            } 
            else{
                System.out.println("Please only choose one of the following options!");
                continue;
            }
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

    private static float getDuration(ArrayList<ArrayList<ArrayList<String>>> bookingList, int i, int p) {
        //get start time of the booking
        String startTime = bookingList.get(i).get(p).get(0) + bookingList.get(i).get(p).get(1);

        //get end time of the booking
        String endTime = bookingList.get(i).get(p).get(2) + bookingList.get(i).get(p).get(3);

        //subtracting the time together will not get you the correct duration
        //need to minus off to get the correct duration
        //first, get the subtract multiplier (since as the time increase, the more inaccuracy)
        //subtract multiplier will be used to remove the addtional values
        float subtractMultiplier = Float.parseFloat(bookingList.get(i).get(p).get(2)) - Float.parseFloat(bookingList.get(i).get(p).get(0));
        
        //change duration to minutes format then 
        //return duration in hour format
        return ((Float.parseFloat(endTime) - Float.parseFloat(startTime)) - (40 * subtractMultiplier)) / 60;
    }
    
    private int askBookingDay(){
        int choice = 0;
        System.out.println("Please choose date (1-7 for Mon-Sun)");
        choice = Util.safeReadInt();
        if(choice < 1 || choice > 7){
            System.out.println("Choice must be 1-7!");
        }
        return choice;
    }

    private String askStartTime() {
        String s;
        while(true){
            System.out.println("Please enter start time in 24hr format (eg. 1400 represent 2PM)");
            s = Util.readLine();
            //check if entered start time meets required length of 4
            if(s.length() > 4){
                System.out.println("Time must only have 4 digits!");
                continue;
            }
            //check if entered start time contain only numbers
            if(Pattern.matches("[0-9]+", s) == false){
                System.out.println("Time can only contain digits!");
                continue;
            }
            //check if entered start hour is within correct range 9-16
            if(Integer.parseInt(s.substring(0,2)) < 9 || Integer.parseInt(s.substring(0,2)) > 16)
            {
                System.out.println("Hour must be 09-16!");
                continue;
            }
            //check if entered start min is 00 or 30
            if(Integer.parseInt(s.substring(2,4)) == 0 || Integer.parseInt(s.substring(2,4)) == 30)
            {
                break;
            }
            System.out.println("Minute must be 00 or 30!");
            continue;
        }
        return s;
    }

    private String askEndTime() {
        String s;
        while(true){
            System.out.println("Please enter end time in 24hr format (eg. 1400 represent 2PM)");
            s = Util.readLine();
            //check if entered end hour is within correct range 9-17
            if(Integer.parseInt(s.substring(0,2)) < 9 || Integer.parseInt(s.substring(0,2)) > 17)
            {
                System.out.println("Hour must be 09-17!");
                continue;
            }
            //check for special case: since facility closes at 1700, cannot end at 1730
            if(Integer.parseInt(s.substring(0,2)) == 17 && Integer.parseInt(s.substring(2,4)) == 30)
            {
                System.out.println("Facility closes at 17:00 hrs. Choose an earlier end time!");
                continue;
            }
            //check if entered end min is 00 or 30
            if(Integer.parseInt(s.substring(2,4)) == 0 || Integer.parseInt(s.substring(2,4)) == 30)
            {
                break;
            }
            System.out.println("Minute must be 00 or 30!");
            continue;
            
        }
        return s;
    }

    

    private String askReview(){
        System.out.println("Please enter a review for this facility: ");
        String review = Util.readLine();
        return review;
    }
}
