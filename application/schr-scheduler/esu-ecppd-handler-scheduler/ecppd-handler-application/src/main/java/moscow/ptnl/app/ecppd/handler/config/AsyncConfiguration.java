package moscow.ptnl.app.ecppd.handler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author m.kachalov
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "threadPoolExecutor", destroyMethod = "shutdownNow")
    @Primary
    public ExecutorService threadPoolExecutor() {
        return new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
                (runnable, executor) -> {
                    try {
                        // this needs to be put(...) and not add(...)
                        executor.getQueue().put(runnable);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }
}
