/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import org.eclipse.rap.rwt.internal.RWTProperties;


public final class UITestUtil {

  static boolean enabled;

  static {
    String property = System.getProperty( RWTProperties.ENABLE_UI_TESTS );
    enabled = Boolean.valueOf( property ).booleanValue();
  }

  public static void checkId( String id ) {
    if( !isValidId( id ) ) {
      String msg = "The widget id contains illegal characters: " + id;
      throw new IllegalArgumentException( msg ) ;
    }
  }

  public static boolean isEnabled() {
    return enabled;
  }

  static boolean isValidId( String id ) {
    // see http://www.w3.org/TR/html401/types.html#type-cdata (id and name)
    // for what characters are allowed
    boolean result
      =  id != null
      && id.length() > 0
      && Character.isLetter( id.charAt( 0 ) );
    for( int i = 1; result && i < id.length(); i++ ) {
      char ch = id.charAt( i );
      result &= Character.isLetter( ch )
             || isNumber( ch )
             || ch == '-'
             || ch == '.'
             || ch == '_'
             || ch == ':';
    }
    return result;
  }

  private static boolean isNumber( char ch ) {
    return ( ch >= '0' && ch <= '9' );
  }

  private UITestUtil() {
    // prevent instantiation
  }

}
