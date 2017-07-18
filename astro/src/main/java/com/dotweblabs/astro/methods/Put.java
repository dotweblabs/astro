package com.dotweblabs.astro.methods;

import io.atomix.copycat.Command;

public class Put implements Command<Object> {
    public Object key;
    public Object value;

    @Override
    public CompactionMode compaction() {
        return CompactionMode.QUORUM;
    }

    public Put(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
}