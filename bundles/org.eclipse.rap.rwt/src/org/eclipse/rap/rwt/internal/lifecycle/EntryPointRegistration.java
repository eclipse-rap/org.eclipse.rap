/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.EntryPointFactory;


public class EntryPointRegistration {

  private final EntryPointFactory factory;
  private final Map<String, String> properties;

  public EntryPointRegistration( EntryPointFactory factory, Map<String, String> properties ) {
    this.factory = factory;
    this.properties = createPropertiesCopy( properties );
  }

  public EntryPointFactory getFactory() {
    return factory;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  private static Map<String, String> createPropertiesCopy( Map<String, String> properties ) {
    Map<String, String> result;
    if( properties != null ) {
      result = new HashMap<>( properties );
    } else {
      result = Collections.emptyMap();
    }
    return Collections.unmodifiableMap( result );
  }

}
