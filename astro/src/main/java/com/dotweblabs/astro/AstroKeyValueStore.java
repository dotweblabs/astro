package com.dotweblabs.astro;

import com.dotweblabs.astro.methods.Delete;
import com.dotweblabs.astro.methods.Get;
import com.dotweblabs.astro.methods.Put;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class AstroKeyValueStore extends StateMachine {
    private Map<Object, Commit> storage = new HashMap<>();

    public Object put(Commit<Put> commit) {
        Commit<Put> put = storage.put(commit.operation().key, commit);
        return put == null ? null : put.operation().value;
    }

    public Object get(Commit<Get> commit) {
        try {
            Commit<Put> put = storage.get(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            commit.release();
        }
    }

    public Object delete(Commit<Delete> commit) {
        Commit<Put> put = null;
        try {
            put = storage.remove(commit.operation().key);
            return put == null ? null : put.operation().value;
        } finally {
            if (put != null)
                put.release();
            commit.release();
        }
    }
}