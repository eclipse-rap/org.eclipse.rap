/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.treekit.TreeOperationHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public class KeyEvent_Test {

  private Display display;
  private Shell shell;
  private List<Object> events;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    events = new LinkedList<Object>();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.time = 4325;
    event.stateMask = 23;
    event.keyCode = 42;
    event.character = 'f';
    event.doit = true;
    event.data = new Object();

    KeyEvent keyEvent = new KeyEvent( event );

    EventTestHelper.assertFieldsEqual( keyEvent, event );
  }

  @Test
  public void testKeySelectionEventsOrder() {
    KeyListener keyListener = mock( KeyListener.class );
    SelectionListener selectionListener = mock( SelectionListener.class );
    Tree tree = createTreeWithKeyListener();
    tree.addKeyListener( keyListener );
    tree.addSelectionListener( selectionListener );

    Fixture.fakeNewRequest();
    fakeKeyDownRequest( tree, 65, 65 );
    fakeSelectionRequest( tree, tree.getItem( 1 ) );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( keyListener, selectionListener );
    inOrder.verify( keyListener ).keyPressed( any( KeyEvent.class ) );
    inOrder.verify( selectionListener ).widgetSelected( any( SelectionEvent.class ) );
    inOrder.verify( keyListener ).keyReleased( any( KeyEvent.class ) );
  }

  @Test
  public void testKeyTreeEventsOrder() {
    KeyListener keyListener = mock( KeyListener.class );
    TreeListener treeListener = mock( TreeListener.class );
    Tree tree = createTreeWithKeyListener();
    Fixture.fakeNewRequest();
    fakeKeyDownRequest( tree, 65, 65 );
    fakeTreeRequest( tree.getItem( 1 ) );

    tree.addKeyListener( keyListener );
    tree.addTreeListener( treeListener );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( keyListener, treeListener );
    inOrder.verify( keyListener ).keyPressed( any( KeyEvent.class ) );
    inOrder.verify( treeListener ).treeExpanded( any( TreeEvent.class ) );
    inOrder.verify( keyListener ).keyReleased( any( KeyEvent.class ) );
  }

  @Test
  public void testKeyHelpEventsOrder() {
    KeyListener keyListener = mock( KeyListener.class );
    HelpListener helpListener = mock( HelpListener.class );
    Tree tree = createTreeWithKeyListener();
    Fixture.fakeNewRequest();
    fakeKeyDownRequest( tree, 65, 65 );
    fakeHelpRequest( tree );

    tree.addKeyListener( keyListener );
    tree.addHelpListener( helpListener );
    Fixture.readDataAndProcessAction( display );

    InOrder inOrder = inOrder( keyListener, helpListener );
    inOrder.verify( keyListener ).keyPressed( any( KeyEvent.class ) );
    inOrder.verify( helpListener ).helpRequested( any( HelpEvent.class ) );
    inOrder.verify( keyListener ).keyReleased( any( KeyEvent.class ) );
  }

  private Tree createTreeWithKeyListener() {
    Tree tree = new Tree( shell, SWT.NONE );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    tree.setSize( 100, 100 );
    for( int i = 0; i < 5; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE);
      for( int j = 0; j < 5; j++ ) {
        new TreeItem( item, SWT.NONE);
      }
    }
    tree.addKeyListener( new LoggingKeyListener( events ) );
    return tree;
  }

  private static void fakeSelectionRequest( Widget widget, Widget item ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( widget ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 parameters );
  }

  private static void fakeTreeRequest( TreeItem item ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( item.getParent() ), "Expand", parameters );
  }

  private static void fakeHelpRequest( Widget widget ) {
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_HELP, null  );
  }

  private static void fakeKeyDownRequest( Widget widget, int keyCode, int charCode ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_KEY_CODE, keyCode )
      .add( ClientMessageConst.EVENT_PARAM_CHAR_CODE, charCode )
      .add( ClientMessageConst.EVENT_PARAM_MODIFIER, "" );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_KEY_DOWN, parameters );
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
