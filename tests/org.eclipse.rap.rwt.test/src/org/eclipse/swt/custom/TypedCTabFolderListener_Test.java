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
package org.eclipse.swt.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class TypedCTabFolderListener_Test {

  private Event event;

  @Before
  public void setUp() {
    event = new Event();
    event.widget = mock( Widget.class );
  }

  @Test
  public void testGetEventListener() {
    SWTEventListener wrappedListener = mock( SWTEventListener.class );
    TypedCTabFolderListener tabFolderListener = new TypedCTabFolderListener( wrappedListener );

    assertSame( wrappedListener, tabFolderListener.getEventListener() );
  }

  @Test
  public void testHandleEventWithUnknownEventType() {
    SWTEventListener wrappedListener = mock( SWTEventListener.class );
    TypedCTabFolderListener tabFolderListener = new TypedCTabFolderListener( wrappedListener );
    event.type = 12345;

    tabFolderListener.handleEvent( event );

    verifyZeroInteractions( wrappedListener );
  }

  @Test
  public void testHandleEventForClose() {
    CTabFolder2Listener tabFolderListener = mock( CTabFolder2Listener.class );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_CLOSE;
    event.doit = true;
    event.item = mock( Item.class );

    typedListener.handleEvent( event );

    ArgumentCaptor<CTabFolderEvent> captor = ArgumentCaptor.forClass( CTabFolderEvent.class );
    verify( tabFolderListener ).close( captor.capture() );
    assertEquals( event.item, captor.getValue().item );
    assertTrue( captor.getValue().doit );
  }

  @Test
  public void testHandleEventForVetoedClose() {
    CTabFolder2Listener tabFolderListener = spy( new CTabFolder2Adapter() {
      @Override
      public void close( CTabFolderEvent event ) {
        event.doit = false;
      }
    } );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_CLOSE;
    event.doit = true;

    typedListener.handleEvent( event );

    verify( tabFolderListener ).close( any( CTabFolderEvent.class ) );
    assertFalse( event.doit );
  }

  @Test
  public void testHandleEventForMinimize() {
    CTabFolder2Listener tabFolderListener = mock( CTabFolder2Listener.class );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_MINIMIZE;

    typedListener.handleEvent( event );

    verify( tabFolderListener ).minimize( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleEventForMaximize() {
    CTabFolder2Listener tabFolderListener = mock( CTabFolder2Listener.class );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_MAXIMIZE;

    typedListener.handleEvent( event );

    verify( tabFolderListener ).maximize( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleEventForRestore() {
    CTabFolder2Listener tabFolderListener = mock( CTabFolder2Listener.class );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_RESTORE;

    typedListener.handleEvent( event );

    verify( tabFolderListener ).restore( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleEventForShowList() {
    CTabFolder2Listener tabFolderListener = mock( CTabFolder2Listener.class );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_SHOW_LIST;
    event.x = 1;
    event.y = 2;
    event.width = 3;
    event.height = 3;
    event.doit = true;

    typedListener.handleEvent( event );

    ArgumentCaptor<CTabFolderEvent> captor = ArgumentCaptor.forClass( CTabFolderEvent.class );
    verify( tabFolderListener ).showList( captor.capture() );
    assertTrue( event.doit == captor.getValue().doit );
    assertEquals( event.x, captor.getValue().x );
    assertEquals( event.y, captor.getValue().y );
    assertEquals( event.width, captor.getValue().width );
    assertEquals( event.height, captor.getValue().height );
  }

  @Test
  public void testHandleEventForVetoedShowList() {
    CTabFolder2Listener tabFolderListener = spy( new CTabFolder2Adapter() {
      @Override
      public void showList( CTabFolderEvent event ) {
        event.doit = false;
      }
    } );
    TypedCTabFolderListener typedListener = new TypedCTabFolderListener( tabFolderListener );
    event.type = EventTypes.CTAB_FOLDER_SHOW_LIST;
    event.doit = true;

    typedListener.handleEvent( event );

    verify( tabFolderListener ).showList( any( CTabFolderEvent.class ) );
    assertFalse( event.doit );
  }

}
