package Aud.aud10;


import java.util.*;


interface Subscriber {
    void update(float temperature, float humidity, float pressure);
}

interface Publisher {
    void register(Subscriber subscriber);

    void remove(Subscriber subscriber);

    void notifySubscribers(float temperature, float humidity, float pressure);
}

class WeatherDispatcher implements Publisher {

    Set<Subscriber> subscribers;

    public WeatherDispatcher() {
        subscribers = new HashSet<>();
    }

    @Override
    public void register(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void remove(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(float temperature, float humidity, float pressure) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(temperature, humidity, pressure);
        }
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        notifySubscribers(temperature, humidity, pressure);
        System.out.println();
    }
}

class CurrentConditionsDisplay implements Subscriber {

    public CurrentConditionsDisplay(Publisher publisher) {
        publisher.register(this);
    }


    @Override
    public void update(float temperature, float humidity, float pressure) {
        System.out.printf(
                "Temperature: %.1fF%nHumidity: %.1f%%%n", temperature, humidity
        );
    }
}

class ForecastDisplay implements Subscriber {

    float prevPressure;

    public ForecastDisplay(Publisher publisher) {
        publisher.register(this);
        prevPressure = 0;
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        if (pressure > prevPressure) {
            System.out.println("Forecast: Improving");
        } else if (pressure == prevPressure) {
            System.out.println("Forecast: Same");
        } else {
            System.out.println("Forecast: Cooler");
        }

        prevPressure = pressure;
    }

}


public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if (parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if (operation == 1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if (operation == 2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if (operation == 3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if (operation == 4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}