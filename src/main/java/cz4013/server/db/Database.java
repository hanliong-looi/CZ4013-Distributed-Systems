package cz4013.server.db;

import cz4013.server.entity.BookingDetail;
import cz4013.server.entity.FacilityDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An in-memory key-value based database which records of facilities and their respective bookings.
 * For facility DB, key =  facility name, value = FacilityDetail
 * For booking DB, key = facility name, value = BookingDetail
 */

public class Database {
    private HashMap<String, FacilityDetail> facDB = new HashMap<String, FacilityDetail>();
    private HashMap<String, ArrayList<BookingDetail>> bookDB = new HashMap<String, ArrayList<BookingDetail>>();

    /**
     * Adds a booking for a facility.
     * If the booking already exists, it will return false, meaning the booking has failed.
     *
     * @param facName name of facility
     * @param bookingDetail detail of the booking
     */
    public boolean addBooking(String facName, BookingDetail bookingDetail) {
        if(bookDB.containsKey(facName)){
            if(bookDB.get(facName).contains(bookingDetail)){
                return false;
            }
            else{
                bookDB.get(facName).add(bookingDetail);
                return true;
            }
        }
        else{
            bookDB.put(facName, new ArrayList<BookingDetail>());
            bookDB.get(facName).add(bookingDetail);
            return true;
        }
    }

    /**
     * Queries all bookings for a facility
     * Returns the list of bookings for that facility
     *
     * @param facName name of facility
     */
    public ArrayList<BookingDetail> getBookings(String facName){
        if(bookDB.containsKey(facName)){
            return bookDB.get(facName);
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
            if(bookDB.get(facName).contains(bookingDetail)){
                int index = bookDB.get(facName).indexOf(bookingDetail);
                bookDB.get(facName).remove(index);
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
}
