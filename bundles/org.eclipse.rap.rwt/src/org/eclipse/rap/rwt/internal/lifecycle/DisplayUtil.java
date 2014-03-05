/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.swt.widgets.Display;


public final class DisplayUtil {

  private DisplayUtil() {
    // prevent instance creation
  }

  public static DisplayLifeCycleAdapter getLCA( Display display ) {
    DisplayLifeCycleAdapter result = display.getAdapter( DisplayLifeCycleAdapter.class );
    if( result == null ) {
      String message = "Could not retrieve an instance of DisplayLifeCycleAdapter.";
      throw new IllegalStateException( message );
    }
    return result;
  }

  public static String getId( Display display ) {
    return getAdapter( display ).getId();
  }

  @SuppressWarnings( "deprecation" )
  public static WidgetAdapter getAdapter( Display display ) {
    WidgetAdapter result = display.getAdapter( org.eclipse.rap.rwt.lifecycle.WidgetAdapter.class );
    if( result == null ) {
      throw new IllegalStateException( "Could not retrieve an instance of WidgetAdapter." );
    }
    return result;
  }

}
