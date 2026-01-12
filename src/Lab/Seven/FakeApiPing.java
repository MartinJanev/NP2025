/*
Даден ви е стартен Java програм FakeApiPing кој симулира конкурентни повици кон лажен (fake) API. API-то симулира работа така што чека одредено време пред да врати резултат, при што времето на чекање зависи од влезниот параметар. Некои API повици може да траат подолго и да не завршат во рамките на дозволеното време.

Ваша задача е да ги пополните делови кои не се имплементирани во програмата така што сите API повици ќе се извршуваат конкурентно и нивните резултати ќе бидат коректно собрани. Секој API повик мора да се извршува во паралела и да биде поврзан со уникатен идентификатор кој се изведува од редоследот на читање на влезните параметри.

Откако сите API повици ќе бидат submit-нати за извршување, програмата треба да ги собере нивните резултати со почитување на максимално дозволеното време на чекање за секој повик. Доколку API повикот заврши во рамките на дозволеното време, резултатот треба да се евидентира како успешен. Доколку повикот не заврши навреме, треба да се смета за неуспешен и соодветно да се евидентира.

Крајниот излез мора да содржи по еден резултат за секој API повик, со информација дали повикот бил успешен или неуспешен поради надминување на дозволеното време.

Не смеете да го менувате форматот на влезот, дадената симулација на API-то или форматот на излезот. Решението мора коректно да се справи со конкурентно извршување и временските ограничувања
 */

package Lab.Seven;


import java.util.*;
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

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<ApiResult>> futures = new ArrayList<>();
        //TODO submit all callables to the executure and get the Futures
        for (Callable<ApiResult> task : tasks) {
            futures.add(executor.submit(task));
        }

        List<ApiResult> results = new ArrayList<>();

        long timeoutMillis = 200;

        //TODO get the ApiResult from all the futures and allow a max timeout of timeoutMillis
        for (int i = 0; i < futures.size(); i++) {
            Future<ApiResult> future = futures.get(i);
            int requestId = i + 1;
            try {
                ApiResult result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                results.add(result);
            } catch (TimeoutException e) {
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

