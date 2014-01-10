/*
 * Copyright 2012 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.loadtest.service;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.atmosphere.util.SimpleBroadcaster;

import java.io.IOException;

@AtmosphereHandlerService(path = "/echohttp/{id}", broadcaster= SimpleBroadcaster.class, broadcasterCache = UUIDBroadcasterCache.class)
public class HttpEcho extends AbstractReflectorAtmosphereHandler{
    private static volatile int count=0;

    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {
        if (resource.getRequest().getMethod().equalsIgnoreCase("GET")) {
            resource.suspend();
        } else {
            resource.getBroadcaster().broadcast("message" + ++count + "-");
        }
    }
}
