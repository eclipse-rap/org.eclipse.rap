/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.service.IApplicationStore;

public class ApplicationStoreImpl implements IApplicationStore {
  private final Map attributes;
  
  public ApplicationStoreImpl() {
    attributes = new HashMap();
  }
  
  public Object getAttribute( String name ) {
    Object result;
    synchronized( attributes ) {
      result = attributes.get( name );
    }
    return result;
  }

  public void setAttribute( String name, Object value ) {
    synchronized( attributes ) {
      attributes.put( name, value );
    }
  }

  public void removeAttribute( String name ) {
    synchronized( attributes ) {
      attributes.remove( name );
    }
  }
}
