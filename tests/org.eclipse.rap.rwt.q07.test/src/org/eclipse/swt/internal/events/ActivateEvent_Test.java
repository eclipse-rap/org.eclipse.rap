/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.*;


public class ActivateEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

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
      public void activated( final ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( final ActivateEvent event ) {
        deactivated[ deactivatedCount[ 0 ] ] = ( Widget )event.getSource();
        deactivatedCount[ 0 ]++;
      }
    } );

    fakeActivateRequestParam( label );
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
      public void activated( final ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( final ActivateEvent event ) {
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
    
    fakeActivateRequestParam( label );
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
    
    final java.util.List log = new ArrayList();
    ActivateEvent.addListener( labelToActivate, new ActivateListener() {
      public void activated( final ActivateEvent event ) {
        log.add( event );
      }
      public void deactivated( final ActivateEvent event ) {
        log.add( event );
      }
    } );
    labelToActivate.forceFocus();
    assertEquals( 1, log.size() );
    ActivateEvent event = ( ActivateEvent )log.get( 0 );
    assertEquals( labelToActivate, event.widget );
    assertEquals( ActivateEvent.ACTIVATED, event.getID() );
  }
  
  public void testUntypedListener() {
    final List log = new ArrayList(); 
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    shell.addListener( SWT.Activate, listener );
    shell.addListener( SWT.Deactivate, listener );
    Control control = new Label( shell, SWT.NONE );
    control.addListener( SWT.Activate, listener );
    control.addListener( SWT.Deactivate, listener );
    // simulated request: activate control -> Activate event fired
    fakeActivateRequestParam( control );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, log.size() );
    Event loggedEvent = ( Event )log.get( 0 );
    assertEquals( SWT.Activate, loggedEvent.type );
    assertSame( control, loggedEvent.widget );
    // simulated request: activate another control -> Deactivate event for 
    // previously activated control is fired, then Activate event for new 
    // control is fired
    log.clear();
    Control newControl = new Label( shell, SWT.NONE );
    newControl.addListener( SWT.Activate, listener );
    fakeActivateRequestParam( newControl );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    loggedEvent = ( Event )log.get( 0 );
    assertEquals( SWT.Deactivate, loggedEvent.type );
    assertSame( control, loggedEvent.widget );
    loggedEvent = ( Event )log.get( 1 );
    assertEquals( SWT.Activate, loggedEvent.type );
  }
  
  public void testShellWithTypedAndUntypedListener() {
    final Event[] untypedEvent = { null };
    final ShellEvent[] typedEvent = { null };
    shell.addListener( SWT.Activate, new Listener() {
      public void handleEvent( final Event event ) {
        untypedEvent[ 0 ] = event;
      }
    } );
    shell.addShellListener( new ShellAdapter() {
      public void shellActivated( final ShellEvent event ) {
        typedEvent[ 0 ] = event;
      }
    } );
    String controlId = WidgetUtil.getId( shell );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED, controlId );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( untypedEvent[ 0 ] );
    assertNotNull( typedEvent[ 0 ] );
    assertEquals( SWT.Activate, untypedEvent[ 0 ].type );
    assertSame( shell, untypedEvent[ 0 ].widget );
    assertSame( shell, typedEvent[ 0 ].widget );
  }

  private void fakeActivateRequestParam( Control control ) {
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, WidgetUtil.getId( control ) );
  }
}
