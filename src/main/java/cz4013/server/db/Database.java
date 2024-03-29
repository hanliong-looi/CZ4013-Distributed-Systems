package cz4013.server.db;

import cz4013.server.entity.BookingDetail;
import cz4013.server.entity.FacilityDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An in-memory key-value based database which records of facilities and their respective bookings.
 * For facility DB, key =  facility name, value = FacilityDetail
 * For booking DB, key = facility name, value = ArrayList<ArrayList<BookingDetail>> (Outer array = 7 days of booking, inner array = all bookings in that day)
 */

public class Database {
    private HashMap<String, FacilityDetail> facDB = new HashMap<String, FacilityDetail>();
    private HashMap<String, ArrayList<ArrayList<BookingDetail>>> bookDB = new HashMap<String, ArrayList<ArrayList<BookingDetail>>>();
    private HashMap<String, ArrayList<ArrayList<Integer>>> availDB = new HashMap<String, ArrayList<ArrayList<Integer>>>();

    //initialize the facilities in facDB and bookDB
    public Database(){
        // Creating facilities
        System.out.println("Initializing facilities in DB...");
        initializeFacDB();

        // Creating bookings
        System.out.println("Initializing bookings in array...");
        initializeAvailDB("North Hill Gym");
        initializeAvailDB("North Hill Multi Purpose Hall");

        System.out.println("Initializing bookDB...");
        ArrayList<ArrayList<BookingDetail>> ar = new ArrayList<ArrayList<BookingDetail>>();
        ArrayList<ArrayList<BookingDetail>> ar1 = new ArrayList<ArrayList<BookingDetail>>();
        for(int i = 0; i < 7; i++){
            ar.add(new ArrayList<BookingDetail>());
            ar1.add(new ArrayList<BookingDetail>());
        }

        System.out.println("Populating some bookings in DB...");
        bookDB.put("North Hill Gym", ar);
        bookDB.put("North Hill Multi Purpose Hall", ar1);

        BookingDetail bd = new BookingDetail("North Hill Gym", 123456, 1, 9, 30, 1.0);
        if(!addBooking("North Hill Gym", bd)){
            System.out.println("Adding of booking for NH Gym failed");
        }

        // End Initiliazation
        System.out.println("DB Initialized");
    }

    /**
     * Adds a booking for a facility.
     * If the booking already exists, it will return false, meaning the booking has failed.
     *
     * @param facName name of facility
     * @param bookingDetail detail of the booking
     */
    public boolean addBooking(String facName, BookingDetail bookingDetail) {
        int day = bookingDetail.day;
        int count = (int)(bookingDetail.duration * 2);
        int startIdx = convertStartTimeToIndex(bookingDetail.startHour, bookingDetail.startMin);

        // Checks if the facility name already exists in the HashMap
        if(bookDB.containsKey(facName)){

            // If the booking detail already exists in bookDB, return false
            if(bookDB.get(facName).get(day-1).contains(bookingDetail)){
                return false;
            }
            else{
                bookDB.get(facName).get(day-1).add(bookingDetail);
                for(int i = 0; i < count; i++){
                    availDB.get(facName).get(day-1).set(startIdx + i, 1); 
                }
                // bookingArrayToString(facName);
                return true;
            }
        }

        // If the facility name doesn't exist, initialize an empty 2D array of booking detail for the facility and insert the key-value pair
        else{
            ArrayList<ArrayList<BookingDetail>> ar = new ArrayList<ArrayList<BookingDetail>>();
            for(int i = 0; i < 7; i++){
                ar.add(new ArrayList<BookingDetail>());
            }
            ar.get(bookingDetail.day-1).add(bookingDetail);
            bookDB.put(facName, ar);
            initializeAvailDB(facName);
            for(int i = 0; i < count; i++){
                availDB.get(facName).get(day-1).set(startIdx + i, 1);
            }
            // bookingArrayToString(facName);
            return true;
        }
    }

    /**
     * Queries all bookings for a facility for the particular days
     * Returns the list of bookings for that facility
     *
     * @param facName name of facility
     */
    public ArrayList<ArrayList<BookingDetail>> getBookings(String facName, ArrayList<Integer> days){
        if(bookDB.containsKey(facName)){
            ArrayList<ArrayList<BookingDetail>> ar = new ArrayList<ArrayList<BookingDetail>>();
            for(int i = 0; i < days.size(); i++){
                ar.add(bookDB.get(facName).get(days.get(i)-1));
            }
            return ar;
        }
        else{
            return null;
        }
    }

