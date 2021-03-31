package cz4013.shared.request;

import java.util.ArrayList;

/**
 * The request to view personal bookings.
 */
public class ViewPersonalBookingsRequest {
    public ArrayList<String> facNameList;
    public ArrayList<Integer> dayList;
    public ArrayList<Integer> idList;

    public ViewPersonalBookingsRequest(){

    }
    public ViewPersonalBookingsRequest(ArrayList<String> facNameList, ArrayList<Integer> dayList, ArrayList<Integer> idList){
        this.facNameList = facNameList;
        this.dayList = dayList;
        this.idList = idList;
    }

    @Override
    public String toString(){
        String str =  "ViewPersonalBookingsRequest( \n";
        String nameStr = "facNameList: ";
        String dayStr = "dayList: ";
        String idStr = "idList: ";
        for(int i = 0; i < facNameList.size(); i++) {
            nameStr =  nameStr + facNameList.get(i) +", ";
            dayStr =  dayStr + dayList.get(i) +", ";
            idStr =  idStr + idList.get(i) +", ";
        }
        nameStr += "\n";
        dayStr += "\n";
        idStr += "\n";
        return str + nameStr + dayStr + idStr;
    }
}
