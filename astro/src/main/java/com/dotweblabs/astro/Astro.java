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
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class Astro {

    public static Logger logger = Logger.getLogger(KeyValueStoreStateMachine.class.getName());

    CopycatServer server = null;
    List<Address> members = null;
    public Astro(String path, String[] address) {
        members = new ArrayList<>();
        for (String arg : address) {
            String[] parts = arg.split(":");
            members.add(new Address(parts[0], Integer.valueOf(parts[1])));
        }
        Address addr = new Address(members.get(0));
        server = CopycatServer.builder(addr)
                .withStateMachine(AstroKeyValueStore::new)
                .withTransport(new NettyTransport())
                .withStorage(Storage.builder()
                        .withDirectory(path)
                        .withMaxSegmentSize(1024 * 1024 * 32)
                        .withMinorCompactionInterval(Duration.ofMinutes(1))
                        .withMajorCompactionInterval(Duration.ofMinutes(15))
                        .build())
                .build();

        server.serializer().register(Put.class, 1);
        server.serializer().register(Get.class, 2);
        server.serializer().register(Delete.class, 3);

        server.cluster().onJoin(member -> {
            logger.info(member.address() + " joined the cluster");
        });

        server.cluster().onLeave(member -> {
            logger.info(member.address() + " left the cluster");
        });
    }

    public void shutdown() {
        server.shutdown();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public void start() {
        if(server.isRunning()) {
            logger.info("Server running already");
        } else {
            logger.info("Server is not running!");
            server.bootstrap(members).join();
            server.cluster().join(members).join();
        }
    }

    public void connect() {
        if(server.isRunning()) {
            logger.info("Server running already");
        } else {
            logger.info("Server is not running...");
            Collection<Address> cluster = Collections.singleton(members.get(0));
            server.cluster().join(cluster).join();
        }
    }

}
