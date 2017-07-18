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
package com.dotweblabs.astro.resource.jee;

import com.google.common.collect.Sets;
import com.dotweblabs.astro.guice.SelfInjectingServerResource;
import org.restlet.data.Header;
import org.restlet.data.Method;
import org.restlet.util.Series;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Handle common server resource tasks
 *
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class BaseServerResource extends SelfInjectingServerResource {

	private static final Logger LOG
			= Logger.getLogger(BaseServerResource.class.getName());

	protected Map<String,Object> queryMap = new LinkedHashMap<>();
	protected Map<String,String> propsMap = new LinkedHashMap<>();
	protected String key;

	@Override
	protected void doInit() {
		super.doInit();
		getHeaders();
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
			responseHeaders = new Series(Header.class);
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add(new Header("X-Powered-By", "Restlet"));
		setAllowedMethods(Sets.newHashSet(Method.GET,
				Method.PUT,
				Method.POST,
				Method.DELETE,
				Method.OPTIONS));
		propsMap = appProperties();
		key = getAttribute("key");
	}

	protected void getHeaders() {
		Series<Header> headers = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");
	}

	protected Map<String,String> appProperties(){
		Map<String,String> map = new LinkedHashMap<String, String>();
		InputStream is =  getContext().getClass().getResourceAsStream("/app.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			map = new LinkedHashMap<String, String>((Map) props);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	protected byte[] toByteArray(Object object) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(object);
			return bos.toByteArray();
		}
	}

	protected Object fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			 ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		}
	}

}
