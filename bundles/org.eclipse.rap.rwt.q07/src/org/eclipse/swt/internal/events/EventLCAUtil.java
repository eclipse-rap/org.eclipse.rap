/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.SWT;

public final class EventLCAUtil {

  public static int readStateMask( final String paramName ) {
    int result = 0;
    String modifiers = readStringParam( paramName );
    if( modifiers != null ) {
      result = translateModifier( modifiers );
    }
    return result;
  }

  private static String readStringParam( final String paramName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( paramName );
    return value;
  }

  static int translateModifier( final String value ) {
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

  private EventLCAUtil() {
    // prevent instantiation
  }
}
