/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ParamCheck;


public final class ServiceStore {

  private final Map<String,Object> attributes;

  public ServiceStore() {
    attributes = new HashMap<>();
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

  public void clear() {
    attributes.clear();
  }

}
