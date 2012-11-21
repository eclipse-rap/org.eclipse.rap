/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Innoopract Informationssysteme GmbH - initial API and implementation
 *   EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal;


public final class RWTProperties {

  public static final String DEVELOPMEMT_MODE = "org.eclipse.rap.rwt.developmentMode";

  public static final String ENABLE_THEME_WARNINGS = "org.eclipse.rap.enableThemeWarnings";
  /*
   * Used in conjunction with <code>WidgetUtil#CUSTOM_WIDGET_ID</code>,
   * to activate support for custom widget ids.</p>
   */
  public static final String ENABLE_UI_TESTS = "org.eclipse.rap.rwt.enableUITests";

  private RWTProperties() {
    // prevent instantiation
  }

  // TODO [rst] Temporary system property, see bug 254478
  public static boolean enableThemeDebugOutput() {
    return getBooleanProperty( ENABLE_THEME_WARNINGS, false );
  }

  public static boolean isDevelopmentMode() {
    return getBooleanProperty( DEVELOPMEMT_MODE, false );
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
