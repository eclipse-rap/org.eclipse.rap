/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.osgi.service.http.HttpContext;


public class HttpContextWrapper_Test extends TestCase {
  private static final File DIRECTORY = new File( Fixture.TEMP_DIR, "testGetSourceDirectory" );
  
  private HttpContext httpContext;
  private HttpContextWrapper contextWrapper;
  private String directoryName;
  private String doesNotExistName;
  private String existsName;


  public void testGetResource() throws Exception {
    initializeResourceNames();
    
    URL directory = contextWrapper.getResource( directoryName );
    URL doesNotExist = contextWrapper.getResource( doesNotExistName );
    URL exists = contextWrapper.getResource( existsName );
    
    assertNull( directory );
    assertNull( doesNotExist );
    assertNotNull( exists );
  }
  
  public void testGetMimeType() {
    String name = "mimeType";
    contextWrapper.getMimeType( name );
    
    verify( httpContext ).getMimeType( name );
  }
  
  public void testHandleSecurity() throws Exception {
    HttpServletResponse response = mock( HttpServletResponse.class );
    HttpServletRequest request = mock( HttpServletRequest.class );
    
    contextWrapper.handleSecurity( request, response );
    
    verify( httpContext ).handleSecurity( request, response );
  }
  
  protected void setUp() {
    httpContext = mock( HttpContext.class );
    contextWrapper = new HttpContextWrapper( httpContext );
    DIRECTORY.mkdirs();
  }
  
  protected void tearDown() {
    Fixture.delete( DIRECTORY );
  }
  
  private void initializeResourceNames() throws IOException {
    directoryName = DIRECTORY.getPath();
    doesNotExistName = new File( DIRECTORY, "doesNotExist.txt" ).getPath();
    File fileExists = new File( DIRECTORY, "exits.txt" );
    fileExists.createNewFile();
    existsName = fileExists.getPath();
  }
}