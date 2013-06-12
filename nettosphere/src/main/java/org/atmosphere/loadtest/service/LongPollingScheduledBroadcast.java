package org.atmosphere.loadtest.service;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@AtmosphereHandlerService(path = "/longpolling/{id}",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = TrackMessageSizeInterceptor.class)
public class LongPollingScheduledBroadcast extends AbstractReflectorAtmosphereHandler {

    private static final Logger logger = LoggerFactory.getLogger(LongPollingScheduledBroadcast.class);

    final static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(new Runnable() {

            private int message = 0;

            @Override
            public void run() {
                MetaBroadcaster.getDefault().broadcastTo("/longpolling/*", message++);
            }
        }, 20 * 1000, 100, TimeUnit.MILLISECONDS);
    }


    @Override
    public void onRequest(final AtmosphereResource resource) throws IOException {
        if (resource.getRequest().getMethod().equalsIgnoreCase("GET")) {
            resource.suspend();
        }
    }
}
