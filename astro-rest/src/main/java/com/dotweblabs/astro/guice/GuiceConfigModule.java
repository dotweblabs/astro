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
package com.dotweblabs.astro.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.restlet.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class GuiceConfigModule extends AbstractModule {

    private static final Logger LOG = Logger.getLogger(GuiceConfigModule.class.getName());
    private Context context;

    public GuiceConfigModule(){}

    public GuiceConfigModule(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void configure() {
        // Suppress Guice warning when on GAE
        // see https://code.google.com/p/google-guice/issues/detail?id=488
        Logger.getLogger("com.google.inject.internal.util").setLevel(Level.WARNING);
        bind(String.class).annotatedWith(Names.named("app")).toInstance("Astro");
        Names.bindProperties(binder(), readProperties());
    }

    protected Properties readProperties(){
        InputStream is = this.getClass().getResourceAsStream("/app.properties");
        Properties props = new Properties();
        try {
            props.load(is);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
