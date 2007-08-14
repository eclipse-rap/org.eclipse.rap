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
 * <p>DetectorBase defines the base methods needed for browser detection</p> 
 */
public abstract class DetectorBase {

  public static final String OS_LINUX = "linux";
  public static final String OS_WINDOWS = "win";
  public static final String OS_MAC = "mac";
  public static final String OS_UNIX = "unix";
  public static final String OS_NONE = "none";
  public static final String OS_X11 = "x11";
  
  protected static final String BROWSER_PACKAGE 
    = "org.eclipse.rwt.internal.browser.";

  /**
   * <p>Must return whether the given <code>userAgent</code> known by this
   * <code>DetectorBase</code> instance.</p>
   * @param userAgent - the user agent string in <strong>lowercase</strong>
   */
  public abstract boolean knowsBrowserString( final String userAgent );

  /**
   * <p>This method is only called when a former call to 
   * <code>knowsBrowserString</code> returned <code>true</code>. 
   * This method must then return the fully qualified class name of the 
   * <code>Browser</code> class taht represents the given
   * <code>userAgent</code>.
   * @param userAgent - the user agent string in <strong>lowercase</strong>
   */
  // TODO [rh] Is there any reason why class name is returned as string?
  public abstract String getBrowserClassName( final String userAgent );

  public static String getBrowserOS( final String userAgent ) {
    String result;
    if( contains( userAgent, OS_LINUX ) ) {
      result = OS_LINUX;
    } else if( contains( userAgent, OS_X11 ) ) {
      result = OS_UNIX;
    } else if( contains( userAgent, OS_MAC ) ) {
      result = OS_MAC;
    } else if( contains( userAgent, OS_WINDOWS ) ) {
      result = OS_WINDOWS;
    } else {
      result = OS_NONE;
    }
    return result;
  }

  // helper method for string detection
  protected static boolean contains( final String fullString,
                                     final String searchString )
  {
    return fullString.indexOf( searchString ) != -1;
  }

}
