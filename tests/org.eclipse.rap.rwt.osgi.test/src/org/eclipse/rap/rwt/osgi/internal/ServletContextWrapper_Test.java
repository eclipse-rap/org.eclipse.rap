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

import javax.servlet.ServletContext;

import junit.framework.TestCase;


public class ServletContextWrapper_Test extends TestCase {
  private static final String CONTEXT_DIRECTORY = "contextDirectory";
  
  private ServletContextWrapper wrapper;
  private ServletContext context;

  public void testGetContext() {
    String uripath = "uriPath";
    wrapper.getContext( uripath );
    
    verify( context ).getContext( uripath );
  }
  
  public void testGetContextPath() {
    wrapper.getContextPath();
    
    verify( context ).getContextPath();
  }

  public void testGetMajorVersion() {
    wrapper.getMajorVersion();
    
    verify( context ).getMajorVersion();
  }

  public void testGetMinorVersion() {
    wrapper.getMinorVersion();
     
    verify( context ).getMinorVersion();
  }

  public void tetsGetMimeType() {
    String mimeType = "mimeType";
    wrapper.getMimeType( mimeType );
    
    verify( context ).getMimeType( mimeType );
  }

  public void testGetResourcePaths() {
    String path = "path";
    wrapper.getResourcePaths( path );
    
    verify( context ).getResourcePaths( path );
  }
  
  public void testGetResource() throws Exception {
    String path = "path";
    wrapper.getResource( path );
    
    verify( context ).getResource( path );
  }

  public void testGetResourceAsStream() {
    String path = "path";
    wrapper.getResourceAsStream( path );
    
    verify( context ).getResourceAsStream( path );
  }

  public void testGetRequestDispatcher() {
    String path = "path";
    wrapper.getRequestDispatcher( path );
    
    verify( context ).getRequestDispatcher( path );
  }

  public void testGetNamedDispatcher() {
    String name = "path";
    wrapper.getNamedDispatcher( name );
    
    verify( context ).getNamedDispatcher( name );
  }

  @SuppressWarnings( "deprecation" )
  public void testGetServlet() throws Exception {
    String name = "name";
    wrapper.getServlet( name );
    
    verify( context ).getServlet( name );
  }

  @SuppressWarnings( "deprecation" )
  public void testGetServlets() {
    wrapper.getServlets();
    
    verify( context ).getServlets();
  }

  @SuppressWarnings( "deprecation" )
  public void testGetServletNames() {
    wrapper.getServletNames();
    
    verify( context ).getServletNames();
  }

  public void testLog() {
    String msg = "msg";
    wrapper.log( msg );
    
    verify( context ).log( msg );
  }

  @SuppressWarnings( "deprecation" )
  public void testLogWithException() {
    Exception exception = new Exception();
    String msg = "msg";
    wrapper.log( exception, msg );
    
    verify( context ).log( exception, msg );
  }

  @SuppressWarnings( "deprecation" )
  public void testLogWithThrowable() {
    Throwable throwable = new Throwable();
    String msg = "msg";
    wrapper.log( msg, throwable );
    
    verify( context ).log( msg, throwable );
  }

  public void testGetRealPath() {
    String path = "/path";
    
    String realPath = wrapper.getRealPath( path );
    
    assertEquals( CONTEXT_DIRECTORY + path, realPath );
  }

  public void testGetServerInfo() {
    wrapper.getServerInfo();
    
    verify( context ).getServerInfo();
  }

  public void testGetInitParameter() {
    String name = "name";
    wrapper.getInitParameter( name );
    
    verify( context ).getInitParameter( name );
  }

  public void testGetInitParameterNames() {
    wrapper.getInitParameterNames();
    
    verify( context ).getInitParameterNames();
  }

  public void testGetAttribute() {
    String name = "name";
    wrapper.getAttribute( name );
    
    verify( context ).getAttribute( name );
  }

  public void testGetAttributeNames() {
    wrapper.getAttributeNames();
    
    verify( context ).getAttributeNames();
  }

  public void testSetAttribute() {
    String name = "name";
    Object object = new Object();
    wrapper.setAttribute( name, object );
    
    verify( context ).setAttribute( name, object );
  }

  public void testRemoveAttribute() {
    String name = "name";
    wrapper.removeAttribute( name );
    
    verify( context ).removeAttribute( name );
  }

  public void testGetServletContextName() {
    wrapper.getServletContextName();
    
    verify( context ).getServletContextName();
  }
  
  protected void setUp() {
    context = mock( ServletContext.class );
    wrapper = new ServletContextWrapper( context, CONTEXT_DIRECTORY );
  }
}
