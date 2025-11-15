package Kolokviumski.fourth10;

import java.util.*;

interface Updatable {
    void update(float temp, float hum, float press);

}

interface Subject {
    void register(Updatable o);

    void remove(Updatable o);

    void notifyUpdate();
}

interface Displayable {
    void display();
}

class CurrentConditionsDisplay implements Updatable, Displayable {
    private float temperature;
    private float humidity;
    private Subject weatherStation;

    public CurrentConditionsDisplay(Subject weatherStation) {
        this.weatherStation = weatherStation;
        weatherStation.register(this);
    }

    @Override
    public void display() {
        System.out.printf("Temperature: %.1fF\nHumidity: %.1f%%\n", temperature, humidity);
    }

    @Override
    public void update(float temp, float hum, float press) {
        this.temperature = temp;
        this.humidity = hum;
        display();
    }
}

class ForecastDisplay implements Updatable, Displayable {
    private float currPressure = 0.0f;
    private float lastPressure;
    private WeatherDispatcher weatherDispatcher;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        this.weatherDispatcher = weatherDispatcher;
        weatherDispatcher.register(this);
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currPressure > lastPressure) {
            System.out.println("Improving");
        } else if (currPressure == lastPressure) {
            System.out.println("Same");
        } else if (currPressure < lastPressure) {
            System.out.println("Cooler");
        }
        System.out.println();
    }

    @Override
    public void update(float temp, float hum, float press) {
        lastPressure = currPressure;
        currPressure = press;
        display();
    }
}

class WeatherDispatcher implements Subject {
    float temperature, humidity, pressure;

    Set<Updatable> updatables;

    public WeatherDispatcher() {
        updatables = new HashSet<>();
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    void setMeasurements(float temperature, float humidity, float pressure) {
        setTemperature(temperature);
        setHumidity(humidity);
        setPressure(pressure);
        notifyUpdate();
    }


    public void register(Updatable display) {
        updatables.add(display);
    }

    public void remove(Updatable display) {
        updatables.remove(display);
    }

    @Override
    public void notifyUpdate() {
        for (Updatable updatable : updatables) {
            updatable.update(temperature, humidity, pressure);
        }
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