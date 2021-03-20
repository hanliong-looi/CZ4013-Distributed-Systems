package cz4013.shared.request;

import java.util.ArrayList;

public class ViewPersonalBookingsRequest {
    public int length;
    public ArrayList<String> facNameList;
    public ArrayList<Integer> dayList;
    public ArrayList<Integer> idList;

    public ViewPersonalBookingsRequest(){

    }
    public ViewPersonalBookingsRequest(int length, ArrayList<String> facNameList, ArrayList<Integer> dayList, ArrayList<Integer> idList){
        this.length = length;
        this.facNameList = facNameList;
        this.dayList = dayList;
        this.idList = idList;
    }
}
