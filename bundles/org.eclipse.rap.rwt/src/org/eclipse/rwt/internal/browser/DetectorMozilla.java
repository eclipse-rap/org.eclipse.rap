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

/**
 * <p>The Mozilla Browser Detection Class</p>
 */
public class DetectorMozilla extends DetectorBase {

  public boolean knowsBrowserString( final String userAgent ) {
    // tricky: all browser contain mozilla string, so
    // we have to make sure it is no other browser
    return     contains( userAgent, "mozilla/5.0" )
           &&  contains( userAgent, "gecko" )
           && !contains( userAgent, "mozilla/4" )
           && !contains( userAgent, "konqueror" )
           && !contains( userAgent, "msie" )
           && !contains( userAgent, "safari" );
  }

  public String getBrowserClassName( final String userAgent ) {
    return BROWSER_PACKAGE + "Mozilla" + getBrowserVersion( userAgent );
  }

  public String getBrowserVersion( final String userAgent ) {
    String result = "1_6";
    if( contains( userAgent, "rv:1.7" ) ) {
      result = "1_7";
    } else if( contains( userAgent, "rv:1.8" ) ) {
      result = "1_7";
    } else if( contains( userAgent, "rv:1.9" ) ) {
      result = "1_7";
    }
    return result;
  }
}
