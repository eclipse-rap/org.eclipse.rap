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
package org.eclipse.rap.rwt.internal.engine;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class RWTClusterSupport_Test {

  private RWTClusterSupport rwtClusterSupport;
  private FilterChain chain;
  private TestRequest request;
  private TestResponse response;

  @Before
  public void setUp() {
    request = new TestRequest();
    response = new TestResponse();
    chain = mock( FilterChain.class );
    rwtClusterSupport = new RWTClusterSupport();
  }

  @Test
  public void testWithNonExistingSession() throws Exception {
    request.setSession( null );

    rwtClusterSupport.doFilter( request, response, chain );

    verify( chain ).doFilter( same( request ), same( response ) );
  }

  @Test
  public void testUISessionIsAttached() throws Exception {
    HttpSession httpSession = new TestSession();
    request.setSession( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, new UISessionImpl( httpSession ) );

    rwtClusterSupport.doFilter( request, response, chain );

    verify( chain ).doFilter( same( request ), same( response ) );
    UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( httpSession );
    assertSame( httpSession, uiSession.getHttpSession() );
  }

  @Test
  public void testSessionIsMarkedAsChanged() throws Exception {
    HttpSession httpSession = mock( HttpSession.class );
    request.setSession( httpSession );
    UISessionImpl uiSession = new UISessionImpl( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );

    rwtClusterSupport.doFilter( request, response, chain );

    verify( httpSession ).setAttribute( anyString(), same( uiSession ) );
  }

  @Test
  public void testRequestCounterIsMarkedAsChanged() throws Exception {
    HttpSession httpSession = mock( HttpSession.class );
    request.setSession( httpSession );
    UISessionImpl uiSession = new UISessionImpl( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );

    rwtClusterSupport.doFilter( request, response, chain );

    String attr = RequestCounter.class.getName() + "#instance";
    verify( httpSession ).setAttribute( eq( attr ), any( RequestCounter.class ) );
  }

}
