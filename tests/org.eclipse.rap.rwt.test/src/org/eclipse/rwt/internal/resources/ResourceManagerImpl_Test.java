/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestRequest;
import org.eclipse.rwt.internal.IConfiguration;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class ResourceManagerImpl_Test extends TestCase {
  private static final String DELIVER_FROM_DISK = ResourceManagerImpl.DELIVER_FROM_DISK;
  private static final String DELIVER_BY_SERVLET = ResourceManagerImpl.DELIVER_BY_SERVLET;
  private static final String DELIVER_BY_SERVLET_AND_TEMP_DIR
    = ResourceManagerImpl.DELIVER_BY_SERVLET_AND_TEMP_DIR;
  
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
  private static final String TEST_CONTEXT = TestRequest.CONTEX_PATH;
  private static final int TEST_PORT = TestRequest.PORT;
  private static final String TEST_SERVER = TestRequest.SERVER_NAME;
  private static final String TEST_SERVLET_PATH = TestRequest.SERVLET_PATH;
  private static final String TEST_CONTEXT_URL
    =   "http://"
      + TEST_SERVER
      + ":"
      + TEST_PORT
      + TEST_CONTEXT;
  private static final String TEST_LOCATION_DISK
    =   "rwt-resources/"
      + TEST_RESOURCE_1;
  private static final String TEST_LOCATION_VERSIONED_DISK
    =   "rwt-resources/"
      + TEST_RESOURCE_1_VERSIONED;
  private static final String TEST_LOCATION_SERVLET
    =   TEST_CONTEXT_URL
      + TEST_SERVLET_PATH
      + "?"
      + ResourceManagerImpl.RESOURCE
      + "="
      + TEST_RESOURCE_2;
  private static final String TEST_LOCATION_VERSIONED_SERVLET
    =   TEST_CONTEXT_URL
      + TEST_SERVLET_PATH
      + "?"
      + ResourceManagerImpl.RESOURCE
      + "="
      + TEST_RESOURCE_1
      + "&"
      + ResourceManagerImpl.RESOURCE_VERSION
      + "="
      + "1895582734";

  
  public void testInstanceCreationDisk() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    
    assertNotNull( "ResourceManager instance was not created", manager );
  }

  public void testInstanceCreationServlet() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    
    assertNotNull( "ResourceManager instance was not created", manager );
  }

  public void testInstanceCreationServletTempDir() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET_AND_TEMP_DIR );
    
    assertNotNull( "ResourceManager instance was not created", manager );
  }

  public void testRegistrationDiskWithNotExistingResource() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String doesNotExist = "doesNotExist";
  
    try {
      manager.register( doesNotExist );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }
    
  public void testRegistrationDisk() throws Exception {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1_JAR;
    
    manager.register( resource );
    
    File jarFile = getResourceCopyFile( resource );
    assertTrue( "Resource not registered",  manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", jarFile.exists() );
    assertEquals( read( openStream( resource ) ), read( jarFile ) );
  }
  
  public void testRegistrationDiskIsIdempotent() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1_JAR;
    manager.register( resource );
    clearTempFile();
    File jarFile = getResourceCopyFile( resource );

    manager.register( resource );

    assertFalse( "Resource must not be written twice", jarFile.exists() );
  }
  
  public void testRegistrationServletWithNotExistingResource() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    String doesNotExist = "doesNotExist";
  
    try {
      manager.register( doesNotExist );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }

  public void testRegistrationServlet() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    String resource = TEST_RESOURCE_2;
    manager.register( resource );

    File resourceFile = getResourceCopyFile( resource );
    assertTrue( "Resource written to disk", !resourceFile.exists() );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
  }

  public void testRegistrationServletTempDirWithNotExistingResource() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET_AND_TEMP_DIR );
    String doesNotExist = "doesNotExist";
  
    try {
      manager.register( doesNotExist );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }

  public void testRegistrationServletTempDir() throws Exception {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET_AND_TEMP_DIR );
    String resource = TEST_RESOURCE_3;

    manager.register( resource );
    
    File file = getResourceCopyFile( resource );
    File tmpFile = getResourceCopyInTempFile( resource );
    assertFalse( "Resource written to disk", file.exists() );
    assertTrue( "Resource not written to temp directory", tmpFile.exists() );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertEquals( read( openStream( resource ) ), read( tmpFile ) );
  }
  
  public void testRegistrationServletTempDirIsIdempotent() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET_AND_TEMP_DIR );
    String resource = TEST_RESOURCE_3;
    manager.register( resource );
    clearTempFile();
    File tmpFile = getResourceCopyInTempFile( resource );

    manager.register( resource );

    assertFalse( "Resource must not be written twice", tmpFile.exists() );
  }

  public void testRegistrationWithNullParams() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
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

  public void testVersionedRegistrationDiskWithNotExistingResource() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String doesNotExist = "doesNotExist";
  
    try {
      manager.register( doesNotExist, HTTP.CHARSET_UTF_8, RegisterOptions.NONE );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }

  public void testVersionedRegistrationDisk() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1;

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    File resourceFile = getResourceCopyFile( TEST_RESOURCE_1_VERSIONED );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertNotNull( "Versioned resource must have version number",
                   ResourceManagerImpl.findVersion( resource ) );
    assertTrue( "Resource was not written to disk", resourceFile.exists() );
    assertEquals( read( openStream( resource ) ), read( resourceFile ) );
  }
  
  public void testVersionedRegistrationDiskIsIdempotent() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1;
    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    clearTempFile();

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    File resourceFile = getResourceCopyFile( TEST_RESOURCE_1_VERSIONED );
    assertFalse( "Resource must not be written twice", resourceFile.exists() );
  }
  
  public void testCompressedRegistrationDisk() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1;

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );

    File resourceFile = getResourceCopyFile( resource );
    int[] origin = read( openStream( resource ) );
    int[] copy = read( resourceFile );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", resourceFile.exists() );
    assertTrue( "Compressed resource too big", origin.length > copy.length );
  }
  
  public void testCompressedRegistrationDiskIsIdempotent() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String resource = TEST_RESOURCE_1;
    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );
    clearTempFile();

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );
    
    File resourceFile = getResourceCopyFile( resource );
    assertFalse( "file must not be written twice", resourceFile.exists() );
  }
  
  public void testUnregisterNonExistingResource() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    
    boolean unregistered = manager.unregister( "foo" );
    
    assertFalse( unregistered );
  }

  public void testUnregisterWithIllegalArgument() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    try {
      manager.unregister( null );
      fail( "Unregister must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregister() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    manager.register( TEST_RESOURCE_1_JAR );
    
    boolean unregistered = manager.unregister( TEST_RESOURCE_1_JAR );
    
    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( TEST_RESOURCE_1_JAR ).exists() );
  }

  public void testUnregisterVersionedResource() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String testResource = TEST_RESOURCE_1_VERSIONED;
    Integer version = ResourceManagerImpl.findVersion( testResource );
    String versionedResourceName
      = ResourceManagerImpl.versionedResourceName( testResource, version );
    manager.register( TEST_RESOURCE_1, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    
    boolean unregistered = manager.unregister( TEST_RESOURCE_1 );
    
    File resourceFile = getResourceCopyFile( versionedResourceName );
    assertTrue( unregistered );
    assertFalse( resourceFile.exists() );
  }

  public void testLocationRetrievalDisk() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    manager.register( TEST_RESOURCE_1 );
    String location = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "Different locations", TEST_LOCATION_DISK, location );
  }
  
  public void testVersionedLocationRetrievalDisk() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    manager = getManager( DELIVER_FROM_DISK );

    manager.register( TEST_RESOURCE_1, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    
    String loc = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "Different locations", TEST_LOCATION_VERSIONED_DISK, loc );
  }

  public void testLocationRetrievalServlet() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_2 );
    String location = manager.getLocation( TEST_RESOURCE_2 );
    assertEquals( "Different locations", TEST_LOCATION_SERVLET, location );
  }

  public void testVersionedLocationRetrievalServlet() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_1, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    String loc = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "Different locations", TEST_LOCATION_VERSIONED_SERVLET, loc );
  }

  public void testFindResourceDisk() {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    manager.register( TEST_RESOURCE_1 );
    manager.register( TEST_RESOURCE_2, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    
    assertNull( ResourceManagerImpl.findResource( TEST_RESOURCE_1, null ) );
    assertNull( ResourceManagerImpl.findResource( "not registered", null ) );
    assertNull( ResourceManagerImpl.findResource( TEST_RESOURCE_2, null ) );
  }

  public void testFindResourceServlet() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    manager.register( TEST_RESOURCE_2 );
    
    assertNotNull( ResourceManagerImpl.findResource( TEST_RESOURCE_2, null ) );
    assertNull( ResourceManagerImpl.findResource( "not registered", null ) );
  }

  public void testRegisterDiskWithCharset() throws Exception {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    String charset = "ISO-8859-1";

    manager.register( ISO_RESOURCE, charset );

    int[] expected = read( openStream( UTF_8_RESOURCE ) );
    File copiedFile = getResourceCopyFile( ISO_RESOURCE );
    int[] actual = read( copiedFile );
    assertEquals( charset, manager.getCharset( ISO_RESOURCE ) );
    assertEquals( expected.length, actual.length );
    assertTrue( Arrays.equals( actual, expected ) );
  }

  public void testRegisterServletWithCharset() throws Exception {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
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

  public void testGetLocationWithWrongParams() {
    IResourceManager manager = getManager( DELIVER_BY_SERVLET );
    try {
      manager.getLocation( "trallala" );
      fail( "should not accept a not existing key." );
    } catch( RuntimeException expected ) {
    }

    try {
      manager.getLocation( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetRegisteredContent() throws Exception {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    InputStream is = openStream( TEST_RESOURCE_2 );
    String resourcename = "myfile";
    manager.register( resourcename, is );
    is.close();

    InputStream content = manager.getRegisteredContent( resourcename );
    
    assertNotNull( content );
    content.close();
    assertNull( manager.getRegisteredContent( "not-there" ) );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterWithInvalidFilename() throws Exception {
    IResourceManager manager = getManager( DELIVER_FROM_DISK );
    InputStream inputStream = openStream( TEST_RESOURCE_2 );
    String name = "http://host:port/path$1";
    manager.register( name, inputStream );
    inputStream.close();
    
    String location = manager.getLocation( name );
    
    String expected = "rwt-resources/http$1//host$1port/path$$1";
    assertEquals( expected, location );
  }
  
  protected void setUp() throws Exception {
    clearTempFile();
    Fixture.setUp();
    System.setProperty( IConfiguration.PARAM_RESOURCES, 
                        IConfiguration.RESOURCES_DELIVER_FROM_DISK );
  }

  protected void tearDown() throws Exception {
    clearTempFile();
    Fixture.tearDown();
  }

  ///////////////////
  // helping methods

  private void assertEquals( int[] origin, int[] copy ) {
    assertEquals( "Content sizes are different", origin.length, copy.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertEquals( "Content is different", origin[ i ], copy[ i ] );
    }
  }    

  private static int[] read( File file ) throws IOException {
    return read( new FileInputStream( file ) );
  }

  private static int[] read( InputStream input ) throws IOException {
    BufferedInputStream bis = new BufferedInputStream( input );
    int[] result = null;
    try {
      result = new int[ bis.available() ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = bis.read();
      }
    } finally {
      bis.close();
    }
    return result;
  }

  private void clearTempFile() {
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1_JAR ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1 ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1_VERSIONED ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_2 ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_3 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1_JAR ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1_VERSIONED ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_2 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_3 ) );
  }

  private File getResourceCopyFile( final String resourceName ) {
    String path =   getWebContextDirectory()
                  + File.separator
                  + ResourceManagerImpl.RESOURCES
                  + File.separator
                  + resourceName;
    return new File( path );
  }

  private File getResourceCopyInTempFile( final String resourceName ) {
    String tempDir = System.getProperty( "java.io.tmpdir" );
    String user = System.getProperty( "user.name" );
    String sep = File.separator;
    String path = tempDir + sep + user + sep + "w4toolkit" + sep + resourceName;
    return new File( path );
  }

  private IResourceManager getManager( final String mode ) {
    System.setProperty( IConfiguration.PARAM_RESOURCES, mode );
    return ResourceManagerImpl.getInstance();
  }

  private String getWebContextDirectory() {
    return Fixture.WEB_CONTEXT_DIR.getPath();
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
}
