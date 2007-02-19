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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
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
  private final Map map = new HashMap();

  public Object getAdapter( final Object adaptable, final Class adapter ) {
    // Note [fappel]: Since this code is performance critical, don't change
    //                anything without checking it against a profiler.
    Object result = null;
    if (   ( adaptable instanceof Display || adaptable instanceof Widget ) 
         && adapter == IWidgetAdapter.class ) 
    {
      // [fappel] We use a hash as key to avoid using WeakHashMap, which doesn't
      //          perform as well as a simple HashMap.
      Integer hash = new Integer( adaptable.hashCode() );
      result = map.get( hash );
      if( result == null ) {
        result = new WidgetAdapter();
        map.put( hash, result );
      }
    }
    return result;
  }

  public Class[] getAdapterList() {
    return ADAPTER_LIST;
  }
}
