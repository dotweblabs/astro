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

import com.dotweblabs.astro.guice.SelfInjectingServerResourceModule;
import com.dotweblabs.astro.resource.jee.KeyValueServerResource;
import com.dotweblabs.astro.resource.jee.PingServerResource;
import com.dotweblabs.astro.resource.jee.RootServerResource;
import com.dotweblabs.astro.guice.GuiceConfigModule;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.engine.Engine;
import org.restlet.engine.application.CorsFilter;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.swagger.Swagger2SpecificationRestlet;
import org.restlet.routing.Router;

import java.util.*;
import java.util.logging.Logger;

/**
 * Main REST Application
 *
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class AstroApplication extends Application {

	private static final Logger LOG
          = Logger.getLogger(AstroApplication.class.getName());
	private static final String ROOT_URI = "/";
  	/**
   	* Creates a root Restlet that will receive all incoming calls.
   	*/
	@Override
	public Restlet createInboundRoot() {
	  	Guice.createInjector(new GuiceConfigModule(this.getContext()),
            new SelfInjectingServerResourceModule());
	  	LOG.info("Starting application");

	  	configureConverters();

	  	Router router = new Router(getContext());

	  	CorsFilter corsFilter = new CorsFilter(getContext());
	  	corsFilter.setAllowedOrigins(new HashSet(Arrays.asList("*")));
	  	corsFilter.setAllowedHeaders(Sets.newHashSet(
            "X-Requested-With",
            "Content-Type"));
	  	corsFilter.setAllowedCredentials(true);
		router.attach(ROOT_URI + "ping", PingServerResource.class);
	  	router.attach(ROOT_URI + "/values/{key}", KeyValueServerResource.class);
	  	router.attachDefault(RootServerResource.class);

		// Configuring Swagger 2 support
		Swagger2SpecificationRestlet swagger2SpecificationRestlet
				= new Swagger2SpecificationRestlet(this);
		swagger2SpecificationRestlet.setBasePath("/api-docs");
		swagger2SpecificationRestlet.attach(router);

	  	//router.attachDefault(GaeRootServerResource.class);
	  	corsFilter.setNext(router);

	  	return corsFilter;
	}

	private void configureConverters() {
		List<ConverterHelper> converters = Engine.getInstance()
				.getRegisteredConverters();
		JacksonConverter jacksonConverter = null;
		for (ConverterHelper converterHelper : converters) {
			System.err.println(converterHelper.getClass());
			if (converterHelper instanceof JacksonConverter) {
				jacksonConverter = (JacksonConverter) converterHelper;
				break;
			}
		}
		if (jacksonConverter != null) {
			Engine.getInstance()
					.getRegisteredConverters().remove(jacksonConverter);
		}
	}

}
