/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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
import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.AsyncExecEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.ButtonEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.DNDEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.DialogEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.ImageEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.ResourcesEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.TimerExecEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterTestHelper;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineCluster;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings("restriction")
public abstract class SessionFailoverTestBase extends TestCase {

  public interface SerializableRunnable extends Runnable, Serializable {
  }

  private IServletEngineCluster cluster;
  private IServletEngine primary;
  private IServletEngine secondary;
  private RWTClient client;

  abstract IServletEngineFactory getServletEngineFactory();

  public void testButtonEntryPoint() throws Exception {
    initializeClient( ButtonEntryPoint.class );
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
    HttpSession primarySession = ClusterTestHelper.getFirstHttpSession( primary );
    assertSessionIsIntact( primarySession, client );
    HttpSession secondarySession = ClusterTestHelper.getFirstHttpSession( secondary );
    assertSessionIsIntact( secondarySession, client );
    assertEquals( primarySession.getId(), secondarySession.getId() );
    // Displays
    Display primaryDisplay = ClusterTestHelper.getSessionDisplay( primarySession );
    Display secondaryDisplay = ClusterTestHelper.getSessionDisplay( secondarySession );
    assertNotSame( primaryDisplay, secondaryDisplay );
  }

  public void testResourcesEntryPoint() throws Exception {
    initializeClient( ResourcesEntryPoint.class );

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
    initializeClient( ImageEntryPoint.class );
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

  public void testAsyncExecEntryPoint() throws Exception {
    initializeClient( AsyncExecEntryPoint.class );
    AsyncExecEntryPoint.scheduleAsyncRunnable( getFirstDisplay( primary ) );

    cluster.removeServletEngine( primary );
    client.changeServletEngine( secondary );
    client.sendDisplayResizeRequest( 100, 100 );

    prepareExamination();
    UISession secondaryUiSession = ClusterTestHelper.getFirstUISession( secondary );
    assertTrue( AsyncExecEntryPoint.wasRunnableExecuted( secondaryUiSession ) );
  }

  public void testSyncExecEntryPoint() throws Exception {
    initializeClient( AsyncExecEntryPoint.class );
    AsyncExecEntryPoint.scheduleSyncRunnable( getFirstDisplay( primary ) );

    cluster.removeServletEngine( primary );
    client.changeServletEngine( secondary );
    client.sendDisplayResizeRequest( 100, 100 );

    prepareExamination();
    UISession secondaryUiSession = ClusterTestHelper.getFirstUISession( secondary );
    assertTrue( AsyncExecEntryPoint.wasRunnableExecuted( secondaryUiSession ) );
  }

  public void testTimerExecEntryPoint() throws Exception {
    initializeClient( TimerExecEntryPoint.class );

    cluster.removeServletEngine( primary );
    prepareExamination( primary );
    disposeDisplay( getFirstDisplay( primary ) );
    client.changeServletEngine( secondary );
    Thread.sleep( TimerExecEntryPoint.TIMER_DELAY * 2 );
    client.sendDisplayResizeRequest( 100, 100 );

    prepareExamination( secondary );
    UISession primaryUiSession = ClusterTestHelper.getFirstUISession( primary );
    UISession secondaryUiSession = ClusterTestHelper.getFirstUISession( secondary );
    assertFalse( TimerExecEntryPoint.wasRunnableExecuted( primaryUiSession ) );
    assertTrue( TimerExecEntryPoint.wasRunnableExecuted( secondaryUiSession ) );
  }

  public void testDNDEntryPoint() throws Exception {
    initializeClient( DNDEntryPoint.class );
    client.sendDragStartRequest( DNDEntryPoint.ID_SOURCE_LABEL );

    cluster.removeServletEngine( primary );
    client.changeServletEngine( secondary );

    client.sendDragFinishedRequest( DNDEntryPoint.ID_SOURCE_LABEL, DNDEntryPoint.ID_TARGET_LABEL );

    prepareExamination( secondary );
    UISession secondaryUiSession = ClusterTestHelper.getFirstUISession( secondary );
    assertTrue( DNDEntryPoint.isDragFinished( secondaryUiSession ) );
    assertTrue( DNDEntryPoint.isDropFinished( secondaryUiSession ) );
  }

  public void testDialogEntryPoint() throws Exception {
    initializeClient( DialogEntryPoint.class );
    cluster.removeServletEngine( primary );
    prepareExamination( primary );
    Shell dialogShell = getFirstDisplay( primary ).getShells()[ 1 ];
    String dialogShellId = WidgetUtil.getId( dialogShell );

    client.changeServletEngine( secondary );
    client.sendShellCloseRequest( dialogShellId );

    prepareExamination( secondary );
    UISession uiSession = ClusterTestHelper.getFirstUISession( secondary );
    assertEquals( 1, getFirstDisplay( secondary ).getShells().length );
    assertEquals( SWT.CANCEL, DialogEntryPoint.getDialogReturnCode( uiSession ) );
  }

  protected void setUp() throws Exception {
    ClusterTestHelper.enableUITests( true );
    cluster = getServletEngineFactory().createServletEngineCluster();
    primary = cluster.addServletEngine();
    secondary = cluster.addServletEngine();
    client = new RWTClient( primary );
  }

  protected void tearDown() throws Exception {
    cluster.stop();
  }

  private void initializeClient( Class<? extends EntryPoint> entryPoint ) throws Exception {
    cluster.start( entryPoint );
    client.sendStartupRequest();
    client.sendInitializationRequest();
  }

  private static void assertEquals( ImageData expected, ImageData actual ) {
    byte[] expectedBytes = getImageBytes( expected );
    byte[] actualBytes = getImageBytes( actual );
    assertEquals( expectedBytes.length, actualBytes.length );
    for( int i = 0; i < actualBytes.length; i++ ) {
      assertEquals( expectedBytes[ i ], actualBytes[ i ] );
    }
  }

  private static byte[] getImageBytes( ImageData imageData ) {
    ImageLoader imageLoader = new ImageLoader();
    imageLoader.data = new ImageData[] { imageData };
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    imageLoader.save( outputStream, SWT.IMAGE_PNG );
    return outputStream.toByteArray();
  }

  private void prepareExamination() {
    prepareExamination( primary );
    prepareExamination( secondary );
  }

  private static void prepareExamination( IServletEngine servletEngine ) {
    attachApplicationContextToSession( servletEngine );
    attachCurrentThreadToDisplay( servletEngine );
  }

  private static void disposeDisplay( final Display display ) {
    if( display != null ) {
      UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
        public void run() {
          display.dispose();
        }
      } );
    }
  }

  private static void attachApplicationContextToSession( IServletEngine servletEngine ) {
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    UISessionImpl uiSession = UISessionImpl.getInstanceFromSession( session );
    ServletContext servletContext = session.getServletContext();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    ApplicationContextUtil.set( uiSession, applicationContext );
  }

  private static void attachCurrentThreadToDisplay( IServletEngine servletEngine ) {
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    getDisplayAdapter( display ).attachThread();
  }

  private static void assertSessionIsIntact( HttpSession session, RWTClient client ) {
    assertTrue( client.getSessionId().startsWith( session.getId() ) );
    UISession uiSession = ClusterTestHelper.getUISession( session );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    assertNotNull( uiSession );
    assertNotNull( display );
    assertSame( uiSession, getDisplayAdapter( display ).getUISession() );
    assertNotNull( uiSession.getHttpSession() );
    ServletContext servletContext = session.getServletContext();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    assertNotNull( applicationContext );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return display.getAdapter( IDisplayAdapter.class );
  }

  private static Display getFirstDisplay( IServletEngine servletEngine ) {
    HttpSession sessioin = ClusterTestHelper.getFirstHttpSession( servletEngine );
    return ClusterTestHelper.getSessionDisplay( sessioin );
  }

  private static Shell getFirstShell( IServletEngine servletEngine ) {
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    return display.getShells()[ 0 ];
  }

  private void clickCenterButton( int start, int end ) throws IOException {
    for( int i = start; i <= end; i++ ) {
      Response response = client.sendWidgetSelectedRequest( "w5" );
      assertTrue( response.isValidJsonResponse() );
      String expectedLabelPart = "relocated " + i + "/1";
      String msg = "label update mismatch, missing part: '" + expectedLabelPart + "'";
      assertTrue( msg, response.getContentText().contains( expectedLabelPart ) );
    }
  }
}
