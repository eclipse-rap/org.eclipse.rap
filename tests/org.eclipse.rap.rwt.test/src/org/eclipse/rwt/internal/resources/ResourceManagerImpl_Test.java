/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResourceManager;


public class ResourceManagerImpl_Test extends TestCase {

  // /////////////////////
  // constant definitions
  private final static String TEST_RESOURCE_1_JAR
    = "resources/js/resourcetest.js";
  private final static String TEST_RESOURCE_1
    = "org/eclipse/rwt/internal/resources/resourcetest1.js";
  private final static String TEST_RESOURCE_1_VERSIONED
    = "org/eclipse/rwt/internal/resources/resourcetest11895582734.js";
  private final static String TEST_RESOURCE_2
    = "org/eclipse/rwt/internal/resources/resourcetest2.gif";
  private final static String TEST_RESOURCE_3
    = "org/eclipse/rwt/internal/resources/resourcetest3.gif";
  private final static String ISO_RESOURCE
    = "org/eclipse/rwt/internal/resources/iso-resource.js";
  private final static String UTF_8_RESOURCE
    = "org/eclipse/rwt/internal/resources/utf-8-resource.js";
  private static final String TEST_CONTEXT = "/test";
  private static final int TEST_PORT = 4711;
  private static final String TEST_SERVER = "TestCase";
  private static final String TEST_SERVLET_PATH = "/W4TDelegate";
  private static final String TEST_REQUEST_URI 
    = TEST_CONTEXT + "/W4TDelegate?anyParam=true";
  private static final String TEST_CONTEXT_URL
    =   "http://"
      + TEST_SERVER
      + ":"
      + TEST_PORT
      + TEST_CONTEXT;
  private static final String TEST_LOCATION_DISK
    =   TEST_CONTEXT_URL
      + "/"
      + TEST_RESOURCE_1;
  private static final String TEST_LOCATION_VERSIONED_DISK
    =   TEST_CONTEXT_URL
      + "/"
      + TEST_RESOURCE_1_VERSIONED;
  private static final String TEST_LOCATION_SERVLET
    =   TEST_CONTEXT_URL
      + TEST_SERVLET_PATH
      + "?"
      + RequestParams.RESOURCE
      + "="
      + TEST_RESOURCE_2;
  private static final String TEST_LOCATION_VERSIONED_SERVLET
    =   TEST_CONTEXT_URL
      + TEST_SERVLET_PATH
      + "?"
      + RequestParams.RESOURCE
      + "="
      + TEST_RESOURCE_1
      + "&"
      + RequestParams.RESOURCE_VERSION
      + "="
      + "1895582734";
  
  /////////
  // fields
  private String webAppRoot;

  /**
   * <p>
   * creates a new instance of ResourceManager_Test.
   * </p>
   */
  public ResourceManagerImpl_Test( final String name ) {
    super( name );
  }

  protected void tearDown() throws Exception {
    ContextProvider.disposeContext();
    Fixture.removeContext();
    Fixture.clearSingletons();
  }

  protected void setUp() throws Exception {
    Fixture.createContextWithoutResourceManager();
  }

