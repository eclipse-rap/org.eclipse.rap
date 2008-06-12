/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.browser;

/**
 * The Konqueror Browser Detection Class
 */
public class DetectorKonqueror extends DetectorBase {

  private static final String VERSION_PREFIX = "konqueror/";
  private final static String ID_KONQUEROR = "konqueror";
  

  public DetectorKonqueror() {
    super();
  }

  public boolean knowsBrowserString( final String userAgent ) {
    return contains( userAgent, ID_KONQUEROR );
  }

  public String getBrowserName( final String userAgent ) {
    return "Konqueror";
  }

  public String getBrowserClassName( final String userAgent ) {
    String result 
      = BROWSER_PACKAGE 
      + getBrowserName( userAgent ) 
      + getBrowserVersion( userAgent );
    return result;
  }

  public String getBrowserVersion( final String userAgent ) {
    String result = "";
    int[] versions = getVersions( userAgent );
    if( versions[ 0 ] == 3 ) {
      if( versions[ 1 ] == 1 ) {
        result = "3_1";
      } else if( versions[ 1 ] == 2 ) {
        result = "3_2";
      } else if( versions[ 1 ] == 3 ) {
        result = "3_3";
      } else if( versions[ 1 ] >= 4 ) {
        result = "3_4";
      }
    }
    return result;
  }

  private int[] getVersions( final String userAgent ) {
    int result[] = new int[]{ -1, -1 };
    int startIndex = userAgent.indexOf( VERSION_PREFIX );
    if( startIndex != -1 ) {
      int endIndex = userAgent.substring( startIndex ).indexOf( ";" );
      if( endIndex != -1 ) {
        int absoluteStart = startIndex + VERSION_PREFIX.length();
        int absoluteEnd = startIndex + endIndex;
        String version = userAgent.substring( absoluteStart, absoluteEnd );
        String[] parts = version.split( "\\.", 2 );
        if( parts.length == 2 ) {
          try {
            result[ 0 ] = Integer.parseInt( parts[ 0 ] );
            result[ 1 ] = Integer.parseInt( parts[ 1 ] );
          } catch( NumberFormatException e) {
            // ignore
          }
        }
      }
    }
    return result;
  }

}
