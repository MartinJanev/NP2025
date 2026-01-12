package LabsPrereseni.seven.dop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankBalance {

    // Shared bank account
    public static class BankAccount {
        private int balance;

        private final Lock lock = new ReentrantLock();


        public BankAccount(int initialBalance) {
            this.balance = initialBalance;
        }

        public boolean deposit(int amount, long lockTimeoutMs) throws InterruptedException {
            if (lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    balance += amount;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        public boolean withdraw(int amount, long lockTimeoutMs) throws InterruptedException {
            if (lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS)) {
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
            return false;
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

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int initialBalance = sc.nextInt();
        int n = sc.nextInt(); // number of operations

        BankAccount account = new BankAccount(initialBalance);
        List<Callable<OperationResult>> tasks = new ArrayList<>();

        ConcurrentMap<Integer, Boolean> operationLog = new ConcurrentHashMap<>();

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

                operationLog.put(operationId, success);
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


        // Deterministic final balance
        System.out.println("FINAL_BALANCE " + account.getBalance());

        operationLog.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e ->
                        System.out.println(
                                "OPERATION " + e.getKey() + " " +
                                        (e.getValue() ? "SUCCESS" : "FAILURE")
                        ));
        executor.shutdown();
    }

}
