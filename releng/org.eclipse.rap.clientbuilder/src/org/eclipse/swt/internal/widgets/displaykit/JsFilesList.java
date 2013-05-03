/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.lang.reflect.Field;


@SuppressWarnings( "restriction" )
public final class JsFilesList {

  private JsFilesList() {
    // prevent instantiation
  }

  public static String[] getFiles()
    throws SecurityException, NoSuchFieldException, IllegalAccessException
  {
    Class<?> clazz = ClientResources.class;
    Field field = clazz.getDeclaredField( "JAVASCRIPT_FILES" );
    field.setAccessible( true );
    String[] files = ( String[] )field.get( null );
    for( int i = 0; i < files.length; i++ ) {
      if( "debug-settings.js".equals( files[ i ] ) ) {
        files[ i ] = "settings.js";
      }
    }
    return files;
  }

}
