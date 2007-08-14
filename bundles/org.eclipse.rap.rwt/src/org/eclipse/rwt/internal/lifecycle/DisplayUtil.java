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

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Display;


public class DisplayUtil {
  
  private DisplayUtil() {
    // prevent instance creation
  }

  public static IDisplayLifeCycleAdapter getLCA( final Display display ) {
    Class clazz = ILifeCycleAdapter.class;
    IDisplayLifeCycleAdapter result;
    result = ( IDisplayLifeCycleAdapter )display.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }

  public static IWidgetAdapter getAdapter( final Display display ) {
    Class clazz = IWidgetAdapter.class;
    IWidgetAdapter result;
    result = ( IWidgetAdapter )display.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }
  
  public static String getId( final Display display ) {
    return getAdapter( display ).getId();
  }
  
  private static void throwAdapterException( final Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
  }
  
  public static void writeAppScript( final String id ) throws IOException {
    DisplayLCA.writeAppScript( id );
  }
  
  public static void writeLibraries() throws IOException {
    DisplayLCA.writeLibraries();
  }
}