  public void testInstanceCreationDisk() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    assertNotNull( "The ResourceManager instance was not created", manager );
  }

  public void testInstanceCreationServlet() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    assertNotNull( "The ResourceManager instance was not created", manager );
  }

  public void testInstanceCreationServletTempDir() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET_AND_TEMP_DIR );
    assertNotNull( "The ResourceManager instance was not created", manager );
  }

  public void testRegistrationDisk() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    // register only existing resources
    Exception ioe = null;
    try {
      manager.register( "trallala" );
    } catch( ResourceRegistrationException e ) {
      ioe = e;
    }
    assertNotNull( "resource registered which does not exist", ioe );
    assertTrue( "dummy resource is registered",
                !manager.isRegistered( "trallala" ) );
    // write file
    manager.register( TEST_RESOURCE_1_JAR );
    String name = getResourceCopyFile( TEST_RESOURCE_1_JAR );
    assertTrue( "file was not written to disk", new File( name ).exists() );
    assertTrue( "resource not registered",
                manager.isRegistered( TEST_RESOURCE_1_JAR ) );
    // compare content
    int[] origin = read( openStream( TEST_RESOURCE_1_JAR ) );
    int[] copy = read( new FileInputStream( name ) );
    String msg = "origin and copy must have the same size should be ["
                 + origin.length
                 + "]  is ["
                 + copy.length
                 + "]";
    assertTrue( msg, copy.length == origin.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertTrue( "origin and copy must have the same content",
                  origin[ i ] == copy[ i ] );
    }
    // check that the file only is written once per application lifecycle
    clearTempFile();
    manager.register( TEST_RESOURCE_1_JAR );
    assertTrue( "file must not be written twice", !new File( name ).exists() );
  }

  public void testRegistrationServlet() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    clearTempFile();
    // register only existing resources
    Exception ioe = null;
    try {
      manager.register( "trallala" );
    } catch( ResourceRegistrationException e ) {
      ioe = e;
    }
    assertNotNull( "resource registered which does not exist", ioe );
    // register resource
    manager.register( TEST_RESOURCE_2 );
    String name = getResourceCopyFile( TEST_RESOURCE_2 );
    assertTrue( "file written to disk", !new File( name ).exists() );
    assertTrue( "resource not registered",
                manager.isRegistered( TEST_RESOURCE_2 ) );
  }

  public void testRegistrationServletTempDir() throws Exception {
    IResourceManager manager 
      = getManager( ResourceBase.DELIVER_BY_SERVLET_AND_TEMP_DIR );
    clearTempFile();
    // register only existing resources
    Exception ioe = null;
    try {
      manager.register( "trallala" );
    } catch( ResourceRegistrationException e ) {
      ioe = e;
    }
    assertNotNull( "resource registered which does not exist", ioe );
    // register resource
    manager.register( TEST_RESOURCE_3 );
    String name = getResourceCopyFile( TEST_RESOURCE_3 );
    assertTrue( "file written to disk", !new File( name ).exists() );
    String tempName = getResourceCopyInTempFile( TEST_RESOURCE_3 );
    assertTrue( "file not written to temporary directory",
                new File( tempName ).exists() );
    assertTrue( "resource not registered",
                manager.isRegistered( TEST_RESOURCE_3 ) );
    // compare content
    int[] origin = read( openStream( TEST_RESOURCE_3 ) );
    int[] copy = read( new FileInputStream( tempName ) );
    assertTrue( "origin and copy must have the same size",
                copy.length == origin.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertTrue( "origin and copy must have the same content",
                  origin[ i ] == copy[ i ] );
    }
    // check that the file only is written once per application lifecycle
    clearTempFile();
    manager.register( TEST_RESOURCE_3 );
    assertTrue( "file must not be written twice",
                !new File( tempName ).exists() );
  }

  public void testRegistrationWithNullParams() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    try {
      manager.register( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      String notAssigned = null;
      manager.register( "some-resource", notAssigned );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      manager.register( null, "UTF-8" );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      manager.register( "some-resource", "UTF-8", null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testVersionedRegistrationDisk() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    // register only existing resources
    try {
      manager.register( "trallala",
                        "UTF-8",
                        IResourceManager.RegisterOptions.NONE );
      fail( "resource registered which does not exist" );
    } catch( ResourceRegistrationException e ) {
      // expected
    }
    assertFalse( "dummy resource must not be registered",
                 manager.isRegistered( "trallala" ) );
    // register resource which will be written to disk
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.VERSION );
    assertTrue( "resource not registered",
                manager.isRegistered( TEST_RESOURCE_1 ) );
    assertNotNull( "Versioned resource must have version number",
                   ResourceManagerImpl.findVersion( TEST_RESOURCE_1 ) );
    String name = getResourceCopyFile( TEST_RESOURCE_1_VERSIONED );
    assertTrue( "file was not written to disk", new File( name ).exists() );
    // compare content
    int[] origin = read( openStream( TEST_RESOURCE_1 ) );
    int[] copy = read( new FileInputStream( name ) );
    String msg = "origin and copy must have the same size should be ["
                 + origin.length
                 + "]  is ["
                 + copy.length
                 + "]";
    assertTrue( msg, copy.length == origin.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertTrue( "origin and copy must have the same content",
                  origin[ i ] == copy[ i ] );
    }
    // check that the file only is written once per application lifecycle
    clearTempFile();
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.VERSION );
    assertFalse( "file must not be written twice", new File( name ).exists() );
  }

  public void testCompressedRegistrationDisk() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    // register resource which will be written to disk
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.COMPRESS );
    assertTrue( "resource not registered",
                manager.isRegistered( TEST_RESOURCE_1 ) );
    String name = getResourceCopyFile( TEST_RESOURCE_1 );
    assertTrue( "file was not written to disk", new File( name ).exists() );
    // compare content
    int[] origin = read( openStream( TEST_RESOURCE_1 ) );
    int[] copy = read( new FileInputStream( name ) );
    String msg = "copy must be smaller in size, since it is compressed";
    assertTrue( msg, origin.length > copy.length );
    // check that the file only is written once per application lifecycle
    clearTempFile();
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.COMPRESS );
    assertFalse( "file must not be written twice", new File( name ).exists() );
  }

  public void testLocationRetrievalDisk() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    // unversioned
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    manager.register( TEST_RESOURCE_1 );
    String location = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "different locations", TEST_LOCATION_DISK, location );
    // versioned
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.VERSION );
    location = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "different locations", TEST_LOCATION_VERSIONED_DISK, location );
  }

  public void testLocationRetrievalServlet() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    // unversioned
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_2 );
    String location = manager.getLocation( TEST_RESOURCE_2 );
    assertEquals( "different locations", TEST_LOCATION_SERVLET, location );
    // versioned
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_1,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.VERSION );
    location = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "different locations",
                  TEST_LOCATION_VERSIONED_SERVLET,
                  location );
  }

  public void testFindResourceDisk() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    manager.register( TEST_RESOURCE_1 );
    assertNull( ResourceManagerImpl.findResource( TEST_RESOURCE_1, null ) );
    assertNull( ResourceManagerImpl.findResource( "not registered", null ) );
    manager.register( TEST_RESOURCE_2,
                      HTML.CHARSET_NAME_ISO_8859_1,
                      IResourceManager.RegisterOptions.VERSION );
    assertNull( ResourceManagerImpl.findResource( TEST_RESOURCE_2, null ) );
  }

  public void testFindResourceServlet() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_2 );
    assertNotNull( ResourceManagerImpl.findResource( TEST_RESOURCE_2, null ) );
    assertNull( ResourceManagerImpl.findResource( "not registered", null ) );
  }

  public void testRegisterDiskWithCharset() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    clearTempFile();
    String charset = "ISO-8859-1";
    manager.register( ISO_RESOURCE, charset );
    assertEquals( charset, manager.getCharset( ISO_RESOURCE ) );
    int[] expected = read( openStream( UTF_8_RESOURCE ) );
    String copiedFile = getResourceCopyFile( ISO_RESOURCE );
    int[] actual = read( new FileInputStream( copiedFile ) );
    assertEquals( expected.length, actual.length );
    assertTrue( Arrays.equals( actual, expected ) );
  }

  public void testRegisterServletWithCharset() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    String charset = "ISO-8859-1";
    manager.register( ISO_RESOURCE, charset );
    int[] expected = read( openStream( UTF_8_RESOURCE ) );
    int[] actual = ResourceManagerImpl.findResource( ISO_RESOURCE, null );
    assertEquals( expected.length, actual.length );
    assertTrue( Arrays.equals( actual, expected ) );
  }

  public void testVersionedResourceName() {
    String name;
    Integer version = new Integer( 1 );
    name = ResourceManagerImpl.versionedResourceName( "path/to/name.ext", version );
    assertEquals( "path/to/name1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( "name.ext", version );
    assertEquals( "name1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".ext", version );
    assertEquals( "1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".", version );
    assertEquals( "1.", name );
    name = ResourceManagerImpl.versionedResourceName( "", version );
    assertEquals( "1", name );
    name = ResourceManagerImpl.versionedResourceName( "name", version );
    assertEquals( "name1", name );
    String resource = "path.width.dot/andnamew/osuffix";
    name = ResourceManagerImpl.versionedResourceName( resource, version );
    assertEquals( "path.width.dot/andnamew/osuffix1", name );
  }

  public void testGetLocationWithWrongParams() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_BY_SERVLET );
    try {
      manager.getLocation( "trallala" );
      fail( "should not accept a not existing key." );
    } catch( RuntimeException e ) {
      // expected
    }
    try {
      manager.getLocation( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testGetRegisteredContent() throws Exception {
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    InputStream is = openStream( TEST_RESOURCE_2 );
    manager.register( "myfile", is );
    is.close();
    InputStream content = manager.getRegisteredContent( "myfile" );
    assertNotNull( content );
    content.close();
    assertNull( manager.getRegisteredContent( "not-there" ) );
  }
  
  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterWithInvalidFilename() throws Exception {
    ServiceContext context = new ServiceContext( new TestRequest(),
                                                 new TestResponse() );
    ContextProvider.setContext( context );
    IResourceManager manager = getManager( ResourceBase.DELIVER_FROM_DISK );
    InputStream inputStream = openStream( TEST_RESOURCE_2 );
    String name = "http://host:port/path$1";
    manager.register( name, inputStream );
    inputStream.close();
    String location = manager.getLocation( name );
    assertEquals( "http://TestCase:4711/test/http$1//host$1port/path$$1", 
                  location );
  }

  ///////////////////
  // helping methods
  
  private static int[] read( final InputStream input ) throws IOException {
    int[] result = null;
    try {
      result = new int[ input.available() ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = input.read();
      }
    } finally {
      input.close();
    }
    return result;
  }

  private void clearTempFile() {
    doClearTempFile( TEST_RESOURCE_1_JAR );
    doClearTempFile( TEST_RESOURCE_1 );
    doClearTempFile( TEST_RESOURCE_1_VERSIONED );
    doClearTempFile( TEST_RESOURCE_2 );
    doClearTempFile( TEST_RESOURCE_3 );
  }

  private void doClearTempFile( final String fileName ) {
    deleteFile( getResourceCopyFile( fileName ) );
    deleteFile( getResourceCopyInTempFile( fileName ) );
  }

  private void deleteFile( final String name ) {
    File file = new File( name );
    if( file.exists() ) {
      file.delete();
    }
  }

  private String getResourceCopyFile( final String resourceName ) {
    return webAppRoot + File.separator + resourceName;
  }

  private String getResourceCopyInTempFile( final String resourceName ) {
    String tempDir = System.getProperty( "java.io.tmpdir" );
    String user = System.getProperty( "user.name" );
    String sep = File.separator;
    return tempDir + sep + user + sep + "w4toolkit" + sep + resourceName;
  }

  private IResourceManager getManager( final String mode ) throws Exception {
    createManager( mode );
    return ResourceManagerImpl.getInstance();
  }

  private void createManager( final String mode ) throws Exception {
    // cleanup some singletons to get them reinitialized within the
    // current configuration
    Field instance = ResourceManagerImpl.class.getDeclaredField( "_instance" );
    instance.setAccessible( true );
    instance.set( ResourceManagerImpl.class, null );
    webAppRoot = Fixture.getWebAppBase().toString();
    Class[] paramTypes = new Class[]{
      String.class, String.class
    };
    Method creator = ResourceManagerImpl.class.getMethod( "createInstance",
                                                          paramTypes );
    Object[] params = new Object[]{
      webAppRoot, mode
    };
    creator.invoke( null, params );
    ResourceBase.setDeliveryMode( mode );
  }

  private static InputStream openStream( final String name ) {
    ClassLoader loader = ResourceManagerImpl_Test.class.getClassLoader();
    InputStream result = loader.getResourceAsStream( name );
    if( result == null ) {
      String encodedName = name.replace( '\\', '/' );
      result = loader.getResourceAsStream( encodedName );
    }
    return result;
  }
  // //////////////
  // inner classes
  private class TestRequest implements HttpServletRequest {

    public String getAuthType() {
      return null;
    }

    public Cookie[] getCookies() {
      return null;
    }

    public long getDateHeader( String arg0 ) {
      return 0;
    }

    public String getHeader( String arg0 ) {
      return null;
    }

    public Enumeration getHeaders( String arg0 ) {
      return null;
    }

    public Enumeration getHeaderNames() {
      return null;
    }

    public int getIntHeader( String arg0 ) {
      return 0;
    }

    public String getMethod() {
      return null;
    }

    public String getPathInfo() {
      return null;
    }

    public String getPathTranslated() {
      return null;
    }

    public String getContextPath() {
      return TEST_CONTEXT;
    }

    public String getQueryString() {
      return null;
    }

    public String getRemoteUser() {
      return null;
    }

    public boolean isUserInRole( String arg0 ) {
      return false;
    }

    public Principal getUserPrincipal() {
      return null;
    }

    public String getRequestedSessionId() {
      return null;
    }

    public String getRequestURI() {
      return TEST_REQUEST_URI;
    }

    public StringBuffer getRequestURL() {
      return null;
    }

    public String getServletPath() {
      return TEST_SERVLET_PATH;
    }

    public HttpSession getSession( boolean arg0 ) {
      return null;
    }

    public HttpSession getSession() {
      return null;
    }

    public boolean isRequestedSessionIdValid() {
      return false;
    }

    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    public Object getAttribute( String arg0 ) {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public void setCharacterEncoding( String arg0 )
      throws UnsupportedEncodingException
    {
    }

    public int getContentLength() {
      return 0;
    }

    public String getContentType() {
      return null;
    }

    public ServletInputStream getInputStream() throws IOException {
      return null;
    }

    public String getParameter( String arg0 ) {
      return null;
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues( String arg0 ) {
      return null;
    }

    public Map getParameterMap() {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public String getScheme() {
      return "http";
    }

    public String getServerName() {
      return TEST_SERVER;
    }

    public int getServerPort() {
      return TEST_PORT;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRemoteAddr() {
      return null;
    }

    public String getRemoteHost() {
      return null;
    }

    public void setAttribute( String arg0, Object arg1 ) {
    }

    public void removeAttribute( String arg0 ) {
    }

    public Locale getLocale() {
      return null;
    }

    public Enumeration getLocales() {
      return null;
    }

    public boolean isSecure() {
      return false;
    }

    public RequestDispatcher getRequestDispatcher( String arg0 ) {
      return null;
    }

    public String getRealPath( String arg0 ) {
      return null;
    }

    public String getLocalAddr() {
      throw new UnsupportedOperationException();
    }

    public String getLocalName() {
      throw new UnsupportedOperationException();
    }

    public int getLocalPort() {
      throw new UnsupportedOperationException();
    }

    public int getRemotePort() {
      throw new UnsupportedOperationException();
    }
  }
  private class TestResponse implements HttpServletResponse {

    public void addCookie( Cookie arg0 ) {
    }

    public boolean containsHeader( String arg0 ) {
      return false;
    }

    public String encodeURL( String arg0 ) {
      return arg0;
    }

    public String encodeRedirectURL( String arg0 ) {
      return null;
    }

    public String encodeUrl( String arg0 ) {
      return arg0;
    }

    public String encodeRedirectUrl( String arg0 ) {
      return null;
    }

    public void sendError( int arg0, String arg1 ) throws IOException {
    }

    public void sendError( int arg0 ) throws IOException {
    }

    public void sendRedirect( String arg0 ) throws IOException {
    }

    public void setDateHeader( String arg0, long arg1 ) {
    }

    public void addDateHeader( String arg0, long arg1 ) {
    }

    public void setHeader( String arg0, String arg1 ) {
    }

    public void addHeader( String arg0, String arg1 ) {
    }

    public void setIntHeader( String arg0, int arg1 ) {
    }

    public void addIntHeader( String arg0, int arg1 ) {
    }

    public void setStatus( int arg0 ) {
    }

    public void setStatus( int arg0, String arg1 ) {
    }

    public String getCharacterEncoding() {
      return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
      return null;
    }

    public PrintWriter getWriter() throws IOException {
      return null;
    }

    public void setContentLength( int arg0 ) {
    }

    public void setContentType( String arg0 ) {
    }

    public void setBufferSize( int arg0 ) {
    }

    public int getBufferSize() {
      return 0;
    }

    public void flushBuffer() throws IOException {
    }

    public void resetBuffer() {
    }

    public boolean isCommitted() {
      return false;
    }

    public void reset() {
    }

    public void setLocale( Locale arg0 ) {
    }

    public Locale getLocale() {
      return null;
    }

    public String getContentType() {
      throw new UnsupportedOperationException();
    }

    public void setCharacterEncoding( String charset ) {
      throw new UnsupportedOperationException();
    }
  }
}