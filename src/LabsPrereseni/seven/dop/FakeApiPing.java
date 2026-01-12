package LabsPrereseni.seven.dop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class FakeApiPing {

    // Result holder
    public static class ApiResult {
        public final int requestId;
        public final boolean success;
        public final String value;

        public ApiResult(int requestId, boolean success, String value) {
            this.requestId = requestId;
            this.success = success;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ApiResult{" +
                    "requestId=" + requestId +
                    ", success=" + success +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Api {
        public static ApiResult get(int requestId, int parameter) throws InterruptedException {
            long delayMillis = parameter * 100L;
            Thread.sleep(delayMillis);

            String response = "VALUE_" + parameter;
            return new ApiResult(requestId, true, response);
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt(); // number of API calls

        List<Callable<ApiResult>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();

            // requestId is the loop index
            int requestId = i + 1;

            //TODO add a Callable that invokes the API get method in the tasks list
            tasks.add(() -> Api.get(requestId, parameter));
        }

        //TODO Additional - limit on parallelism
        int maxParallelCalls = Math.min(4, n);


        ExecutorService executor =
                Executors.newFixedThreadPool(maxParallelCalls);

        List<Future<ApiResult>> futures = new ArrayList<>();

        //TODO submit all callables to the executure and get the Futures
        for (Callable<ApiResult> task : tasks) {
            futures.add(executor.submit(task));
        }

        List<ApiResult> results = new ArrayList<>();

        long timeoutMillis = 200;

        boolean timeoutOccurred = false;

        //TODO get the ApiResult from all the futures and allow a max timeout of timeoutMillis
        // If a timeout occurs, cancel all remaining futures
        for (int i = 0; i < futures.size(); i++) {
            Future<ApiResult> future = futures.get(i);
            int requestId = i + 1;

            if (timeoutOccurred) {
                future.cancel(true);
                results.add(new ApiResult(requestId, false, "TIMEOUT"));
                continue;
            }
            try {
                results.add(future.get(timeoutMillis, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                timeoutOccurred = true;
                results.add(new ApiResult(requestId, false, "TIMEOUT"));
                futures.get(i).cancel(true);
            } catch (CancellationException e) {
                results.add(new ApiResult(requestId, false, "TIMEOUT"));
            } catch (Exception e) {
                results.add(new ApiResult(requestId, false, "ERROR"));
            }
        }

        executor.shutdown();

        // Sorting by requestId
        results.sort(Comparator.comparingInt(r -> r.requestId));

        // Output
        for (ApiResult r : results) {
            System.out.printf(
                    "%d %s %s%n",
                    r.requestId,
                    r.success ? "OK" : "FAILED",
                    r.value
            );
        }
    }
}

