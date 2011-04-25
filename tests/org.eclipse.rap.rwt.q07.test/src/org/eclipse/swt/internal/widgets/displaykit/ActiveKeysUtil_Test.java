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

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


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

  public void testReadKeyBindingEvents_CtrlAltSpace() {
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
    Event event = ( Event )log.get( 0 );
    assertEquals( SWT.KeyDown, event.type );
    assertEquals( null, event.widget );
    assertEquals( 32, event.keyCode );
    assertEquals( ' ', event.character );
    assertEquals( SWT.CTRL | SWT.ALT, event.stateMask );
  }

  public void testReadKeyBindingEvents_AltShiftF5() {
    final ArrayList log = new ArrayList();
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    } );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, "w1" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "116" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "alt,shift" );

    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, log.size() );
    Event event = ( Event )log.get( 0 );
    assertEquals( SWT.KeyDown, event.type );
    assertEquals( null, event.widget );
    assertEquals( SWT.F5, event.keyCode );
    assertEquals( 0, event.character );
    assertEquals( SWT.SHIFT | SWT.ALT, event.stateMask );
  }

  public void testWriteKeyBindings() {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] {
      "ALT+'",
      "CTRL+INSERT",
      "CTRL+E",
      "SHIFT+CTRL+ALT+1",
      "CTRL+ALT+.",
      "F1"
    };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );
    ActiveKeysUtil.writeActiveKeys( display );

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

  public void testWriteKeyBindings_UnrecognizedKey() {
    Fixture.fakeNewRequest();

    String[] keyBindings = new String[] { "ALT+ABC" };
    display.setData( RWT.ACTIVE_KEYS, keyBindings );
    
    try {
      ActiveKeysUtil.writeActiveKeys( display );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_UnrecognizedModifier() {
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new String[] { "ALT+CONTROL+A" } );
    
    try {
      ActiveKeysUtil.writeActiveKeys( display );
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
      ActiveKeysUtil.writeActiveKeys( display );
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
      ActiveKeysUtil.writeActiveKeys( display );
      fail( "Should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testWriteKeyBindings_InvalidKeyBindingListClass() {
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new Integer( 123 ) );

    try {
      ActiveKeysUtil.writeActiveKeys( display );
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
    ActiveKeysUtil.writeActiveKeys( display );

    String expected = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings({});";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteKeyBindings_EmptyKeyBindingList() {
    Fixture.markInitialized( display );
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+E" } );
    Fixture.preserveWidgets();
    Fixture.fakeNewRequest();

    display.setData( RWT.ACTIVE_KEYS, new String[ 0 ] );
    ActiveKeysUtil.writeActiveKeys( display );

    String expected = "org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings({});";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

}
