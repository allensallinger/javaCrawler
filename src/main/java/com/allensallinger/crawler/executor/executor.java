package com.allensallinger.crawler.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class executor {
    static ExecutorService executor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(15));

    public static void execute(Runnable r) {

        try {
            executor.execute(r);
            executor.awaitTermination(4000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        executor.shutdown();
    }

}
