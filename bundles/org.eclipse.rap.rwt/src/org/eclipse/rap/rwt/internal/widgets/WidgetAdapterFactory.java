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

package org.eclipse.rap.rwt.internal.widgets;

import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.rap.rwt.widgets.Display;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.AdapterFactory;


/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public final class WidgetAdapterFactory implements AdapterFactory {

  private static final Class[] ADAPTER_LIST = new Class[] { 
    IWidgetAdapter.class 
  };

  // Map keeping the association between extensions and a set of objects. 
  // Key: Object (adaptable), value: IWidgetAdapter (adapter).
  private final Map map = new WeakHashMap();

  public Object getAdapter( final Object adaptable, final Class adapter ) {
    Object result = null;
    if (   ( adaptable instanceof Display || adaptable instanceof Widget ) 
         && adapter == IWidgetAdapter.class ) 
    {
      result = map.get( adaptable );
      if ( result == null ) {
        result = new WidgetAdapter();
        map.put( adaptable, result );
      }
    }
    return result;
  }

  public Class[] getAdapterList() {
    return ADAPTER_LIST;
  }
}
