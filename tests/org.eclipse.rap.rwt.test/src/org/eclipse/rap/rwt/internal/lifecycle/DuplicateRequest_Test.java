/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class DuplicateRequest_Test extends TestCase {

  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDisabledButton() {
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
        button.setEnabled( false );
      }
    } );
    shell.open();

    String buttonId = WidgetUtil.getId( button );

    // First request - within this request the button will become disabled
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertFalse( button.getEnabled() );
    assertEquals( 1, events.size() );

    // Second request - simulating a click on the now disabled button
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

  public void testInvisibleButton() {
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
        button.setVisible( false );
      }
    } );
    shell.open();

    String buttonId = WidgetUtil.getId( button );

    // First request - within this request the button will become disabled
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertFalse( button.getVisible() );
    assertEquals( 1, events.size() );

    // Second request - simulating a click on the now disabled button
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

  public void testDisposedButton() {
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
        button.dispose();
      }
    } );
    shell.open();

    String buttonId = WidgetUtil.getId( button );

    // First request - within this request the button will become disabled
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertTrue( button.isDisposed() );
    assertEquals( 1, events.size() );

    // Second request - simulating a click on the now disabled button
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

  public void testButtonOpensModalShell() {
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    final Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
        Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
        dialog.setSize( 100, 100 );
        dialog.open();
      }
    } );
    shell.open();

    String buttonId = WidgetUtil.getId( button );

    // First request - within this request a modal dialog will be opened
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 1, events.size() );

    // Second request - simulates click on the button that should not be
    // available anymore as it is blocked by the modal dialog
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

  public void testFocusOutOpensModalShell() {
    final java.util.List<TypedEvent> events = new ArrayList<TypedEvent>();
    final Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Text text = new Text( shell, SWT.NONE );
    text.addFocusListener( new FocusAdapter() {
      @Override
      public void focusLost( FocusEvent event ) {
        events.add( event );
        Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
        dialog.setSize( 100, 100 );
        dialog.open();
      }
    } );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
      }
    } );
    shell.open();

    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Within this request a focusLost and widgetSelected (for the button)
    // is sent. The focusList listener opens a modal shell, thus the event on
    // button must not be executed
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( displayId + ".focusControl", buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
    assertEquals( FocusEvent.class, events.get( 0 ).getClass() );
    FocusEvent event = ( FocusEvent )events.get( 0 );
    assertSame( text, event.widget );
  }

  public void testCloseClosedShell() {
    final java.util.List<ShellEvent> events = new ArrayList<ShellEvent>();
    final Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setSize( 100, 100 );
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent event ) {
        events.add( event );
      }
    } );
    shell.open();

    String shellId = WidgetUtil.getId( shell );

    // First request - simulates click on close button of shell
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
    assertTrue( shell.isDisposed() );

    // Second request - simulates click on close button of shell that was
    // already closed by the first request
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

  public void testNestedModalShell() {
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    final Shell shell1 = new Shell( display, SWT.APPLICATION_MODAL );
    shell1.setSize( 100, 100 );
    shell1.open();

    Shell shell2 = new Shell( display, SWT.APPLICATION_MODAL );
    shell2.setSize( 100, 100 );
    Button button = new Button( shell2, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        events.add( event );
      }
    } );
    shell2.open();

    String buttonId = WidgetUtil.getId( button );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, events.size() );
  }

}
