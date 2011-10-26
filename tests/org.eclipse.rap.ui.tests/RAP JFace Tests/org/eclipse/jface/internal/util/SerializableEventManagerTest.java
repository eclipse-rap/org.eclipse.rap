/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

public class SerializableEventManagerTest extends TestCase {

	private static class TestEventManager extends SerializableEventManager {
		private static final long serialVersionUID = 1L;

		void addListener(Object listener) {
			addListenerObject(listener);
		}

		Object getListener(int index) {
			return getListeners()[index];
		}
	}

	public void testSerializeWithSerializableListeners() throws Exception {
		String listener = "listener";
		TestEventManager eventManager = new TestEventManager();
		eventManager.addListener(listener);

		TestEventManager deserializedEventManager = (TestEventManager) serializeAndDeserialize(eventManager);

		assertEquals( listener, deserializedEventManager.getListener(0));
	}

	public void testSerializeWithNonSerializableListeners() throws Exception {
		Object listener = new Object();
		TestEventManager eventManager = new TestEventManager();
		eventManager.addListener(listener);
		
		try {
			serialize(eventManager);
			fail();
		} catch (NotSerializableException expected) {
		}
	}
	
	private Object serializeAndDeserialize(Serializable object)
			throws Exception {
		byte[] bytes = serialize(object);
		return deserialize(bytes);
	}

	private static byte[] serialize(Object object) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				outputStream);
		objectOutputStream.writeObject(object);
		return outputStream.toByteArray();
	}

	private static Object deserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		return objectInputStream.readObject();
	}
}
