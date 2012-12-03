/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.text.MessageFormat;

import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.widgets.Display;


public final class DisplayUtil {

  private DisplayUtil() {
    // prevent instance creation
  }

  public static DisplayLifeCycleAdapter getLCA( Display display ) {
    DisplayLifeCycleAdapter result = display.getAdapter( DisplayLifeCycleAdapter.class );
    if( result == null ) {
      throwAdapterException( DisplayLifeCycleAdapter.class );
    }
    return result;
  }

  public static String getId( Display display ) {
    return getAdapter( display ).getId();
  }

  public static IWidgetAdapter getAdapter( Display display ) {
    IWidgetAdapter result = display.getAdapter( IWidgetAdapter.class );
    if( result == null ) {
      throwAdapterException( IWidgetAdapter.class );
    }
    return result;
  }

  private static void throwAdapterException( Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
  }
}
