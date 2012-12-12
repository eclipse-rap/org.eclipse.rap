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
package org.eclipse.rap.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class ContextUtil_Test extends TestCase {

  private UISessionImpl uiSession;

  public void testGetSessionOnFakedRequest() {
    ServiceContext serviceContext = ContextUtil.createFakeContext( uiSession );

    HttpServletRequest request = serviceContext.getRequest();

    assertSame( request.getSession(), uiSession.getHttpSession() );
    assertSame( request.getSession( true ), uiSession.getHttpSession() );
    assertSame( request.getSession( false ), uiSession.getHttpSession() );
  }

  public void testGetLocaleOnFakedRequest() {
    ServiceContext serviceContext = ContextUtil.createFakeContext( uiSession );

    HttpServletRequest request = serviceContext.getRequest();

    assertNull( request.getLocale() );
  }

  public void testFakedRequest() {
    ServiceContext serviceContext = ContextUtil.createFakeContext( uiSession );
    HttpServletRequest request = serviceContext.getRequest();
    try {
      request.getAuthType();
    } catch( UnsupportedOperationException expected ) {
    }
  }

  public void testFakedResponse() throws Exception {
    ServiceContext serviceContext = ContextUtil.createFakeContext( uiSession );
    HttpServletResponse response = serviceContext.getResponse();
    try {
      response.flushBuffer();
    } catch( UnsupportedOperationException expected ) {
    }
  }

  public void testFakedServiceStore() {
    ServiceContext serviceContext = ContextUtil.createFakeContext( uiSession );

    assertNotNull( serviceContext.getServiceStore() );
  }

  @Override
  protected void setUp() throws Exception {
    uiSession = new UISessionImpl( new TestSession() );
  }
}
