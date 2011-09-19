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
package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class FakeContextUtil_Test extends TestCase {
  
  private SessionStoreImpl sessionStore;

  public void testGetSessionOnFakedRequest() {
    ServiceContext serviceContext = FakeContextUtil.createFakeContext( sessionStore );
    
    HttpServletRequest request = serviceContext.getRequest();
    
    assertSame( request.getSession(), sessionStore.getHttpSession() );
    assertSame( request.getSession( true ), sessionStore.getHttpSession() );
    assertSame( request.getSession( false ), sessionStore.getHttpSession() );
  }
  
  public void testGetLocaleOnFakedRequest() {
    ServiceContext serviceContext = FakeContextUtil.createFakeContext( sessionStore );
    
    HttpServletRequest request = serviceContext.getRequest();
    
    assertNull( request.getLocale() );
  }
  
  public void testFakedRequest() throws Exception {
    ServiceContext serviceContext = FakeContextUtil.createFakeContext( sessionStore );
    HttpServletRequest request = serviceContext.getRequest();
    try {
      request.getAuthType();
    } catch( UnsupportedOperationException expected ) {
    }
  }
  
  public void testFakedResponse() throws Exception {
    ServiceContext serviceContext = FakeContextUtil.createFakeContext( sessionStore );
    HttpServletResponse response = serviceContext.getResponse();
    try {
      response.flushBuffer();
    } catch( UnsupportedOperationException expected ) {
    }
  }
  
  public void testFakedStateInfo() {
    ServiceContext serviceContext = FakeContextUtil.createFakeContext( sessionStore );

    assertNotNull( serviceContext.getStateInfo() );
  }
  
  protected void setUp() throws Exception {
    sessionStore = new SessionStoreImpl( new TestSession() );
  }
}
