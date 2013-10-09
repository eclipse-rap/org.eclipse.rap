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
package org.eclipse.rap.rwt.internal.template;

import static org.eclipse.rap.rwt.internal.util.ParamCheck.notNullOrEmpty;

import java.util.HashMap;
import java.util.Map;


public class CellData {

  private final HashMap<String, Object> data;

  public CellData() {
    data = new HashMap<String, Object>();
  }

  public void addAttribute( String key, Object value ) {
    checkNotNullOrEmpty( key );
    checkNotNull( value, "Value" );
    data.put( key, value );
  }

  public Map<String, Object> getAttributes() {
    return new HashMap<String, Object>( data );
  }

  private void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }

  private void checkNotNullOrEmpty( String name ) {
    if( name == null ) {
      throw new IllegalArgumentException( "Name must not be null" );
    }
    notNullOrEmpty( name, "Attribute name" );
  }
}
