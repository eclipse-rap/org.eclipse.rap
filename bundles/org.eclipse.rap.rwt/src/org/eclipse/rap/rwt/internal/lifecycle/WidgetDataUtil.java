/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;


public final class WidgetDataUtil {

  private static final String ATTR_DATA_KEYS = WidgetUtil.class.getName() + "#dataKeys";

  public static void registerDataKeys( String... keys ) {
    List<String> dataKeys = getDataKeys();
    if( dataKeys == null ) {
      dataKeys = new ArrayList<String>();
      getUISession().setAttribute( ATTR_DATA_KEYS, dataKeys );
    }
    for( String key : keys ) {
      if( key != null && !dataKeys.contains( key ) ) {
        dataKeys.add( key );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  public static List<String> getDataKeys() {
    return ( List<String> )getUISession().getAttribute( ATTR_DATA_KEYS );
  }

  private WidgetDataUtil() {
    // prevent instantiation
  }

}
