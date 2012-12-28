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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.internal.SWTEventListener;
import org.junit.Test;


public class TypedListener_Test {

  @Test
  public void testGetEventListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    TypedListener typedListener = new TypedListener( listener );

    SWTEventListener eventListener = typedListener.getEventListener();

    assertSame( listener, eventListener );
  }

  @Test
  public void testHandleEventWithNonExistingEventType() {
    SWTEventListener listener = mock( SWTEventListener.class );

    TypedListener typedListener = new TypedListener( listener );
    typedListener.handleEvent( createEvent( -1 ) );

    verifyZeroInteractions( listener );
  }

  @Test
  public void testHandleEventWithMismatchingEventType() {
    TypedListener typedListener = new TypedListener( mock( SelectionListener.class ) );
    Event event = createEvent( SWT.Resize );

    try {
      typedListener.handleEvent( event );
      fail();
    } catch( ClassCastException expected ) {
    }
  }

  @Test
  public void testHandleEventForWidgetSelectedEven() {
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

  @Test
  public void testHandleEventForWidgetDefaultSelectedEven() {
    SelectionListener selectionListener = mock( SelectionListener.class );
    TypedListener typedListener = new TypedListener( selectionListener );

    Event event = createEvent( SWT.DefaultSelection );
    typedListener.handleEvent( event );

    verify( selectionListener ).widgetDefaultSelected( any( SelectionEvent.class ) );
  }

  @Test
  public void testHandleEventForShellActivatedEven() {
    ShellListener shellListener = mock( ShellListener.class );
    TypedListener typedListener = new TypedListener( shellListener );

    Event event = createEvent( SWT.Activate );
    typedListener.handleEvent( event );

    verify( shellListener ).shellActivated( any( ShellEvent.class ) );
  }

  @Test
  public void testHandleEventForShellDeactivatedEvent() {
    ShellListener shellListener = mock( ShellListener.class );
    TypedListener typedListener = new TypedListener( shellListener );

    Event event = createEvent( SWT.Deactivate );
    typedListener.handleEvent( event );

    verify( shellListener ).shellDeactivated( any( ShellEvent.class ) );
  }

  @Test
  public void testHandleEventForShellClosedEvent() {
    ShellListener shellListener = mock( ShellListener.class );
    TypedListener typedListener = new TypedListener( shellListener );

    Event event = createEvent( SWT.Close );
    typedListener.handleEvent( event );

    verify( shellListener ).shellClosed( any( ShellEvent.class ) );
  }

  @Test
  public void testHandleEventForShellClosedEventWithModifyingListener() {
    ShellListener shellListener = new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent e ) {
        e.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( shellListener );

    Event event = createEvent( SWT.Close );
    typedListener.handleEvent( event );

    assertFalse( event.doit );
  }

  @Test
  public void testHandleEventForArmEvent() {
    ArmListener armListener = mock( ArmListener.class );
    TypedListener typedListener = new TypedListener( armListener );

    Event event = createEvent( SWT.Arm );
    typedListener.handleEvent( event );

    verify( armListener ).widgetArmed( any( ArmEvent.class ) );
  }

  @Test
  public void testHandleEventForTreeCollapsedEvent() {
    TreeListener treeListener = mock( TreeListener.class );
    TypedListener typedListener = new TypedListener( treeListener );

    Event event = createEvent( SWT.Collapse );
    typedListener.handleEvent( event );

    verify( treeListener ).treeCollapsed( any( TreeEvent.class ) );
  }

  @Test
  public void testHandleEventForTreeExpandedEvent() {
    TreeListener treeListener = mock( TreeListener.class );
    TypedListener typedListener = new TypedListener( treeListener );

    Event event = createEvent( SWT.Expand );
    typedListener.handleEvent( event );

    verify( treeListener ).treeExpanded( any( TreeEvent.class ) );
  }

  @Test
  public void testHandleEventForItemCollapsedEvent() {
    ExpandListener expandListener = mock( ExpandListener.class );
    TypedListener typedListener = new TypedListener( expandListener );

    Event event = createEvent( SWT.Collapse );
    typedListener.handleEvent( event );

    verify( expandListener ).itemCollapsed( any( ExpandEvent.class ) );
  }

  @Test
  public void testHandleEventForItemExpandedEvent() {
    ExpandListener expandListener = mock( ExpandListener.class );
    TypedListener typedListener = new TypedListener( expandListener );

    Event event = createEvent( SWT.Expand );
    typedListener.handleEvent( event );

    verify( expandListener ).itemExpanded( any( ExpandEvent.class ) );
  }

  @Test
  public void testHandleEventForWidgetDisposedEvent() {
    DisposeListener disposeListener = mock( DisposeListener.class );
    TypedListener typedListener = new TypedListener( disposeListener );

    Event event = createEvent( SWT.Dispose );
    typedListener.handleEvent( event );

    verify( disposeListener ).widgetDisposed( any( DisposeEvent.class ) );
  }

  @Test
  public void testHandleEventForDragDetectEvent() {
    DragDetectListener dragDetectListener = mock( DragDetectListener.class );
    TypedListener typedListener = new TypedListener( dragDetectListener );

    Event event = createEvent( SWT.DragDetect );
    typedListener.handleEvent( event );

    verify( dragDetectListener ).dragDetected( any( DragDetectEvent.class ) );
  }

  @Test
  public void testHandleEventForFocusGainedEvent() {
    FocusListener focusListener = mock( FocusListener.class );
    TypedListener typedListener = new TypedListener( focusListener );

    Event event = createEvent( SWT.FocusIn );
    typedListener.handleEvent( event );

    verify( focusListener ).focusGained( any( FocusEvent.class ) );
  }

  @Test
  public void testHandleEventForFocusLostEvent() {
    FocusListener focusListener = mock( FocusListener.class );
    TypedListener typedListener = new TypedListener( focusListener );

    Event event = createEvent( SWT.FocusOut );
    typedListener.handleEvent( event );

    verify( focusListener ).focusLost( any( FocusEvent.class ) );
  }

  @Test
  public void testHandleEventForHelpRequestedEvent() {
    HelpListener helpListener = mock( HelpListener.class );
    TypedListener typedListener = new TypedListener( helpListener );

    Event event = createEvent( SWT.Help );
    typedListener.handleEvent( event );

    verify( helpListener ).helpRequested( any( HelpEvent.class ) );
  }

  @Test
  public void testHandleEventForMenuShownEvent() {
    MenuListener menuListener = mock( MenuListener.class );
    TypedListener typedListener = new TypedListener( menuListener );

    Event event = createEvent( SWT.Show );
    typedListener.handleEvent( event );

    verify( menuListener ).menuShown( any( MenuEvent.class ) );
  }

  @Test
  public void testHandleEventForMenuHiddenEvent() {
    MenuListener menuListener = mock( MenuListener.class );
    TypedListener typedListener = new TypedListener( menuListener );

    Event event = createEvent( SWT.Hide );
    typedListener.handleEvent( event );

    verify( menuListener ).menuHidden( any( MenuEvent.class ) );
  }

  @Test
  public void testHandleEventForKeyPressedEvent() {
    KeyListener keyListener = mock( KeyListener.class );
    TypedListener typedListener = new TypedListener( keyListener );

    Event event = createEvent( SWT.KeyDown );
    typedListener.handleEvent( event );

    verify( keyListener ).keyPressed( any( KeyEvent.class ) );
  }

  @Test
  public void testHandleEventForKeyPressedEventWithModifyingListener() {
    KeyListener keyListener = new KeyAdapter() {
      @Override
      public void keyPressed( KeyEvent e ) {
        e.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( keyListener );

    Event event = createEvent( SWT.KeyDown );
    typedListener.handleEvent( event );

    assertFalse( event.doit );
  }

  @Test
  public void testHandleEventForKeyReleasedEvent() {
    KeyListener keyListener = mock( KeyListener.class );
    TypedListener typedListener = new TypedListener( keyListener );

    Event event = createEvent( SWT.KeyUp );
    typedListener.handleEvent( event );

    verify( keyListener ).keyReleased( any( KeyEvent.class ) );
  }

  @Test
  public void testHandleEventForKeyReleasedEventWithModifyingListener() {
    KeyListener keyListener = new KeyAdapter() {
      @Override
      public void keyReleased( KeyEvent e ) {
        e.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( keyListener );

    Event event = createEvent( SWT.KeyUp );
    typedListener.handleEvent( event );

    assertFalse( event.doit );
  }

  @Test
  public void testHandleEventForModifyEvent() {
    ModifyListener modifyListener = mock( ModifyListener.class );
    TypedListener typedListener = new TypedListener( modifyListener );

    Event event = createEvent( SWT.Modify );
    typedListener.handleEvent( event );

    verify( modifyListener ).modifyText( any( ModifyEvent.class ) );
  }

  @Test
  public void testHandleEventForMenuDetectEvent() {
    MenuDetectListener menuDetectListener = mock( MenuDetectListener.class );
    TypedListener typedListener = new TypedListener( menuDetectListener );

    Event event = createEvent( SWT.MenuDetect );
    typedListener.handleEvent( event );

    verify( menuDetectListener ).menuDetected( any( MenuDetectEvent.class ) );
  }

  @Test
  public void testHandleEventForMenuDetectEventWithModifyingListener() {
    MenuDetectListener menuDetectListener = new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent event ) {
        event.x = 1;
        event.y = 2;
        event.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( menuDetectListener );

    Event event = createEvent( SWT.MenuDetect );
    typedListener.handleEvent( event );

    assertEquals( 1, event.x );
    assertEquals( 2, event.y );
    assertFalse( event.doit );
  }

  @Test
  public void testHandleEventForMouseDownEvent() {
    MouseListener mouseListener = mock( MouseListener.class );
    TypedListener typedListener = new TypedListener( mouseListener );

    Event event = createEvent( SWT.MouseDown );
    typedListener.handleEvent( event );

    verify( mouseListener ).mouseDown( any( MouseEvent.class ) );
  }

  @Test
  public void testHandleEventForMouseUpEvent() {
    MouseListener mouseListener = mock( MouseListener.class );
    TypedListener typedListener = new TypedListener( mouseListener );

    Event event = createEvent( SWT.MouseUp );
    typedListener.handleEvent( event );

    verify( mouseListener ).mouseUp( any( MouseEvent.class ) );
  }

  @Test
  public void testHandleEventForMouseDoubleClickEvent() {
    MouseListener mouseListener = mock( MouseListener.class );
    TypedListener typedListener = new TypedListener( mouseListener );

    Event event = createEvent( SWT.MouseDoubleClick );
    typedListener.handleEvent( event );

    verify( mouseListener ).mouseDoubleClick( any( MouseEvent.class ) );
  }

  @Test
  public void testHandleEventForControlMovedEvent() {
    ControlListener controlListener = mock( ControlListener.class );
    TypedListener typedListener = new TypedListener( controlListener );

    Event event = createEvent( SWT.Move );
    typedListener.handleEvent( event );

    verify( controlListener ).controlMoved( any( ControlEvent.class ) );
  }

  @Test
  public void testHandleEventForControlResizedEvent() {
    ControlListener controlListener = mock( ControlListener.class );
    TypedListener typedListener = new TypedListener( controlListener );

    Event event = createEvent( SWT.Resize );
    typedListener.handleEvent( event );

    verify( controlListener ).controlResized( any( ControlEvent.class ) );
  }

  @Test
  public void testHandleEventForPaintControlEvent() {
    PaintListener paintListener = mock( PaintListener.class );
    TypedListener typedListener = new TypedListener( paintListener );

    Event event = createEvent( SWT.Paint );
    typedListener.handleEvent( event );

    verify( paintListener ).paintControl( any( PaintEvent.class ) );
  }

  @Test
  public void testHandleEventForPaintControlEventWithModifyingListener() {
    final GC gc = mock( GC.class );
    PaintListener paintListener = new PaintListener() {
      public void paintControl( PaintEvent event ) {
        event.gc = gc;
      }
    };
    TypedListener typedListener = new TypedListener( paintListener );

    Event event = createEvent( SWT.Paint );
    typedListener.handleEvent( event );

    assertEquals( gc, event.gc );
  }

  @Test
  public void testHandleEventForTraverseEvent() {
    TraverseListener verifyListener = mock( TraverseListener.class );
    TypedListener typedListener = new TypedListener( verifyListener );

    Event event = createEvent( SWT.Traverse );
    typedListener.handleEvent( event );

    verify( verifyListener ).keyTraversed( any( TraverseEvent.class ) );
  }

  @Test
  public void testHandleEventForTraverseEventWithModifyingListener() {
    TraverseListener verifyListener = new TraverseListener() {
      public void keyTraversed( TraverseEvent e ) {
        e.detail = 3;
        e.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( verifyListener );

    Event event = createEvent( SWT.Traverse );
    typedListener.handleEvent( event );

    assertFalse( event.doit );
    assertEquals( 3, event.detail );
  }

  @Test
  public void testHandleEventForVerifyEvent() {
    VerifyListener verifyListener = mock( VerifyListener.class );
    TypedListener typedListener = new TypedListener( verifyListener );

    Event event = createEvent( SWT.Verify );
    typedListener.handleEvent( event );

    verify( verifyListener ).verifyText( any( VerifyEvent.class ) );
  }

  @Test
  public void testHandleEventForVerifyEventWithModifyingListener() {
    VerifyListener verifyListener = new VerifyListener() {
      public void verifyText( VerifyEvent event ) {
        event.text = "text";
        event.doit = false;
      }
    };
    TypedListener typedListener = new TypedListener( verifyListener );

    Event event = createEvent( SWT.Verify );
    typedListener.handleEvent( event );

    assertEquals( "text", event.text );
    assertFalse( event.doit );
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
    result.doit = true;
    return result;
  }
}
