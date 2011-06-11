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
package org.eclipse.rap.rwt.cluster.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.*;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.*;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.ApplicationContextUtil;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings("restriction")
public abstract class SessionFailover_Test extends TestCase {

  private IServletEngineCluster cluster;
  private IServletEngine primary;
  private IServletEngine secondary;
  private RWTClient client;
  
  abstract IServletEngineFactory getServletEngineFactory();
  
  public void testButtonEntryPoint() throws Exception {
    cluster.start( ButtonEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    // Click center button four times on primary
    clickCenterButton( 1, 4 );
    // Click center button four times on secondary
    cluster.removeServletEngine( primary );
    client.changeServletEngine( secondary );
    clickCenterButton( 5, 8 );
    // Number of sessions
    assertEquals( 1, primary.getSessions().length );
    assertEquals( 1, secondary.getSessions().length );
    // HttpSessions
    HttpSession primarySession = ClusterFixture.getFirstSession( primary );
    assertSessionIsIntact( primarySession, client );
    HttpSession secondarySession = ClusterFixture.getFirstSession( secondary );
    assertSessionIsIntact( secondarySession, client );
    assertEquals( primarySession.getId(), secondarySession.getId() );
    // Displays
    Display primaryDisplay = ClusterFixture.getSessionDisplay( primarySession );
    Display secondaryDisplay = ClusterFixture.getSessionDisplay( secondarySession );
    assertNotSame( primaryDisplay, secondaryDisplay );
  }

  public void testResourcesEntryPoint() throws Exception {
    cluster.start( ResourcesEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    
    client.changeServletEngine( secondary );
    client.sendDisplayResizeRequest( 400, 600 );

    prepareExamination();
    Shell primaryShell = getFirstShell( primary );
    Shell secondaryShell = getFirstShell( secondary );
    Font primaryFont = primaryShell.getFont();
    Font secondaryFont = secondaryShell.getFont();
    assertEquals( primaryFont, secondaryFont );
    assertNotSame( primaryFont, secondaryFont );
    assertSame( primaryShell.getDisplay(), primaryFont.getDevice() );
    assertSame( secondaryShell.getDisplay(), secondaryFont.getDevice() );
  }
  
  public void testImageEntryPoint() throws Exception {
    cluster.start( ImageEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    client.sendResourceRequest( ImageEntryPoint.imagePath );
    
    client.changeServletEngine( secondary );
    client.sendDisplayResizeRequest( 400, 600 );
    
    prepareExamination();
    Shell primaryShell = getFirstShell( primary );
    Shell secondaryShell = getFirstShell( secondary );
    Image primaryImage = primaryShell.getImage();
    Image secondaryImage = secondaryShell.getImage();
    assertEquals( primaryImage.getImageData(), secondaryImage.getImageData() );
    assertNotSame( primaryImage, secondaryImage );
    assertSame( primaryShell.getDisplay(), primaryImage.getDevice() );
    assertSame( secondaryShell.getDisplay(), secondaryImage.getDevice() );
  }
  
  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    cluster = getServletEngineFactory().createServletEngineCluster();
    primary = cluster.addServletEngine();
    secondary = cluster.addServletEngine();
    client = new RWTClient( primary );
  }

  protected void tearDown() throws Exception {
    cluster.stop();
    ClusterFixture.tearDown();
  }

  private static boolean assertEquals( ImageData expected, ImageData actual ) {
    boolean result;
    byte[] expectedBytes = getImageBytes( expected );
    byte[] actualBytes = getImageBytes( actual );
    if( expectedBytes.length == actualBytes.length ) {
      result = true;
      for( int i = 0; result && i < actualBytes.length; i++ ) {
        if( expectedBytes[ i ] != actualBytes[ i ] ) {
          result = false;
        }
      }
    } else {
      result = false;
    }
    return result;
  }
  
  private static byte[] getImageBytes( ImageData imageData ) {
    ImageLoader imageLoader = new ImageLoader();
    imageLoader.data = new ImageData[] { imageData }; 
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    imageLoader.save( outputStream, SWT.IMAGE_PNG );
    return outputStream.toByteArray();
  }

  private void prepareExamination() {
    attachApplicationContextToSession( primary );
    attachCurrentThreadToDisplay( primary );
    attachApplicationContextToSession( secondary );
    attachCurrentThreadToDisplay( secondary );
  }
  
  private static void attachApplicationContextToSession( IServletEngine servletEngine ) {
    HttpSession session = ClusterFixture.getFirstSession( servletEngine );
    SessionStoreImpl sessionStore = SessionStoreImpl.getInstanceFromSession( session );
    ServletContext servletContext = session.getServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    ApplicationContextUtil.set( sessionStore, applicationContext );
  }

  private static void attachCurrentThreadToDisplay( IServletEngine servletEngine ) {
    HttpSession session = ClusterFixture.getFirstSession( servletEngine );
    Display display = ClusterFixture.getSessionDisplay( session );
    getDisplayAdapter( display ).attachThread();
  }

  private static void assertSessionIsIntact( HttpSession session, RWTClient client ) {
    assertTrue( client.getSessionId().startsWith( session.getId() ) );
    ISessionStore sessionStore = ClusterFixture.getSessionStore( session );
    Display display = ClusterFixture.getSessionDisplay( session );
    assertNotNull( sessionStore );
    assertNotNull( display );
    assertSame( sessionStore, getDisplayAdapter( display ).getSessionStore() );
    assertNotNull( sessionStore.getHttpSession() );
    ServletContext servletContext = session.getServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    assertNotNull( applicationContext );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return( ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class ) );
  }

  private static Shell getFirstShell( IServletEngine servletEngine ) {
    HttpSession session = ClusterFixture.getFirstSession( servletEngine );
    Display display = ClusterFixture.getSessionDisplay( session );
    return display.getShells()[ 0 ];
  }

  private void clickCenterButton( int start, int end ) throws IOException {
    for( int i = start; i <= end; i++ ) {
      Response response = client.sendWidgetSelectedRequest( "w5" );
      assertTrue( response.isValidJavascript() );
      String expectedLabelPart = "relocated " + i + "/1";
      String msg = "label update mismatch, missing part: '" + expectedLabelPart + "'";
      assertTrue( msg, response.getContentText().contains( expectedLabelPart ) );
    }
  }
}
