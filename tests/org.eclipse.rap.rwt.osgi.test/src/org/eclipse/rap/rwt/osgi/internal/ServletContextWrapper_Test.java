/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing developemnt
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.junit.Before;
import org.junit.Test;


public class ServletContextWrapper_Test {
  private static final String CONTEXT_DIRECTORY = "contextDirectory";

  private ServletContextWrapper wrapper;
  private ServletContext context;

  @Before
  public void setUp() {
    context = mock( ServletContext.class );
    wrapper = new ServletContextWrapper( context, CONTEXT_DIRECTORY );
  }

  @Test
  public void testGetContext() {
    String uripath = "uriPath";
    wrapper.getContext( uripath );

    verify( context ).getContext( uripath );
  }

  @Test
  public void testGetContextPath() {
    wrapper.getContextPath();

    verify( context ).getContextPath();
  }

  @Test
  public void testGetMajorVersion() {
    wrapper.getMajorVersion();

    verify( context ).getMajorVersion();
  }

  @Test
  public void testGetMinorVersion() {
    wrapper.getMinorVersion();

    verify( context ).getMinorVersion();
  }

  public void tetsGetMimeType() {
    String mimeType = "mimeType";
    wrapper.getMimeType( mimeType );

    verify( context ).getMimeType( mimeType );
  }

  @Test
  public void testGetResourcePaths() {
    String path = "path";
    wrapper.getResourcePaths( path );

    verify( context ).getResourcePaths( path );
  }

  @Test
  public void testGetResource() throws Exception {
    String path = "path";
    wrapper.getResource( path );

    verify( context ).getResource( path );
  }

  @Test
  public void testGetResourceAsStream() {
    String path = "path";
    wrapper.getResourceAsStream( path );

    verify( context ).getResourceAsStream( path );
  }

  @Test
  public void testGetRequestDispatcher() {
    String path = "path";
    wrapper.getRequestDispatcher( path );

    verify( context ).getRequestDispatcher( path );
  }

  @Test
  public void testGetNamedDispatcher() {
    String name = "path";
    wrapper.getNamedDispatcher( name );

    verify( context ).getNamedDispatcher( name );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetServlet() throws Exception {
    String name = "name";
    wrapper.getServlet( name );

    verify( context ).getServlet( name );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetServlets() {
    wrapper.getServlets();

    verify( context ).getServlets();
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetServletNames() {
    wrapper.getServletNames();

    verify( context ).getServletNames();
  }

  @Test
  public void testLog() {
    String msg = "msg";
    wrapper.log( msg );

    verify( context ).log( msg );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testLogWithException() {
    Exception exception = new Exception();
    String msg = "msg";
    wrapper.log( exception, msg );

    verify( context ).log( exception, msg );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testLogWithThrowable() {
    Throwable throwable = new Throwable();
    String msg = "msg";
    wrapper.log( msg, throwable );

    verify( context ).log( msg, throwable );
  }

  @Test
  public void testGetRealPath() {
    String path = "/path";

    String realPath = wrapper.getRealPath( path );

    assertEquals( null, realPath );
  }

  @Test
  public void testGetRealPathInServletContainer() {
    String path = "/path";
    String containerRealPath = "containerContextPath" +  path;
    when( context.getRealPath( path ) ).thenReturn( containerRealPath );

    String realPath = wrapper.getRealPath( path );

    assertEquals( containerRealPath, realPath );
  }

  @Test
  public void testGetServerInfo() {
    wrapper.getServerInfo();

    verify( context ).getServerInfo();
  }

  @Test
  public void testGetInitParameter() {
    String name = "name";
    wrapper.getInitParameter( name );

    verify( context ).getInitParameter( name );
  }

  @Test
  public void testGetInitParameterNames() {
    wrapper.getInitParameterNames();

    verify( context ).getInitParameterNames();
  }

  @Test
  public void testGetAttribute() {
    String name = "name";
    wrapper.getAttribute( name );

    verify( context ).getAttribute( name );
  }

  @Test
  public void testGetAttributeNames() {
    wrapper.getAttributeNames();

    verify( context ).getAttributeNames();
  }

  @Test
  public void testSetAttribute() {
    String name = "name";
    Object object = new Object();
    wrapper.setAttribute( name, object );

    verify( context ).setAttribute( name, object );
  }

  @Test
  public void testRemoveAttribute() {
    String name = "name";
    when( context.getAttribute( name ) ).thenReturn( new Object() );
    wrapper.removeAttribute( name );

    verify( context ).removeAttribute( name );
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // START ATTRIBUTE BEHAVIOR TEST
  //
  // Note [fappel]: This fixes a problem with underlying OSGi ServletContext implementations
  //                that do not implement the attributes API part.

  @Test
  public void testLocalAttributeBuffering() {
    Object object = new Object();
    String name = "name";

    wrapper.setAttribute( name, object );
    Object found = wrapper.getAttribute( name );
    boolean hasMoreElements = wrapper.getAttributeNames().hasMoreElements();
    wrapper.removeAttribute( name );
    Object foundAfterRemove = wrapper.getAttribute( name );

    assertSame( object, found );
    assertTrue( hasMoreElements );
    assertNull( foundAfterRemove );
  }

  @Test
  public void testAttributeBufferingInWrappedServletContext() {
    Object object = new Object();
    String name = "name";
    ServletContext wrapped = new TestServletContext();
    wrapper = new ServletContextWrapper( wrapped, "" );

    wrapper.setAttribute( name, object );
    Object found = wrapped.getAttribute( name );
    boolean hasMoreElements = wrapper.getAttributeNames().hasMoreElements();
    wrapper.removeAttribute( name );
    Object foundAfterRemove = wrapped.getAttribute( name );

    assertSame( object, found );
    assertTrue( hasMoreElements );
    assertNull( foundAfterRemove );
  }

  // END ATTRIBUTE BEHAVIOR TEST
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void testGetServletContextName() {
    wrapper.getServletContextName();

    verify( context ).getServletContextName();
  }

  public void getContextDirectory() {
    Object found = wrapper.getAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION );
  
    assertSame( CONTEXT_DIRECTORY, found );
  }
}
