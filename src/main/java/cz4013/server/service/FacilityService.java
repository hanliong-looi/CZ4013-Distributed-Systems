package cz4013.server.service;

import cz4013.server.db.Database;
import cz4013.server.entity.BookingDetail;
import cz4013.server.entity.FacilityDetail;
import cz4013.shared.request.*;
import cz4013.shared.response.*;
import cz4013.shared.rpc.Transport;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class provides various facility services and handle interactions with
 * DB.
 */
public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();
    private int nextAvailableBookingId = 1;

    /**
     * Constructs a {@code BankService}.
     *
     * @param transport transport layer to be used to send data for registered
     *                  monitoring clients
     */
    public FacilityService(Transport transport) {
        this.transport = transport;
    }

    /**
     * Processes a view facility detail request.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewFacilityDetailResponse processViewFacilityDetail(ViewFacilityDetailRequest req) {
        FacilityDetail facDetail = db.getFacility(req.facName);
        if (facDetail == null) {
            return ViewFacilityDetailResponse.failed("There is no such facility.");
        } 
        else {
            return new ViewFacilityDetailResponse(facDetail.name, facDetail.operatingHours, facDetail.address, facDetail.reviews, "");
        }
    }

    /**
     * Processes a request to write a review about a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public AddFacilityReviewResponse processAddFacilityReview(AddFacilityReviewRequest req) {
        boolean success = db.addReview(req.facName, req.review);
        if(success){
            return new AddFacilityReviewResponse(true, "");
        }
        else{
            return AddFacilityReviewResponse.failed("There is no such facility.");
        }
    }

    /**
     * Processes a view facility availability request.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewFacilityAvailabilityResponse processViewFacilityAvailability(ViewFacilityAvailabilityRequest req) {
        ArrayList<ArrayList<BookingDetail>> bookingList = db.getBookings(req.facName, req.days);
        if (bookingList == null) {
            return ViewFacilityAvailabilityResponse.failed("There is no such facility/ no bookings for this facility.");
        } else {
            ArrayList<ArrayList<ArrayList<String>>> ar = new ArrayList<ArrayList<ArrayList<String>>>();
            int startHour, startMin;
            for (int i = 0; i < bookingList.size(); i++) {
                ar.add(new ArrayList<ArrayList<String>>());

                for (int j = 0; j < bookingList.get(i).size(); j++) {
                    // Content of inner most array: facName, bookID, day, startHour, startMin, endHour, endMin, duration
                    ArrayList<String> bookingDetail = new ArrayList<String>();

                    startHour = bookingList.get(i).get(j).startHour;
                    startMin = bookingList.get(i).get(j).startMin;
                    bookingDetail.add(Integer.toString(startHour));
                    bookingDetail.add(Integer.toString(startMin));

                    double duration = bookingList.get(i).get(j).duration;
                    ArrayList<String> endTime = convertDurationToEndTime(startHour, startMin, duration);

                    bookingDetail.add(endTime.get(0));
                    bookingDetail.add(endTime.get(1));
                    bookingDetail.add(Double.toString(duration));
                    ar.get(i).add(bookingDetail);
                }
            }
            // broadcast(String.format("Someone queried facility availability for %s ", req.facName));
            return new ViewFacilityAvailabilityResponse(ar, "");
        }
    }

    /**
     * Processes a view facility availability in array request.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewFacilityAvailabilityArrayResponse processViewFacilityAvailabilityArray(ViewFacilityAvailabilityArrayRequest req) {
        ArrayList<ArrayList<Integer>> ar = new ArrayList<ArrayList<Integer>>();
        ar = db.getBookingArray(req.facName, req.days);
        if(ar!=null){
            return new ViewFacilityAvailabilityArrayResponse(ar, "");
        }
        else{
            return ViewFacilityAvailabilityArrayResponse.failed("Error returning availability array");
        }
    }

    /**
     * Processes a request to view all bookings made by the client.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewPersonalBookingsResponse processViewPersonalBookings(ViewPersonalBookingsRequest req){
        ArrayList<ArrayList<String>> resp = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < req.facNameList.size(); i++){
            String facName = req.facNameList.get(i);
            int day = req.dayList.get(i);
            int id = req.idList.get(i);
            BookingDetail bd = db.getOneBooking(facName, day, id);
            if(bd == null){
                System.out.println("Booking not found, index: " + Integer.toString(i));
                continue;
            }
            ArrayList<String> bdStr = new ArrayList<String>();
            bdStr.add(facName);
            bdStr.add(Integer.toString(id));
            bdStr.add(Integer.toString(day));
            bdStr.add(Integer.toString(bd.startHour));
            bdStr.add(Integer.toString(bd.startMin));
            ArrayList<String> endTime = convertDurationToEndTime(bd.startHour, bd.startMin, bd.duration);
            bdStr.add(endTime.get(0));
            bdStr.add(endTime.get(1));
            resp.add(bdStr);
        }
        return new ViewPersonalBookingsResponse(resp, "");
    }
    
    /**
     * Processes a request to make a booking for a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public AddFacilityBookingResponse processAddFacilityBooking(AddFacilityBookingRequest req) {
        int bookingId = nextAvailableBookingId++;
        
        double duration = req.endHour - req.startHour;
        if(req.endMin - req.startMin == 30){
            duration += 0.5;
        }
        else if(req.startMin - req.endMin == 30){
            duration -= 0.5;
        }

        BookingDetail bookingDetail =  new BookingDetail(req.facName, bookingId, req.day, req.startHour, req.startMin, duration);
        boolean check = checkBooking(req.facName, req.day, req.startHour, req.startMin, duration);
        if(!check){
            return AddFacilityBookingResponse.failed("Booking timing clashes with existing bookings.");
        }
        boolean success = db.addBooking(req.facName, bookingDetail);
        if(!success){
            return AddFacilityBookingResponse.failed("Booking failed.");
        }
        else{
            broadcast(String.format("Client added new booking %s on %s", bookingDetail.facName, convertIntToDay(bookingDetail.day)));
            return new AddFacilityBookingResponse(bookingId, true, "");
        }
    }

    /**
     * Processes a request to modify a booking for a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ModifyFacilityBookingResponse processModifyFacilityBooking(ModifyFacilityBookingRequest req) {
        // if offset is positive, means client is requesting to push the booking timing to a later time
        BookingDetail oldBookingDetail = db.getOneBooking(req.facName, req.day, req.bookingId);
        if(oldBookingDetail == null){
            return ModifyFacilityBookingResponse.failed("Booking doesn't exist");
        }
        System.out.println(oldBookingDetail.toString());
        boolean deleteSuccess = db.deleteBooking(req.facName, oldBookingDetail);
        if(!deleteSuccess){
            return ModifyFacilityBookingResponse.failed("Booking modification failed, cannot delete existing booking");
        }
        BookingDetail newBookingDetail = new BookingDetail(oldBookingDetail.facName, oldBookingDetail.bookingId, oldBookingDetail.day, 
                                                            oldBookingDetail.startHour, oldBookingDetail.startMin, oldBookingDetail.duration);
        if(req.offset > 0){
            if(req.offset % 2 == 1){
                if(oldBookingDetail.startMin == 30){
                    newBookingDetail.startMin = 0;
                    req.offset += 1;
                }
                else{
                    newBookingDetail.startMin = 30;
                }
            }
            newBookingDetail.startHour += (req.offset / 2);
        }
        else{
            req.offset *= -1;
            if(req.offset % 2 == 1){
                if(oldBookingDetail.startMin == 30){
                    newBookingDetail.startMin = 0;
                }
                else{
                    newBookingDetail.startMin = 30;
                    req.offset += 1;
                }
            }
            newBookingDetail.startHour -= (req.offset / 2);
        }
        System.out.println(newBookingDetail.toString());
        boolean check = checkBooking(newBookingDetail.facName, newBookingDetail.day, newBookingDetail.startHour, newBookingDetail.startMin, newBookingDetail.duration);
        if(!check){
            db.addBooking(oldBookingDetail.facName, oldBookingDetail);
            return ModifyFacilityBookingResponse.failed("Booking modification failed, new booking clashes with existing bookings.");
        }
        if(db.addBooking(newBookingDetail.facName, newBookingDetail)){
            int bookingId = nextAvailableBookingId++;
            newBookingDetail.bookingId = bookingId;
            broadcast(String.format("Client modified booking %s on %s", newBookingDetail.facName, convertIntToDay(newBookingDetail.day)));
            return new ModifyFacilityBookingResponse(newBookingDetail.bookingId, true, "");
        }
        else{
            db.addBooking(oldBookingDetail.facName, oldBookingDetail);
            return ModifyFacilityBookingResponse.failed("Booking modification failed.");
        }
    }

    /**
     * Converts start time and duration to end time
     *
     */
    private ArrayList<String> convertDurationToEndTime(int startHour, int startMin, Double duration){
        ArrayList<String> endTime = new ArrayList<String>();
        int endHour, endMin;
        String durationStr = String.valueOf(duration);
        int indexOfDecimal = durationStr.indexOf(".");
        String minStr = durationStr.substring(indexOfDecimal+1);
        if(minStr.equals("5")){
            if(startMin == 30){
                endHour = startHour + (int)Math.floor(duration) + 1;
                endMin = 0;
            }
            else{
                endHour = startHour + (int)Math.floor(duration);
                endMin = 30;
            }
        }
        else{
            endHour = startHour + (int)Math.floor(duration);
            endMin = startMin;
        }
        endTime.add(Integer.toString(endHour));
        endTime.add(Integer.toString(endMin));
        return endTime;
    }

    /**
     * Checks if the booking made is valid
     *
     */
    private boolean checkBooking(String facName, int day, int startHour, int startMin, Double duration){
        ArrayList<Integer> dayAr = new ArrayList<Integer>();
        dayAr.add(day);
        ArrayList<ArrayList<Integer>> bookArray = new ArrayList<ArrayList<Integer>>();
        bookArray = db.getBookingArray(facName, dayAr);
        int startIdx = convertStartTimeToIndex(startHour, startMin);
        int count = (int)(duration * 2);
        for(int i = 0; i < count; i ++){
            if(bookArray.get(0).get(startIdx+i) == 1){
                System.out.println("timing clashed");
                return false;
            }
        }
        System.out.println("timing no clash");
        return true;
    }

    /**
     * Given a start time and start min, return the index of the time on the availability array
     * @param startHour
     * @param startMin
     * @return
     */
    public int convertStartTimeToIndex(int startHour, int startMin){
        int index = 0;
        index = (startHour - 9) * 2;
        if(startMin == 30){
            index += 1;
        }
        return index;
    }

    /**
     * Converts an integer to its corresponding day of the week
     * @param i
     * @return
     */
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

    /**
   * Processes a monitoring request.
   *
   * @param req the request to be processed
   * @return the response after processing the given request
   */
    public MonitorStatusResponse processMonitor(MonitorFacilityAvailabilityRequest req, SocketAddress remote) {
        long interval = req.interval;
        listeners.put(remote, Instant.now().plusSeconds(interval));
        System.out.printf("User at %s starts to monitor for %d seconds\n", remote, interval);
        return new MonitorStatusResponse(true);
    }
    
    /**
     * Sends a message to all clients that have requested to monitor the particular facility
     * @param info the message to be displayed to the clients
     */
    private void broadcast(String info) {
        purgeListeners();
        System.out.println(info);
        Response<MonitorUpdateResponse> resp = new Response<>(new ResponseHeader(UUID.randomUUID(), Status.OK),
                Optional.of(new MonitorUpdateResponse(info)));
        listeners.forEach((socketAddress, x) -> {
            transport.send(socketAddress, resp);
        });
    }

    /**
     * Checks if the client's monitoring duration has ran out.
     */
    private void purgeListeners() {
        listeners.entrySet().removeIf(x -> x.getValue().isBefore(Instant.now()));
    }
}
