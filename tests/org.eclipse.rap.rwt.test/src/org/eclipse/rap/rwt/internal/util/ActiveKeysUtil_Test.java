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
package org.eclipse.rap.rwt.internal.util;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;


public class ActiveKeysUtil_Test extends TestCase {

  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveKeyBindingsEmpty() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    Fixture.preserveWidgets();

    assertNull( adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS ) );
  }

  public void testPreserveKeyBindings() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] keyBindings = new String[] { "CTRL+A" };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertTrue( Arrays.equals( keyBindings, preserved ) );
  }

  public void testPreserveActiveKeys() {
    Shell shell = new Shell( display );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );

    String[] keyBindings = new String[] { "CTRL+A" };
    shell.setData( RWT.ACTIVE_KEYS, keyBindings );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertTrue( Arrays.equals( keyBindings, preserved ) );
  }

  public void testKeyBindingsSafeCopy() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] keyBindings = new String[] { "CTRL+A" };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );
    Fixture.preserveWidgets();
    keyBindings[ 0 ] = "CTRL+B";

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_ACTIVE_KEYS );
    assertEquals( "CTRL+A", preserved[ 0 ] );
  }

  public void testPreserveCancelKeysEmpty() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    Fixture.preserveWidgets();

    assertNull( adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS ) );
  }

  public void testPreserveCancelKeys() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] keyBindings = new String[] { "CTRL+A" };
    display.setData( RWT.CANCEL_KEYS, keyBindings );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertTrue( Arrays.equals( keyBindings, preserved ) );
  }

  public void testPreserveCancelKeysOnWidget() {
    Shell shell = new Shell( display );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );

    String[] keyBindings = new String[] { "CTRL+A" };
    shell.setData( RWT.CANCEL_KEYS, keyBindings );
    Fixture.preserveWidgets();

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertTrue( Arrays.equals( keyBindings, preserved ) );
  }

  public void testCancelKeySafeCopy() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );

    String[] keyBindings = new String[] { "CTRL+A" };
    display.setData( RWT.CANCEL_KEYS, keyBindings );
    Fixture.preserveWidgets();
    keyBindings[ 0 ] = "CTRL+B";

    String[] preserved = ( String[] )adapter.getPreserved( ActiveKeysUtil.PROP_CANCEL_KEYS );
    assertEquals( "CTRL+A", preserved[ 0 ] );
  }

  public void testWriteKeyBindings() throws JSONException {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] {
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
    display.setData( RWT.ACTIVE_KEYS, keyBindings );
    ActiveKeysUtil.renderActiveKeys( display );

    String expected
      =   "\"#88\","
        + "\"ALT+#88\","
        + "\"#69\","
        + "\"CTRL+#45\","
        + "\"CTRL+#69\","
        + "\"ALT+CTRL+SHIFT+#49\","
        + "\"ALT+CTRL+#69\","
        + "\"#112\","
        + "\"/\","
        + "\"SHIFT+~\","
        + "\"ALT+CTRL+#\","
        + "\".\","
        + "\",\"";
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    JSONArray activeKeys = ( JSONArray )operation.getProperty( "activeKeys" );
    assertEquals( expected, activeKeys.join( "," ) );
  }

  public void testWriteKeyBindingsOnWidget() throws JSONException {
    Fixture.fakeNewRequest();
    Shell shell = new Shell( display );
    String[] activeKeys = new String[] { "x", "ALT+x", };
    shell.setData( RWT.ACTIVE_KEYS, activeKeys );
    ActiveKeysUtil.renderActiveKeys( shell );

    String expected = "\"#88\",\"ALT+#88\"";
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( shell, "activeKeys" );
    JSONArray renderedKeys = ( JSONArray )operation.getProperty( "activeKeys" );
    assertEquals( expected, renderedKeys.join( "," ) );
  }

  public void testWriteCancelKeys() throws JSONException {
    Fixture.fakeNewRequest();
    String[] activeKeys = new String[] { "x", "ALT+x", };
    display.setData( RWT.CANCEL_KEYS, activeKeys );
    ActiveKeysUtil.renderCancelKeys( display );

    String expected = "\"#88\",\"ALT+#88\"";
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "cancelKeys" );
    JSONArray renderedKeys = ( JSONArray )operation.getProperty( "cancelKeys" );
    assertEquals( expected, renderedKeys.join( "," ) );
  }

  public void testWriteCancelKeysOnWidget() throws JSONException {
    Fixture.fakeNewRequest();
    Shell shell = new Shell( display );
    String[] activeKeys = new String[] { "x", "ALT+x", };
    shell.setData( RWT.CANCEL_KEYS, activeKeys );
    ActiveKeysUtil.renderCancelKeys( shell );

    String expected = "\"#88\",\"ALT+#88\"";
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( shell, "cancelKeys" );
    JSONArray renderedKeys = ( JSONArray )operation.getProperty( "cancelKeys" );
    assertEquals( expected, renderedKeys.join( "," ) );
  }

  public void testWriteKeyBindings_UnrecognizedKey() {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] { "ALT+ABC" };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );

    try {
      ActiveKeysUtil.renderActiveKeys( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_UnrecognizedModifier() {
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new String[] { "ALT+CONTROL+A" } );

    try {
      ActiveKeysUtil.renderActiveKeys( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_EmptyKeyBinding() {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] {
      "CTRL+A",
      "",
      "ALT+INSERT"
    };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );

    try {
      ActiveKeysUtil.renderActiveKeys( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_NullKeyBinding() {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] {
      "CTRL+A",
      null,
      "ALT+INSERT"
    };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );

    try {
      ActiveKeysUtil.renderActiveKeys( display );
      fail( "Should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_InvalidKeyBindingListClass() {
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new Integer( 123 ) );

    try {
      ActiveKeysUtil.renderActiveKeys( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_NullKeyBindingList() {
    Fixture.markInitialized( display );
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+E" } );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, null );
    ActiveKeysUtil.renderActiveKeys( display );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    JSONArray activeKeys = ( JSONArray )operation.getProperty( "activeKeys" );
    assertEquals( 0, activeKeys.length() );
  }

  public void testWriteKeyBindings_EmptyKeyBindingList() {
    Fixture.markInitialized( display );
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+E" } );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new String[ 0 ] );
    ActiveKeysUtil.renderActiveKeys( display );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( "w1", "activeKeys" );
    JSONArray activeKeys = ( JSONArray )operation.getProperty( "activeKeys" );
    assertEquals( 0, activeKeys.length() );
  }

}
