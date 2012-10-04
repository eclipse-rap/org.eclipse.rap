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
package org.eclipse.swt.widgets;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.SWTEventListener;


public class TypedListener_Test extends TestCase {
  
  public void testGetEventListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    TypedListener typedListener = new TypedListener( listener );
    
    SWTEventListener eventListener = typedListener.getEventListener();
    
    assertSame( listener, eventListener );
  }

  public void testHandleEventWithNonExistingEventType() {
    SWTEventListener listener = mock( SWTEventListener.class );
    
    TypedListener typedListener = new TypedListener( listener );
    typedListener.handleEvent( createEvent( -1 ) );
    
    verifyZeroInteractions( listener );
  }
  
  public void testHandleEventWithMismatchingEventType() {
    TypedListener typedListener = new TypedListener( mock( SelectionListener.class ) );
    Event event = createEvent( SWT.Resize );
    
    try {
      typedListener.handleEvent( event );
      fail();
    } catch( ClassCastException expected ) {
    }
  }
  
  public void testHandleEventForWidgetSelected() {
    SelectionListener selectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        event.x = 1;
        event.y = 2;
        event.doit = true;
      }
    };
    selectionListener = spy( selectionListener );
    
    Event event = notifyListener( SWT.Selection, selectionListener );
    
    verify( selectionListener ).widgetSelected( any( SelectionEvent.class ) );
    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
    assertTrue( event.doit );
  }

  public void testHandleEventForWidgetDefaultSelected() {
    SelectionListener selectionListener = mock( SelectionListener.class );
    TypedListener typedListener = new TypedListener( selectionListener );
    
    Event event = createEvent( SWT.DefaultSelection );
    typedListener.handleEvent( event );
    
    verify( selectionListener ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }
  
  private Event notifyListener( int eventType, SWTEventListener listener ) {
    TypedListener typedListener = new TypedListener( listener );
    Event event = createEvent( eventType );
    typedListener.handleEvent( event );
    return event;
  }

  private Event createEvent( int eventType ) {
    Event result = new Event();
    result.widget = mock( Widget.class );
    result.type = eventType;
    return result;
  }
}
