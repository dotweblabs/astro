package com.dotweblabs.astro.methods;

import io.atomix.copycat.Command;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class Delete implements Command<Object> {
    public Object key;

    @Override
    public CompactionMode compaction() {
        return CompactionMode.TOMBSTONE;
    }

    public Delete(Object key) {
        this.key = key;
    }
}
