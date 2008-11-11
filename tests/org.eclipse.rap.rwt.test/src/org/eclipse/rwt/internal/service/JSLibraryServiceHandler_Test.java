/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.swt.RWTFixture;


public class JSLibraryServiceHandler_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testResponseEncoding() throws IOException, ServletException {
    // as there is js concatenation in unit test mode is switched of
    // we only test header settings...
    
    
    // test with encoding not allowed by browser
    TestResponse response = ( TestResponse )RWT.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    JSLibraryServiceHandler handler = new JSLibraryServiceHandler();
    handler.service();
    String encoding
      = response.getHeader( HTML.CONTENT_ENCODING );
    assertNull( encoding );
    String contentType = response.getHeader( HTML.CONTENT_TYPE );
    assertEquals( HTML.CONTENT_TEXT_JAVASCRIPT, contentType );
    String expires = response.getHeader( HTML.EXPIRES );
    assertEquals( JSLibraryServiceHandler.EXPIRES_NEVER, expires );
    
    // test with encoding allowed by browser
    response.setOutputStream( new TestServletOutputStream() );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( HTML.ACCEPT_ENCODING,
                       HTML.ENCODING_GZIP );
    handler.service();
    encoding = response.getHeader( HTML.CONTENT_ENCODING );
    assertNotNull( encoding );
    assertEquals( HTML.ENCODING_GZIP, encoding );
  }
  
  public void testRequestURLCreation() throws IOException {
    String requestURL = JSLibraryServiceHandler.getRequestURL(); 
    String expected
      =   "W4TDelegate?custom_service_handler"
        + "=org.eclipse.rwt.internal.service.JSLibraryServiceHandler"
        + "&hash=H0";
    assertEquals( expected, requestURL );
  }
}
