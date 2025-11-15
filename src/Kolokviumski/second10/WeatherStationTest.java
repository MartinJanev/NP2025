package Kolokviumski.second10;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.stream.Collectors;

class Measurement implements Comparable<Measurement> {
    private float temperature;
    private float windSpeed;
    private float humidity;
    private float visibility;
    private Date date;

    public Measurement(float temperature, float windSpeed, float humidity, float visibility, Date date) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getVisibility() {
        return visibility;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Measurement o) {
        return this.date.compareTo(o.date);
    }

    private static String toGMT(Date date) {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    @Override
    public String toString() {
        //22.1 18.9 km/h 1.3% 24.6 km Tue Dec 17 23:30:15 GMT 2013↩
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",
                temperature, windSpeed, humidity, visibility, toGMT(date));
    }
}

class WeatherStation {
    private int days;
    private List<Measurement> measurements;

    public WeatherStation(int days) {
        this.days = days;
        this.measurements = new ArrayList<>();
    }


    public void addMeasurment(float temp, float wind, float hum, float vis, Date date) {
        long time = date.getTime();
        boolean close = measurements.stream()
                .anyMatch(m -> Math.abs(m.getDate().getTime() - time) < 150_000L);
        if (close) return;

        long windowStart = time - days * 24 * 60 * 60 * 1000L;
        measurements.removeIf(m -> m.getDate().getTime() < windowStart);

        measurements.add(new Measurement(temp, wind, hum, vis, date));

    }

    public int total() {
        return measurements.size();
    }

    public void status(Date from, Date to) {
        List<Measurement> inRange = measurements.stream()
                .filter(m -> !m.getDate().before(from) && !m.getDate().after(to))
                .sorted()
                .collect(Collectors.toList());

        if (inRange.isEmpty()) {
            throw new RuntimeException();
        }

        double sumT = 0;
        for (Measurement m : inRange) {
            sumT += m.getTemperature();
            System.out.println(m);
        }
        System.out.printf("Average temperature: %.2f%n", sumT / inRange.size());

    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

// vashiot kod ovde
