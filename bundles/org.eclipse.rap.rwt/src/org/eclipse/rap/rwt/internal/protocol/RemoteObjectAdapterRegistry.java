/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;


public class RemoteObjectAdapterRegistry {
  
  private final List<RemoteObjectAdapter> adapters;

  public static RemoteObjectAdapterRegistry getInstance() {
    return SingletonUtil.getSessionInstance( RemoteObjectAdapterRegistry.class );
  }

  private RemoteObjectAdapterRegistry() {
    adapters = Collections.synchronizedList( new ArrayList<RemoteObjectAdapter>() );
  }
  
  public void register( RemoteObjectAdapter adapter ) {
    adapters.add( adapter );
  }
  
  void remove( RemoteObjectAdapter adapter ) {
    adapters.remove( adapter );
  }
  
  public List<RemoteObjectAdapter> getAdapters() {
    return new ArrayList<RemoteObjectAdapter>( adapters );
  }

}
