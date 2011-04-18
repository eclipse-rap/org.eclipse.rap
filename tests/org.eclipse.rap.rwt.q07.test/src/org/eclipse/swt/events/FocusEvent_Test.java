/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class FocusEvent_Test extends TestCase {
  
  private Display display;
  private Shell shell;
  private List events;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    shell.open();
    events = new ArrayList();
  }
  
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    Button button = new Button( shell, SWT.PUSH );
    button.addFocusListener( new FocusAdapter() {
      public void focusGained( final FocusEvent event ) {
        events.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.FocusIn, event );
    assertEquals( 1, events.size() );
    FocusEvent focusEvent = ( FocusEvent )events.get( 0 );
    assertSame( button, focusEvent.getSource() );
    assertSame( button, focusEvent.widget );
    assertSame( display, focusEvent.display );
    assertSame( data, focusEvent.data );
    assertEquals( SWT.FocusIn, focusEvent.getID() );
  }

  public void testFocusLost() {
    Control unfocusControl = new Button( shell, SWT.PUSH );
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( new FocusAdapter() {
      public void focusLost( FocusEvent event ) {
        events.add( event );
      }
      public void focusGained( FocusEvent event ) {
        fail( "Unexpected event: focusGained" );
      }
    } );
    Control focusControl = new Button( shell, SWT.PUSH );
    String focusControlId = WidgetUtil.getId( focusControl );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( DisplayUtil.getId( display ) + ".focusControl", focusControlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusLost", focusControlId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    FocusEvent event = ( FocusEvent )events.get( 0 );
    assertEquals( FocusEvent.FOCUS_LOST, event.getID() );
    assertSame( unfocusControl, event.getSource() );
  }
  
  public void testFocusGained() {
    Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( new FocusAdapter() {
      public void focusLost( FocusEvent event ) {
        fail( "Unexpected event: focusLost" );
      }
      public void focusGained( FocusEvent event ) {
        events.add( event );
      }
    } );
    String controlId = WidgetUtil.getId( control );
    
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( DisplayUtil.getId( display ) + ".focusControl", controlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusGained", controlId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    FocusEvent event = ( FocusEvent )events.get( 0 );
    assertEquals( FocusEvent.FOCUS_GAINED, event.getID() );
    assertSame( control, event.getSource() );
  }
}
