/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import static org.eclipse.rap.rwt.internal.clientscripting.ClientScriptingSupport.isClientListener;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValueAsString;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Widget;


public final class EventLCAUtil {

  public static int readStateMask( Widget widget, String eventName ) {
    String altKey = readEventPropertyValueAsString( getId( widget ), eventName, "altKey" );
    String ctrlKey = readEventPropertyValueAsString( getId( widget ), eventName, "ctrlKey" );
    String shiftKey = readEventPropertyValueAsString( getId( widget ), eventName, "shiftKey" );
    return translateModifier( altKey, ctrlKey, shiftKey );
  }

  static int translateModifier( String altKey, String ctrlKey, String shiftKey ) {
    int result = 0;
    if( "true".equals( ctrlKey ) ) {
      result |= SWT.CTRL;
    }
    if( "true".equals( altKey ) ) {
      result |= SWT.ALT;
    }
    if( "true".equals( shiftKey ) ) {
      result |= SWT.SHIFT;
    }
    return result;
  }

  public static int translateButton( int value ) {
    int result = 0;
    switch( value ) {
      case 1:
        result = SWT.BUTTON1;
      break;
      case 2:
        result = SWT.BUTTON2;
      break;
      case 3:
        result = SWT.BUTTON3;
      break;
      case 4:
        result = SWT.BUTTON4;
      break;
      case 5:
        result = SWT.BUTTON5;
      break;
    }
    return result;
  }

  public static boolean hasScrollBarsSelectionListener( Scrollable scrollable ) {
    boolean result = false;
    ScrollBar horizontalBar = scrollable.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || isListening( horizontalBar, SWT.Selection );
    }
    ScrollBar verticalBar = scrollable.getVerticalBar();
    if( verticalBar != null ) {
      result = result || isListening( verticalBar, SWT.Selection );
    }
    return result;
  }

  public static void processRadioSelection( Widget widget, boolean isWidgetSelected ) {
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( wasEventSent( widget, eventName ) ) {
      Event event = new Event();
      if( !isWidgetSelected ) {
        event.time = -1;
      }
      event.stateMask = readStateMask( widget, eventName );
      widget.notifyListeners( SWT.Selection, event );
    }
  }

  public static boolean isListening( Widget widget, int eventType ) {
    for( Listener listener : widget.getListeners( eventType ) ) {
      if( !isClientListener( listener ) ) {
        return true;
      }
    }
    return false;
  }

  private EventLCAUtil() {
    // prevent instantiation
  }

}
