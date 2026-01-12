package Lab.Seven;

/*
Даден ви е Java програм кој извршува повеќе операции за уплата и подигнување средства од заедничка банкарска сметка. Операциите се извршуваат конкурентно како посебни задачи.

Ваша задача е да обезбедите коректно ракување со заедничката состојба на сметката кога повеќе операции се извршуваат истовремено. Програмата мора да ја зачува исправноста на состојбата без разлика на редоследот или времето на извршување на операциите.

Секоја операција за уплата мора секогаш коректно да ја ажурира состојбата. Секоја операција за подигнување средства смее да биде успешна само доколку во моментот на извршување има доволно средства на сметката. Состојбата на сметката никогаш не смее да стане некоректна како резултат на конкурентно извршување.

По завршувањето на сите операции, програмата мора да ја испечати конечната состојба на сметката. Испечатената вредност мора да биде детерминистичка и да го одразува точниот резултат од сите успешно извршени операции.

Не смеете да го менувате форматот на влезот, логиката за креирање на задачите или рамката за нивно извршување. Решението мора коректно да функционира при конкурентно извршување и не смее да се потпира на специфичен редослед на извршување.

Подари вештачки одложувања во извршувањето на задачите, тестирањето на решението на CodeRunner ќе ви трае подолго.

ДОПОЛНИТЕЛНО: Проширете ја програмата така што, покрај печатењето на конечната состојба на сметката, ќе печати и детерминистички лог на сите операции, при што ќе биде наведено дали секоја операција била успешна или неуспешна.

Секоја операција мора да биде евидентирана точно еднаш, а излезот мора да биде подреден според operationId, без разлика на редоследот по кој задачите реално се извршуваат.

Ограничувања:

Решението не смее да зависи од редоследот на извршување или на распоредувањето на нишките.

Излезот мора секогаш да биде детерминистички за ист влез.

Сите споделени податочни структури на податоци што се користат за логирање мора да бидат thread-safe (безбедни за работа со нишки).
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class BankBalance {

    // Shared bank account
    public static class BankAccount {
        private int balance;
        private final Lock lock = new ReentrantLock();

        public BankAccount(int initialBalance) {
            this.balance = initialBalance;
        }

        public boolean deposit(int amount, long timeoutMs) throws InterruptedException {
            if (lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    balance += amount;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false; // failed to acquire lock in time
        }

        public boolean withdraw(int amount, long timeoutMs) throws InterruptedException {
            if (lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        return true;
                    }
                    return false;
                } finally {
                    lock.unlock();
                }
            }
            return false; // failed to acquire lock in time
        }

        public int getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }
    }

    // Operation result
    public static class OperationResult {
        public final int operationId;
        public final boolean success;

        public OperationResult(int operationId, boolean success) {
            this.operationId = operationId;
            this.success = success;
        }
    }

    // [ADDED] Thread-safe structure for deterministic logging
//    static ConcurrentMap<Integer, Boolean> operationLog = new ConcurrentHashMap<>();


    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int initialBalance = sc.nextInt();
        int n = sc.nextInt(); // number of operations

        BankAccount account = new BankAccount(initialBalance);

        List<Callable<OperationResult>> tasks = new ArrayList<>();

        long lockTimeoutMs = 100; // max time to wait for the lock

        for (int i = 0; i < n; i++) {
            String type = sc.next();
            int amount = sc.nextInt();
            int operationId = i + 1;

            tasks.add(() -> {
                Thread.sleep(3000);
                boolean success;

                if (type.equals("deposit")) {
                    success = account.deposit(amount, lockTimeoutMs);
                } else { // withdraw
                    success = account.withdraw(amount, lockTimeoutMs);
                }

//                operationLog.put(operationId, success);

                return new OperationResult(operationId, success);
            });
        }


        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        List<Future<OperationResult>> futures = executor.invokeAll(tasks);

        List<OperationResult> results = new ArrayList<>();
        for (Future<OperationResult> f : futures) {
            results.add(f.get());
        }

        executor.shutdown();

        // Deterministic final balance
        System.out.println("FINAL_BALANCE " + account.getBalance());

        // [ADDED] Deterministic operation log ordered by operationId
//        operationLog.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .forEach(e ->
//                        System.out.println(
//                                "OPERATION " + e.getKey() + " " +
//                                        (e.getValue() ? "SUCCESS" : "FAILED")
//                        )
//                );
    }
}