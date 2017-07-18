package com.dotweblabs.astro.resource.jee;

import com.dotweblabs.astro.Astro;
import me.alexpanov.net.FreePortFinder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
@WebListener
public class KeyValueStore implements ServletContextListener {

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
                logger.info("Running Astro");
                String port = DEFAULT_PORT;
                if(isPortUsed("127.0.0.1", Integer.valueOf(port).intValue())) {
                    int p = FreePortFinder.findFreeLocalPort();
                    port = String.valueOf(p);
                }
                String[] address = {DEFAULT_ADDR + ":" + port};
                server = new Astro(DEFAULT_PATH, address);
                server.start();
                if(server.isRunning()) {
                    logger.info("Astro running on " + port);
                } else {
                    logger.warning("Astro is not running");
                }
                while(server.isRunning()) {
                    try {
                        logger.info("Astro is running...");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        executor.submit(runnable);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        server.shutdown();
        executor.shutdown();
    }

//    @Override
//    protected Injector getInjector() {
//        return Guice.createInjector(new GuiceConfigModule(), new GuiceServletModule());
//    }

    private boolean isPortUsed(String hostName, int portNumber) {
        boolean result;
        try {
            Socket s = new Socket(hostName, portNumber);
            s.close();
            result = true;
        }
        catch(Exception e) {
            result = false;
        }
        return(result);
    }
}
