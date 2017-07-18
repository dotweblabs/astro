/**
 * Copyright 2017 Dotweblabs Web Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.dotweblabs.astro;

import com.dotweblabs.astro.methods.Delete;
import com.dotweblabs.astro.methods.Get;
import com.dotweblabs.astro.methods.Put;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.ConnectionStrategies;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.client.ServerSelectionStrategies;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class AstroClient {

    CopycatClient client;

    public AstroClient(String[] address) {
        List<Address> members = new ArrayList<>();
        for (String arg : address) {
            String[] parts = arg.split(":");
            members.add(new Address(parts[0], Integer.valueOf(parts[1])));
        }
        client = CopycatClient.builder()
                .withTransport(new NettyTransport())
                .withConnectionStrategy(ConnectionStrategies.FIBONACCI_BACKOFF)
                .withRecoveryStrategy(RecoveryStrategies.RECOVER)
                .withServerSelectionStrategy(ServerSelectionStrategies.LEADER)
                .withSessionTimeout(Duration.ofSeconds(15))
                .build();
        client.serializer().register(Put.class, 1);
        client.serializer().register(Get.class, 2);
        client.serializer().register(Delete.class, 3);

        client.connect(members).join();
    }
    public Object put(Object key, Object value) {
        try {
            if(client.state() == CopycatClient.State.CLOSED) {
                throw new RuntimeException("Client not connected to server");
            } else {
                CompletableFuture future = client.submit(new Put(key, value));
                //future.thenAccept(r -> System.out.println(r));
               // return future.get();
            }
            return client.submit(new Get(key)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object get(Object key) {
        Object value = null;
        try {
            if(client.state() == CopycatClient.State.CLOSED) {
                throw new RuntimeException("Client not connected to server");
            } else {
                value = client.submit(new Get(key)).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public void delete(Object key) {
        try {
            if(client.state() == CopycatClient.State.CLOSED) {
                throw new RuntimeException("Client not connected to server");
            } else {
                //client.submit(new Delete(key)).thenRun(() -> System.out.println(key + " has been delete"));
                CompletableFuture future = client.submit(new Delete(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
