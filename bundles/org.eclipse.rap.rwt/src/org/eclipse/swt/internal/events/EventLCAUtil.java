/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValueAsString;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;


public final class EventLCAUtil {

  public static int readStateMask( Widget widget, String eventName ) {
    int result = 0;
    String modifiers = readEventPropertyValueAsString( getId( widget ),
                                                       eventName,
                                                       ClientMessageConst.EVENT_PARAM_MODIFIER );
    if( modifiers != null ) {
      result = translateModifier( modifiers );
    }
    return result;
  }

  static int translateModifier( String value ) {
    String[] modifiers = value.split( "," );
    int result = 0;
    for( int i = 0; i < modifiers.length; i++ ) {
      if( "ctrl".equals( modifiers[ i ] ) ) {
        result |= SWT.CTRL;
      } else if( "alt".equals( modifiers[ i ] ) ) {
        result |= SWT.ALT;
      } else if ( "shift".equals(  modifiers[ i ] ) ) {
        result |= SWT.SHIFT;
      }
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

  private EventLCAUtil() {
    // prevent instantiation
  }
}
