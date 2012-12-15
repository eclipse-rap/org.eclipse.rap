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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class RWTClusterSupport_Test extends TestCase {

  private static class TestFilterChain implements FilterChain {
    boolean doFilterWasCalled;
    public void doFilter( ServletRequest request, ServletResponse response )
      throws IOException, ServletException
    {
      doFilterWasCalled = true;
    }
  }

  private RWTClusterSupport rwtClusterSupport;
  private TestFilterChain chain;
  private TestRequest request;
  private TestResponse response;

  public void testWithNonExistingSession() throws Exception {
    request.setSession( null );

    rwtClusterSupport.doFilter( request, response, chain );

    assertTrue( chain.doFilterWasCalled );
  }

  public void testUISessionIsAttached() throws Exception {
    HttpSession session = new TestSession();
    request.setSession( session );
    UISessionImpl.attachInstanceToSession( session, new UISessionImpl( session ) );

    rwtClusterSupport.doFilter( request, response, chain );

    UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( session );
    assertTrue( chain.doFilterWasCalled );
    assertSame( session, uiSession.getHttpSession() );
  }

  public void testSessionIsMarkedAsChanged() throws Exception {
    final List<Object> log = new ArrayList<Object>();
    HttpSession session = new TestSession() {
      @Override
      public void setAttribute( String name, Object value ) {
        super.setAttribute( name, value );
        log.add( value );
      }
    };
    request.setSession( session );
    session.setAttribute( "foo", "bar" );
    UISessionImpl uiSession = new UISessionImpl( session );
    UISessionImpl.attachInstanceToSession( session, uiSession );
    log.clear();

    rwtClusterSupport.doFilter( request, response, chain );

    assertEquals( 1, log.size() );
    assertEquals( uiSession, log.get( 0 ) );
  }

  @Override
  protected void setUp() throws Exception {
    request = new TestRequest();
    response = new TestResponse();
    chain = new TestFilterChain();
    rwtClusterSupport = new RWTClusterSupport();
  }
}
