/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.browser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser.TypedBrowserListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.mockito.ArgumentCaptor;


public class TypedBrowserListener_Test extends TestCase {
  
  private Event event;

  public void testGetEventListener() {
    SWTEventListener wrappedListener = mock( SWTEventListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( wrappedListener );
    
    assertSame( wrappedListener, browserListener.getEventListener() );
  }
  
  public void testHandleEventWithUnknownEventType() {
    SWTEventListener wrappedListener = mock( SWTEventListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( wrappedListener );
    event.type = 123456;

    browserListener.handleEvent( event );
    
    verifyZeroInteractions( wrappedListener );
  }
  
  public void testHandleEventWithLocationChanging() {
    LocationListener locationListener = mock( LocationListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( locationListener );
    event.type = EventTypes.LOCALTION_CHANGING;
    event.text = "location";
    event.detail = SWT.TOP;
    
    browserListener.handleEvent( event );
    
    ArgumentCaptor<LocationEvent> captor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( locationListener ).changing( captor.capture() );
    assertEquals( event.text, captor.getValue().location );
    assertTrue( captor.getValue().doit );
    assertTrue( captor.getValue().top );
  }
  
  public void testHandleEventWithVetoedLocationChanging() {
    LocationListener locationListener = new LocationAdapter() {
      @Override
      public void changing( LocationEvent event ) {
        event.doit = false;
      }
    };
    TypedBrowserListener browserListener = new TypedBrowserListener( locationListener );
    event.type = EventTypes.LOCALTION_CHANGING;
    event.text = "location";
    
    browserListener.handleEvent( event );

    assertFalse( event.doit );
  }
  
  public void testHandleEventWithLocationChanged() {
    LocationListener locationListener = mock( LocationListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( locationListener );
    event.type = EventTypes.LOCALTION_CHANGED;
    event.text = "location";
    event.detail = SWT.TOP;
    
    browserListener.handleEvent( event );
    
    ArgumentCaptor<LocationEvent> captor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( locationListener ).changed( captor.capture() );
    assertEquals( event.text, captor.getValue().location );
    assertTrue( captor.getValue().doit );
    assertTrue( captor.getValue().top );
  }
  
  public void testHandleEventWithProgressChanged() {
    ProgressListener locationListener = mock( ProgressListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( locationListener );
    event.type = EventTypes.PROGRESS_CHANGED;
    
    browserListener.handleEvent( event );
    
    ArgumentCaptor<ProgressEvent> captor = ArgumentCaptor.forClass( ProgressEvent.class );
    verify( locationListener ).changed( captor.capture() );
    assertEquals( 0, captor.getValue().current );
    assertEquals( 0, captor.getValue().total );
  }
  
  public void testHandleEventWithProgressCompleted() {
    ProgressListener locationListener = mock( ProgressListener.class );
    TypedBrowserListener browserListener = new TypedBrowserListener( locationListener );
    event.type = EventTypes.PROGRESS_COMPLETED;
    
    browserListener.handleEvent( event );
    
    ArgumentCaptor<ProgressEvent> captor = ArgumentCaptor.forClass( ProgressEvent.class );
    verify( locationListener ).completed( captor.capture() );
    assertEquals( 0, captor.getValue().current );
    assertEquals( 0, captor.getValue().total );
  }
  
  @Override
  protected void setUp() throws Exception {
    event = new Event();
    event.widget = mock( Widget.class );
  }
}
