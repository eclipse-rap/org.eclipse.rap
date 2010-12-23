/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class FocusEvent_Test extends TestCase {
  
  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }
  
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    final List log = new ArrayList();
    Button button = new Button( shell, SWT.PUSH );
    button.addFocusListener( new FocusAdapter() {
      public void focusGained( final FocusEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.FocusIn, event );
    assertEquals( 1, log.size() );
    FocusEvent focusEvent = ( FocusEvent )log.get( 0 );
    assertSame( button, focusEvent.getSource() );
    assertSame( button, focusEvent.widget );
    assertSame( display, focusEvent.display );
    assertSame( data, focusEvent.data );
    assertEquals( SWT.FocusIn, focusEvent.getID() );
  }

  public void testFocusLost() {
    final StringBuffer log = new StringBuffer();
    final Control unfocusControl = new Button( shell, SWT.PUSH );
    shell.open();
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent event ) {
        log.append( "focusLost" );
        assertSame( unfocusControl, event.getSource() );
      }
      public void focusGained( final FocusEvent e ) {
        fail( "Unexpected event: focusGained" );
      }
    } );
    Control focusControl = new Button( shell, SWT.PUSH );
    String displayId = DisplayUtil.getId( display );
    String focusControlId = WidgetUtil.getId( focusControl );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", focusControlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusLost", 
                              focusControlId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "focusLost", log.toString() );
  }
  
  public void testFocusGained() {
    final StringBuffer log = new StringBuffer();
    shell.open();
    final Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent e ) {
        fail( "Unexpected event: focusLost" );
      }
      public void focusGained( final FocusEvent event ) {
        log.append( "focusGained" );
        assertSame( control, event.getSource() );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String controlId = WidgetUtil.getId( control );
    
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusGained", 
                              controlId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "focusGained", log.toString() );
  }
}
