/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rap.rwt.internal.resources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.resources.IResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ResourceManagerImpl_Test extends TestCase {

  public void testRegistration() throws Exception {
    IResourceManager manager = getResourceManager();
    String resource = "path/to/resource";
    byte[] bytes = new byte[] { 1, 2, 3 };
    manager.register( resource, new ByteArrayInputStream( bytes ) );

    File jarFile = getResourceCopyFile( resource );
    assertTrue( "Resource not registered",  manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", jarFile.exists() );
    assertEquals( bytes, read( jarFile ) );
  }

  public void testRegisterOverridesPreviousVersion() {
    IResourceManager manager = getResourceManager();
    String resource = "path/to/resource";
    InputStream inputStream = new ByteArrayInputStream( new byte[ 0 ] );
    manager.register( resource, inputStream );
    File file = getResourceCopyFile( resource );
    Fixture.delete( file );

    manager.register( resource, inputStream );

    assertTrue( file.exists() );
  }

  public void testRegistrationWithNullParams() {
    ResourceManagerImpl manager = new ResourceManagerImpl( mock( ResourceDirectory.class ) );
    try {
      manager.register( "path", null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
    try {
      manager.register( null, mock( InputStream.class ) );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregisterNonExistingResource() {
    IResourceManager manager = getResourceManager();

    boolean unregistered = manager.unregister( "foo" );

    assertFalse( unregistered );
  }

  public void testUnregisterWithIllegalArgument() {
    IResourceManager manager = getResourceManager();
    try {
      manager.unregister( null );
      fail( "Unregister must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregister() {
    IResourceManager manager = getResourceManager();
    String path = "path/to/resource";
    manager.register( path, createInputStream() );

    boolean unregistered = manager.unregister( path );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( path ).exists() );
  }

  public void testGetLocation() {
    IResourceManager manager = getResourceManager();
    String path = "path/to/resource";
    manager.register( path, createInputStream() );
    String location = manager.getLocation( path );
    assertEquals( "rwt-resources/" + path, location );
  }

  public void testVersionedResourceName() {
    String name;
    Integer version = new Integer( 1 );
    name = ResourceManagerImpl.versionedResourceName( "path/to/name.ext", version );
    assertEquals( "path/to/name-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( "name.ext", version );
    assertEquals( "name-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".ext", version );
    assertEquals( "-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".", version );
    assertEquals( "-1.", name );
    name = ResourceManagerImpl.versionedResourceName( "", version );
    assertEquals( "-1", name );
    name = ResourceManagerImpl.versionedResourceName( "name", version );
    assertEquals( "name-1", name );
    String resource = "path.width.dot/andnamew/osuffix";
    name = ResourceManagerImpl.versionedResourceName( resource, version );
    assertEquals( "path.width.dot/andnamew/osuffix-1", name );
  }

  public void testGetLocationWithWrongParams() {
    IResourceManager manager = getResourceManager();
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

  public void testGetRegisteredContent() throws IOException {
    IResourceManager manager = getResourceManager();
    InputStream inputStream = createInputStream();
    manager.register( "myfile", inputStream );
    inputStream.close();

    InputStream content = manager.getRegisteredContent( "myfile" );
    content.close();

    assertNotNull( content );
  }
  
  public void testGetRegisteredContentForNonExistingResource() {
    IResourceManager manager = getResourceManager();
    
    assertNull( manager.getRegisteredContent( "not-there" ) );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterWithInvalidPath() throws Exception {
    IResourceManager manager = getResourceManager();
    InputStream inputStream = mock( InputStream.class );
    String path = "http://host:port/path$1";
    manager.register( path, inputStream );
    inputStream.close();

    String location = manager.getLocation( path );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }
  
  public void testRegisterWithEmptyPath() {
    IResourceManager manager = getResourceManager();

    try {
      manager.register( "", mock( InputStream.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterWithTrailingSlash() {
    IResourceManager manager = getResourceManager();
    
    try {
      manager.register( "/", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterWithTrailingBackslash() {
    IResourceManager manager = getResourceManager();
    
    try {
      manager.register( "\\", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  @SuppressWarnings( "resource" )
  public void testRegisterWithInputStreamClosesStream() throws IOException {
    IResourceManager manager = getResourceManager();
    InputStream inputStream = mock( InputStream.class );

    manager.register( "resource-name", inputStream );

    verify( inputStream ).close();
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    File path = new File( getWebContextDirectory(), ResourceDirectory.DIRNAME );
    Fixture.delete( path );
    Fixture.tearDown();
  }

  ///////////////////
  // helping methods

  private void assertEquals( byte[] origin, byte[] copy ) {
    assertEquals( "Content sizes are different", origin.length, copy.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertEquals( "Content is different", origin[ i ], copy[ i ] );
    }
  }

  private static byte[] read( File file ) throws IOException {
    FileInputStream inputStream = new FileInputStream( file );
    try {
      return read( inputStream );
    } finally {
      inputStream.close();
    }
  }

  private ByteArrayInputStream createInputStream() {
    return new ByteArrayInputStream( new byte[] { 1, 2, 3 } );
  }

  private static byte[] read( InputStream input ) throws IOException {
    BufferedInputStream bis = new BufferedInputStream( input );
    byte[] result = null;
    try {
      result = new byte[ bis.available() ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = ( byte )bis.read();
      }
    } finally {
      bis.close();
    }
    return result;
  }

  private static File getResourceCopyFile( String resourceName ) {
    String path =   getWebContextDirectory()
                  + File.separator
                  + ResourceDirectory.DIRNAME
                  + File.separator
                  + resourceName;
    return new File( path );
  }

  private static ResourceManagerImpl getResourceManager() {
    return new ResourceManagerImpl( RWTFactory.getResourceDirectory() );
  }

  private static String getWebContextDirectory() {
    return Fixture.WEB_CONTEXT_DIR.getPath();
  }

}
