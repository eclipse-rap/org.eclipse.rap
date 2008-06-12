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
 * <p>The Opera Browser Detection Class</p>
 */
public class DetectorOpera extends DetectorBase {

  private final static String ID_OPERA = "opera";

  public boolean knowsBrowserString( final String userAgent ) {
    return !"".equals( getBrowserVersion( userAgent ) );
  }

  public String getBrowserClassName( final String userAgent ) {
    return BROWSER_PACKAGE + "Opera" + getBrowserVersion( userAgent );
  }

  public String getBrowserVersion( final String userAgent ) {
    String result = "";
    if( contains( userAgent, ID_OPERA ) ) {
      if( contains( userAgent, "opera 8" ) || contains( userAgent, "opera/8" ) )
      {
        result = "8";
      } else if(    contains( userAgent, "opera 9" )
                 || contains( userAgent, "opera/9" ) )
      {
        result = "9";
      }
    }
    return result;
  }
}