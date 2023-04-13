/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_ACTIVATE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_CLOSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOVE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_RESIZE;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ShellOperationHandler_Test {

  private Display display;
  private Shell shell;
  private ShellOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = spy( new Shell( display ) );
    shell.setMenuBar( new Menu( shell, SWT.BAR ) );
    handler = new ShellOperationHandler( shell );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetMode_maximized() {
    shell.open();

    handler.handleSet( new JsonObject().add( "mode", "maximized" ) );

    verify( shell ).setMaximized( true );
    verify( shell, never() ).setMinimized( anyBoolean() );
  }

  @Test
  public void testHandleSetMode_minimized() {
    shell.open();

    handler.handleSet( new JsonObject().add( "mode", "minimized" ) );

    verify( shell ).setMinimized( true );
    verify( shell, never() ).setMaximized( anyBoolean() );
  }

  @Test
  public void testHandleSetMode_normal() {
    shell.open();
    shell.setMaximized( true );

    handler.handleSet( new JsonObject().add( "mode", "normal" ) );

    verify( shell ).setMinimized( false );
    verify( shell ).setMaximized( false );
  }

  @Test
  public void testHandleSetBounds() {
    JsonObject properties = new JsonObject()
      .add( "bounds", new JsonArray().add( 10 ).add( 10 ).add( 100 ).add( 100 ) );

    handler.handleSet( properties  );

    assertEquals( new Rectangle( 10, 10, 100, 100 ), shell.getBounds() );
  }

  @Test
  public void testHandleSetActiveControl() {
    Label label = new Label( shell, SWT.NONE );
    Label otherLabel = new Label( shell, SWT.NONE );
    setActiveControl( shell, otherLabel );

    handler.handleSet( new JsonObject().add( "activeControl", getId( label ) ) );

    assertSame( label, getActiveControl( shell ) );
  }

  @Test
  public void testHandleSetActiveControl_notAccessibleControl() {
    Label label = new Label( shell, SWT.NONE );
    label.setEnabled( false );
    Label otherLabel = new Label( shell, SWT.NONE );
    setActiveControl( shell, otherLabel );

    handler.handleSet( new JsonObject().add( "activeControl", getId( label ) ) );

    assertSame( otherLabel, getActiveControl( shell ) );
  }

  @Test
  public void testHandleSetActiveControl_disposedControl() {
    Label label = new Label( shell, SWT.NONE );
    label.dispose();
    Label otherLabel = new Label( shell, SWT.NONE );
    setActiveControl( shell, otherLabel );

    handler.handleSet( new JsonObject().add( "activeControl", getId( label ) ) );

    assertSame( otherLabel, getActiveControl( shell ) );
  }

  @Test
  public void testHandleNotifyClose() {
    handler.handleNotify( EVENT_CLOSE, new JsonObject() );

    verify( shell ).close();
  }

  @Test
  public void testHandleNotifyMove() {
    handler.handleNotify( EVENT_MOVE, new JsonObject() );

    verify( shell, never() ).notifyListeners( eq( SWT.Move ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyResize() {
    handler.handleNotify( EVENT_RESIZE, new JsonObject() );

    verify( shell, never() ).notifyListeners( eq( SWT.Resize ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyActivate() {
    handler.handleNotify( EVENT_ACTIVATE, new JsonObject() );

    assertSame( shell, display.getActiveShell() );
  }

  @Test
  public void testHandleNotifyActivate_invalidateFocus() {
    handler.handleNotify( EVENT_ACTIVATE, new JsonObject() );

    assertTrue( getDisplayAdapter( display ).isFocusInvalidated() );
  }

  @Test
  public void testHandleNotifyMouseDown_skippedOnBorder() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 5 )
      .add( "y", 0 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( shell, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDown_skippedOnTitlebar() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 5 )
      .add( "y", 3 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( shell, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyMouseDown_skippedOnMenubar() {
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 5 )
      .add( "y", 40 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( shell, never() ).notifyListeners( eq( SWT.MouseDown ), any( Event.class ) );
  }

  @Test
  public void testHandleSet_modeBoundsOrder_maximize() {
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.open();

    JsonObject properties = new JsonObject()
      .add( "mode", "maximized" )
      .add( "bounds", new JsonArray().add( 0 ).add( 0 ).add( 800 ).add( 600 ) );
    handler.handleSet( properties  );

    assertEquals( displayBounds, shell.getBounds() );
  }

  @Test
  public void testHandleSet_modeBoundsOrder_restore() {
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.setMaximized( true );
    shell.open();

    JsonObject properties = new JsonObject()
      .add( "mode", "maximized" )
      .add( "bounds", new JsonArray().add( 10 ).add( 10 ).add( 100 ).add( 100 ) );
    handler.handleSet( properties  );

    assertEquals( shellBounds, shell.getBounds() );
  }

  private static Control getActiveControl( Shell shell ) {
    return shell.getAdapter( IShellAdapter.class ).getActiveControl();
  }

  private static void setActiveControl( Shell shell, Control control ) {
    shell.getAdapter( IShellAdapter.class ).setActiveControl( control );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

}
