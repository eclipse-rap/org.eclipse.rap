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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.*;


public class ActivateEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testListenerOnControl() {
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    Label label = new Label( shell, SWT.NONE );
    ActivateEvent.addListener( label, new ActivateListener() {
      public void activated( ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( ActivateEvent event ) {
        deactivated[ deactivatedCount[ 0 ] ] = ( Widget )event.getSource();
        deactivatedCount[ 0 ]++;
      }
    } );

    fakeActivateEvent( label );
    Fixture.readDataAndProcessAction( display );
    
    assertEquals( 1, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
  }

  public void testListenerOnComposite() {
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    ActivateListener listener = new ActivateListener() {
      public void activated( ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( ActivateEvent event ) {
        deactivated[ deactivatedCount[ 0 ] ] = ( Widget )event.getSource();
        deactivatedCount[ 0 ]++;
      }
    };
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );
    Composite otherComposite = new Composite( shell, SWT.NONE );
    Label otherLabel = new Label( otherComposite, SWT.NONE );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( otherLabel );
    ActivateEvent.addListener( composite, listener );
    ActivateEvent.addListener( label, listener );
    ActivateEvent.addListener( otherComposite, listener );
    ActivateEvent.addListener( otherLabel, listener );

    fakeActivateEvent( label );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
    assertSame( composite, activated[ 1 ] );
    assertEquals( 2, deactivatedCount[ 0 ] );
    assertSame( otherLabel, deactivated[ 0 ] );
    assertSame( otherComposite, deactivated[ 1 ] );
  }

  public void testActivateOnFocus() {
    // This label gets implicitly focused (and thus activated) on Shell#open()
    new Label( shell, SWT.NONE );
    // This is the label to test the ActivateEvent on
    Label labelToActivate = new Label( shell, SWT.NONE );
    shell.open();

    final java.util.List<ActivateEvent> log = new ArrayList<ActivateEvent>();
    ActivateEvent.addListener( labelToActivate, new ActivateListener() {
      public void activated( ActivateEvent event ) {
        log.add( event );
      }
      public void deactivated( ActivateEvent event ) {
        log.add( event );
      }
    } );
    labelToActivate.forceFocus();
    assertEquals( 1, log.size() );
    ActivateEvent event = log.get( 0 );
    assertEquals( labelToActivate, event.widget );
    assertEquals( ActivateEvent.ACTIVATED, event.getID() );
  }

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
    fakeActivateEvent( control );
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
    fakeActivateEvent( newControl );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    loggedEvent = log.get( 0 );
    assertEquals( SWT.Deactivate, loggedEvent.type );
    assertSame( control, loggedEvent.widget );
    loggedEvent = log.get( 1 );
    assertEquals( SWT.Activate, loggedEvent.type );
  }

  public void testShellWithTypedActivateListener() {
    ShellListener listener = mock( ShellListener.class );
    shell.addShellListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_SHELL_ACTIVATED, null );
    Fixture.readDataAndProcessAction( display );

    verify( listener, times( 1 ) ).shellActivated( any( ShellEvent.class ) );
  }

  public void testShellWithUntypedActivateListener() {
    Listener listener = mock( Listener.class );
    shell.addListener( SWT.Activate, listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeNotifyOperation( getId( shell ), ClientMessageConst.EVENT_SHELL_ACTIVATED, null );
    Fixture.readDataAndProcessAction( display );

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }

  private void fakeActivateEvent( Control control ) {
    Fixture.fakeNewRequest( display );
    Fixture.fakeNotifyOperation( getId( control ),
                                 ClientMessageConst.EVENT_CONTROL_ACTIVATED,
                                 null );
  }
}
