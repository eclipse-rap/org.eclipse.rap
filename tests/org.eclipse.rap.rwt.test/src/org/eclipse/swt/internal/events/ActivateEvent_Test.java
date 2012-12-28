/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ActivateEvent_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testListenerOnControl() {
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    Label label = new Label( shell, SWT.NONE );
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        if( event.type == SWT.Activate ) {
          activated[ activatedCount[ 0 ] ] = event.widget;
          activatedCount[ 0 ]++;
        } else {
          deactivated[ deactivatedCount[ 0 ] ] = event.widget;
          deactivatedCount[ 0 ]++;
        }
      }
    };
    label.addListener( SWT.Activate, listener );
    label.addListener( SWT.Deactivate, listener );

    fakeActiveControl( label );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
  }

  @Test
  public void testListenerOnComposite() {
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        if( event.type == SWT.Activate ) {
          activated[ activatedCount[ 0 ] ] = event.widget;
          activatedCount[ 0 ]++;
        } else {
          deactivated[ deactivatedCount[ 0 ] ] = event.widget;
          deactivatedCount[ 0 ]++;
        }
      }
    };
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "right" );
    Composite otherComposite = new Composite( shell, SWT.NONE );
    Label otherLabel = new Label( otherComposite, SWT.NONE );
    otherLabel.setText( "wrong" );
    IShellAdapter shellAdapter = shell.getAdapter( IShellAdapter.class );
    shellAdapter.setActiveControl( otherLabel );
    composite.addListener( SWT.Activate, listener );
    composite.addListener( SWT.Deactivate, listener );
    label.addListener( SWT.Activate, listener );
    label.addListener( SWT.Deactivate, listener );
    otherComposite.addListener( SWT.Activate, listener );
    otherComposite.addListener( SWT.Deactivate, listener );
    otherLabel.addListener( SWT.Activate, listener );
    otherLabel.addListener( SWT.Deactivate, listener );

    fakeActiveControl( label );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 2, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
    assertSame( composite, activated[ 1 ] );
    assertEquals( 2, deactivatedCount[ 0 ] );
    assertSame( otherLabel, deactivated[ 0 ] );
    assertSame( otherComposite, deactivated[ 1 ] );
  }

  @Test
  public void testActivateOnFocus() {
    // This label gets implicitly focused (and thus activated) on Shell#open()
    new Label( shell, SWT.NONE );
    // This is the label to test the ActivateEvent on
    Label labelToActivate = new Label( shell, SWT.NONE );
    shell.open();
    Listener activateListener = mock( Listener.class );
    Listener deactivateListener = mock( Listener.class );
    labelToActivate.addListener( SWT.Activate, activateListener );
    labelToActivate.addListener( SWT.Deactivate, deactivateListener );

    labelToActivate.forceFocus();

    verify( deactivateListener, never() ).handleEvent( any( Event.class ) );
    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( activateListener ).handleEvent( captor.capture() );

    assertEquals( labelToActivate, captor.getValue().widget );
  }

  @Test
  public void testUntypedListener() {
    final List<Event> log = new ArrayList<Event>();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        log.add( event );
      }
    };
    shell.addListener( SWT.Activate, listener );
    shell.addListener( SWT.Deactivate, listener );
    Control control = new Label( shell, SWT.NONE );
    control.addListener( SWT.Activate, listener );
    control.addListener( SWT.Deactivate, listener );
    // simulated request: activate control -> Activate event fired
    fakeActiveControl( control );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, log.size() );
    Event loggedEvent = log.get( 0 );
    assertEquals( SWT.Activate, loggedEvent.type );
    assertSame( control, loggedEvent.widget );
    // simulated request: activate another control -> Deactivate event for
    // previously activated control is fired, then Activate event for new
    // control is fired
    log.clear();
    Control newControl = new Label( shell, SWT.NONE );
    newControl.addListener( SWT.Activate, listener );
    fakeActiveControl( newControl );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    loggedEvent = log.get( 0 );
    assertEquals( SWT.Deactivate, loggedEvent.type );
    assertSame( control, loggedEvent.widget );
    loggedEvent = log.get( 1 );
    assertEquals( SWT.Activate, loggedEvent.type );
  }

  private void fakeActiveControl( Control control ) {
    Fixture.fakeNewRequest();
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "activeControl", getId( control ) );
    Fixture.fakeSetOperation( getId( control.getShell() ), properties );
  }
}
