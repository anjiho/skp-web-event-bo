package kr.co.syrup.adreport.web.event.session;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedThreadLocal<T> extends ThreadLocal<T> {
    private final long expirationTimeMillis;

    public TimedThreadLocal(long expirationTimeMillis) {
        this.expirationTimeMillis = expirationTimeMillis;

        // Schedule a task to remove the value after the specified time
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::remove, expirationTimeMillis, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    @Override
    public void set(T value) {
        super.set(value);
    }
}
