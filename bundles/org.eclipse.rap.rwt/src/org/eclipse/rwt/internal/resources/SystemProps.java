/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH amd others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Innoopract Informationssysteme GmbH - initial API and implementation
 *   EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;


public final class SystemProps {

  public static final String USE_VERSIONED_JAVA_SCRIPT = "org.eclipse.rap.useVersionedJavaScript";
  public static final String USE_COMPRESSED_JAVA_SCRIPT = "org.eclipse.rap.useCompressedJavaScript";
  public static final String CLIENT_LIBRARY_VARIANT = "org.eclipse.rwt.clientLibraryVariant";
  public static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";
  public static final String ENABLE_THEME_WARNINGS = "org.eclipse.rap.enableThemeWarnings";

  private SystemProps() {
    // prevent instantiation
  }

  public static boolean useVersionedJavaScript() {
    return getBooleanProperty( USE_VERSIONED_JAVA_SCRIPT, true );
  }

  public static boolean useCompressedJavaScript() {
    return getBooleanProperty( USE_COMPRESSED_JAVA_SCRIPT, true );
  }

  // TODO [rst] Temporary system property, see bug 254478
  public static boolean enableThemeDebugOutput() {
    return getBooleanProperty( ENABLE_THEME_WARNINGS, false );
  }

  public static boolean isDevelopmentMode() {
    String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
    return DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
  }

  private static boolean getBooleanProperty( String key, boolean defaultValue ) {
    boolean result = defaultValue;
    String propertyValue = System.getProperty( key );
    if( propertyValue != null ) {
      result = propertyValue.equalsIgnoreCase( "true" );
    }
    return result;
  }
}
