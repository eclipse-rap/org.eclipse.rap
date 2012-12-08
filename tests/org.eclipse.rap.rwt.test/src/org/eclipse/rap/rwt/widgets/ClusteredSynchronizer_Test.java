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
package org.eclipse.rap.rwt.widgets;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.TestRequest;


public class ClusteredSynchronizer_Test extends TestCase {
  
  public void testExtractRequestCookiesWithSingleCookie() {
    TestRequest request = new TestRequest();
    request.addCookie( new Cookie( "name", "value" ) );
    
    String cookies = ClusteredSynchronizer.extractRequestCookies( request );
    
    assertEquals( "name=value", cookies );
  }

  public void testExtractRequestCookiesWithMultipleCookies() {
    TestRequest request = new TestRequest();
    request.addCookie( new Cookie( "name1", "value1" ) );
    request.addCookie( new Cookie( "name2", "value2" ) );
    
    String cookies = ClusteredSynchronizer.extractRequestCookies( request );
    
    assertEquals( "name1=value1; name2=value2", cookies );
  }
  
  public void testExtractRequestCookiesWithNoCookies() {
    TestRequest request = new TestRequest();
    
    String cookies = ClusteredSynchronizer.extractRequestCookies( request );
    
    assertEquals( "", cookies );
  }
}
