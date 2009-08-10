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
package org.eclipse.swt.internal.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.widgets.*;


public final class UntypedEventAdapter
  implements ControlListener,
             DisposeListener,
             SelectionListener,
             FocusListener,
             TreeListener,
             ShellListener,
             MenuListener,
             ModifyListener,
             SetDataListener,
             VerifyListener,
             MouseListener,
             KeyListener,
             TraverseListener,
             ShowListener,
             ActivateListener,
             HelpListener
{

  private class Entry {
    final int eventType;
    final Listener listener;
    private Entry( final int eventType, final Listener listener ) {
      this.eventType = eventType;
      this.listener = listener;
    }
  }

  private final java.util.List listeners;

  public UntypedEventAdapter() {
    listeners = new ArrayList();
  }

  // XXXListener interface imlementations

  public void controlMoved( final ControlEvent evt ) {
    Event event = createEvent( SWT.Move, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void controlResized( final ControlEvent evt ) {
    Event event = createEvent( SWT.Resize, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void widgetDisposed( final DisposeEvent evt ) {
    Event event = createEvent( SWT.Dispose, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void widgetDefaultSelected( final SelectionEvent evt ) {
    Event event = createEvent( SWT.DefaultSelection, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void widgetSelected( final SelectionEvent evt ) {
    Event event = createEvent( SWT.Selection, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void focusGained( final FocusEvent evt ) {
    Event event = createEvent( SWT.FocusIn, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void focusLost( final FocusEvent evt ) {
    Event event = createEvent( SWT.FocusOut, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void treeCollapsed( final TreeEvent evt ) {
    Event event = createEvent( SWT.Collapse, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void treeExpanded( final TreeEvent evt ) {
    Event event = createEvent( SWT.Expand, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void shellActivated( final ShellEvent evt ) {
    Event event = createEvent( SWT.Activate, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void shellClosed( final ShellEvent evt ) {
    Event event = createEvent( SWT.Close, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void shellDeactivated( final ShellEvent evt ) {
    Event event = createEvent( SWT.Deactivate, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void menuHidden( final MenuEvent evt ) {
    Event event = createEvent( SWT.Hide, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void menuShown( final MenuEvent evt ) {
    Event event = createEvent( SWT.Show, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void modifyText( final ModifyEvent evt ) {
    Event event = createEvent( SWT.Modify, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void verifyText( final VerifyEvent evt ) {
    Event event = createEvent( SWT.Verify, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void update( final SetDataEvent evt ) {
    Event event = createEvent( SWT.SetData, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void mouseDown( final MouseEvent evt ) {
    Event event = createEvent( SWT.MouseDown, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void mouseUp( final MouseEvent evt ) {
    Event event = createEvent( SWT.MouseUp, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void mouseDoubleClick( final MouseEvent evt ) {
    Event event = createEvent( SWT.MouseDoubleClick, evt.getSource() );
    copyFields( evt, event );
    dispatchEvent( event );
  }

  public void keyPressed( final KeyEvent typedEvent ) {
    Event event = createEvent( SWT.KeyDown, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
    typedEvent.doit = event.doit;
  }

  public void keyReleased( final KeyEvent typedEvent ) {
    Event event = createEvent( SWT.KeyUp, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void keyTraversed( final TraverseEvent typedEvent ) {
    Event event = createEvent( SWT.Traverse, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
    typedEvent.doit = event.doit;
  }

  public void controlShown( final ShowEvent typedEvent ) {
    Event event = createEvent( SWT.Show, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void controlHidden( final ShowEvent typedEvent ) {
    Event event = createEvent( SWT.Hide, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void activated( final ActivateEvent typedEvent ) {
    Event event = createEvent( SWT.Activate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void deactivated( final ActivateEvent typedEvent ) {
    Event event = createEvent( SWT.Deactivate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }
  
  public void helpRequested( final HelpEvent typedEvent ) {
    Event event = createEvent( SWT.Help, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  //////////////////////
  // Listener management

  public void addListener( final Widget widget,
                           final int eventType,
                           final Listener listener )
  {
    boolean validEventType = true;
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        ControlEvent.addListener( widget, this );
      break;
      case SWT.Dispose:
        DisposeEvent.addListener( widget, this );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        SelectionEvent.addListener( widget, this );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        FocusEvent.addListener( widget, this );
      break;
      case SWT.Expand:
      case SWT.Collapse:
        TreeEvent.addListener( widget, ( TreeListener )this );
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( widget instanceof Shell ) {
          ShellEvent.addListener( widget, this );
        } else {
          ActivateEvent.addListener( widget, this );
        }
      break;
      case SWT.Close:
        ShellEvent.addListener( widget, this );
      break;
      case SWT.Hide:
        if( widget instanceof Control ) {
          ShowEvent.addListener( widget, this );
        } else {
          MenuEvent.addListener( widget, this );
        }
        break;
      case SWT.Show:
        if( widget instanceof Control ) {
          ShowEvent.addListener( widget, this );
        } else {
          MenuEvent.addListener( widget, this );
        }
      break;
      case SWT.Modify:
        ModifyEvent.addListener( widget, this );
      break;
      case SWT.Verify:
        VerifyEvent.addListener( widget, this );
      break;
      case SWT.SetData:
        SetDataEvent.addListener( widget, this );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        MouseEvent.addListener( widget, this );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        KeyEvent.addListener( widget, this );
      break;
      case SWT.Traverse:
        TraverseEvent.addListener( widget, ( TraverseListener )this );
      break;
      default:
        validEventType = false;
    }
    if( validEventType ) {
      addListener( eventType, listener );
    }
  }

  void addListener( final int eventType, final Listener listener ) {
    listeners.add( new Entry( eventType, listener ) );
  }

  public void removeListener( final Widget widget,
                              final int eventType,
                              final Listener listener )
  {
    boolean validEventType = true;
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        ControlEvent.removeListener( widget, this );
      break;
      case SWT.Dispose:
        DisposeEvent.removeListener( widget, this );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        SelectionEvent.removeListener( widget, this );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        FocusEvent.removeListener( widget, this );
      break;
      case SWT.Expand:
      case SWT.Collapse:
        TreeEvent.removeListener( widget, ( TreeListener )this );
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( widget instanceof Shell ) {
          ShellEvent.removeListener( widget, this );
        } else {
          ActivateEvent.removeListener( widget, this );
        }
      break;
      case SWT.Close:
        ShellEvent.removeListener( widget, this );
      break;
      case SWT.Hide:
        if( widget instanceof Control ) {
          ShowEvent.removeListener( widget, this );
        } else {
          MenuEvent.removeListener( widget, this );
        }
        break;
      case SWT.Show:
        if( widget instanceof Control ) {
          ShowEvent.removeListener( widget, this );
        } else {
          MenuEvent.removeListener( widget, this );
        }
      break;
      case SWT.Modify:
        ModifyEvent.removeListener( widget, this );
      break;
      case SWT.Verify:
        VerifyEvent.removeListener( widget, this );
      break;
      case SWT.SetData:
        SetDataEvent.removeListener( widget, this );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        MouseEvent.removeListener( widget, this );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        KeyEvent.removeListener( widget, this );
      break;
      case SWT.Traverse:
        TraverseEvent.removeListener( widget, ( TraverseListener )this );
      break;
      default:
        validEventType = false;
    }
    if( validEventType ) {
      removeListener( eventType, listener );
    }
  }

  void removeListener( final int eventType, final Listener listener ) {
    Entry[] entries = getEntries();
    boolean found = false;
    for( int i = 0; !found && i < entries.length; i++ ) {
      // TODO [fappel]: check whether we have also to compare eventType!
      found = entries[ i ].listener == listener;
      if( found ) {
        listeners.remove( entries[ i ] );
      }
    }
  }

  public Listener[] getListeners( final int eventType ) {
    Entry[] entries = getEntries();
    java.util.List result = new ArrayList();
    for( int i = 0; i < entries.length; i++ ) {
      Entry entry = entries[ i ];
      if( entry.eventType == eventType ) {
        result.add( entry.listener );
      }
    }
    return ( Listener[] )result.toArray( new Listener[0] );
  }

  public static void notifyListeners( final int eventType, final Event event ) {
    TypedEvent typedEvent = null;
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        typedEvent = new ControlEvent( event );
      break;
      case SWT.Dispose:
        typedEvent = new DisposeEvent( event );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        typedEvent = new SelectionEvent( event );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        typedEvent = new FocusEvent( event );
      break;
      case SWT.Expand:
      case SWT.Collapse:
        typedEvent = new TreeEvent( event );
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( event.widget instanceof Shell ) {
          typedEvent = new ShellEvent( event );
        } else {
          typedEvent = new ActivateEvent( event );
        }
      break;
      case SWT.Close:
        typedEvent = new ShellEvent( event );
      break;
      case SWT.Hide:
      case SWT.Show:
        if( event.widget instanceof Control ) {
          typedEvent = new ShowEvent( event );
        } else {
          typedEvent = new MenuEvent( event );
        }
      break;
      case SWT.Modify:
        typedEvent = new ModifyEvent( event );
      break;
      case SWT.Verify:
        typedEvent = new VerifyEvent( event );
      break;
      case SWT.SetData:
        typedEvent = new SetDataEvent( event );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        typedEvent = new MouseEvent( event );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        typedEvent = new KeyEvent( event );
      break;
      case SWT.Traverse:
        typedEvent = new TraverseEvent( event );
      break;
    }
    if( typedEvent != null ) {
      typedEvent.processEvent();
    }
  }

  public boolean isEmpty() {
    return listeners.isEmpty();
  }

  //////////////////
  // helping methods

  private void dispatchEvent( final Event event ) {
    // [rh] protect against manipulating the event type in listener code
    int eventType = event.type;
    Entry[] entries = getEntries();
    for( int i = 0; i < entries.length; i++ ) {
      if( entries[ i ].eventType == eventType ) {
        entries[ i ].listener.handleEvent( event );
      }
    }
  }

  private Entry[] getEntries() {
    Entry[] result = new Entry[ listeners.size() ];
    listeners.toArray( result );
    return result;
  }

  private static Event createEvent( final int eventType, final Object source ) {
    Widget widget = ( Widget )source;
    Event result = new Event();
    result.type = eventType;
    result.widget = widget;
    result.display = widget.getDisplay();
    return result;
  }

  private static void copyFields( final TypedEvent from, final Event to ) {
    to.display = from.display;
    to.widget = from.widget;
    to.data = from.data;
  }

  private static void copyFields( final SelectionEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.detail = from.detail;
    to.doit = from.doit;
    to.x = from.x;
    to.y = from.y;
    to.width = from.width;
    to.height = from.height;
    to.item = from.item;
    to.text = from.text;
  }

  private static void copyFields( final TreeEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.detail = from.detail;
    to.doit = from.doit;
    to.x = from.x;
    to.y = from.y;
    to.height = from.height;
    to.width = from.width;
    to.item = from.item;
    to.text = from.text;
  }

  private static void copyFields( final VerifyEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.start = from.start;
    to.end = from.end;
    to.doit = from.doit;
    to.text = from.text;
  }

  private static void copyFields( final SetDataEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.index = from.index;
    to.item = from.item;
  }

  private static void copyFields( final MouseEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.button = from.button;
    to.time = from.time;
    to.x = from.x;
    to.y = from.y;
  }

  private static void copyFields( final ShellEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.doit = from.doit;
  }

  private static void copyFields( final KeyEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.character = from.character;
    to.keyCode = from.keyCode;
    to.stateMask = from.stateMask;
    to.doit = from.doit;
  }

  private static void copyFields( final TraverseEvent from, final Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.character = from.character;
    to.keyCode = from.keyCode;
    to.stateMask = from.stateMask;
    to.detail = from.detail;
    to.doit = from.doit;
  }
}
