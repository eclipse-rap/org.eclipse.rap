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
package org.eclipse.rwt.internal.browser;

import java.text.NumberFormat;

/**
 * <p>The Internet Explorer Browser Detection Class</p>
 */
public class DetectorIE extends DetectorBase {

  private final static String ID_OPERA = "opera";
  private final static String ID_MSIE = "msie";
  private final static String ID_MSIE5 = "msie 5";
  private final static String ID_MSIE5_5 = "msie 5.5";
  private final static String ID_MSIE6 = "msie 6";
  private final static String ID_MSIE7 = "msie 7";

  public boolean knowsBrowserString( final String userAgent ) {
    return !"".equals( getBrowserVersion( userAgent ) );
  }

  public String getBrowserClassName( final String userAgent ) {
    return BROWSER_PACKAGE + "Ie" + getBrowserVersion( userAgent );
  }

  public String getBrowserVersion( final String userAgent ) {
    String result = "";
    if( contains( userAgent, ID_MSIE ) && !contains( userAgent, ID_OPERA ) ) {
      if( contains( userAgent, ID_MSIE5_5 ) ) {
        result = "5_5";
      } else if( contains( userAgent, ID_MSIE5 ) ) {
        result = "5";
      } else if( contains( userAgent, ID_MSIE6 ) ) {
        result = "6";
      } else if( contains( userAgent, ID_MSIE7 ) ) {
        result = "7";
      } else if( getMajor( userAgent ) > 6 ) {
        result = "7";
      }
    }
    return result;
  }

  /**
   * helper method to extract the major release number
   */
  private static int getMajor( final String userAgent ) {
    NumberFormat nf = NumberFormat.getInstance();
    nf.setParseIntegerOnly( true );
    int result = -1;
    try {
      String appVersion = parseAppVersion( userAgent );
      int index = appVersion.indexOf( "." );
      if( index != -1 ) {
        appVersion = appVersion.substring( 0, index );
      }
      result = nf.parse( appVersion ).intValue();
    } catch( Exception ex ) {
      // ignore - can't determine major version number
    }
    return result;
  }

  /**
   * helper method to parse the userAgent String and extract the appVersion
   */
  private static String parseAppVersion( final String userAgent ) {
    String result = "";
    int index = userAgent.indexOf( ID_MSIE );
    int start = index + ID_MSIE.length() + 1;
    result = userAgent.substring( start, start + 3 );
    return result;
  }
}