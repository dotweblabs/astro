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

import org.restlet.resource.ServerResource;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for ServerResources that do their own member injection. Not
 * available in maven.
 * 
 * @see http
 *      ://tembrel.blogspot.co.uk/2012/03/restlet-guice-extension-considered.
 *      html
 * @see https 
 *      ://github.com/restlet/restlet-framework-java/blob/master/incubator/org
 *      .restlet
 *      .ext.guice/src/org/restlet/ext/guice/SelfInjectingServerResource.java
 * @author Tembrel
 * 
 */
public abstract class SelfInjectingServerResource extends ServerResource {

	/**
	 * Implemented by DI framework-specific code. For example, with Guice, the
	 * statically-injected MembersInjector just calls
	 * {@code injector.injectMembers(object)}.
	 */
	public interface MembersInjector {
		void injectMembers(Object object);
	}

	/**
	 * Subclasseses overriding this method must call {@code super.doInit()}
	 * first.
	 */
	protected void doInit() {
		ensureInjected(theMembersInjector);
	}

	@Inject
	private void injected() { // NOPMD
		injected.set(true);
	}

	void ensureInjected(MembersInjector membersInjector) {
		if (injected.compareAndSet(false, true)) {
			membersInjector.injectMembers(this);
		}
	}

	/**
	 * Whether we've been injected yet. This protects against multiple injection
	 * of a subclass that gets injected before doInit is called.
	 */
	private final AtomicBoolean injected = new AtomicBoolean(false);

	/**
	 * Must be statically injected by DI framework.
	 */
	@Inject
	private static volatile MembersInjector theMembersInjector;
}
