/*
 * Copyright 2015 akquinet engineering GmbH
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.akquinet.engineering.vaadin.vaangular.angular;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

import elemental.json.JsonArray;
import elemental.json.JsonString;
import elemental.json.impl.JreJsonFactory;

public class MethodInvokerTest {

	@Test
	public void testCall() {
		JreJsonFactory jsonFactory = new JreJsonFactory();
		JsonArray params = jsonFactory.createArray();
		params.set(0, true);
		params.set(1, "hey");
		params.set(2, 88.0);
		JsonArray innerArray = jsonFactory.createArray();
		innerArray.set(0, "inner");
		params.set(3, innerArray);
		TestTarget target = new TestTarget();
		Method testMethod = Arrays
				.stream(TestTarget.class.getDeclaredMethods())
				.filter(m -> "testMethod".equals(m.getName())).findFirst()
				.get();
		MethodInvoker invoker = new MethodInvoker(target, testMethod);
		invoker.call(params);
		assertEquals(Boolean.TRUE, target.getArg1());
		assertEquals("hey", target.getArg2());
		assertEquals(new Double(88.0), target.getArg3());
		assertEquals(1, target.getArg4().length());
		assertEquals("inner",
				((JsonString) target.getArg4().get(0)).getString());
	}

	@Test(expected = Exception.class)
	public void testCallInvalidArgs() {
		JreJsonFactory jsonFactory = new JreJsonFactory();
		JsonArray params = jsonFactory.createArray();
		params.set(0, true);
		params.set(1, "hey");
		TestTarget target = new TestTarget();
		Method testMethod = Arrays
				.stream(TestTarget.class.getDeclaredMethods())
				.filter(m -> "testMethod".equals(m.getName())).findFirst()
				.get();
		MethodInvoker invoker = new MethodInvoker(target, testMethod);
		invoker.call(params);
	}

	@Test(expected = RuntimeException.class)
	public void testCallException() {
		JreJsonFactory jsonFactory = new JreJsonFactory();
		JsonArray params = jsonFactory.createArray();
		TestTarget target = new TestTarget();
		Method testMethod = Arrays
				.stream(TestTarget.class.getDeclaredMethods())
				.filter(m -> "testMethodExc".equals(m.getName())).findFirst()
				.get();
		MethodInvoker invoker = new MethodInvoker(target, testMethod);
		invoker.call(params);
	}

	private static final class TestTarget {

		private Boolean arg1 = null;
		private String arg2 = null;
		private Double arg3 = null;
		private JsonArray arg4 = null;

		@SuppressWarnings("unused")
		private void testMethod(Boolean arg1, String arg2, Double arg3,
				JsonArray arg4) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
			this.arg4 = arg4;
		}

		@SuppressWarnings("unused")
		private void testMethodExc() throws Exception {
			throw new Exception("Test-Exception");
		}

		public Boolean getArg1() {
			return arg1;
		}

		public String getArg2() {
			return arg2;
		}

		public Double getArg3() {
			return arg3;
		}

		public JsonArray getArg4() {
			return arg4;
		}
	}

}
