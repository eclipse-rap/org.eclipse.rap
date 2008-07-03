/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;

public final class RWTMessages {

  private static final String BUNDLE_NAME
    = "org.eclipse.rwt.internal.RWTMessages"; //$NON-NLS-1$

  private RWTMessages() {
    // prevent instantiation
  }

  public static String getMessage( final String key ) {
    return getMessage( key, BUNDLE_NAME );
  }
  
  public static String getMessage( final String key, String bundleName ) {
    String result = key;
    ResourceBundle bundle = null;
    try {
      bundle = getBundle( bundleName );
    } catch( MissingResourceException ex ) {
      result = key + " (no resource bundle)"; //$NON-NLS-1$
    }
    if( bundle != null ) {
      try {
        result = bundle.getString( key );
      } catch( MissingResourceException ex2 ) {
      }
    }
    return result;
  }

  private static ResourceBundle getBundle( final String baseName ) {
    ResourceBundle result = null;
    try {
      ClassLoader loader = SWT.class.getClassLoader();
      result = ResourceBundle.getBundle( baseName, RWT.getLocale(), loader );
    } catch( final RuntimeException re ) {
      // TODO [fappel]: improve this
      String msg =   "Warning: could not retrieve resource bundle "
        + "- loading system default";
      System.out.println( msg );
      result = ResourceBundle.getBundle( baseName );
    }
    return result;
  }
}
