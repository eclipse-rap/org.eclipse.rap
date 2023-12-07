/*******************************************************************************
 * Copyright (c) 2002, 2016 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_HELP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_CHAR_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_KEY_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_MODIFIER;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_TRAVERSE;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.Fixture.getProtocolMessage;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.shellkit.ShellOperationHandler;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ControlLCAUtil_Test {

  private static final JsonArray RED_JSON = JsonArray.readFrom( "[255, 0, 0, 255]" );
  private static final JsonArray RED_ALPHA_JSON = JsonArray.readFrom( "[255, 0, 0, 128]" );
  private static final JsonArray TRANSPARENT_JSON = JsonArray.readFrom( "[0, 0, 0, 0]" );

  private Display display;
  private Shell shell;
  private Control control;
  private Color red;
  private Color redAlpha;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    red = display.getSystemColor( SWT.COLOR_RED );
    redAlpha = new Color( display, 255, 0, 0, 128 );
    shell = new Shell( display );
    getRemoteObject( shell ).setHandler( new ShellOperationHandler( shell ) );
    control = new Button( shell, SWT.PUSH );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.tearDown();
  }

  @Test
  public void testRenderChildren_initial() {
    ControlLCAUtil.renderChanges( shell );

    JsonArray expected = new JsonArray().add( getId( control ) );
    assertEquals( expected, getProtocolMessage().findSetProperty( shell, "children" ) );
  }

  @Test
  public void testRenderChildren_initialOnControl() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "children" ) );
  }

  @Test
  public void testRenderChildren_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    Button button = new Button( shell, SWT.PUSH );
    ControlLCAUtil.renderChanges( shell );

    JsonArray expected = new JsonArray().add( getId( control ) ).add( getId( button ) );
    assertEquals( expected, getProtocolMessage().findSetProperty( shell, "children" ) );
  }

  @Test
  public void testRenderChildren_changedOrder() {
    Fixture.markInitialized( control );
    Button button = new Button( shell, SWT.PUSH );
    Fixture.clearPreserved();

    control.moveBelow( button );
    ControlLCAUtil.renderChanges( shell );

    JsonArray expected = new JsonArray().add( getId( button ) ).add( getId( control ) );
    assertEquals( expected, getProtocolMessage().findSetProperty( shell, "children" ) );
  }

  @Test
  public void testRenderChildren_unchanged() {
    Fixture.markInitialized( shell );
    new Button( shell, SWT.PUSH );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( shell );

    assertNull( getProtocolMessage().findSetOperation( shell, "children" ) );
  }

  @Test
  public void testRenderBounds_initial_withZeroBounds() {
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[ 0, 0, 0, 0 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "bounds" ) );
  }

  @Test
  public void testRenderBounds_initial_withNotZeroBounds() {
    control.setBounds( 10, 20, 100, 200 );

    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[ 10, 20, 100, 200 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "bounds" ) );
  }

  @Test
  public void testRenderBounds_changed() {
    Fixture.markInitialized( control );

    control.setBounds( 1, 2, 3, 4 );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[ 1, 2, 3, 4 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "bounds" ) );
  }

  @Test
  public void testRenderBounds_changedTwice() {
    Fixture.markInitialized( control );

    control.setBounds( 1, 2, 3, 4 );
    control.setBounds( 2, 3, 4, 5 );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[ 2, 3, 4, 5 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "bounds" ) );
  }

  @Test
  public void testRenderBounds_unchanged() {
    Fixture.markInitialized( control );

    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  @Test
  public void testRenderBounds_changedBackToOriginalValue() {
    control.setBounds( 1, 2, 3, 4 );
    Fixture.clearPreserved();
    Fixture.markInitialized( control );

    control.setBounds( 2, 3, 4, 5 );
    control.setBounds( 1, 2, 3, 4 );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  @Test
  public void testRenderTabIndex_initial() {
    ControlLCAUtil.renderChanges( shell );
    ControlLCAUtil.renderChanges( control );

    assertEquals( 1, getProtocolMessage().findSetProperty( control, "tabIndex" ).asInt() );
  }

  @Test
  public void testRenderTabIndex_disabled() {
    shell.setTabList( new Control[ 0 ] );

    ControlLCAUtil.renderChanges( shell );
    ControlLCAUtil.renderChanges( control );

    assertEquals( -1, getProtocolMessage().findSetProperty( control, "tabIndex" ).asInt() );
  }

  @Test
  public void testRenderTabIndex_nonFocusableControl() {
    control = new Composite( shell, SWT.NO_FOCUS );

    ControlLCAUtil.renderChanges( shell );
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "tabIndex" ) );
  }

  @Test
  public void testRenderTabIndex_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    shell.setTabList( new Control[] { new Button( shell, SWT.PUSH ), control } );
    ControlLCAUtil.renderChanges( shell ); // needed for tab index recomputation
    ControlLCAUtil.renderChanges( control );

    assertEquals( 2, getProtocolMessage().findSetProperty( control, "tabIndex" ).asInt() );
  }

  @Test
  public void testRenderTabIndex_unchanged() {
    Fixture.markInitialized( control );
    shell.setTabList( new Control[] { new Button( shell, SWT.PUSH ), control } );
    ControlLCAUtil.renderChanges( shell ); // needed for tab index recomputation

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( shell ); // needed for tab index recomputation
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "tabIndex" ) );
  }

  @Test
  public void testRenderToolTipText_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "toolTip" ) );
  }

  @Test
  public void testRenderToolTipText_unchanged() {
    Fixture.markInitialized( control );
    control.setToolTipText( "foo" );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "toolTip" ) );
  }

  @Test
  public void testRenderToolTipText_unchanged_null() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "toolTip" ) );
  }

  @Test
  public void testRenderToolTipText_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setToolTipText( "foo" );
    ControlLCAUtil.renderChanges( control );

    assertEquals( "foo", getProtocolMessage().findSetProperty( control, "toolTip" ).asString() );
  }

  @Test
  public void testRenderToolTipText_reset() {
    Fixture.markInitialized( control );
    control.setToolTipText( "foo" );
    Fixture.clearPreserved();

    control.setToolTipText( null );
    ControlLCAUtil.renderChanges( control );

    assertEquals( "", getProtocolMessage().findSetProperty( control, "toolTip" ).asString() );
  }

  @Test
  public void testRenderMenu_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "menu" ) );
  }

  @Test
  public void testRenderMenu_unchanged() {
    Fixture.markInitialized( control );
    control.setMenu( new Menu( shell ) );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "menu" ) );
  }

  @Test
  public void testRenderMenu_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setMenu( new Menu( shell ) );
    ControlLCAUtil.renderChanges( control );

    String expected = getId( control.getMenu() );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "menu" ).asString() );
  }

  @Test
  public void testRenderVisible_initial() {
    control.setSize( 16, 16 ); // needed to make the control visible
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "visibility" ) );
  }

  @Test
  public void testRenderVisible_initialWithFalse() {
    control.setSize( 16, 16 ); // needed to make the control visible
    control.setVisible( false );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findSetProperty( control, "visibility" ) );
  }

  @Test
  public void testRenderVisible_unchanged() {
    control.setSize( 16, 16 ); // needed to make the control visible
    Fixture.markInitialized( control );
    control.setVisible( false );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "visibility" ) );
  }

  @Test
  public void testRenderVisible_changed() {
    control.setSize( 16, 16 ); // needed to make the control visible
    Fixture.markInitialized( control );

    control.setVisible( false );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findSetProperty( control, "visibility" ) );
  }

  @Test
  public void testRenderEnabled_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "enabled" ) );
  }

  @Test
  public void testRenderEnabled_unchanged() {
    Fixture.markInitialized( control );
    control.setEnabled( false );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "enabled" ) );
  }

  @Test
  public void testRenderEnabled_changed() {
    Fixture.markInitialized( control );

    control.setEnabled( false );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findSetProperty( control, "enabled" ) );
  }

  @Test
  public void testRenderOrientation_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "direction" ) );
  }

  @Test
  public void testRenderOrientation_unchanged() {
    Fixture.markInitialized( control );
    control.setOrientation( SWT.RIGHT_TO_LEFT );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "direction" ) );
  }

  @Test
  public void testRenderOrientation_changed() {
    Fixture.markInitialized( control );

    control.setOrientation( SWT.RIGHT_TO_LEFT );
    ControlLCAUtil.renderChanges( control );

    assertEquals( "rtl", getProtocolMessage().findSetProperty( control, "direction" ).asString() );
  }

  @Test
  public void testRenderForeground_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "foreground" ) );
  }

  @Test
  public void testRenderForeground_unchanged() {
    Fixture.markInitialized( control );
    control.setForeground( red );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "foreground" ) );
  }

  @Test
  public void testRenderForeground_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setForeground( red );
    ControlLCAUtil.renderChanges( control );

    assertEquals( RED_JSON, getProtocolMessage().findSetProperty( control, "foreground" ) );
  }

  @Test
  public void testRenderForeground_reset() {
    Fixture.markInitialized( control );
    control.setForeground( red );

    Fixture.clearPreserved();
    control.setForeground( null );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.NULL, getProtocolMessage().findSetProperty( control, "foreground" ) );
  }

  @Test
  public void testRenderBackground_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "background" ) );
  }

  @Test
  public void testRenderBackground_unchanged() {
    Fixture.markInitialized( control );
    control.setBackground( red );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "background" ) );
  }

  @Test
  public void testRenderBackground_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setBackground( red );
    ControlLCAUtil.renderChanges( control );

    assertEquals( RED_JSON, getProtocolMessage().findSetProperty( control, "background" ) );
  }

  @Test
  public void testRenderBackground_changed_withAlpha() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setBackground( redAlpha );
    ControlLCAUtil.renderChanges( control );

    assertEquals( RED_ALPHA_JSON, getProtocolMessage().findSetProperty( control, "background" ) );
  }

  @Test
  public void testRenderBackground_changedTransparency() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.getParent().setBackgroundMode( SWT.INHERIT_FORCE );
    ControlLCAUtil.renderChanges( control );

    assertEquals( TRANSPARENT_JSON, getProtocolMessage().findSetProperty( control, "background" ) );
  }

  @Test
  public void testRenderBackground_reset() {
    Fixture.markInitialized( control );
    control.setBackground( red );

    Fixture.clearPreserved();
    control.setBackground( null );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.NULL, getProtocolMessage().findSetProperty( control, "background" ) );
  }

  @Test
  public void testRenderBackgroundImage_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderBackgroundImage_unchanged() throws IOException {
    Fixture.markInitialized( control );
    control.setBackgroundImage( createImage( display, Fixture.IMAGE1 ) );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderBackgroundImage_changed() throws IOException {
    Fixture.markInitialized( control );
    Image image = createImage( display, Fixture.IMAGE1 );

    control.setBackgroundImage( image );
    ControlLCAUtil.renderChanges( control );

    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 58 ).add( 12 );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "backgroundImage" ) );
  }

  @Test
  public void testRenderFont_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "font" ) );
  }

  @Test
  public void testRenderFont_changedNormal() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, false, false]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFont_changedBold() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, true, false]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFont_changedItalic() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC ) );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, false, true]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFont_changedBoldItalic() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setFont( new Font( display, "Arial", 12, SWT.BOLD | SWT.ITALIC ) );
    ControlLCAUtil.renderChanges( control );

    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, true, true]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderFont_unchanged() {
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "font" ) );
  }

  @Test
  public void testRenderFont_reset() {
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.clearPreserved();
    control.setFont( null );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonObject.NULL, getProtocolMessage().findSetProperty( control, "font" ) );
  }

  @Test
  public void testRenderCursor_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "cursor" ) );
  }

  @Test
  public void testRenderCursor_unchanged() {
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "cursor" ) );
  }

  @Test
  public void testRenderCursor_changed() {
    Fixture.markInitialized( control );
    Fixture.clearPreserved();

    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );
    ControlLCAUtil.renderChanges( control );

    assertEquals( "pointer", getProtocolMessage().findSetProperty( control, "cursor" ).asString() );
  }

  @Test
  public void testRenderCursor_reset() {
    Fixture.markInitialized( control );
    control.setCursor( display.getSystemCursor( SWT.CURSOR_HAND ) );

    Fixture.clearPreserved();
    control.setCursor( null );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonObject.NULL, getProtocolMessage().findSetProperty( control, "cursor" ) );
  }

  @Test
  public void testRenderData_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "data" ) );
  }

  @Test
  public void testRenderData_unchanged() {
    Fixture.markInitialized( control );
    registerDataKeys( new String[]{ "foo" } );
    control.setData( "foo", Boolean.TRUE );

    Fixture.clearPreserved();
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findSetOperation( control, "data" ) );
  }

  @Test
  public void testRenderData_changed() {
    Fixture.markInitialized( control );
    registerDataKeys( new String[]{ "foo" } );

    control.setData( "foo", Boolean.TRUE );
    ControlLCAUtil.renderChanges( control );

    JsonObject expected = new JsonObject().add( "foo", true );
    assertEquals( expected, getProtocolMessage().findSetProperty( control, "data" ) );
  }

  @Test
  public void testRenderData_inSameOperationWithOtherProperties() {
    registerDataKeys( new String[]{ "foo" } );
    control.setData( "foo", "bar" );
    control.setEnabled( false );
    control.addListener( SWT.FocusIn, mock( Listener.class ) );

    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    JsonObject properties = operation.getProperties();
    assertNotNull( properties.get( "data" ) );
    assertNotNull( properties.get( "enabled" ) );
  }

  @Test
  public void testRenderListenActivate_initial() {
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "Activate" ) );
    assertNull( message.findListenOperation( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivate_unchanged() {
    Fixture.markInitialized( control );
    control.addListener( SWT.Activate, mock( Listener.class ) );
    control.addListener( SWT.Deactivate, mock( Listener.class ) );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "Activate" ) );
    assertNull( message.findListenOperation( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivate_activate() {
    control.addListener( SWT.Activate, mock( Listener.class ) );

    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.TRUE, getProtocolMessage().findListenProperty( control, "Activate" ) );
  }

  @Test
  public void testRenderListenActivate_deactivate() {
    control.addListener( SWT.Deactivate, mock( Listener.class ) );

    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.TRUE, getProtocolMessage().findListenProperty( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenActivate_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Activate, listener );
    control.addListener( SWT.Deactivate, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.Activate, listener );
    control.removeListener( SWT.Deactivate, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "Activate" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "Deactivate" ) );
  }

  @Test
  public void testRenderListenMouse_initial() {
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "MouseDown" ) );
    assertNull( message.findListenOperation( control, "MouseDoubleClick" ) );
    assertNull( message.findListenOperation( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenMouse_unchanged() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.MouseDown, listener );
    control.addListener( SWT.MouseDoubleClick, listener );
    control.addListener( SWT.MouseUp, listener );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "MouseDown" ) );
    assertNull( message.findListenOperation( control, "MouseDoubleClick" ) );
    assertNull( message.findListenOperation( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenMouse_changed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.MouseDown, listener );
    control.addListener( SWT.MouseDoubleClick, listener );
    control.addListener( SWT.MouseUp, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseDown" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseDoubleClick" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenMouse_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.MouseDown, listener );
    control.addListener( SWT.MouseDoubleClick, listener );
    control.addListener( SWT.MouseUp, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.MouseDown, listener );
    control.removeListener( SWT.MouseDoubleClick, listener );
    control.removeListener( SWT.MouseUp, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseDown" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseDoubleClick" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "MouseUp" ) );
  }

  @Test
  public void testRenderListenFocus_initial() {
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "FocusIn" ) );
    assertNull( message.findListenOperation( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenFocus_unchanged() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.FocusIn, listener );
    control.addListener( SWT.FocusOut, listener );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "FocusIn" ) );
    assertNull( message.findListenOperation( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenFocus_changed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.FocusIn, listener );
    control.addListener( SWT.FocusOut, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "FocusIn" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenFocus_changed_onNotFocusableControl() {
    control = new Label( shell, SWT.NONE );
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.FocusIn, listener );
    control.addListener( SWT.FocusOut, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertNull( message.findListenOperation( control, "FocusIn" ) );
    assertNull( message.findListenOperation( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenFocus_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.FocusIn, listener );
    control.addListener( SWT.FocusOut, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.FocusIn, listener );
    control.removeListener( SWT.FocusOut, listener );
    ControlLCAUtil.renderChanges( control );

    TestMessage message = getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "FocusIn" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( control, "FocusOut" ) );
  }

  @Test
  public void testRenderListenTraverse_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverse_unchanged() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Traverse, listener );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverse_changed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.Traverse, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.TRUE, getProtocolMessage().findListenProperty( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenTraverse_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Traverse, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.Traverse, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findListenProperty( control, "Traverse" ) );
  }

  @Test
  public void testRenderListenMenuDetect_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "MenuDetect" ) );
  }

  @Test
  public void testRenderListenMenuDetect_changed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.MenuDetect, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.TRUE, getProtocolMessage().findListenProperty( control, "MenuDetect" ) );
  }

  @Test
  public void testRenderListenMenuDetect_unchanged() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.MenuDetect, listener );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "MenuDetect" ) );
  }

  @Test
  public void testRenderListenMenuDetect_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.MenuDetect, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.MenuDetect, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findListenProperty( control, "MenuDetect" ) );
  }

  @Test
  public void testRenderListenHelp_initial() {
    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "Help" ) );
  }

  @Test
  public void testRenderListenHelp_changed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    Fixture.clearPreserved();

    control.addListener( SWT.Help, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.TRUE, getProtocolMessage().findListenProperty( control, "Help" ) );
  }

  @Test
  public void testRenderListenHelp_unchanged() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Help, listener );
    Fixture.clearPreserved();

    ControlLCAUtil.renderChanges( control );

    assertNull( getProtocolMessage().findListenOperation( control, "Help" ) );
  }

  @Test
  public void testRenderListenHelp_removed() {
    Fixture.markInitialized( control );
    Listener listener = mock( Listener.class );
    control.addListener( SWT.Help, listener );
    Fixture.clearPreserved();

    control.removeListener( SWT.Help, listener );
    ControlLCAUtil.renderChanges( control );

    assertEquals( JsonValue.FALSE, getProtocolMessage().findListenProperty( control, "Help" ) );
  }

  // TODO [rst] These test are not releated to the class under test. Delete them or move them to
  // a proper place

  @Test
  public void testProcessKeyEventWithDisplayFilter() {
    shell.open();
    Listener listener = mock( Listener.class );
    display.addFilter( SWT.KeyDown, listener );

    fakeNotifyKeyDown( getId( shell ), 65, 97, "" );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener, times( 1 ) ).handleEvent( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'a', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  @Test
  public void testProcessKeyEventWithLowerCaseCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeNotifyKeyDown( getId( shell ), 65, 97, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'a', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  @Test
  public void testProcessKeyEventWithUpperCaseCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeNotifyKeyDown( getId( shell ), 65, 65, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 97, captor.getValue().keyCode );
    assertEquals( 'A', captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  @Test
  public void testProcessKeyEventWithDigitCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeNotifyKeyDown( getId( shell ), 49, 49, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 49, captor.getValue().keyCode );
    assertEquals( 49, captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  @Test
  public void testProcessKeyEventWithPunctuationCharacter() {
    shell.open();
    KeyListener listener = mock( KeyListener.class );
    shell.addKeyListener( listener );

    fakeNotifyKeyDown( getId( shell ), 49, 33, "" );
    Fixture.readDataAndProcessAction( shell );

    ArgumentCaptor<KeyEvent> captor = ArgumentCaptor.forClass( KeyEvent.class );
    verify( listener, times( 1 ) ).keyPressed( captor.capture() );
    assertEquals( 49, captor.getValue().keyCode );
    assertEquals( 33, captor.getValue().character );
    assertEquals( 0, captor.getValue().stateMask );
  }

  @Test
  public void testKeyAndTraverseEvents() {
    final List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Listener listener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    };
    shell.addListener( SWT.Traverse, listener );
    shell.addListener( SWT.KeyDown, listener );
    shell.addListener( SWT.KeyUp, listener );

    fakeNotifyTraverse( getId( shell ), 27, 0, "" );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 3, eventLog.size() );
    Event traverseEvent = eventLog.get( 0 );
    assertEquals( SWT.Traverse, traverseEvent.type );
    assertEquals( SWT.TRAVERSE_ESCAPE, traverseEvent.detail );
    assertTrue( traverseEvent.doit );
    Event downEvent = eventLog.get( 1 );
    assertEquals( SWT.KeyDown, downEvent.type );
    Event upEvent = eventLog.get( 2 );
    assertEquals( SWT.KeyUp, upEvent.type );
  }

  @Test
  public void testProcessHelpEvent() {
    shell.open();
    HelpListener listener = mock( HelpListener.class );
    shell.addHelpListener( listener );

    Fixture.fakeNotifyOperation( getId( shell ), EVENT_HELP, null );
    Fixture.readDataAndProcessAction( shell );

    verify( listener, times( 1 ) ).helpRequested( any( HelpEvent.class) );
  }

  private void fakeNotifyKeyDown( String target, int keyCode, int charCode, String modifier ) {
    JsonObject properties = new JsonObject()
      .add( EVENT_PARAM_KEY_CODE, keyCode )
      .add( EVENT_PARAM_CHAR_CODE, charCode )
      .add( EVENT_PARAM_MODIFIER, modifier );
    Fixture.fakeNotifyOperation( target, EVENT_KEY_DOWN, properties  );
  }

  private void fakeNotifyTraverse( String target, int keyCode, int charCode, String modifier ) {
    JsonObject properties = new JsonObject()
      .add( EVENT_PARAM_KEY_CODE, keyCode )
      .add( EVENT_PARAM_CHAR_CODE, charCode )
      .add( EVENT_PARAM_MODIFIER, modifier );
    Fixture.fakeNotifyOperation( target, EVENT_KEY_DOWN, properties  );
    Fixture.fakeNotifyOperation( target, EVENT_TRAVERSE, properties  );
  }

}
