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
package org.atmosphere.wasync.loader;

import com.ning.http.client.AsyncHttpClient;
import org.atmosphere.wasync.Client;
import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.impl.AtmosphereClient;
import org.atmosphere.wasync.impl.AtmosphereRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility class that can be used to load a WebSocket enabled Server
 *
 * @author jeanfrancois Arcand
 */
public class WebSocketLoader {

    public static void main(String[] s) throws InterruptedException, IOException {

        if (s.length == 0) {
            s = new String[]{"50", "0", "http://127.0.0.1:8080/longpolling"};
        }

        final int clientNum = Integer.valueOf(s[0]);
        final int messageNum = Integer.valueOf(s[1]);
        String url = s[2];

        System.out.println("Number of Client: " + clientNum);
        System.out.println("Number of Message: " + messageNum);

        final AsyncHttpClient c = new AsyncHttpClient();

        final CountDownLatch l = new CountDownLatch(clientNum * 2);

        final CountDownLatch messages = new CountDownLatch(messageNum * clientNum);

        AtmosphereClient client = ClientFactory.getDefault().newClient(AtmosphereClient.class);

        long clientCount = l.getCount()/2;

        Socket[] sockets = new Socket[clientNum];
        final CountDownLatch messageLostAllowed = new CountDownLatch(1);
        for (int i = 0; i < clientCount; i++)
            sockets[i] = client.create(client.newOptionsBuilder().waitBeforeUnlocking(100).runtime(c).build())
                    .on(new Function<Integer>() {
                        @Override
                        public void on(Integer statusCode) {
                            l.countDown();
                        }
                    }).on(new Function<String>() {

                        int mCount = 0;
                        ArrayList<String> l = new ArrayList<String>();

                        @Override
                        public void on(String s) {
                            System.out.println(s + "<=>" + mCount);
                            l.add(s);
                            if (Integer.valueOf(s) != mCount) {
                                System.out.println("Messsage LOST: " + l);
                                messageLostAllowed.countDown();
                            }
                            mCount++;
                        }
                    }).on(new Function<Throwable>() {
                        @Override
                        public void on(Throwable t) {
                            t.printStackTrace();
                        }
                    });

        for (int i = 0; i < clientCount; i++) {
            AtmosphereRequest.AtmosphereRequestBuilder request = client.newRequestBuilder();
            request.method(Request.METHOD.GET);
            request.transport(Request.TRANSPORT.LONG_POLLING);
            request.trackMessageLength(true);
            sockets[i].open(request.uri(url + "/" + i).build());
        }

        l.await(5, TimeUnit.SECONDS);

        System.out.println("OK, all Connected: " + clientNum);
        messageLostAllowed.await(15, TimeUnit.MINUTES);
        System.exit(-1);
    }

}
