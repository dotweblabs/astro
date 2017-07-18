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
import com.google.inject.Injector;
import com.google.inject.Provides;

/*
*
* Copyright (c) 2016 Dotweblabs Web Technologies. All Rights Reserved.
* Licensed under Dotweblabs Commercial License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.dotweblabs.com/licenses/LICENSE-1.0
*
* Unless required by applicable law or agreed to in writing, software distributed
* under the License is distributed as Proprietary and Confidential to
* Dotweblabs Web Technologies and must not be redistributed in any form.
*
*/

/**
 * Install this module to arrange for SelfInjectingServerResource instances to
 * have their members injected (idempotently) by the doInit method (which is
 * called automatically after construction).
 * 
 * Incubator code. Not available in maven
 * 
 * @see https
 *      ://github.com/restlet/restlet-framework-java/blob/master/incubator/org
 *      .restlet
 *      .ext.guice/src/org/restlet/ext/guice/SelfInjectingServerResourceModule
 *      .java
 * @author Tembrel
 */
public class SelfInjectingServerResourceModule extends AbstractModule {

	@Override
	protected final void configure() {
		requestStaticInjection(SelfInjectingServerResource.class);
	}

	@Provides
	SelfInjectingServerResource.MembersInjector membersInjector(final Injector injector) {
		return new SelfInjectingServerResource.MembersInjector() {
			public void injectMembers(Object object) {
				injector.injectMembers(object);
			}
		};
	}
}
