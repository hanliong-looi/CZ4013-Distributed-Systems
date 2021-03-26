package cz4013.client;

import cz4013.shared.request.*;
import cz4013.shared.response.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

    // Hashmap listing bookings made by client
    // Key: facility name
    // Value: 2D array, outer array: day, inner array: list of bookingID for that day
    private HashMap<String, ArrayList<ArrayList<Integer>>> bookingsMade = new HashMap<String, ArrayList<ArrayList<Integer>>>();

    public FacilityClient(Client client) {
        this.client = client;
    }

    /**
     * Sends a request to server to view facility availability.
     */
    public void runViewFacilityAvailability() {
        System.out.println("Please input the following information");
        // get facility name
        String name = askFacilityName();

        // get days selected
        ArrayList<Integer> days = askDays();

        ViewFacilityAvailabilityResponse resp = client.request("viewFacilityAvailability",
                new ViewFacilityAvailabilityRequest(name, days), new Response<ViewFacilityAvailabilityResponse>() {
                });

        ViewFacilityAvailabilityArrayResponse arrayResp = client.request("viewFacilityAvailabilityArray",
                new ViewFacilityAvailabilityArrayRequest(name, days),
                new Response<ViewFacilityAvailabilityArrayResponse>() {
                });

        System.out.format("\n");
        System.out.printf("Facility %s availability: \n", name);

        // for table design
        System.out.format("\n");
        System.out.format(
                "+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
        System.out.format("\n");

        // to print time
        System.out.format("|      | ");
        System.out.format(
                "09:00 | 09:30 | 10:00 | 10:30 | 11:00 | 11:30 | 12:00 | 12:30 | 13:00 | 13:30 | 14:00 | 14:30 | 15:00 | 15:30 | 16:00 | 16:30 | 17:00 |");
        System.out.print("\n");

        // for table design
        System.out.format(
                "+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
        System.out.format("\n");

        float duration;
        int count;
        float countHour = 9;
        float countMin = 0;

        if (days.size() == 0) {
            System.out.println("No date selected!");
        }

        // loop through the days selected
        for (int i = 0; i < days.size(); i++) {

            // if the day contains booking(s)
            if (!arrayResp.availArray.get(i).isEmpty()) {
                // print days in first column
                System.out.print("| " + convertIntToDay(days.get(i)) + "  |");
                // loop through the timeslots
                for (int j = 0; j < 17; j++) {
                    // if there is a booking in that slot
                    if (arrayResp.availArray.get(i).get(j) == 1) {
                        System.out.print("  hi   |");
                    }
                    // if no booking in that slow
                    else {
                        System.out.print("       |");
                    }
                }
                // for table design
                System.out.print("\n");
                System.out.format(
                        "+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
                System.out.format("\n");
            }
            // if no booking for that day
            else {
                // for table design
                for (int j = 0; j <= 16; j++) {
                    System.out.print("       |");
                }
                System.out.print("\n");
                System.out.format(
                        "+------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+");
                System.out.format("\n");
            }
        }
        System.out.print("\n");
    }

    /**
     * Sends a request to server to add facility booking.
     */
    public void runAddFacilityBooking() {
        System.out.println("Please input the following information");
        String name = askFacilityName();
        // choose a day
        int bookingDay = askBookingDay();
        // choose timing, start time, end time
        String startTime = askStartTime();
        String endTime = askEndTime();
        // need check that end time is not earlier than start time
        while (true) {
            if (Integer.parseInt(endTime) < Integer.parseInt(startTime)) {
                System.out.println("End time must be later than start time!");
                startTime = askStartTime();
                endTime = askEndTime();
            }
            break;
        }

        // convert start time and end time into integers
        int startHour = Integer.parseInt(startTime.substring(0, 2));
        int startMin = Integer.parseInt(startTime.substring(2, 4));
        int endHour = Integer.parseInt(endTime.substring(0, 2));
        int endMin = Integer.parseInt(endTime.substring(2, 4));

        AddFacilityBookingResponse resp = client.request("addFacilityBooking",
                new AddFacilityBookingRequest(name, bookingDay, startHour, startMin, endHour, endMin),
                new Response<AddFacilityBookingResponse>() {
                });

        // get back unique confirmation ID
        // need check for availability, return error msg if unavailable
        if (resp.success) {
            System.out.printf("Your confirmation ID is: %d\n", resp.bookingId);
            System.out.println(resp.errorMessage);
            if (bookingsMade.containsKey(name)) {
                bookingsMade.get(name).get(bookingDay - 1).add(resp.bookingId);
            } else {
                bookingsMade.put(name, new ArrayList<ArrayList<Integer>>());
                for (int i = 0; i < 7; i++) {
                    bookingsMade.get(name).add(new ArrayList<Integer>());
                }
                bookingsMade.get(name).get(bookingDay - 1).add(resp.bookingId);
            }
        } else {
            System.out.println(resp.errorMessage);
        }
    }

    /**
     * Sends a request to server to modify facility booking.
     * @throws ParseException
     */
    public void runModifyFacilityBooking() throws ParseException {
        // If client has not made any booking, end function call
        if (bookingsMade.isEmpty()) {
            System.out.println("You have not made any bookings yet.");
            return;
        }

        // Query list of bookings made by client
        ArrayList<String> facNameList = new ArrayList<String>();
        ArrayList<Integer> dayList = new ArrayList<Integer>();
        ArrayList<Integer> idList = new ArrayList<Integer>();
        for (Entry<String, ArrayList<ArrayList<Integer>>> entry : bookingsMade.entrySet()) {
            String facName = entry.getKey();
            ArrayList<ArrayList<Integer>> bookingsMadeList = entry.getValue();
            for (int i = 0; i < bookingsMadeList.size(); i++) {
                if (bookingsMadeList.get(i).size() == 0) {
                    continue;
                }
                for (int j = 0; j < bookingsMadeList.get(i).size(); j++) {
                    facNameList.add(facName);
                    dayList.add(i + 1);
                    idList.add(bookingsMadeList.get(i).get(j));
                }
            }
        }
        ViewPersonalBookingsRequest vreq = new ViewPersonalBookingsRequest(facNameList, dayList, idList);
        ViewPersonalBookingsResponse vresp = client.request("viewPersonalBookings",
                vreq,
                new Response<ViewPersonalBookingsResponse>() {
                });
        
        // Print list of bookings queried by client
        String str = String.format("[1-%d]", vresp.bookingsMade.size());
        System.out.printf("Please select the booking to be modified " + str + ": \n");
        for (int i = 0; i < vresp.bookingsMade.size(); i++) {
            String facName = vresp.bookingsMade.get(i).get(0);
            String dayStr = convertIntToDay(Integer.valueOf(vresp.bookingsMade.get(i).get(2)));
            String startHour = vresp.bookingsMade.get(i).get(3);
            if (startHour.length() == 1) {
                startHour = "0" + startHour;
            }
            String startMin = vresp.bookingsMade.get(i).get(4);
            if (startMin.length() == 1) {
                startMin = "0" + startMin;
            }
            String startTime = startHour + startMin;
            String endHour = vresp.bookingsMade.get(i).get(5);
            if (endHour.length() == 1) {
                endHour = "0" + endHour;
            }
            String endMin = vresp.bookingsMade.get(i).get(6);
            if (endMin.length() == 1) {
                endMin = "0" + endMin;
            }
            String endTime = endHour + endMin;
            System.out.println(
                    "[" + Integer.toString(i + 1) + "]: " + facName + " " + dayStr + " " + startTime + "-" + endTime);
        }
        System.out.println("[0]: Return to menu");

        // Ask client to select the booking to be modified
        String chosenFacName, chosenStartHour, chosenStartMin, chosenEndHour, chosenEndMin;
        int chosenDay, chosenBookingId, choice;
        while (true) {
            choice = Util.safeReadInt();
            if (choice == 0) {
                return;
            } else if (choice < 1 || choice > vresp.bookingsMade.size()) {
                System.out.println("Invalid choice!");
            } else {
                chosenFacName = vresp.bookingsMade.get(choice - 1).get(0);
                chosenDay = Integer.valueOf(vresp.bookingsMade.get(choice - 1).get(2));
                chosenBookingId = Integer.valueOf(vresp.bookingsMade.get(choice - 1).get(1));
                chosenStartHour = vresp.bookingsMade.get(choice - 1).get(3);
                chosenStartMin = vresp.bookingsMade.get(choice - 1).get(4);
                chosenEndHour = vresp.bookingsMade.get(choice - 1).get(5);
                chosenEndMin = vresp.bookingsMade.get(choice - 1).get(6);
                break;
            }
        }
        
        if (chosenStartHour.length() == 1) {
            chosenStartHour = "0" + chosenStartHour;
        }
        if (chosenStartMin.length() == 1) {
            chosenStartMin = "0" + chosenStartMin;
        }
        if (chosenEndHour.length() == 1) {
            chosenEndHour = "0" + chosenEndHour;
        }
        if (chosenEndMin.length() == 1) {
            chosenEndMin = "0" + chosenEndMin;
        }
        String chosenStartTime = chosenStartHour + chosenStartMin;
        String chosenEndTime = chosenEndHour + chosenEndMin;

        // Ask client for the details of modification
        System.out.println("Do you want to bring forward or postpone your booking?");
        System.out.println("1: Bring forward");
        System.out.println("2: Postpone");
        int offchoice;
        boolean bf = false;
        while (true) {
            offchoice = Util.safeReadInt();
            if (offchoice < 1 || offchoice > 2) {
                System.out.println("Invalid choice!");
            }else{ 
                if(offchoice == 1){
                    if(chosenStartTime.equals("0900")){                        
                        System.out.println("Start time cannot be earlier than 0900 AM!");
                        System.out.println("Invalid choice!");
                        continue;
                    }else{
                        bf = true;
                    }
                }else if(offchoice == 2){
                    if(chosenEndTime.equals("1700")){
                        System.out.println("End time cannot be later than 1700 PM!");
                        System.out.println("Invalid choice!");
                        continue;
                    } 
                }
                break;
            }
        }

        int offset;
        while (true) {
            System.out.println("Please indicate the duration: (e.g 1 = 30 minutes, 2 = 1 hour)");
            offset = Util.safeReadInt();
            if(offset < 1 || offset > 16){
                System.out.println("Invalid duration! ");
            }
            // check that client does not postpone booking past 5pm or bring forward before
            // 9am
            
            if (bf && checkDuration(chosenStartTime, chosenEndTime, offset, bf)) {
                System.out.println("Start time cannot be earlier than 0900 AM!");
                continue;
            } else if (!bf && checkDuration(chosenStartTime, chosenEndTime, offset, bf)) {
                System.out.println("End time cannot be later than 1700 PM!");
                continue;
            } else {
                break;
            }
        }
        if(bf){
            offset *= -1;
        }

        // Send modify facility request to server
        ModifyFacilityBookingResponse mresp = client.request("modifyFacilityBooking",
                new ModifyFacilityBookingRequest(chosenFacName, chosenDay, chosenBookingId, offset),
                new Response<ModifyFacilityBookingResponse>() {
                });
        if (mresp.success) {
            bookingsMade.get(chosenFacName).get(chosenDay - 1).remove((Object) chosenBookingId);
            bookingsMade.get(chosenFacName).get(chosenDay - 1).add(mresp.bookingId);
            System.out.println("Booking successfully modified.");
        } else {
            System.out.println(mresp.errorMessage);
        }
    }

    /**
     * Monitors the availability of a facility.
     */
    public void runMonitorFacilityAvailability() {
        String name = askFacilityName();
        System.out.print("Monitor interval (s) = ");
        int interval = Util.safeReadInt();

        MonitorStatusResponse status = client.request(
            "monitor", 
            new MonitorFacilityAvailabilityRequest(name, interval),
            new Response<MonitorStatusResponse>() {}
        );

        if(status.success){
            System.out.println("Successfully requested to monitor, waiting for updates...");
            client.poll(
                new Response<MonitorUpdateResponse>() {}, 
                Duration.ofSeconds(interval), 
                update -> System.out.println("Update: " + update.info)
            );
            System.out.println("Finished monitoring");
        }
        else{
            System.out.println("Failed to request to monitor");
        }
    }

    /**
     * Sends a request to server to view facility details.
     */
    public void runViewFacilityDetail() {
        System.out.println("Please input the following information");
        String name = askFacilityName();
        ViewFacilityDetailResponse resp = client.request("viewFacilityDetail", new ViewFacilityDetailRequest(name),
                new Response<ViewFacilityDetailResponse>() {
                });
        System.out.println("Facility Name: " + resp.name);
        System.out.println("Operating Hours: " + resp.operatingHours);
        System.out.println("Address: " + resp.address);
        System.out.println("Reviews: ");
        if(resp.reviews.size()==0){
            System.out.println("There are no reviews for this facility yet.");
        }
        for (int i = 0; i < resp.reviews.size(); i++) {
            System.out.println("[" + Integer.toString(i + 1) + "] " + resp.reviews.get(i));
        }
    }

    /**
     * Sends a request to server to add a review for a facility.
     */
    public void runAddFacilityReview() {
        System.out.println("Please input the following information");
        String name = askFacilityName();
        String review = askReview();
        AddFacilityReviewResponse resp = client.request("addFacilityReview", new AddFacilityReviewRequest(name, review),
                new Response<AddFacilityReviewResponse>() {
                });
        if (resp.success) {
            System.out.println("Review successfully added!");
        } else {
            System.out.println("Error adding review");
        }
    }

    private String convertIntToDay(int i) {
        switch (i) {
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
        while (true) {
            System.out.print("----------------------------------------------------------------\n"
                    + "Please choose a Facility\n" + "1: North Hill Gym\n" + "2: North Hill Multi Purpose Hall\n");
            choice = Util.safeReadInt();
            if (choice == 1) {
                str = "North Hill Gym";
                break;
            }
            else if (choice == 2) {
                str = "North Hill Multi Purpose Hall";
                break;
            }
            else {
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
        while (choice != 8) {
            choice = Util.safeReadInt();
            if (choice < 1 || choice > 8) {
                System.out.println("Choice must be 1-8!");
                continue;
            }
            if (choice != 8) {
                days.add(choice);
            }
        }
        Collections.sort(days);
        return days;
    }

    private int askBookingDay() {
        int choice = 0;
        System.out.println("Please choose date (1-7 for Mon-Sun)");
        while (true) {
            choice = Util.safeReadInt();
            if (choice < 1 || choice > 7) {
                System.out.println("Choice must be 1-7!");
                continue;
            } else
                break;

        }
        return choice;
    }

    private String askStartTime() {
        String s;
        while (true) {
            System.out.println("Please enter start time in 24hr format (eg. 1400 represent 2PM)");
            s = Util.readLine();
            // check if entered start time meets required length of 4
            if (s.length() != 4) {
                System.out.println("Time must only have 4 digits!");
                continue;
            }
            // check if entered start time contain only numbers
            if (Pattern.matches("[0-9]+", s) == false) {
                System.out.println("Time can only contain digits!");
                continue;
            }
            // check if entered start hour is within correct range 9-16
            if (Integer.parseInt(s.substring(0, 2)) < 9 || Integer.parseInt(s.substring(0, 2)) > 16) {
                System.out.println("Hour must be 09-16!");
                continue;
            }
            // check if entered start min is 00 or 30
            if (Integer.parseInt(s.substring(2, 4)) == 0 || Integer.parseInt(s.substring(2, 4)) == 30) {
                break;
            }
            System.out.println("Minute must be 00 or 30!");
            continue;
        }
        return s;
    }

    private String askEndTime() {
        String s;
        while (true) {
            System.out.println("Please enter end time in 24hr format (eg. 1400 represent 2PM)");
            s = Util.readLine();
            // check if entered start time meets required length of 4
            if (s.length() != 4) {
                System.out.println("Time must only have 4 digits!");
                continue;
            }
            // check if entered start time contain only numbers
            if (Pattern.matches("[0-9]+", s) == false) {
                System.out.println("Time can only contain digits!");
                continue;
            }
            // check if entered end hour is within correct range 9-17
            if (Integer.parseInt(s.substring(0, 2)) < 9 || Integer.parseInt(s.substring(0, 2)) > 17) {
                System.out.println("Hour must be 09-17!");
                continue;
            }
            // check for special case: since facility closes at 1700, cannot end at 1730
            if (Integer.parseInt(s.substring(0, 2)) == 17 && Integer.parseInt(s.substring(2, 4)) == 30) {
                System.out.println("Facility closes at 17:00 hrs. Choose an earlier end time!");
                continue;
            }
            // check if entered end min is 00 or 30
            if (Integer.parseInt(s.substring(2, 4)) == 0 || Integer.parseInt(s.substring(2, 4)) == 30) {
                break;
            }
            System.out.println("Minute must be 00 or 30!");
            continue;

        }
        return s;
    }

    private boolean checkDuration(String startTime, String endTime, int offset, boolean bf) throws ParseException {
        // check that start time dont go before 9am
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        int duration = offset * 30;
        if (bf) {
            Date d = df.parse(startTime); 
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, -duration);
            String newTime = df.format(cal.getTime());
            if(Integer.parseInt(newTime) < 900)
                return true;
            else
                return false;
        } else {
            Date d = df.parse(endTime); 
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, duration);
            String newTime = df.format(cal.getTime());
            if(Integer.parseInt(newTime) > 1700)
                return true;
            else
                return false;
        }
    }
    

    private String askReview() {
        System.out.println("Please enter a review for this facility: ");
        String review = Util.readLine();
        return review;
    }

    private void bookingsMadeToString(String facName){
        System.out.println("To string: " + facName);
        for(int i = 0; i < bookingsMade.get(facName).size(); i++){
            System.out.print("Day: " + Integer.toString(i+1));
            System.out.print(" Booking IDs: ");
            for(int j = 0; j < bookingsMade.get(facName).get(i).size(); j ++){
                System.out.print(Integer.toString(bookingsMade.get(facName).get(i).get(j)) + ",");
            }
            System.out.println();
        }
    }
}
