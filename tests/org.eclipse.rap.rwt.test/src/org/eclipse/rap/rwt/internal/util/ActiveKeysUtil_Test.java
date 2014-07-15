/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ActiveKeysUtil_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveActiveKeys_emptyList() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    Fixture.preserveWidgets();

    assertNull( adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS ) );
  }

  @Test
  public void testPreserveActiveKeys_onDisplay() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] activeKeys = new String[] { "CTRL+A" };
    display.setData( RWT.ACTIVE_KEYS, activeKeys );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertTrue( Arrays.equals( activeKeys, preserved ) );
  }

  @Test
  public void testPreserveActiveKeys_onWidget() {
    Shell shell = new Shell( display );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    WidgetAdapter adapter = WidgetUtil.getAdapter( shell );

    String[] activeKeys = new String[] { "CTRL+A" };
    shell.setData( RWT.ACTIVE_KEYS, activeKeys );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertTrue( Arrays.equals( activeKeys, preserved ) );
  }

  @Test
  public void testActiveKeysSafeCopy() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] activeKeys = new String[] { "CTRL+A" };
    display.setData( RWT.ACTIVE_KEYS, activeKeys );
    Fixture.preserveWidgets();
    activeKeys[ 0 ] = "CTRL+B";

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertEquals( "CTRL+A", preserved[ 0 ] );
  }

  @Test
  public void testPreserveCancelKeys_emptyList() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    Fixture.preserveWidgets();

    assertNull( adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS ) );
  }

  @Test
  public void testPreserveCancelKeys_onDisplay() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] cancelKeys = new String[] { "CTRL+A" };
    display.setData( RWT.CANCEL_KEYS, cancelKeys );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertTrue( Arrays.equals( cancelKeys, preserved ) );
  }

  @Test
  public void testPreserveCancelKeys_onWidget() {
    Shell shell = new Shell( display );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    WidgetAdapter adapter = WidgetUtil.getAdapter( shell );

    String[] cancelKeys = new String[] { "CTRL+A" };
    shell.setData( RWT.CANCEL_KEYS, cancelKeys );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertTrue( Arrays.equals( cancelKeys, preserved ) );
  }

  @Test
  public void testCancelKeySafeCopy() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] cancelKeys = new String[] { "CTRL+A" };
    display.setData( RWT.CANCEL_KEYS, cancelKeys );
    Fixture.preserveWidgets();
    cancelKeys[ 0 ] = "CTRL+B";

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertEquals( "CTRL+A", preserved[ 0 ] );
  }

  @Test
  public void testPreserveMnemonicActivator() {
    Fixture.markInitialized( display );
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );

    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL" );
    Fixture.preserveWidgets();

    String preserved = ( String )adapter.getPreserved( ActiveKeysUtil.PROP_MNEMONIC_ACTIVATOR );
    assertEquals( "ALT+CTRL+", preserved );
  }

  @Test
  public void testRenderActiveKeys_onDisplay() {
    Fixture.fakeNewRequest();
    String[] activeKeys = new String[] {
      "x",
      "ALT+x",
      "E",
      "CTRL+INSERT",
      "CTRL+E",
      "SHIFT+CTRL+ALT+1",
      "CTRL+ALT+E",
      "F1",
      "/",
      "SHIFT+~",
      "CTRL+ALT+#",
      ".",
      ","
    };
    display.setData( RWT.ACTIVE_KEYS, activeKeys );

    ActiveKeysUtil.renderActiveKeys( display );

    JsonArray expected = new JsonArray()
      .add( "#88" )
      .add( "ALT+#88" )
      .add( "#69" )
      .add( "CTRL+#45" )
      .add( "CTRL+#69" )
      .add( "ALT+CTRL+SHIFT+#49" )
      .add( "ALT+CTRL+#69" )
      .add( "#112" )
      .add( "/" )
      .add( "SHIFT+~" )
      .add( "ALT+CTRL+#" )
      .add( "." )
      .add( "," );
    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    assertEquals( expected, operation.getProperties().get( "activeKeys" ) );
  }

  @Test
  public void testRenderActiveKeys_onWidget() {
    Fixture.fakeNewRequest();
    Shell shell = new Shell( display );
    shell.setData( RWT.ACTIVE_KEYS, new String[] { "x", "ALT+x", } );

    ActiveKeysUtil.renderActiveKeys( shell );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( shell, "activeKeys" );
    JsonArray expected = new JsonArray().add( "#88" ).add( "ALT+#88" );
    assertEquals( expected, operation.getProperties().get( "activeKeys" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderActiveKeys_unrecognizedKey() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "ALT+ABC" } );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderActiveKeys_modifiersOnly() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "ALT+CTRL+" } );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderActiveKeys_unrecognizedModifier() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "ALT+CONTROL+A" } );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderActiveKeys_emptyKeyBinding() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+A", "", "ALT+INSERT" } );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test( expected = NullPointerException.class )
  public void testRenderActiveKeys_nullKey() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+A", null, "ALT+INSERT" } );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderActiveKeys_invalidActiveKeysListClass() {
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new Integer( 123 ) );

    ActiveKeysUtil.renderActiveKeys( display );
  }

  @Test
  public void testRenderActiveKeys_nullActiveKeysList() {
    Fixture.markInitialized( display );
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+E" } );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, null );

    ActiveKeysUtil.renderActiveKeys( display );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    JsonArray activeKeys = ( JsonArray )operation.getProperties().get( "activeKeys" );
    assertEquals( 0, activeKeys.size() );
  }

  @Test
  public void testRenderActiveKeys_emptyActiveKeysList() {
    Fixture.markInitialized( display );
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+E" } );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[ 0 ] );

    ActiveKeysUtil.renderActiveKeys( display );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    JsonArray activeKeys = ( JsonArray )operation.getProperties().get( "activeKeys" );
    assertEquals( 0, activeKeys.size() );
  }

  @Test
  public void testRenderActiveKeys_plusKey() {
    Fixture.markInitialized( display );
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "+" } );

    ActiveKeysUtil.renderActiveKeys( display );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    assertEquals( new JsonArray().add( "+" ), operation.getProperties().get( "activeKeys" ) );
  }

  /*
   * 438277: IllegalArgumentException when using CTRL + (ZoomIn) as key binding
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=438277
   */
  @Test
  public void testRenderActiveKeys_plusKeyWithModifiers() {
    Fixture.markInitialized( display );
    Fixture.fakeNewRequest();
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL++" } );

    ActiveKeysUtil.renderActiveKeys( display );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    assertEquals( new JsonArray().add( "CTRL++" ), operation.getProperties().get( "activeKeys" ) );
  }

  @Test
  public void testRenderCancelKeys_onDisplay() {
    Fixture.fakeNewRequest();
    display.setData( RWT.CANCEL_KEYS, new String[] { "x", "ALT+x", } );

    ActiveKeysUtil.renderCancelKeys( display );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "cancelKeys" );
    JsonArray expected = new JsonArray().add( "#88" ).add( "ALT+#88" );
    assertEquals( expected, operation.getProperties().get( "cancelKeys" ) );
  }

  @Test
  public void testRenderCancelKeys_onWidget() {
    Fixture.fakeNewRequest();
    Shell shell = new Shell( display );
    shell.setData( RWT.CANCEL_KEYS, new String[] { "x", "ALT+x", } );

    ActiveKeysUtil.renderCancelKeys( shell );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( shell, "cancelKeys" );
    JsonArray expected = new JsonArray().add( "#88" ).add( "ALT+#88" );
    assertEquals( expected, operation.getProperties().get( "cancelKeys" ) );
  }

  @Test
  public void testRenderMnemonicActivator() {
    Fixture.fakeNewRequest();
    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL" );

    ActiveKeysUtil.renderMnemonicActivator( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "ALT+CTRL+", message.findSetProperty( "w1", "mnemonicActivator" ).asString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderMnemonicActivator_notString() {
    Fixture.fakeNewRequest();
    display.setData( RWT.MNEMONIC_ACTIVATOR, Boolean.TRUE );

    ActiveKeysUtil.renderMnemonicActivator( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderMnemonicActivator_notOnlyModifiers() {
    Fixture.fakeNewRequest();
    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL+1" );

    ActiveKeysUtil.renderMnemonicActivator( display );
  }

}
