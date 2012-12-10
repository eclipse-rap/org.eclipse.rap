/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.io.IOException;
import java.io.NotSerializableException;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class UISessionImplSerialization_Test extends TestCase {

  private static class LoggingUISessionListener implements UISessionListener {
    private static final long serialVersionUID = 1L;
    static boolean wasCalled;
    public void beforeDestroy( UISessionEvent event ) {
      wasCalled = true;
    }
  }

  private HttpSession httpSession;
  private UISessionImpl uiSession;

  public void testAttributesAreSerializable() throws Exception {
    String attributeName = "foo";
    String attributeValue = "bar";
    uiSession.setAttribute( attributeName, attributeValue );
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertEquals( attributeValue, deserializedUiSession.getAttribute( attributeName ) );
  }

  public void testHttpSessionIsNotSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertNull( deserializedUiSession.getHttpSession() );
  }
  
  public void testIdIsSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertEquals( uiSession.getId(), deserializedUiSession.getId() );
  }

  public void testBoundIsSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertTrue( deserializedUiSession.isBound() );
  }

  public void testListenersAreSerializable() throws Exception {
    UISessionListener listener = new LoggingUISessionListener();
    uiSession.addUISessionListener( listener );
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );
    HttpSession newHttpSession = new TestSession();
    deserializedUiSession.attachHttpSession( newHttpSession );
    UISessionImpl.attachInstanceToSession( newHttpSession, deserializedUiSession );
    newHttpSession.invalidate();

    assertTrue( LoggingUISessionListener.wasCalled );
  }

  public void testNonSerializableAttributeCausesException() throws IOException {
    uiSession.setAttribute( "foo", new Object() );
    try {
      Fixture.serialize( uiSession );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }

  protected void setUp() throws Exception {
    LoggingUISessionListener.wasCalled = false;
    httpSession = new TestSession();
    uiSession = new UISessionImpl( httpSession );
  }
}
