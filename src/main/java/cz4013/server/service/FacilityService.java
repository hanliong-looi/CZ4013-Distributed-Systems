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
 * This class provides various facility services and handle interactions with DB.
 */
public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();

    /**
     * Constructs a {@code BankService}.
     *
     * @param transport transport layer to be used to send data for registered monitoring clients
     */
    public FacilityService(Transport transport) {
        this.transport = transport;
    }

    /**
   * Processes a view facility availability request.
   *
   * @param req the request to be processed
   * @return the response after processing the given request
   */
    public ViewFacilityAvailabilityResponse processViewFacilityAvailability(ViewFacilityAvailabilityRequest req) {
        ArrayList<ArrayList<BookingDetail>> bookingList = db.getBookings(req.facName, req.days);
        if(bookingList == null){
            return ViewFacilityAvailabilityResponse.failed("There is no such facility/ no bookings for this facility.");
        }
        else{
            return new ViewFacilityAvailabilityResponse(bookingList, "");
        }

        // AccountDetail accountDetail = db.query(req.accountNumber);
        // if (accountDetail == null) {
        // return QueryResponse.failed("This account number doesn't exist");
        // }
        // if (!req.password.equals(accountDetail.password)) {
        // return QueryResponse.failed("Wrong password");
        // }
        // broadcast(String.format("Someone queries account %d", req.accountNumber));
        // return new QueryResponse(accountDetail.name, accountDetail.currency, accountDetail.amount, true, "");
    }
}
