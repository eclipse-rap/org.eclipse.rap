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
package org.eclipse.swt.internal.widgets.controlkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ControlLCA_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveValues() {
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    control.setBounds( rectangle );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( control );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    control.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    control.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ));
    Fixture.clearPreserved();
    control.setEnabled( true );
    //foreground background font
    Color background = new Color( display, 122, 33, 203 );
    control.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    control.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    control.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( null, control.getToolTipText() );
    Fixture.clearPreserved();
    control.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( "some text", control.getToolTipText() );
  }

  @Test
  public void testMenuDetectListener() {
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    MenuDetectListener listener = mock( MenuDetectListener.class );
    label.addMenuDetectListener( listener );

    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_X, 10 )
      .add( ClientMessageConst.EVENT_PARAM_Y, 30 );
    Fixture.fakeNotifyOperation( getId( label ), ClientMessageConst.EVENT_MENU_DETECT, parameters );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<MenuDetectEvent> captor = ArgumentCaptor.forClass( MenuDetectEvent.class );
    verify( listener, times( 1 ) ).menuDetected( captor.capture() );
    MenuDetectEvent event = captor.getValue();
    assertSame( label, event.getSource() );
    assertEquals( 10, event.x );
    assertEquals( 30, event.y );
  }

  @Test
  public void testRedrawAndDispose() {
    final StringBuilder log = new StringBuilder();
    // Set up test scenario
    Shell shell = new Shell( display );
    Control control = new Composite( shell, SWT.NONE ) {
      private static final long serialVersionUID = 1L;
      @Override
      @SuppressWarnings("unchecked")
      public <T> T getAdapter( Class<T> adapter ) {
        Object result;
        if( adapter == WidgetLifeCycleAdapter.class ) {
          result = new AbstractWidgetLCA() {
            @Override
            public void preserveValues( Widget widget ) {
            }
            @Override
            public void renderChanges( Widget widget )
              throws IOException
            {
            }
            @Override
            public void renderDispose( Widget widget ) throws IOException {
              log.append( "renderDispose" );
            }
            @Override
            public void renderInitialization( Widget widget ) throws IOException {
            }
            public void readData( Widget widget ) {
            }
            @Override
            public void doRedrawFake( Control control ) {
              log.append( "FAILED: doRedrawFake was called" );
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return ( T )result;
      }
    };
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( control );
    // redraw & dispose: must revoke redraw
    control.redraw();
    control.dispose();
    // run life cycle that (in this case) won't call doRedrawFake
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "renderDispose", log.toString() );
  }

  @Test
  public void testRenderNoDisposeWhenParentDisposed() {
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( label );

    shell.dispose();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( shell ) );
    assertNull( message.findDestroyOperation( label ) );
  }

}
