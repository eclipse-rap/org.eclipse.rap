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
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class KeyBindingUtil_Test extends TestCase {

  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveKeyBindings() {
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    String[] value = ( String[] )adapter.getPreserved( KeyBindingUtil.PROP_KEYBINDING_LIST );
    assertNull( value );
    Fixture.clearPreserved();
    String[] keyBindings = new String[] {
      "CTRL+A"
    };
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    Fixture.preserveWidgets();
    adapter = DisplayUtil.getAdapter( display );
    value = ( String[] )adapter.getPreserved( KeyBindingUtil.PROP_KEYBINDING_LIST );
    assertTrue( Arrays.equals( keyBindings, value ) );
    Fixture.clearPreserved();
  }

  public void testKeyBindingsSafeCopy() {
    Fixture.markInitialized( display );
    String[] keyBindings = new String[] {
      "CTRL+A"
    };
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    Fixture.preserveWidgets();
    keyBindings[ 0 ] = "CTRL+B";
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    String[] value = ( String[] )adapter.getPreserved( KeyBindingUtil.PROP_KEYBINDING_LIST );
    assertEquals( "CTRL+A", value[ 0 ] );
  }

  public void testNoKeyEventsForIllegalWidgetId() {
    final ArrayList log = new ArrayList();
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    } );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "32" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "ctrl,alt" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, log.size() );
  }

  public void testReadKeyBindingEvents() {
    final ArrayList log = new ArrayList();
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    } );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, "w1" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "32" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "ctrl,alt" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, log.size() );
    Event event1 = ( Event )log.get( 0 );
    assertEquals( SWT.KeyDown, event1.type );
    assertEquals( null, event1.widget );
    assertEquals( 32, event1.keyCode );
    assertEquals( 32, event1.character );
    assertEquals( SWT.CTRL | SWT.ALT, event1.stateMask );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, "w1" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "116" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "alt,shift" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    Event event2 = ( Event )log.get( 1 );
    assertEquals( SWT.KeyDown, event2.type );
    assertEquals( null, event1.widget );
    assertEquals( SWT.F5, event2.keyCode );
    assertEquals( 0, event2.character );
    assertEquals( SWT.SHIFT | SWT.ALT, event2.stateMask );
  }

  public void testWriteKeyBindings() throws IOException {
    String[] keyBindings = new String[] {
      "ALT+'",
      "CTRL+INSERT",
      "CTRL+E",
      "SHIFT+CTRL+ALT+1",
      "CTRL+ALT+.",
      "F1"
    };
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    KeyBindingUtil.writeKeyBindings( display );
    String expected
      =   "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings({"
        + "\"ALT+222\":true,"
        + "\"CTRL+45\":true,"
        + "\"CTRL+69\":true,"
        + "\"ALT+CTRL+SHIFT+49\":true,"
        + "\"ALT+CTRL+190\":true,"
        + "\"112\":true"
        + "});";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteKeyBindings_UnrecognizedKey() throws IOException {
    String[] keyBindings = new String[] {
      "ALT+ABC"
    };
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    try {
      KeyBindingUtil.writeKeyBindings( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_UnrecognizedModifier() throws IOException {
    String[] keyBindings = new String[] {
      "ALT+CONTROL+A"
    };
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    try {
      KeyBindingUtil.writeKeyBindings( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_EmptyKeyBinding() throws IOException {
    String[] keyBindings = new String[] {
      "CTRL+A",
      "",
      "ALT+INSERT"
    };
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    try {
      KeyBindingUtil.writeKeyBindings( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_NullKeyBinding() throws IOException {
    String[] keyBindings = new String[] {
      "CTRL+A",
      null,
      "ALT+INSERT"
    };
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    try {
      KeyBindingUtil.writeKeyBindings( display );
      fail( "Should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_InvalidKeyBindingListClass() throws IOException {
    Integer keyBindings = new Integer( 123 );
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    try {
      KeyBindingUtil.writeKeyBindings( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_NullKeyBindingList() throws IOException {
    Fixture.markInitialized( display );
    String[] keyBindings = new String[] {
      "CTRL+E"
    };
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();
    display.setData( DisplayUtil.KEYBINDING_LIST, null );
    KeyBindingUtil.writeKeyBindings( display );
    String expected = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings({});";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteKeyBindings_EmptylKeyBindingList() throws IOException {
    Fixture.markInitialized( display );
    String[] keyBindings = new String[] {
      "CTRL+E"
    };
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();
    keyBindings = new String[ 0 ];
    display.setData( DisplayUtil.KEYBINDING_LIST, keyBindings );
    KeyBindingUtil.writeKeyBindings( display );
    String expected = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings({});";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

}
