/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.CONNECTION_ID;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serialize;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.NotSerializableException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.engine.RWTClusterSupport;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.swt.internal.graphics.Graphics;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class ImageSerialzation_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.createApplicationContext( true );
    Fixture.createServiceContext();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testSerializedSharedImage() throws Exception {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image image = Graphics.getImage( "image", inputStream );
    inputStream.close();

    try {
      serialize( image );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }

  @Test
  public void testSerializeSessionImage() throws Exception {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, inputStream );
    inputStream.close();
    ImageData imageData = image.getImageData();
    ContextProvider.disposeContext();

    Image deserializedImage = serializeAndDeserialize( image );
    createServiceContext( deserializedImage.getDevice() );
    runClusterSupportFilter();

    assertTrue( image.isDisposed() == deserializedImage.isDisposed() );
    ImageData deserializedImageData = deserializedImage.getImageData();
    assertEquals( imageData, deserializedImageData );
  }

  private void createServiceContext( Device device ) {
    Fixture.createServiceContext();
    UISessionImpl uiSession = ( UISessionImpl )getUISession( device );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( CONNECTION_ID, uiSession.getConnectionId() );
    TestHttpSession session = ( TestHttpSession )request.getSession();
    uiSession.setHttpSession( session );
    uiSession.attachToHttpSession();
  }

  private static UISession getUISession( Device device ) {
    Display display = ( Display )device;
    return display.getAdapter( IDisplayAdapter.class ).getUISession();
  }

  private void runClusterSupportFilter() throws Exception {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    FilterChain filterChain = mock( FilterChain.class );
    new RWTClusterSupport().doFilter( request, response, filterChain );
  }

  private static void assertEquals( ImageData actual, ImageData expected ) {
    assertTrue( actual.width == expected.width );
    assertTrue( actual.height == expected.height );
    assertTrue( Arrays.equals( actual.data, expected.data ) );
  }

}
