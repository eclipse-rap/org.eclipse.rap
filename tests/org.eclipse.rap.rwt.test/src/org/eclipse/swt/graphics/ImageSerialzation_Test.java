/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.*;
import java.util.Arrays;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestSession;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class ImageSerialzation_Test extends TestCase {
  
  private static class TestFilterChain implements FilterChain {
    public void doFilter( ServletRequest request, ServletResponse response ) {
    }
  }

  private ApplicationContext applicationContext;

  public void testSerializedSharedImage() throws Exception {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image image = Graphics.getImage( "image", inputStream );
    inputStream.close();
    
    try {
      Fixture.serialize( image );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }
  
  public void testSerializeSessionImage() throws Exception {
    Display display = new Display();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, inputStream );
    inputStream.close();
    ImageData imageData = image.getImageData();
    ContextProvider.disposeContext();

    Image deserializedImage = Fixture.serializeAndDeserialize( image );
    createServiceContext( deserializedImage.getDevice() );
    runClusterSupportFilter();
    
    assertEquals( image.isDisposed(), deserializedImage.isDisposed() );
    ImageData deserializedImageData = deserializedImage.getImageData();
    assertEquals( imageData, deserializedImageData );
  }

  private void runClusterSupportFilter() throws Exception {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    new RWTClusterSupport().doFilter( request, response, new TestFilterChain() );
  }

  protected void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.useDefaultResourceManager();
    applicationContext = ApplicationContextUtil.getInstance();
    ApplicationContextUtil.set( ContextProvider.getSession(), applicationContext );
  }

  protected void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }
  
  private void createServiceContext( Device device ) {
    Fixture.createServiceContext();
    TestSession session = ( TestSession )ContextProvider.getRequest().getSession();
    ApplicationContextUtil.set( session.getServletContext(), applicationContext );
    SessionStoreImpl sessionStore = ( SessionStoreImpl )getSessionStore( device );
    SessionStoreImpl.attachInstanceToSession( session, sessionStore );
    sessionStore.attachHttpSession( session );
  }

  private static ISessionStore getSessionStore( Device device ) {
    Display display = ( Display )device;
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return displayAdapter.getSessionStore();
  }

  private static void assertEquals( ImageData actual, ImageData expected ) {
    assertEquals( actual.width, expected.width );
    assertEquals( actual.height, expected.height );
    assertTrue( Arrays.equals( actual.data, expected.data ) );
  }
}
