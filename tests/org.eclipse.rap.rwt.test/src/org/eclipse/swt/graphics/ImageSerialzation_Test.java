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

import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.NotSerializableException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.engine.*;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rap.rwt.service.ISessionStore;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class ImageSerialzation_Test extends TestCase {
  
  private Display display;
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

  protected void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.useDefaultResourceManager();
    applicationContext = ApplicationContextUtil.getInstance();
    ApplicationContextUtil.set( ContextProvider.getSessionStore(), applicationContext );
    display = new Display();
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
    IDisplayAdapter displayAdapter = display.getAdapter( IDisplayAdapter.class );
    return displayAdapter.getSessionStore();
  }

  private void runClusterSupportFilter() throws Exception {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    FilterChain filterChain = mock( FilterChain.class );
    new RWTClusterSupport().doFilter( request, response, filterChain );
  }

  private static void assertEquals( ImageData actual, ImageData expected ) {
    assertEquals( actual.width, expected.width );
    assertEquals( actual.height, expected.height );
    assertTrue( Arrays.equals( actual.data, expected.data ) );
  }
}
