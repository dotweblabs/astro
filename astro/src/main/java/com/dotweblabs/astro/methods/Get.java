package com.dotweblabs.astro.methods;

import io.atomix.copycat.Query;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class Get implements Query<Object> {
    public Object key;

    @Override
    public ConsistencyLevel consistency() {
        return ConsistencyLevel.LINEARIZABLE_LEASE;
    }

    public Get(Object key) {
        this.key = key;
    }
}
