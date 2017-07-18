package com.dotweblabs.astro.resource.jee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.dotweblabs.astro.guice.GuiceConfigModule;
import com.dotweblabs.astro.guice.GuiceServletModule;
import com.dotweblabs.astro.Astro;

import javax.servlet.ServletContextEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class KeyValueStore extends GuiceServletContextListener {

    public static Logger logger = Logger.getLogger(KeyValueStore.class.getName());

    private static final String DEFAULT_ADDR = "127.0.0.1";
    private static final String DEFAULT_PORT = "5000";
    private static final String DEFAULT_PATH = "\\data";

    private ExecutorService executor;
    private Astro server;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        executor = Executors.newSingleThreadExecutor();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] address = {DEFAULT_ADDR + ":" + DEFAULT_PORT};
                server = new Astro(DEFAULT_PATH, address);
                server.start();
            }
        };
        executor.submit(runnable);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        server.shutdown();
        executor.shutdown();
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new GuiceConfigModule(), new GuiceServletModule());
    }
}
