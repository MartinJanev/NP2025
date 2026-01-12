package Aud.aud8;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

// ------------------------------------------------------
// Domain classes
// ------------------------------------------------------

class RideRequest {
    private final String id;
    private final double x;
    private final double y;

    public RideRequest(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Rider{" + id + " @(" + x + "," + y + ")}";
    }
}

class DriverAvailableRequest {
    private final String id;
    private final double x;
    private final double y;

    public DriverAvailableRequest(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Driver{" + id + " @(" + x + "," + y + ")}";
    }
}

class Match {
    private final RideRequest rideRequest;
    private final DriverAvailableRequest driverAvailableRequest;
    private final double distance;
    private final Instant matchedAt = Instant.now();

    public Match(RideRequest r, DriverAvailableRequest d, double distance) {
        this.rideRequest = r;
        this.driverAvailableRequest = d;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Match: " + rideRequest.getId() + " -> " + driverAvailableRequest.getId() +
                " (dist=" + String.format("%.2f", distance) + ") at " + matchedAt;
    }
}

// ------------------------------------------------------
// Advanced ride-sharing service
// ------------------------------------------------------

class RideSharingRealisticService {
    private final BlockingQueue<RideRequest> rideRequestsQueue = new LinkedBlockingQueue<>();

    private final List<DriverAvailableRequest> driverAvailableRequests = new ArrayList<>();
    private final ReentrantLock driverLock = new ReentrantLock();

    private final BlockingQueue<Match> matches = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> rejections = new LinkedBlockingQueue<>();

    private final AtomicInteger matchCount = new AtomicInteger();
    private final AtomicInteger rejectionCount = new AtomicInteger();
    private final AtomicLong totalDistance = new AtomicLong();

    private final Random random = new Random();

    public void addRider(RideRequest r) {
        rideRequestsQueue.offer(r);
        System.out.println("[RIDER] " + r);
    }

    public void addDriver(DriverAvailableRequest d) {
        driverLock.lock();
        try {
            driverAvailableRequests.add(d);
            System.out.println("[DRIVER] " + d);
        } finally {
            driverLock.unlock();
        }
    }


    public int getRiderQueueSize() {
        return rideRequestsQueue.size();
    }

    public int getDriverCount() throws InterruptedException {
        if (driverLock.tryLock(3, TimeUnit.SECONDS)) {
            try {
                return driverAvailableRequests.size();
            } finally {
                driverLock.unlock();
            }
        }
        return -1;
    }

    public int getMatchCount() {
        return matchCount.get();
    }

    public int getRejectionCount() {
        return rejectionCount.get();
    }

    public double getAvgDistance() {
        int count = matchCount.get();
        return count == 0 ? 0.0 : (double) (totalDistance.get() * 10) / count;
    }

    public void startDispatcher(String dispatcherName) {
        Thread dispatcher = new Thread(() -> {
            System.out.println("[DISPATCHER " + dispatcherName + "] Started.");
            try {
                while (true) {
                    RideRequest request = rideRequestsQueue.poll(1, TimeUnit.SECONDS);
                    boolean acq = driverLock.tryLock(1, TimeUnit.SECONDS);
                    System.out.println(driverLock.isHeldByCurrentThread());
                    if (acq) {
                        Optional<DriverAvailableRequest> search = driverAvailableRequests.stream()
                                .min((left, right) ->
                                        Double.compare(distance(request, left), distance(request, right)));
                        if (search.isPresent()) {
                            DriverAvailableRequest driver = search.get();
                            Match match = new Match(request, driver, distance(request, driver));
                            driverAvailableRequests.remove(driver);

                            matches.offer(match);
                            matchCount.incrementAndGet();
                            totalDistance.addAndGet((long) distance(request, driver));

                            System.out.println("[MATCH] by dispatcher " + dispatcherName + "->" + match);
                        } else {
                            System.out.println(dispatcherName + "[REJECTION] No drivers available");
                            rejections.offer(dispatcherName + "[REJECTION] No drivers available");
                            rejectionCount.incrementAndGet();
                        }

                        driverLock.unlock();
                    } else {
                        System.out.println(dispatcherName + " [REJECTED] Cannot acquire lock after waiting some time");
                        rejections.offer(dispatcherName + "[REJECTED] Cannot acquire lock after waiting some time");
                        rejectionCount.incrementAndGet();
                    }
                }
            } catch (Exception e) {
                System.out.println("[DISPATCHER " + dispatcherName + "] Stopped");
            }
        }, "Dispatcher-" + dispatcherName);
        dispatcher.start();
    }

    private double distance(RideRequest r, DriverAvailableRequest d) {
        double dx = r.getX() - d.getX();
        double dy = r.getY() - d.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}

// ------------------------------------------------------
// Demo
// ------------------------------------------------------

public class RideSharingRealisticDemo {

    public static void main(String[] args) throws InterruptedException {
        RideSharingRealisticService service = new RideSharingRealisticService();
        Random random = new Random();

        // Start multiple dispatchers
        service.startDispatcher("Alice");
        service.startDispatcher("Bob");
        service.startDispatcher("Charlie");

        ExecutorService producers = Executors.newFixedThreadPool(4);

        Runnable riderProducer = () -> {
            int c = 0;
            try {
                while (true) {
                    String id = "R" + Thread.currentThread().getId() + "-" + (c++);
                    service.addRider(new RideRequest(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(random.nextInt(300));
                }
            } catch (InterruptedException ignored) {
            }
        };

        Runnable driverProducer = () -> {
            int c = 0;
            try {
                while (true) {
                    String id = "D" + Thread.currentThread().getId() + "-" + (c++);
                    service.addDriver(new DriverAvailableRequest(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(250 + random.nextInt(350));
                }
            } catch (InterruptedException ignored) {
            }
        };

        producers.submit(riderProducer);
        producers.submit(riderProducer);
        producers.submit(driverProducer);
        producers.submit(driverProducer);

        // Metrics monitor
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            try {
                System.out.println("\n===== METRICS =====");
                System.out.println("Waiting riders: " + service.getRiderQueueSize());
                System.out.println("Available drivers: " + service.getDriverCount());
                System.out.println("Total matches: " + service.getMatchCount());
                System.out.println("Rejections: " + service.getRejectionCount());
                System.out.println("Average distance: " + String.format("%.2f", service.getAvgDistance()));
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }, 2, 3, TimeUnit.SECONDS);

        Thread.sleep(60000);

        System.out.println("\n[MAIN] Stopping simulation...");
        producers.shutdownNow();
        monitor.shutdownNow();
        System.out.println("[MAIN] Done.");
    }
}
