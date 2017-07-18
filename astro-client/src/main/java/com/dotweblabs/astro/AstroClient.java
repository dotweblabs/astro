package com.dotweblabs.astro;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.ConnectionStrategies;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.client.ServerSelectionStrategies;
import io.atomix.copycat.examples.Delete;
import io.atomix.copycat.examples.Get;
import io.atomix.copycat.examples.Put;

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
