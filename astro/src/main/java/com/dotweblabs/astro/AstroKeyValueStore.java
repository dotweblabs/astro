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