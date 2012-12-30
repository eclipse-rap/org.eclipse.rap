/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

import org.eclipse.rap.rwt.RWT;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


public final class ClientResourcesAdapter {

  private static Bundle rwtBundle = getRwtBundle();

  private ClientResourcesAdapter() {
    // prevent instantiation
  }

  public static String[] getRegisteredClientResources() {
    String[] result;
    try {
      String name = "org.eclipse.swt.internal.widgets.displaykit.ClientResources";
      Class<?> clazz = rwtBundle.loadClass( name );
      Field field = clazz.getDeclaredField( "JAVASCRIPT_FILES" );
      field.setAccessible( true );
      result = ( String[] )field.get( null );
    } catch( Exception exception ) {
      throw new RuntimeException( "Failed to get JS files from RWT ClientResources", exception );
    }
    return result;
  }

  public static InputStream getResourceAsStream( String path ) throws IOException {
    InputStream result = null;
    URL resource = rwtBundle.getResource( path );
    if( resource != null ) {
      result = resource.openStream();
    }
    return result;
  }

  private static Bundle getRwtBundle() {
    return FrameworkUtil.getBundle( RWT.class );
  }

}
