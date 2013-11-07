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

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.swt.widgets.Widget;


public class TemplateLCAUtil {

  private static final String PROP_ROW_TEMPLATE = "rowTemplate";

  public static void renderRowTemplate( Widget widget ) {
    Object data = widget.getData( RowTemplate.ROW_TEMPLATE );
    if( data instanceof RowTemplate ) {
      getRemoteObject( widget ).set( PROP_ROW_TEMPLATE, toJson( ( RowTemplate )data ) );
    }
  }

  public static JsonValue toJson( RowTemplate template ) {
    JsonArray jsonArray = new JsonArray();
    for( Cell<?> cell : template.getCells() ) {
      jsonArray.add( cell.toJson() );
    }
    return jsonArray;
  }

  private TemplateLCAUtil() {
    // prevent instantiation
  }

}
