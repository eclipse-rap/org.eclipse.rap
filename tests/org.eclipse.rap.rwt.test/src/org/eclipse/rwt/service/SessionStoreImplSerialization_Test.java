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
package org.eclipse.rwt.service;

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestSession;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class SessionStoreImplSerialization_Test extends TestCase {

  private static class LoggingSessionStoreListener implements SessionStoreListener, Serializable {
    private static final long serialVersionUID = 1L;
    static boolean wasCalled;
    public void beforeDestroy( SessionStoreEvent event ) {
      wasCalled = true;
    }
  }

  private TestSession httpSession;
  private SessionStoreImpl sessionStore;

  public void testAttributesAreSerializable() throws Exception {
    String attributeName = "foo";
    String attributeValue = "bar";
    sessionStore.setAttribute( attributeName, attributeValue );
    SessionStoreImpl deserializedSession = serializeAndDeserialize( sessionStore );
    
    assertEquals( attributeValue, deserializedSession.getAttribute( attributeName ) );
  }
  
  public void testHttpSessionIsNotSerializable() throws Exception {
    SessionStoreImpl deserializedSession = serializeAndDeserialize( sessionStore );
    
    assertNull( deserializedSession.getHttpSession() );
  }
  
  public void testBoundIsSerializable() throws Exception {
    SessionStoreImpl deserializedSession = serializeAndDeserialize( sessionStore );

    assertTrue( deserializedSession.isBound() );
  }
  
  public void testListenersAreSerializable() throws Exception {
    LoggingSessionStoreListener listener = new LoggingSessionStoreListener();
    sessionStore.addSessionStoreListener( listener );
    SessionStoreImpl deserializedSession = serializeAndDeserialize( sessionStore );
    TestSession newHttpSession = new TestSession();
    deserializedSession.attachHttpSession( newHttpSession );
    SessionStoreImpl.attachInstanceToSession( newHttpSession, deserializedSession );
    newHttpSession.invalidate();
    
    assertTrue( LoggingSessionStoreListener.wasCalled );
  }

  public void testNonSerializableAttributeCausesException() throws IOException {
    sessionStore.setAttribute( "foo", new Object() );
    try {
      Fixture.serialize( sessionStore );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    LoggingSessionStoreListener.wasCalled = false;
    httpSession = new TestSession();
    sessionStore = new SessionStoreImpl( httpSession );  
  }

  private static SessionStoreImpl serializeAndDeserialize( SessionStoreImpl sessionStore ) 
    throws Exception 
  {
    byte[] bytes = Fixture.serialize( sessionStore );
    return ( SessionStoreImpl )Fixture.deserialize( bytes );
  }
}
