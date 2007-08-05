/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;


public class UntypedEventAdapter
  implements ControlListener, 
             DisposeListener,
             SelectionListener,
             FocusListener, 
             TreeListener, 
             ShellListener, 
             MenuListener, 
             ModifyListener,
             SetDataListener,
             VerifyListener
{
  private ArrayList listeners = new ArrayList();

  private class Entry {
    final int eventType;
    final Listener listener;
    private Entry( final int eventType, final Listener listener ) {
      this.eventType = eventType;
      this.listener = listener;
    }
  }
  
  public void controlMoved( final ControlEvent evt ) {
    Event event = createEvent( SWT.Move, evt.getSource() );
    dispatchEvent( SWT.Move, event );
  }

  public void controlResized( final ControlEvent evt ) {
    Event event = createEvent( SWT.Resize, evt.getSource() );
    dispatchEvent( SWT.Resize, event );
  }

  public void widgetDisposed( final DisposeEvent evt ) {
    Event event = createEvent( SWT.Dispose, evt.getSource() );
    dispatchEvent( SWT.Dispose, event );
  }

  public void widgetDefaultSelected( final SelectionEvent evt ) {
    Event event = createEvent( SWT.DefaultSelection, evt.getSource() );
    event.x = evt.x;
    event.y = evt.y;
    event.height = evt.height;
    event.width = evt.width;
    dispatchEvent( SWT.DefaultSelection, event );
  }

  public void widgetSelected( final SelectionEvent evt ) {
    Event event = createEvent( SWT.Selection, evt.getSource() );
    event.x = evt.x;
    event.y = evt.y;
    event.height = evt.height;
    event.width = evt.width;
    event.detail = evt.detail;
    event.item = evt.item;
    dispatchEvent( SWT.Selection, event );
  }

  public void focusGained( final FocusEvent evt ) {
    Event event = createEvent( SWT.FocusIn, evt.getSource() );
    dispatchEvent( SWT.FocusIn, event );
  }

  public void focusLost( final FocusEvent evt ) {
    Event event = createEvent( SWT.FocusOut, evt.getSource() );
    dispatchEvent( SWT.FocusOut, event );
  }

  public void treeCollapsed( final TreeEvent evt ) {
    Event event = createEvent( SWT.Collapse, evt.getSource() );
    event.item = evt.item;
    dispatchEvent( SWT.Collapse, event );
  }

  public void treeExpanded( final TreeEvent evt ) {
    Event event = createEvent( SWT.Expand, evt.getSource() );
    event.item = evt.item;
    dispatchEvent( SWT.Expand, event );
  }

  public void shellActivated( final ShellEvent evt ) {
    Event event = createEvent( SWT.Activate, evt.getSource() );
    dispatchEvent( SWT.Activate, event );
  }

  public void shellClosed( final ShellEvent evt ) {
    Event event = createEvent( SWT.Close, evt.getSource() );
    dispatchEvent( SWT.Close, event );
  }

  public void shellDeactivated( final ShellEvent evt ) {
    Event event = createEvent( SWT.Deactivate, evt.getSource() );
    dispatchEvent( SWT.Deactivate, event );
  }

  public void menuHidden( final MenuEvent evt ) {
    Event event = createEvent( SWT.Hide, evt.getSource() );
    dispatchEvent( SWT.Hide, event );
  }

  public void menuShown( final MenuEvent evt ) {
    Event event = createEvent( SWT.Show, evt.getSource() );
    dispatchEvent( SWT.Show, event );
  }

  public void modifyText( final ModifyEvent evt ) {
    Event event = createEvent( SWT.Modify, evt.getSource() );
    dispatchEvent( SWT.Modify, event );
  }
  
  public void verifyText( final VerifyEvent evt ) {
    Event event = createEvent( SWT.Verify, evt.getSource() );
    dispatchEvent( SWT.Verify, event );
    evt.doit = event.doit;
    evt.text = event.text;
  }
  
  public void update( final SetDataEvent evt ) {
    Event event = createEvent( SWT.SetData, evt.getSource() );
    event.item = evt.item;
    event.index = evt.index;
    dispatchEvent( SWT.SetData, event );
  }
  
  public void addListener( final Widget widget, 
                           final int eventType,
                           final Listener listener )
  {
    addListener( eventType, listener );
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
      case SWT.Close:
        ShellEvent.addListener( widget, this );
      break;
      case SWT.Hide:
      case SWT.Show:
        MenuEvent.addListener( widget, this );
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
      default:
        String txt = "The untyped event ''{0}'' is not supported.";
        Object[] param = new Object[] { new Integer( eventType ) };
        String msg = MessageFormat.format( txt, param );
        throw new IllegalArgumentException( msg );
    }    
  }

  public void removeListener( final Widget widget, 
                              final int eventType, 
                              final Listener listener )
  {
    removeListener( eventType, listener );
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
      case SWT.Close:
        ShellEvent.removeListener( widget, this );
      break;
      case SWT.Hide:
      case SWT.Show:
        MenuEvent.removeListener( widget, this );
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
      default:
        String txt = "The untyped event ''{0}'' is not supported.";
        Object[] param = new Object[] { new Integer( eventType ) };
        String msg = MessageFormat.format( txt, param );
        throw new IllegalArgumentException( msg );
    }
  }

  void addListener( final int eventType, final Listener listener ) {
    listeners.add( new Entry( eventType, listener ) );
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

  public boolean isEmpty() {
    return listeners.isEmpty();
  }

  
  //////////////////
  // helping methods

  private void dispatchEvent( final int eventType, final Event event ) {
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
    Event result = new Event();
    result.type = eventType;
    result.widget = ( Widget )source;
    return result;
  }
}
