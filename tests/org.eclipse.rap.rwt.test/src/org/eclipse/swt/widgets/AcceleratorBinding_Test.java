/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AcceleratorBinding_Test {

  private Display display;
  private MenuItem menuItem;
  private AcceleratorBinding acceleratorSupport;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    menuItem = mock( MenuItem.class );
    menuItem.display = display;
    acceleratorSupport = new AcceleratorBinding( menuItem );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetAccelerator_returnsZeroByDefault() {
    assertEquals( 0, acceleratorSupport.getAccelerator() );
  }

  @Test
  public void testSetAccelerator() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    assertEquals( SWT.ALT | 'A', acceleratorSupport.getAccelerator() );
  }

  @Test
  public void testSetAcceleratorTwice_addsDisplayFilterOnce() {
    display = mock( Display.class );
    menuItem.display = display;

    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    verify( display, times( 1 ) ).addFilter( SWT.KeyDown, acceleratorSupport );
  }

  @Test
  public void testSetAcceleratorToZero_removesDisplayFilter() {
    display = mock( Display.class );
    menuItem.display = display;
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    acceleratorSupport.setAccelerator( SWT.NONE );

    verify( display, times( 1 ) ).removeFilter( SWT.KeyDown, acceleratorSupport );
  }

  @Test
  public void testSetAccelerator_addsActiveKey() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    assertTrue( getActiveKeys().contains( "ALT+A" ) );
  }

  @Test
  public void testSetAccelerator_addsCancelKey() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    assertTrue( getCancelKeys().contains( "ALT+A" ) );
  }

  @Test
  public void testSetAccelerator_keepsExistingActiveKey() {
    setActiveKeys( "CTRL+B" );

    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    assertTrue( getActiveKeys().contains( "CTRL+B" ) );
  }

  @Test
  public void testSetAccelerator_keepsExistingCancelKey() {
    setCancelKeys( "CTRL+B" );

    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    assertTrue( getCancelKeys().contains( "CTRL+B" ) );
  }

  @Test
  public void testRelease_callsSetWithZero() {
    acceleratorSupport = spy( acceleratorSupport );

    acceleratorSupport.release();

    verify( acceleratorSupport ).setAccelerator( 0 );
  }

  @Test
  public void testRelease_removesActiveKey() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    acceleratorSupport.release();

    assertTrue( getActiveKeys().isEmpty() );
  }

  @Test
  public void testRelease_removesCancelKey() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    acceleratorSupport.release();

    assertFalse( getActiveKeys().contains( "ALT+A" ) );
  }

  @Test
  public void testRelease_doesNotRemoveExistingActiveKeys() {
    setActiveKeys( "CTRL+S", "CTRL+T" );
    acceleratorSupport.setAccelerator( SWT.CTRL | 'T' );

    acceleratorSupport.release();

    assertTrue( getActiveKeys().contains( "CTRL+T" ) );
  }

  @Test
  public void testRelease_doesNotRemoveExistingCancelKeys() {
    setCancelKeys( "CTRL+S", "CTRL+T" );
    acceleratorSupport.setAccelerator( SWT.CTRL | 'T' );

    acceleratorSupport.release();

    assertTrue( getCancelKeys().contains( "CTRL+T" ) );
  }

  @Test
  public void testKeyDownEvent_triggersHandleAcceleratorActivation() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'A' ) );

    verify( menuItem ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_triggersHandleAcceleratorActivation_withSmallLetterInEvent() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'a' ) );

    verify( menuItem ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_triggersHandleAcceleratorActivation_withSmallLetterInAccelerator() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'a' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'A' ) );

    verify( menuItem ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_doesNotTriggerHandleAcceleratorActivation_onDisabledItem() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'A' ) );

    verify( menuItem, times( 0 ) ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_doesNotTriggerHandleAcceleratorActivation_withDifferentKey() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'B' ) );

    verify( menuItem, times( 0 ) ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_doesNotTriggerHandleAcceleratorActivation_withDifferentModifier() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.CTRL, 'A' ) );

    verify( menuItem, times( 0 ) ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_doesNotTriggerHandleAcceleratorActivation_withDifferentType() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyUp, mockKeyDownEvent( SWT.ALT, 'A' ) );

    verify( menuItem, times( 0 ) ).handleAcceleratorActivation();
  }

  @Test
  public void testKeyDownEvent_doesNotTriggerHandleAcceleratorActivation_withoutAccelerator() {
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );

    display.sendEvent( SWT.KeyDown, mockKeyDownEvent( SWT.ALT, 'A' ) );

    verify( menuItem, times( 0 ) ).handleAcceleratorActivation();
  }

  @Test
  public void testAcceleratorFilter_filtersKeyDownEvent() {
    acceleratorSupport.setAccelerator( SWT.ALT | 'A' );
    when( Boolean.valueOf( menuItem.isEnabled() ) ).thenReturn( Boolean.TRUE );
    Event keyEvent = mockKeyDownEvent( SWT.ALT, 'A' );

    display.sendEvent( SWT.KeyDown, keyEvent );

    assertEquals( SWT.None, keyEvent.type );
  }

  private void setActiveKeys( String... keys ) {
    display.setData( RWT.ACTIVE_KEYS, keys );
  }

  private void setCancelKeys( String... keys ) {
    display.setData( RWT.CANCEL_KEYS, keys );
  }

  private List<String> getActiveKeys() {
    return getKeys( display.getData( RWT.ACTIVE_KEYS ) );
  }

  private List<String> getCancelKeys() {
    return getKeys( display.getData( RWT.CANCEL_KEYS ) );
  }

  @SuppressWarnings( "unchecked" )
  private static List<String> getKeys( Object data ) {
    return data == null ? Collections.EMPTY_LIST : Arrays.asList( ( String[] )data );
  }

  private static Event mockKeyDownEvent( int modifiers, char character ) {
    Event event = mock( Event.class );
    event.stateMask = modifiers;
    event.character = character;
    return event;
  }

}
