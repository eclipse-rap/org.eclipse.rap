/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class KeyEvent_Test extends TestCase {

  private Display display;
  private Shell shell;
  private List<Object> events;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    events = new LinkedList<Object>();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    final List<KeyEvent> log = new ArrayList<KeyEvent>();
    Button button = new Button( shell, SWT.PUSH );
    button.addKeyListener( new KeyAdapter() {
      @Override
      public void keyPressed( KeyEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.stateMask = 23;
    event.keyCode = 42;
    event.character = 'f';
    event.doit = true;
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.KeyDown, event );
    KeyEvent keyEvent = log.get( 0 );
    assertSame( button, keyEvent.getSource() );
    assertSame( button, keyEvent.widget );
    assertSame( display, keyEvent.display );
    assertSame( data, keyEvent.data );
    assertEquals( 23, keyEvent.stateMask );
    assertEquals( 42, keyEvent.keyCode );
    assertEquals( 'f', keyEvent.character );
    assertEquals( true, keyEvent.doit );
    assertEquals( SWT.KeyDown, keyEvent.getID() );
  }

  public void testKeySelectionEventsOrder() {
    Tree tree = createTreeWithKeyListener();
    tree.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        events.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    fakeKeyDownRequest( tree, 65, 65 );
    fakeSelectionRequest( tree, tree.getItem( 1 ) );

    events.clear();
    Fixture.readDataAndProcessAction( display );

    assertEquals( 3, events.size() );
    assertEquals( SWT.KeyDown, ( ( TypedEvent )events.get( 0 ) ).getID() );
    assertEquals( SWT.Selection, ( ( TypedEvent )events.get( 1 ) ).getID() );
    assertEquals( SWT.KeyUp, ( ( TypedEvent )events.get( 2 ) ).getID() );
  }

  public void testKeyTreeEventsOrder() {
    Tree tree = createTreeWithKeyListener();
    tree.addTreeListener( new TreeListener() {
      public void treeExpanded( TreeEvent event ) {
        events.add( event );
      }
      public void treeCollapsed( TreeEvent event ) {
        events.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    fakeKeyDownRequest( tree, 65, 65 );
    fakeTreeRequest( tree.getItem( 1 ) );

    events.clear();
    Fixture.readDataAndProcessAction( display );

    assertEquals( 3, events.size() );
    assertEquals( SWT.KeyDown, ( ( TypedEvent )events.get( 0 ) ).getID() );
    assertEquals( SWT.Expand, ( ( TypedEvent )events.get( 1 ) ).getID() );
    assertEquals( SWT.KeyUp, ( ( TypedEvent )events.get( 2 ) ).getID() );
  }

  public void testKeyHelpEventsOrder() {
    Tree tree = createTreeWithKeyListener();
    tree.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent event ) {
        events.add( event );
      }
    } );
    Fixture.fakeNewRequest( display );
    fakeKeyDownRequest( tree, 65, 65 );
    fakeHelpRequest( tree );

    events.clear();
    Fixture.readDataAndProcessAction( display );

    assertEquals( 3, events.size() );
    assertEquals( SWT.KeyDown, ( ( TypedEvent )events.get( 0 ) ).getID() );
    assertEquals( SWT.Help, ( ( TypedEvent )events.get( 1 ) ).getID() );
    assertEquals( SWT.KeyUp, ( ( TypedEvent )events.get( 2 ) ).getID() );
  }

  private Tree createTreeWithKeyListener() {
    Tree result = new Tree( shell, SWT.NONE );
    result.setSize( 100, 100 );
    for( int i = 0; i < 5; i++ ) {
      TreeItem item = new TreeItem( result, SWT.NONE);
      for( int j = 0; j < 5; j++ ) {
        new TreeItem( item, SWT.NONE);
      }
    }
    result.addKeyListener( new LoggingKeyListener( events ) );
    return result;
  }

  private static void fakeSelectionRequest( Widget widget, Widget item ) {
    String widgetId = WidgetUtil.getId( widget );
    String itemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, widgetId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, itemId );
  }

  private static void fakeTreeRequest( Widget item ) {
    String itemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, itemId );
  }

  private static void fakeHelpRequest( Widget widget ) {
    String widgetId = WidgetUtil.getId( widget );
    Fixture.fakeRequestParam( JSConst.EVENT_HELP, widgetId );
  }

  private static void fakeKeyDownRequest( Widget widget, int keyCode, int charCode ) {
    String widgetId = WidgetUtil.getId( widget );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, widgetId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, String.valueOf( keyCode ) );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, String.valueOf( charCode ) );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
  }

  private static class LoggingKeyListener implements KeyListener {
    private final List<Object> events;
    private LoggingKeyListener( List<Object> events ) {
      this.events = events;
    }
    public void keyPressed( KeyEvent event ) {
      events.add( event );
    }
    public void keyReleased( KeyEvent event ) {
      events.add( event );
    }
  }
}
