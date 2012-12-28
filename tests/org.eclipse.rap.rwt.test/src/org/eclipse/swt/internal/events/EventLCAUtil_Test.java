/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class EventLCAUtil_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testWidgetDefaultSeletedModifiers() {
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = mock( SelectionListener.class );
    button.addSelectionListener( listener );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "altKey", Boolean.TRUE );
    parameters.put( "ctrlKey", Boolean.FALSE );
    parameters.put( "shiftKey", Boolean.FALSE );
    Fixture.fakeNotifyOperation( getId( button ), EVENT_SELECTION, parameters );
    Fixture.readDataAndProcessAction( button );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertTrue( ( event.stateMask & SWT.ALT ) != 0 );
    assertTrue( ( event.stateMask & SWT.CTRL ) == 0 );
    assertTrue( ( event.stateMask & SWT.SHIFT ) == 0 );
  }

  @Test
  public void testTranslateModifier() {
    int stateMask = EventLCAUtil.translateModifier( "false", "false", "false" );
    assertEquals( 0, stateMask & SWT.MODIFIER_MASK );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertEquals( 0, stateMask & SWT.SHIFT );
    assertEquals( 0, stateMask & SWT.ALT );
    // Shift
    stateMask = EventLCAUtil.translateModifier( "false", "false", "true" );
    assertTrue( ( stateMask & SWT.MODIFIER_MASK ) != 0 );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertTrue( ( stateMask & SWT.SHIFT ) != 0 );
    assertEquals( 0, stateMask & SWT.ALT );
    // Alt
    stateMask = EventLCAUtil.translateModifier( "true", "false", "false" );
    assertTrue( ( stateMask & SWT.MODIFIER_MASK ) != 0 );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertEquals( 0, stateMask & SWT.SHIFT );
    assertTrue( ( stateMask & SWT.ALT ) != 0 );
    // Shift + Ctrl + Alt
    stateMask = EventLCAUtil.translateModifier( "true", "true", "true" );
    assertEquals( SWT.SHIFT | SWT.CTRL | SWT.ALT, stateMask & SWT.MODIFIER_MASK );
    assertEquals( stateMask, stateMask & SWT.MODIFIER_MASK );
  }

  @Test
  public void testTranslateButton() {
    int button = EventLCAUtil.translateButton( 0 );
    assertEquals( 0, button & SWT.BUTTON_MASK );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 1 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertTrue( ( button & SWT.BUTTON1 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 2 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertTrue( ( button & SWT.BUTTON2 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 3 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertTrue( ( button & SWT.BUTTON3 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 4 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertTrue( ( button & SWT.BUTTON4 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 5 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertTrue( ( button & SWT.BUTTON5 ) != 0 );
  }

}
