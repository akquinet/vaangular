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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.vaadin.ui.JavaScriptFunction;

public class NgTemplatePlusTest {

	@Test
	public void testConstructorStrings() {
		NgTemplatePlus template = new NgTemplatePlus("<h1>moin</h1>",
				"NgTemplateTest") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		assertEquals("<h1>moin</h1>", template.getState().templateSource);
		assertEquals("NgTemplateTest", template.getState().moduleName);
	}

	@Test
	public void testConstructorPackage() throws UnsupportedEncodingException,
			URISyntaxException, IOException {
		NgTemplatePlus template = new NgTemplatePlus(
				NgTemplatePlusTest.class.getPackage(), "NgTemplateTest") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		assertEquals("<h1>heyho</h1>", template.getState().templateSource);
		assertEquals("NgTemplateTest", template.getState().moduleName);
	}

	@Test
	public void testAddService() {
		final Set<String> addedServices = new HashSet<>();
		NgTemplatePlus template = new NgTemplatePlus("--", "NgTemplateTest") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void addFunction(String functionName,
					JavaScriptFunction function) {
				addedServices.add(functionName);
			}
		};
		template.addService("testService", new Object() {

			@ServiceMethod
			public void test1() {
			}

			@ServiceMethod
			public void test2() {
			}

			@SuppressWarnings("unused")
			public void test3() {
			}

			@SuppressWarnings("unused")
			private void test4() {
			}
		});
		assertEquals(2, addedServices.size());
		assertTrue(addedServices.contains("testService_test1"));
		assertTrue(addedServices.contains("testService_test2"));
		assertFalse(addedServices.contains("testService_test3"));
		assertFalse(addedServices.contains("testService_test4"));
	}

	@Test
	public void testSetUserState() {
		NgTemplatePlus template = new NgTemplatePlus("--", "NgTemplateTest") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		template.setUserState("test", "bla");
		assertNotNull(template.getState().userState);
		assertEquals("{\"test\":\"bla\"}",
				template.getState().userState.replaceAll("[ \n\r\t]", ""));
	}

}
