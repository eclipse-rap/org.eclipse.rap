/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.testfixture.FileUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.*;
import org.osgi.service.http.HttpContext;


public class HttpContextWrapper_Test {

  private HttpContext httpContext;
  private HttpContextWrapper contextWrapper;
  private File directory;

  @Before
  public void setUp() {
    httpContext = mock( HttpContext.class );
    contextWrapper = new HttpContextWrapper( httpContext );

    directory = new File( Fixture.TEMP_DIR, "directory" );
    directory.mkdirs();
  }

  @After
  public void tearDown() {
    FileUtil.delete( directory );
  }

  @Test
  public void testGetResource() throws Exception {
    File file = new File( directory, "test.txt" );
    file.createNewFile();
    String fileName = file.getPath();

    URL result = contextWrapper.getResource( fileName );

    assertEquals( file, new File( result.toURI() ) );
  }

  @Test
  public void testGetResource_failsWithNonExistingFile() {
    String fileName = new File( directory, "test.txt" ).getPath();

    URL result = contextWrapper.getResource( fileName );

    assertNull( result );
  }

  @Test
  public void testGetResource_failsWithDirectory() {
    String directoryName = directory.getPath();

    URL result = contextWrapper.getResource( directoryName );

    assertNull( result );
  }

  @Test
  public void testGetMimeType() {
    String name = "mimeType";
    contextWrapper.getMimeType( name );

    verify( httpContext ).getMimeType( name );
  }

  @Test
  public void testHandleSecurity() throws Exception {
    HttpServletResponse response = mock( HttpServletResponse.class );
    HttpServletRequest request = mock( HttpServletRequest.class );

    contextWrapper.handleSecurity( request, response );

    verify( httpContext ).handleSecurity( request, response );
  }

}
