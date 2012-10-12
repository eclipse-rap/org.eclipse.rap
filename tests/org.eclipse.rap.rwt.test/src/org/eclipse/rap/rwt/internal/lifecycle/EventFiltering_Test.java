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

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


public class EventFiltering_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testAllowProcessingForActivateEventOnInvisibleControl() {
    Control control = new Button( shell, SWT.PUSH );
    control.setEnabled( false );
    Event event = createEvent( control, SWT.Activate );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForDeactivateEventOnInvisibleControl() {
    Control control = new Button( shell, SWT.PUSH );
    control.setEnabled( false );
    Event event = createEvent( control, SWT.Deactivate );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForProgressEventOnInvisibleBrowser() {
    Browser browser = new Browser( shell, SWT.NONE );
    Event event = createEvent( browser, EventTypes.PROGRESS_CHANGED );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }

  public void testAllowProcessingForResizeEventOnDisabledControl() {
    Control control = new Button( shell, SWT.PUSH );
    control.setEnabled( false );
    Event event = createEvent( control, SWT.Resize );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForMoveEventOnDisabledControl() {
    Control control = new Button( shell, SWT.PUSH );
    control.setEnabled( false );
    Event event = createEvent( control, SWT.Move );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForModifyEventOnDisabledText() {
    Text text = new Text( shell, SWT.PUSH );
    text.setEnabled( false );
    Event event = createEvent( text, SWT.Modify );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForVerifyEventOnDisabledText() {
    Text text = new Text( shell, SWT.PUSH );
    text.setEnabled( false );
    Event event = createEvent( text, SWT.Verify );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForPaintEventOnDisabledControl() {
    Control control = new Canvas( shell, SWT.PUSH );
    control.setEnabled( false );
    Event event = createEvent( control, SWT.Paint );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testAllowProcessingForSetDataEventOnDisabledControl() {
    Control text = new Canvas( shell, SWT.PUSH );
    text.setEnabled( false );
    Event event = createEvent( text, SWT.SetData );
    
    boolean accessible = EventUtil.allowProcessing( event );
    
    assertTrue( accessible );
  }
  
  public void testЕventNotFiredOnDisabledButton() {
    Button button = new Button( shell, SWT.PUSH );
    button.setEnabled( false );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );
    shell.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.readDataAndProcessAction( button );

    verify( listener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testЕventNotFiredOnInvisibleButton() {
    Button button = new Button( shell, SWT.PUSH );
    button.setVisible( false );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );
    shell.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.readDataAndProcessAction( button );

    verify( listener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testЕventNotFiredOnDisposedButton() {
    // LCAs not called for disposed widgets
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );
    button.dispose();
    shell.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testEventNotFiredOnBlockedParentShell() {
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );
    shell.open();
    Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
    dialog.setSize( 100, 100 );
    dialog.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testFocusOutOpensModalShell() {
    final java.util.List<TypedEvent> events = new ArrayList<TypedEvent>();
    Text text = new Text( shell, SWT.NONE );
    text.addFocusListener( new FocusAdapter() {
      @Override
      public void focusLost( FocusEvent event ) {
        events.add( event );
        Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
        dialog.setSize( 100, 100 );
        dialog.open();
      }
    } );
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener selectionListener = mock( SelectionListener.class );
    button.addSelectionListener( selectionListener );
    shell.open();

    // Within this request a focusLost and widgetSelected (for the button)
    // is sent. The focusList listener opens a modal shell, thus the event on
    // button must not be executed
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( button ) );
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( 1, events.size() );
    assertEquals( FocusEvent.class, events.get( 0 ).getClass() );
    FocusEvent event = ( FocusEvent )events.get( 0 );
    assertSame( text, event.widget );
    verify( selectionListener, times( 0 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testCloseEventNotFiredOnDisposedShell() {
    // LCAs not called for disposed widgets
    shell.setSize( 100, 100 );
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );
    shell.open();
    shell.dispose();

    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_SHELL_CLOSED, null );
    Fixture.executeLifeCycleFromServerThread( );

    verify( listener, never() ).shellClosed( any( ShellEvent.class ) );
  }

  public void testNestedModalShell() {
    Shell parentShell = new Shell( shell, SWT.APPLICATION_MODAL );
    parentShell.setSize( 100, 100 );
    parentShell.open();
    Shell childShell = new Shell( parentShell, SWT.APPLICATION_MODAL );
    childShell.setSize( 100, 100 );
    Button button = new Button( childShell, SWT.PUSH );
    SelectionListener selectionListener = mock( SelectionListener.class );
    button.addSelectionListener( selectionListener );
    childShell.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_WIDGET_SELECTED, null );
    Fixture.executeLifeCycleFromServerThread();

    verify( selectionListener ).widgetSelected( any( SelectionEvent.class ) );
  }

  private static Event createEvent( Widget widget, int eventType ) {
    Event result = new Event();
    result.widget = widget;
    result.type = eventType;
    return result;
  }

}
