/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;

import java.util.Enumeration;

import javax.servlet.http.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.TestSession;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class SessionStore_Test extends TestCase {
  
  private static final String BEFORE_DESTROY = "beforeDestroy|";
  private static final String VALUE_BOUND = "valueBound";
  private static final String VALUE_UNBOUND = "valueUnbound";
  private static final String ATTR1 = "attr1";
  private static final String ATTR2 = "attr2";

  public void testSessionStoreImpl() throws Exception {
    final TestSession httpSession = new TestSession();
    ISessionStore session = new SessionStoreImpl( httpSession );
    assertNull( session.getAttribute( ATTR1 ) );
    Object attr1 = new Object();
    session.setAttribute( ATTR1, attr1 );
    assertSame( attr1, session.getAttribute( ATTR1 ) );
    assertNull( session.getAttribute( ATTR2 ) );
    
    session.setAttribute( ATTR1, null );
    assertNull( session.getAttribute( ATTR1 ) );
    session.setAttribute( ATTR1, attr1 );
    session.removeAttribute( ATTR1 );
    assertNull( session.getAttribute( ATTR1 ) );
    
    session.setAttribute( ATTR1, attr1 );
    Enumeration attributeNames = session.getAttributeNames();
    assertTrue( attributeNames.hasMoreElements() );
    assertSame( attributeNames.nextElement(), ATTR1 );
    assertFalse( attributeNames.hasMoreElements() );
    
    final String[] log = new String[] { "" };
    final HttpSession[] sessionLog = new HttpSession[ 1 ];
    final Object[] valueLog = new Object[ 1 ];
    Object attr2 = new HttpSessionBindingListener() {
      public void valueBound( final HttpSessionBindingEvent event ) {
        log[ 0 ] += VALUE_BOUND + " " + event.getName();
        sessionLog[ 0 ] = event.getSession();
        valueLog[ 0 ] = event.getValue();
      }
      public void valueUnbound( final HttpSessionBindingEvent event ) {
        log[ 0 ] += VALUE_UNBOUND + " " + event.getName();
        sessionLog[ 0 ] = event.getSession();
        valueLog[ 0 ] = event.getValue();
      }
    };
    session.setAttribute( ATTR2, attr2 );
    assertEquals( VALUE_BOUND + " " + ATTR2, log[ 0 ] );
    assertSame( httpSession, sessionLog[ 0 ] );
    assertSame( attr2, valueLog[ 0 ] );
    log[ 0 ] = "";
    sessionLog[ 0 ] = null;
    valueLog[ 0 ] = null;
    session.removeAttribute( ATTR2 );
    assertEquals( VALUE_UNBOUND + " " + ATTR2, log[ 0 ] );
    assertSame( httpSession, sessionLog[ 0 ] );
    assertSame( attr2, valueLog[ 0 ] );
    
    session.setAttribute( ATTR2, attr2 );
    log[ 0 ] = "";
    sessionLog[ 0 ] = null;
    valueLog[ 0 ] = null;
    session.setAttribute( ATTR2, new Object() );
    assertEquals( VALUE_UNBOUND + " " + ATTR2, log[ 0 ] );
    assertSame( httpSession, sessionLog[ 0 ] );
    assertSame( attr2, valueLog[ 0 ] );
    
    session.setAttribute( ATTR2, attr2 );
    log[ 0 ] = "";
    sessionLog[ 0 ] = null;
    valueLog[ 0 ] = null;
    final ISessionStore[] storeLog = new ISessionStore[ 1 ];
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        storeLog[ 0 ] = event.getSessionStore();
        log[ 0 ] += BEFORE_DESTROY;
      }
    } );
    httpSession.invalidate();
    assertEquals( BEFORE_DESTROY + VALUE_UNBOUND + " " + ATTR2, log[ 0 ] );
    assertSame( httpSession, sessionLog[ 0 ] );
    assertSame( attr2, valueLog[ 0 ] );
    assertSame( session, storeLog[ 0 ] );
    try {
      session.setAttribute( ATTR1, null );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    try {
      session.getAttribute( ATTR1 );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    try {
      session.removeAttribute( ATTR1 );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    try {
      session.getAttributeNames();
      fail();
    } catch( final IllegalStateException ise ) {
    }
    try {
      session.addSessionStoreListener( null );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    try {
      session.removeSessionStoreListener( null );
      fail();
    } catch( final IllegalStateException ise ) {
    }
    
    
    final SessionStoreImpl checkAboutUnbound
      = new SessionStoreImpl( new TestSession() );
    checkAboutUnbound.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        checkAboutUnbound.addSessionStoreListener( new SessionStoreListener() {
          public void beforeDestroy( SessionStoreEvent event ) {
          }
        } );
      }
    } );
    try {
      checkAboutUnbound.getHttpSession().invalidate();
      fail();
    } catch( final IllegalStateException iae ) {
    }
    
    final boolean[] hasContext = { false };
    SessionStoreImpl checkContext = new SessionStoreImpl( new TestSession() );
    checkContext.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    } );
    checkContext.getHttpSession().invalidate();
    assertTrue( hasContext[ 0 ] );
  }
}
