/*******************************************************************************
 * Copyright (c) 2010, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.*;


public class JSLibraryServiceHandler_Test extends TestCase {
  private TestResponse response;

  public void testResponseWithEncoding() throws IOException, ServletException {    
    new JSLibraryServiceHandler().service();
    
    assertNull( response.getHeader( "Content-Encoding" ) );
    assertEquals( "text/javascript; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertEquals( JSLibraryServiceHandler.EXPIRES_NEVER, response.getHeader( "Expires" ) );
  }
  
  public void testRequestURLCreation() {
    String expected =   "rap?custom_service_handler"
                      + "=org.eclipse.rwt.internal.resources.JSLibraryServiceHandler"
                      + "&hash=H0";
    
    String requestURL = JSLibraryServiceHandler.getRequestURL(); 

    assertEquals( expected, requestURL );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    createResponse();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  private void createResponse() {
    response = ( TestResponse )RWT.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
  }
}
