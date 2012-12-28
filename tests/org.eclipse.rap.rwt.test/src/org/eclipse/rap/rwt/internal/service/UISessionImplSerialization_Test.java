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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.NotSerializableException;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class UISessionImplSerialization_Test {

  private HttpSession httpSession;
  private UISessionImpl uiSession;

  @Before
  public void setUp() {
    LoggingUISessionListener.wasCalled = false;
    httpSession = new TestSession();
    uiSession = new UISessionImpl( httpSession );
  }

  @Test
  public void testAttributesAreSerializable() throws Exception {
    String attributeName = "foo";
    String attributeValue = "bar";
    uiSession.setAttribute( attributeName, attributeValue );
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertEquals( attributeValue, deserializedUiSession.getAttribute( attributeName ) );
  }

  @Test
  public void testHttpSessionIsNotSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertNull( deserializedUiSession.getHttpSession() );
  }

  @Test
  public void testIdIsSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertEquals( uiSession.getId(), deserializedUiSession.getId() );
  }

  @Test
  public void testBoundIsSerializable() throws Exception {
    UISessionImpl deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    assertTrue( deserializedUiSession.isBound() );
  }

  @Test
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

  @Test
  public void testNonSerializableAttributeCausesException() throws IOException {
    uiSession.setAttribute( "foo", new Object() );
    try {
      Fixture.serialize( uiSession );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }

  private static class LoggingUISessionListener implements UISessionListener {
    private static final long serialVersionUID = 1L;
    static boolean wasCalled;
    public void beforeDestroy( UISessionEvent event ) {
      wasCalled = true;
    }
  }

}