    /**
     * Queries all bookings for a facility for the particular days in array
     * Returns the list of bookings for that facility
     *
     * @param facName name of facility
     */
    public ArrayList<ArrayList<Integer>> getBookingArray(String facName, ArrayList<Integer> days){
        if(availDB.containsKey(facName)){
            ArrayList<ArrayList<Integer>> ar = new ArrayList<ArrayList<Integer>>();
            for(int i = 0; i < days.size(); i++){
                ar.add(availDB.get(facName).get(days.get(i)-1));
            }
            return ar;
        }
        else{
            return null;
        }
    }

    /**
     * Queries all bookings for a facility for the particular days
     * Returns the list of bookings for that facility
     *
     * @param facName name of facility
     */
    public BookingDetail getOneBooking(String facName, int day, int bookingId){
        if(bookDB.containsKey(facName)){
            for(int i = 0; i < bookDB.get(facName).get(day-1).size(); i++){
                if(bookDB.get(facName).get(day-1).get(i).bookingId == bookingId){
                    return bookDB.get(facName).get(day-1).get(i);
                }
            }
            return null;
        }
        else{
            return null;
        }
    }

    /**
     * Deletes a booking for a facility.
     * If the booking does not exist, it will return false, meaning the deletion has failed.
     *
     * @param facName name of facility
     * @param bookingDetail detail of the booking to be deleted
     */
    public boolean deleteBooking(String facName, BookingDetail bookingDetail){
        if(bookDB.containsKey(facName)){
            if(bookDB.get(facName).get(bookingDetail.day-1).contains(bookingDetail)){
                int index = bookDB.get(facName).get(bookingDetail.day-1).indexOf(bookingDetail);
                bookDB.get(facName).get(bookingDetail.day-1).remove(index);
                int count = (int)(bookingDetail.duration * 2);
                int startIdx = convertStartTimeToIndex(bookingDetail.startHour, bookingDetail.startMin);
                for(int i = 0; i < count; i++){
                    availDB.get(facName).get(bookingDetail.day-1).set(startIdx + i, 0);
                }
                // bookingArrayToString(facName);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Queries a facility
     * Returns the detail for that facility, or null if facility doesnt exist
     *
     * @param facName name of facility
     */
    public FacilityDetail getFacility(String facName){
        if(facDB.containsKey(facName)){
            return facDB.get(facName);
        }
        else{
            return null;
        }
    }

    /**
     * Adds a review for a facility.
     * If the booking does not exist, it will return false, meaning the deletion has failed.
     *
     * @param facName name of facility
     * @param review detail of the booking to be deleted
     */
    public boolean addReview(String facName, String review){
        if(facDB.containsKey(facName)){
            facDB.get(facName).reviews.add(review);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Given the start hour and start min, convert the time to the positional index in the availability array
     * @param startHour
     * @param startMin
     * @return positional index
     */
    public int convertStartTimeToIndex(int startHour, int startMin){
        System.out.println("Received startHour, startMin: " + Integer.toString(startHour) + Integer.toString(startMin));
        int index = 0;
        index = (startHour - 9) * 2;
        if(startMin == 30){
            index += 1;
        }
        return index;
    }

    /**
     * Initialize the facilities and their information
     */
    public void initializeFacDB(){
        FacilityDetail fd = new FacilityDetail("North Hill Gym", "0900 - 1700", "60 Nanyang Crescent, Lvl 3, Block 19A Binjai Hall", new ArrayList<String>());
        facDB.put("North Hill Gym", fd);
        addReview("North Hill Gym", "Very lovely gym <3");
        FacilityDetail fd1 = new FacilityDetail("North Hill Multi Purpose Hall", "0900 - 1700", "60 Nanyang Crescent, Lvl 3, Block 19A Binjai Hall", new ArrayList<String>());
        facDB.put("North Hill Multi Purpose Hall", fd1);
    }

    /**
     * Initialize the availability array for given the facility name
     * @param facName
     */
    public void initializeAvailDB(String facName){
        ArrayList<ArrayList<Integer>> dayAr = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < 7; i ++){
            ArrayList<Integer> intAr = new ArrayList<Integer>();
            for(int j = 0; j < 17; j++){
                intAr.add(0);
            }
            dayAr.add(intAr);
        }
        availDB.put(facName, dayAr);
    }

    /** 
     * Convert the booking array to String format to be printed on the console for debugging
     */
    public void bookingArrayToString(String facName){
        for(int i = 0 ; i < 7; i ++){
            System.out.print("[");
            for(int j = 0; j < 17; j ++){
                System.out.print(Integer.toString(availDB.get(facName).get(i).get(j)));
            }
            System.out.print("]\n");
        }
    }
}
