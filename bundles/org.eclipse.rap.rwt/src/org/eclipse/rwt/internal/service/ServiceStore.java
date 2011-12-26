/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.IServiceStore;


public final class ServiceStore implements IServiceStore {

  private final Map<String,Object> attributes;

  public ServiceStore() {
    attributes = new HashMap<String,Object>();
  }

  public Object getAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    return attributes.get( name );
  }

  public void setAttribute( String name, Object value ) {
    ParamCheck.notNull( name, "name" );
    attributes.put( name, value );
  }

  public void removeAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    attributes.remove( name );
  }

}
