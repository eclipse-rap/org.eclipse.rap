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
package org.eclipse.rwt.internal.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.lifecycle.IEntryPointFactory;


class EntryPointRegistration {
  private final IEntryPointFactory factory;
  private final Map<String, Object> properties;

  public EntryPointRegistration( IEntryPointFactory factory, Map<String, Object> properties ) {
    this.factory = factory;
    this.properties = createPropertiesCopy( properties );
  }

  public IEntryPointFactory getFactory() {
    return factory;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  private static Map<String, Object> createPropertiesCopy( Map<String, Object> properties ) {
    Map<String, Object> result;
    if( properties != null ) {
      result = new HashMap<String, Object>( properties );
    } else {
      result = Collections.emptyMap();
    }
    return Collections.unmodifiableMap( result );
  }
}
