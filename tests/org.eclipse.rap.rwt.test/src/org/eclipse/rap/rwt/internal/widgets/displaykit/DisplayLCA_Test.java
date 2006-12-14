/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.displaykit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rap.rwt.internal.widgets.WidgetAdapterFactory;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.*;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6up;

public class DisplayLCA_Test extends TestCase {

  private AdapterFactory lifeCycleAdapterFactory;
  private WidgetAdapterFactory widgetAdapterFactory;
  private List log = new ArrayList();

  public void testStartup() throws IOException {
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Display display = new Display();
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    // first request: render html to load JavaScript "application"
    lcAdapter.render( display );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "<html" ) != -1 );
    assertTrue( allMarkup.indexOf( "<body" ) != -1 );
    String expected = "var req = org.eclipse.rap.rwt.Request.getInstance();";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    assertTrue( allMarkup.indexOf( "req.setUIRootId( \"w1\" )" ) != -1 );
  }

  public void testRenderProcessing() throws IOException {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell1 = new Shell( display , RWT.NONE );
    Button button1 = new Button( shell1, RWT.PUSH );
    Composite shell2 = new Shell( display , RWT.NONE );
    Button button2 = new Button( shell2, RWT.PUSH );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.render( display );
    assertEquals( 4, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
    assertSame( shell2, log.get( 2 ) );
    assertSame( button2, log.get( 3 ) );
    log.clear();
    new Composite( shell1, RWT.NONE );
    try {
      lcAdapter.render( display );
      String msg = "IOException of the renderer adapter in case of composite"
                   + "should be rethrown.";
      fail( msg );
    } catch( final IOException ioe ) {
      // expected
    }
    assertEquals( 2, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
  }

  public void testProcessActionProcessing() {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell1 = new Shell( display , RWT.NONE );
    Button button1 = new Button( shell1, RWT.PUSH );
    Composite shell2 = new Shell( display , RWT.NONE );
    Button button2 = new Button( shell2, RWT.PUSH );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.processAction( display );
    assertEquals( 4, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
    assertSame( shell2, log.get( 2 ) );
    assertSame( button2, log.get( 3 ) );
  }

  public void testReadDataProcessing() {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Button button = new Button( shell, RWT.PUSH );
    Text text = new Text( shell, RWT.NONE );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.readData( display );
    assertEquals( 3, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( button, log.get( 1 ) );
    assertSame( text, log.get( 2 ) );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    AdapterManager manager = W4TContext.getAdapterManager();
    lifeCycleAdapterFactory = new AdapterFactory() {

      private AdapterFactory factory = new LifeCycleAdapterFactory();

      public Object getAdapter( final Object adaptable, final Class adapter ) {
        Object result = null;
        if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class )
        {
          result = factory.getAdapter( adaptable, adapter );
        } else {
          result = new AbstractWidgetLCA() {

            public void preserveValues( final Widget widget ) {
            }

            public void processAction( final Widget widget ) {
              log.add( widget );
            }

            public void readData( final Widget widget ) {
              log.add( widget );
            }

            public void renderInitialization( final Widget widget )
              throws IOException
            {
            }

            public void renderChanges( final Widget widget ) throws IOException
            {
              if( widget.getClass().equals( Composite.class ) ) {
                throw new IOException();
              }
              log.add( widget );
            }

            public void renderDispose( final Widget widget ) throws IOException
            {
            }
          };
        }
        return result;
      }

      public Class[] getAdapterList() {
        return factory.getAdapterList();
      }
    };
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
    log.clear();
    RWTFixture.registerResourceManager();
  }

  protected void tearDown() throws Exception {
    AdapterManager manager = W4TContext.getAdapterManager();
    manager.deregisterAdapters( lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Widget.class );
    manager.deregisterAdapters( widgetAdapterFactory, Display.class );
    manager.deregisterAdapters( widgetAdapterFactory, Widget.class );
    RWTFixture.deregisterResourceManager();
    Fixture.tearDown();
  }
}
