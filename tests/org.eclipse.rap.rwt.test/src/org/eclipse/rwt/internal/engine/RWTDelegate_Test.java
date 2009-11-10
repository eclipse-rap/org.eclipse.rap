/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.TestRequest;
import org.eclipse.rwt.Fixture.TestResponse;


public class RWTDelegate_Test extends TestCase {
  
  public void testInvalidRequestUrlWithPathInfo() throws Exception {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    request.setPathInfo( "foo" );
    RWTDelegate.handleInvalidRequest( request, response );
    assertEquals( HttpServletResponse.SC_NOT_FOUND, response.getErrorStatus() );
  }

  public void testCreateRedirectUrl() throws Exception {
    TestRequest request = new TestRequest();
    request.setPathInfo( "/" );
    String url = RWTDelegate.createRedirectUrl( request );
    assertEquals( "/fooapp/W4TDelegate", url );
    request.setParameter( "param1", "value1" );
    url = RWTDelegate.createRedirectUrl( request );
    assertEquals( "/fooapp/W4TDelegate?param1=value1", url );
    request.setParameter( "param2", "value2" );
    url = RWTDelegate.createRedirectUrl( request );
    assertTrue(    "/fooapp/W4TDelegate?param1=value1&param2=value2".equals( url )
                || "/fooapp/W4TDelegate?param2=value2&param1=value1".equals( url ) );
  }
}
