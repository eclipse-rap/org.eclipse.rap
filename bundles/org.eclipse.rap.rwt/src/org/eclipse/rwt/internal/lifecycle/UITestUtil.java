/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Widget;

public final class UITestUtil {

  public static void writeId( final Widget widget ) throws IOException {
    if( !isInitialized( widget ) && isEnabled() ) {
      String id = WidgetUtil.getId( widget );
      if( !isValidId( id ) ) {
        String text = "The widget id contains illegal characters: ''{0}''";
        String msg = MessageFormat.format( text, new Object[] { id } );
        throw new IllegalArgumentException( msg ) ;
      }
      JSWriter writer = JSWriter.getWriterFor( widget );
      Object[] args = new Object[] { widget, id };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setHtmlId", args );
    }
  }
  
  public static boolean isEnabled() {
    String property = System.getProperty( WidgetUtil.ENABLE_UI_TESTS );
    return Boolean.valueOf( property ).booleanValue();
  }
  
  //////////////////
  // helping methods

  private static boolean isInitialized( final Widget widget ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    return adapter.isInitialized();
  }

  static boolean isValidId( final String id ) {
    // see http://www.w3.org/TR/html401/types.html#type-cdata (id and name)
    // for what characters are allowed
    boolean result 
      =  id != null 
      && id.length() > 0 
      && Character.isLetter( id.charAt ( 0 ) );
    for( int i = 1; result && i < id.length(); i++ ) {
      char ch = id.charAt( i );
      result &= Character.isLetter( ch ) 
             || isNumber( ch )
             || ch == '.'
             || ch == '_'
             || ch == ':';
    }
    return result;
  }

  private static boolean isNumber( final char ch ) {
    return ( ch >= '0' && ch <= '9' );
  }

  private UITestUtil() {
    // prevent instantiation
  }
}