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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ResourceManagerImpl_Test extends TestCase {

  private ResourceManagerImpl resourceManager;

  public void testRegistration() throws Exception {
    String resource = "path/to/resource";
    byte[] bytes = new byte[] { 1, 2, 3 };
    resourceManager.register( resource, new ByteArrayInputStream( bytes ) );

    File jarFile = getResourceCopyFile( resource );
    assertTrue( "Resource not registered",  resourceManager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", jarFile.exists() );
    assertEquals( bytes, read( jarFile ) );
  }

  public void testRegisterOverridesPreviousVersion() {
    String resource = "path/to/resource";
    InputStream inputStream = new ByteArrayInputStream( new byte[ 0 ] );
    resourceManager.register( resource, inputStream );
    File file = getResourceCopyFile( resource );
    Fixture.delete( file );

    resourceManager.register( resource, inputStream );

    assertTrue( file.exists() );
  }

  public void testRegistrationWithNullParams() {
    try {
      resourceManager.register( "path", null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
    try {
      resourceManager.register( null, mock( InputStream.class ) );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregisterNonExistingResource() {
    boolean unregistered = resourceManager.unregister( "foo" );

    assertFalse( unregistered );
  }

  public void testUnregisterWithIllegalArgument() {
    try {
      resourceManager.unregister( null );
      fail( "Unregister must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregister() {
    String path = "path/to/resource";
    resourceManager.register( path, createInputStream() );

    boolean unregistered = resourceManager.unregister( path );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( path ).exists() );
  }

  public void testGetLocation() {
    String path = "path/to/resource";
    resourceManager.register( path, createInputStream() );
    String location = resourceManager.getLocation( path );
    assertEquals( "rwt-resources/" + path, location );
  }

  public void testGetLocationWithWrongParams() {
    try {
      resourceManager.getLocation( "trallala" );
      fail( "should not accept a not existing key." );
    } catch( RuntimeException expected ) {
    }
  }

  public void testGetLocationWithNullArgument() {
    try {
      resourceManager.getLocation( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetRegisteredContent() throws IOException {
    InputStream inputStream = createInputStream();
    resourceManager.register( "myfile", inputStream );
    inputStream.close();

    InputStream content = resourceManager.getRegisteredContent( "myfile" );
    content.close();

    assertNotNull( content );
  }

  @SuppressWarnings( "resource" )
  public void testGetRegisteredContentForNonExistingResource() {
    InputStream content = resourceManager.getRegisteredContent( "not-there" );

    assertNull( content );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterWithInvalidPath() throws Exception {
    InputStream inputStream = mock( InputStream.class );
    String path = "http://host:port/path$1";
    resourceManager.register( path, inputStream );
    inputStream.close();

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }

  public void testRegisterWithEmptyPath() {
    try {
      resourceManager.register( "", mock( InputStream.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterWithAbsolutePath() throws Exception {
    InputStream inputStream = createInputStream();
    String path = "/absolute/path/to/resource.txt";
    resourceManager.register( path, inputStream );
    inputStream.close();

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources//absolute/path/to/resource.txt", location );
  }

  public void testRegisterWithTrailingSlash() {
    try {
      resourceManager.register( "/", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterWithTrailingBackslash() {
    try {
      resourceManager.register( "\\", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @SuppressWarnings( "resource" )
  public void testRegisterDoesNotCloseStream() throws IOException {
    InputStream inputStream = mock( InputStream.class );

    resourceManager.register( "resource-name", inputStream );

    verify( inputStream, never() ).close();
  }

  @SuppressWarnings( "resource" )
  public void testRegisterJavascriptDoesNotCloseStream() throws IOException {
    InputStream inputStream = mock( InputStream.class );

    resourceManager.register( "lib.js", inputStream );

    verify( inputStream, never() ).close();
  }

  public void testCallRegisterOnce() throws Exception {
    String resource = "path/to/resource";
    final byte[] bytes = new byte[] { 1, 2, 3 };
    ResourceLoader loader = new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return new ByteArrayInputStream( bytes );
      }
    };
    resourceManager.registerOnce( resource, loader );

    File jarFile = getResourceCopyFile( resource );
    assertTrue( resourceManager.isRegistered( resource ) );
    assertTrue( jarFile.exists() );
    assertEquals( bytes, read( jarFile ) );
  }

  public void testCallRegisterOnceTwice() {
    String resource = "path/to/resource";
    final byte[] bytes = new byte[] { 1, 2, 3 };
    final ArrayList<Boolean> log = new ArrayList<Boolean>();
    ResourceLoader loader = new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        log.add( Boolean.TRUE );
        return new ByteArrayInputStream( bytes );
      }
    };

    resourceManager.registerOnce( resource, loader );
    resourceManager.registerOnce( resource, loader );

    assertEquals( 1, log.size() );
  }

  public void testRegisterOnceCloseStream() throws IOException {
    String resource = "path/to/resource";
    final InputStream stream = mock( InputStream.class );
    ResourceLoader loader = new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return stream;
      }
    };

    resourceManager.registerOnce( resource, loader );

    verify( stream ).close();
  }

  public void testRegisterOnceWithNullParams() {
    try {
      resourceManager.registerOnce( "path", null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
    try {
      resourceManager.registerOnce( null, mock( ResourceLoader.class ) );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterOnceUnregister() {
    String path = "path/to/resource";
    resourceManager.registerOnce( path, createResourceLoader() );

    boolean unregistered = resourceManager.unregister( path );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( path ).exists() );
  }

  public void testRegisterOnceGetLocation() {
    String path = "path/to/resource";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/" + path, location );
  }

  public void testRegisterOnceGetRegisteredContent() throws IOException {
    resourceManager.registerOnce( "myfile", createResourceLoader() );

    InputStream content = resourceManager.getRegisteredContent( "myfile" );
    content.close();

    assertNotNull( content );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterOnceWithInvalidPath() {
    String path = "http://host:port/path$1";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }

  public void testRegisterOnceWithEmptyPath() {
    try {
      resourceManager.registerOnce( "", mock( ResourceLoader.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterOnceWithAbsolutePath() {
    String path = "/absolute/path/to/resource.txt";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources//absolute/path/to/resource.txt", location );
  }

  public void testRegisterOnceWithTrailingSlash() {
    try {
      resourceManager.registerOnce( "/", createResourceLoader() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterOnceWithTrailingBackslash() {
    try {
      resourceManager.registerOnce( "\\", createResourceLoader() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    resourceManager = new ResourceManagerImpl( RWTFactory.getResourceDirectory() );
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

  private InputStream createInputStream() {
    return new ByteArrayInputStream( new byte[] { 1, 2, 3 } );
  }

  private ResourceLoader createResourceLoader() {
    ResourceLoader loader = new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return createInputStream();
      }
    };
    return loader;
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

  private static String getWebContextDirectory() {
    return Fixture.WEB_CONTEXT_DIR.getPath();
  }

}
