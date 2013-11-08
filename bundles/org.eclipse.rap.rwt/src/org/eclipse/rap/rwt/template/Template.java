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
package org.eclipse.rap.rwt.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.TemplateSerializer;


public class Template implements Serializable, Adaptable {

  private final List<Cell<?>> cells;

  public Template() {
    cells = new ArrayList<Cell<?>>();
  }

  void addCell( Cell<?> cell ) {
    checkCell( cell );
    cells.add( cell );
  }

  private void checkCell( Cell<?> cell ) {
    if( cell == null ) {
      throw new IllegalArgumentException( "Cell must not be null" );
    }
  }

  public List<Cell<?>> getCells() {
    return new ArrayList<Cell<?>>( cells );
  }

  @SuppressWarnings( "unchecked" )
  public <T> T getAdapter( Class<T> adapter ) {
    if( adapter == TemplateSerializer.class ) {
      return ( T )new TemplateSerializer() {
        public JsonValue toJson() {
          JsonArray jsonArray = new JsonArray();
          for( Cell<?> cell : getCells() ) {
            jsonArray.add( cell.toJson() );
          }
          return jsonArray;
        }
      };
    }
    return null;
  }

}
