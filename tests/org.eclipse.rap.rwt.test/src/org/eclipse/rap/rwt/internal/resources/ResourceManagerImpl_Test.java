/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.FileUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceManagerImpl_Test {

  private ResourceManagerImpl resourceManager;

  @Before
  public void setUp() {
    Fixture.setUp();
    ResourceDirectory resourceDirectory = getApplicationContext().getResourceDirectory();
    resourceManager = new ResourceManagerImpl( resourceDirectory );
  }

  @After
  public void tearDown() {
    File path = new File( getWebContextDirectory(), ResourceDirectory.DIRNAME );
    FileUtil.delete( path );
    Fixture.tearDown();
  }

  @Test
  public void testRegistration() throws Exception {
    String resource = "path/to/resource";
    byte[] bytes = new byte[] { 1, 2, 3 };
    resourceManager.register( resource, new ByteArrayInputStream( bytes ) );

    File jarFile = getResourceCopyFile( resource );
    assertTrue( "Resource not registered",  resourceManager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", jarFile.exists() );
    assertArrayEquals( bytes, read( jarFile ) );
  }

  @Test
  public void testRegisterOverridesPreviousVersion() {
    String resource = "path/to/resource";
    InputStream inputStream = new ByteArrayInputStream( new byte[ 0 ] );
    resourceManager.register( resource, inputStream );
    File file = getResourceCopyFile( resource );
    FileUtil.delete( file );

    resourceManager.register( resource, inputStream );

    assertTrue( file.exists() );
  }

  @Test
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

  @Test
  public void testUnregisterNonExistingResource() {
    boolean unregistered = resourceManager.unregister( "foo" );

    assertFalse( unregistered );
  }

  @Test
  public void testUnregisterWithIllegalArgument() {
    try {
      resourceManager.unregister( null );
      fail( "Unregister must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testUnregister() {
    String path = "path/to/resource";
    resourceManager.register( path, createInputStream() );

    boolean unregistered = resourceManager.unregister( path );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( path ).exists() );
  }

  @Test
  public void testGetLocation() {
    String path = "path/to/resource";
    resourceManager.register( path, createInputStream() );
    String location = resourceManager.getLocation( path );
    assertEquals( "rwt-resources/" + path, location );
  }

  @Test
  public void testGetLocationWithWrongParams() {
    try {
      resourceManager.getLocation( "trallala" );
      fail( "should not accept a not existing key." );
    } catch( RuntimeException expected ) {
    }
  }

  @Test
  public void testGetLocationWithNullArgument() {
    try {
      resourceManager.getLocation( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetRegisteredContent() throws IOException {
    InputStream inputStream = createInputStream();
    resourceManager.register( "myfile", inputStream );
    inputStream.close();

    InputStream content = resourceManager.getRegisteredContent( "myfile" );
    content.close();

    assertNotNull( content );
  }

  @SuppressWarnings( "resource" )
  @Test
  public void testGetRegisteredContentForNonExistingResource() {
    InputStream content = resourceManager.getRegisteredContent( "not-there" );

    assertNull( content );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  @Test
  public void testRegisterWithInvalidPath() throws Exception {
    InputStream inputStream = mock( InputStream.class );
    String path = "http://host:port/path$1";
    resourceManager.register( path, inputStream );
    inputStream.close();

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }

  @Test
  public void testRegisterWithEmptyPath() {
    try {
      resourceManager.register( "", mock( InputStream.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRegisterWithAbsolutePath() throws Exception {
    InputStream inputStream = createInputStream();
    String path = "/absolute/path/to/resource.txt";
    resourceManager.register( path, inputStream );
    inputStream.close();

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources//absolute/path/to/resource.txt", location );
  }

  @Test
  public void testRegisterWithTrailingSlash() {
    try {
      resourceManager.register( "/", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRegisterWithTrailingBackslash() {
    try {
      resourceManager.register( "\\", createInputStream() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @SuppressWarnings( "resource" )
  @Test
  public void testRegisterDoesNotCloseStream() throws IOException {
    InputStream inputStream = mock( InputStream.class );

    resourceManager.register( "resource-name", inputStream );

    verify( inputStream, never() ).close();
  }

  @SuppressWarnings( "resource" )
  @Test
  public void testRegisterJavascriptDoesNotCloseStream() throws IOException {
    InputStream inputStream = mock( InputStream.class );

    resourceManager.register( "lib.js", inputStream );

    verify( inputStream, never() ).close();
  }

  @Test
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
    assertArrayEquals( bytes, read( jarFile ) );
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testRegisterOnceUnregister() {
    String path = "path/to/resource";
    resourceManager.registerOnce( path, createResourceLoader() );

    boolean unregistered = resourceManager.unregister( path );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( path ).exists() );
  }

  @Test
  public void testRegisterOnceGetLocation() {
    String path = "path/to/resource";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/" + path, location );
  }

  @Test
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
  @Test
  public void testRegisterOnceWithInvalidPath() {
    String path = "http://host:port/path$1";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }

  @Test
  public void testRegisterOnceWithEmptyPath() {
    try {
      resourceManager.registerOnce( "", mock( ResourceLoader.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRegisterOnceWithAbsolutePath() {
    String path = "/absolute/path/to/resource.txt";
    resourceManager.registerOnce( path, createResourceLoader() );

    String location = resourceManager.getLocation( path );

    assertEquals( "rwt-resources//absolute/path/to/resource.txt", location );
  }

  @Test
  public void testRegisterOnceWithTrailingSlash() {
    try {
      resourceManager.registerOnce( "/", createResourceLoader() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRegisterOnceWithTrailingBackslash() {
    try {
      resourceManager.registerOnce( "\\", createResourceLoader() );
      fail();
    } catch( IllegalArgumentException expected ) {
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
