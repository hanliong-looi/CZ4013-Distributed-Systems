package cz4013.shared.request;

/**
 * The request to view details of a facility.
 */
public class ViewFacilityDetailRequest {
    public String facName;

    public ViewFacilityDetailRequest(){

    }

    public ViewFacilityDetailRequest(String facName){
        super();
        this.facName = facName;
    }
}


