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
 * <p>The Safari Browser Detection Class</p>
 */
// TODO [rh] distinguish Safari versions: 
//      return class Safari for version < 2 and class Safari2 for V2.0 and later  
public class DetectorSafari extends DetectorBase {

  private final static String idString = "safari";

  public DetectorSafari() {
    super();
  }

  public boolean knowsBrowserString( final String userAgent ) {
    return contains( userAgent, idString );
  }

  public String getBrowserClassName( final String userAgent ) {
    return BROWSER_PACKAGE + "Safari2";
  }
}
