package cz4013.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.print.event.PrintEvent;

public class schedule {
    public static void main(String[] args) {    
        ArrayList<String> days = new ArrayList<String>() {
            {
                // add("Mon");
                add("Tue");
                // add("Wed");
                // add("Thu");
                // add("Fri");
            }
        };
        
        ArrayList<ArrayList<ArrayList<String>>> bookingList = new ArrayList<ArrayList<ArrayList<String>>>();
        bookingList.add(new ArrayList<ArrayList<String>>());
        ArrayList<String> bookingDetail = new ArrayList<String>();
        bookingDetail.add("15");
        bookingDetail.add("00");
        bookingDetail.add("16");
        bookingDetail.add("30");
        bookingList.get(0).add(bookingDetail);

        // System.out.printf("First get: %s \n", bookingList.get(0));
        // System.out.printf("Second get: %s \n", bookingList.get(0).get(0));
        // System.out.printf("Third get: %s %s\n", bookingList.get(0).get(0).get(0), bookingList.get(0).get(0).get(1));

        //try new method
        //String alignFormat = " %-10s |";
        System.out.format("\n");
        // for table design
        System.out.format("+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+");
        System.out.format("\n");

        // to print time
        System.out.format("| Column name | ");
        System.out.format("  09:00 AM  |  09:30 AM   |  10:00 AM   |  10:30 AM   |  11:00 AM   |  11:30 AM   |  12:00 PM   |  12:30 PM   |  13:00 PM   |  13:30 PM   |  14:00 PM   |  14:30 PM   |  15:00 PM   |  15:30 PM   |  16:00 PM   |  16:30 PM   |  17:00 PM   |");
        System.out.print("\n");

        // for table design
        System.out.format("+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+");
        System.out.format("\n");

        //for days
        float duration;
        int count;
        float countHour = 9;
        float countMin = 0;
        for (int i = 0; i < days.size(); i++) {
            System.out.print("|     " + days.get(i) + "     |");
            
                if(!bookingList.get(i).isEmpty())
                {
                    for(int j = 0; j <=17; j++){
                        duration = getDuration(bookingList);
                        count = (int)(duration/0.5);
                        if((Math.floor(countHour) == Float.parseFloat(bookingList.get(i).get(0).get(0))))
                        {
                            if(bookingList.get(i).get(0).get(1) == "00" && countMin%2==0)
                            {
                                for(int k=0; k < count; k++)
                                    System.out.print("     hi      |");
                                j+=count;
                            }   
                            else if(bookingList.get(i).get(0).get(1) == "30" && countMin%2!=0)
                            {
                                for(int k=0; k < count; k++)
                                    System.out.print("     hi      |");
                                j+=count;
                            }  
                            else
                                System.out.print("             |");
                        }
                        // else if((Math.floor(countHour) == Float.parseFloat(bookingList.get(i).get(0).get(0))) && countMinThirty.equals(bookingList.get(i).get(0).get(1)))
                        // {
                        //     System.out.print("     hi      |");
                        // }
                        else
                        {
                            System.out.print("             |");
                        }
                        countMin++;
                        countHour+=0.5;
                    }
                    
                    // for table design
                    System.out.print("\n");
                    System.out.format("+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+");
                    System.out.format("\n");
                }
                else
                {
                    // for table design
                    System.out.print("\n");
                    System.out.format("+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+-------------+");
                    System.out.format("\n");
                }
        }
        System.out.print("\n");
    }

    private static float getDuration(ArrayList<ArrayList<ArrayList<String>>> bookingList) {
        String startTime = bookingList.get(0).get(0).get(0) + bookingList.get(0).get(0).get(1);
        String endTime = bookingList.get(0).get(0).get(2) + bookingList.get(0).get(0).get(3);
        float subtractMultiplier = Float.parseFloat(bookingList.get(0).get(0).get(2))
                - Float.parseFloat(bookingList.get(0).get(0).get(0));
        return ((Float.parseFloat(endTime) - Float.parseFloat(startTime)) - (40 * subtractMultiplier)) / 60;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
