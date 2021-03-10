package cz4013.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.print.event.PrintEvent;

public class schedule {
    // First get(0) returns the list of bookings for that day
    // Second get(0) returns the particular booking of that day
    // Third get(0) returns the attribute of that booking 0 = startHour, 1 =
    // startMin, 2 = endHour, 3 = endMin
    // public static ArrayList<ArrayList<ArrayList<String>>> bookingList;

    public static void main(String[] args) {
        // tableWithLines();
        int interval = 30; // minutes interval
        List<String> timesList = new ArrayList(); // time array
        String[] ap = { "AM", "PM" }; // AM-PM
        String timeFormat;

        for (int h = 9; h <= 17; h++) {
            for (int m = 0; m < 60;) {
                if (h < 12) {
                    timeFormat = String.format("%02d:%02d %s", h, m, "AM");
                } else {
                    timeFormat = String.format("%02d:%02d %s", h, m, "PM");
                }
                timesList.add(timeFormat);
                m = m + interval;
            }
        }
        // To match the time
        // timeFormat = String.format("%02d:%02d %s", 0, 0, "AM");
        // timesList.add(timeFormat);

        // System.out.println(timesList);

        // for (String time : timesList) {
        // System.out.println(time);
        // }

        ArrayList<String> days = new ArrayList<String>() {
            {
                add("Mon");
                add("Tue");
                add("Wed");
                add("Thu");
            }
        };

        ArrayList<ArrayList<String>> booking = new ArrayList<ArrayList<String>>();
        booking.add(new ArrayList<String>());
        
        booking.get(0).add("hello");
        booking.get(1).add("bye");
        booking.get(2).add("shit");
        System.out.println(booking);
        // ArrayList<String> temp = new ArrayList<String>(); // added () 
        // temp.add("Hello world.");
        // booking.add(temp);

        // for(int i=0; i<days.size(); i++){
        //     //for(int j=0; j<booking.get(i).size(); j++){
        //         System.out.printf("%s", booking.get(0).get(0));
        //     //}
        // }
        System.out.format("\n");
        // bookingList.add(new ArrayList<String>(Arrays.asList(1, 2, 3)));
        // bookingList.add(new ArrayList<String>(Arrays.asList(1, 2, 3)));
        // for (List<String> list : bookingList) {
            
        // }


        String leftAlignFormat = "| %-11s |%n";

        // for table design
        System.out.format("+-------------+");
        for (int i = 0; i < days.size(); i++) {
            System.out.print("-----+");
        }
        System.out.format("\n");

        // to print days
        System.out.format("| Column name | ");
        for (int i = 0; i < days.size(); i++) {
            System.out.print(days.get(i) + " | ");
        }
        System.out.print("\n");

        // for table design
        System.out.format("+-------------+");
        for (int i = 0; i < days.size(); i++) {
            System.out.print("-----+");
        }
        System.out.format("%n");

        // for (int i = 0; i < 9; i++) {
        // System.out.format(leftAlignFormat, "some data" + i, i * i);
        // }

        // print time
        for (String time : timesList) {
            System.out.format(leftAlignFormat, time);
            //to fill the schedule
             

            System.out.format("+-------------+");
            for (int i = 0; i < days.size(); i++) {
                System.out.print("-----+");
            }
            System.out.format("%n");
        }

    }

    public static void tableWithLines() {
        /*
         * leftJustifiedRows - If true, it will add "-" as a flag to format string to
         * make it left justified. Otherwise right justified.
         */
        boolean leftJustifiedRows = true;

        /*
         * Table to print in console in 2-dimensional array. Each sub-array is a row.
         */
        String[][] table = new String[][] { { "id", "First Name", "Last Name", "Age" },
                { "1", "John", "Johnson", "45" }, { "2", "Tom", "", "35" }, { "3", "Rose", "Johnson", "22" },
                { "4", "Jimmy", "Kimmel", "" } };

        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         * 
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table).forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
            if (columnLengths.get(i) == null) {
                columnLengths.put(i, 0);
            }
            if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
            }
        }));
        System.out.println("columnLengths = " + columnLengths);

        /*
         * Prepare format String
         */
        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream().forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");
        System.out.println("formatString = " + formatString.toString());

        /*
         * Prepare line for top, bottom & below header row.
         */
        String line = columnLengths.entrySet().stream().reduce("", (ln, b) -> {
            String templn = "+-";
            templn = templn + Stream.iterate(0, (i -> i < b.getValue()), (i -> ++i)).reduce("", (ln1, b1) -> ln1 + "-",
                    (a1, b1) -> a1 + b1);
            templn = templn + "-";
            return ln + templn;
        }, (a, b) -> a + b);
        line = line + "+\n";
        System.out.println("Line = " + line);

        /*
         * Print table
         */
        System.out.print(line);
        Arrays.stream(table).limit(1).forEach(a -> System.out.printf(formatString.toString(), a));
        System.out.print(line);

        Stream.iterate(1, (i -> i < table.length), (i -> ++i))
                .forEach(a -> System.out.printf(formatString.toString(), table[a]));
        System.out.print(line);
    }
}
