package Kolokviumski.first10;

import java.io.*;
import java.util.*;

class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String message) {
        super(message);
    }
}

class InvalidTimeException extends Exception {
    public InvalidTimeException(String message) {
        super(message);
    }
}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}

class Time implements Comparable<Time> {
    int hours;
    int minutes;

    public Time(String time) throws UnsupportedFormatException, InvalidTimeException {
        String[] parts = time.split("\\.");
        if (parts.length == 1) {
            parts = time.split(":");
        }
        if (parts.length == 1) {
            throw new UnsupportedFormatException(time);
        }
        this.hours = Integer.parseInt(parts[0]);
        this.minutes = Integer.parseInt(parts[1]);
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            throw new InvalidTimeException(time);
        }
    }

    public String toAmPmFormat() {
        String part = (hours < 12 || hours == 24) ? "AM" : "PM";
        int h = (hours % 12 == 0) ? 12 : hours % 12;
        return String.format("%2d:%02d %s", h, minutes, part);
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d", hours, minutes);

    }

    @Override
    public int compareTo(Time o) {
        if (hours == o.hours) {
            return Integer.compare(minutes, o.minutes);
        } else return Integer.compare(hours, o.hours);
    }
}

class TimeTable {
    List<Time> times;

    public TimeTable() {
        times = new ArrayList<>();
    }

    public void readTimes(InputStream in) throws UnsupportedFormatException, InvalidTimeException {
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            for (String p : parts) {
                Time t = new Time(p);
                times.add(t);
            }
        }
    }

    public void writeTimes(OutputStream out, TimeFormat timeFormat) {
        PrintWriter pw = new PrintWriter(out);
        Collections.sort(times);

        for (Time time : times) {
            if (timeFormat == TimeFormat.FORMAT_AMPM) {
                pw.println(time.toAmPmFormat());
            } else {
                pw.println(time.toString());
            }
        }
        pw.flush();

    }
}

public class TimesTest {


    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}
