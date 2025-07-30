/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.internal.canvas;


import java.io.Serializable;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;

@SuppressWarnings("serial")
public class GCOperationDispatcher implements Serializable {

  public static final String PROPERTY_FOREGROUND = "foreground";
  public static final String PROPERTY_LINE_WIDTH = "lineWidth";
  public static final String PROPERTY_PATH = "path";

  private final GC gc;
  private final JsonArray drawings;

  public GCOperationDispatcher( GC gc, String drawings ) {
    this.gc = gc;
    this.drawings = JsonArray.readFrom( drawings );
  }

  public void dispatch() {
    doDispatch();
  }

  private void doDispatch() {
    int lineWidth = gc.getLineWidth();
    Color foreground = gc.getForeground();
    int alpha = gc.getAlpha();
    dispatchOperations();
    restoreLastSettings( lineWidth, foreground, alpha );
  }

  private void dispatchOperations() {
    for( int i = 0; i < drawings.size(); i++ ) {
      JsonArray operation = drawings.get( i ).asArray();
      dispatchOperation( operation );
    }
  }

  private void restoreLastSettings( int lineWidth, Color foreground, int alpha ) {
    gc.setLineWidth( lineWidth );
    gc.setForeground( foreground );
    gc.setAlpha( alpha );
  }

  private void dispatchOperation( JsonArray operation ) {
    String operationType = operation.get( 0 ).asString();
    JsonArray parameters = operation.get( 1 ).asArray();
    if( PROPERTY_LINE_WIDTH.equals( operationType ) ) {
      dispatchLineWidth( parameters );
    } else if( PROPERTY_FOREGROUND.equals( operationType ) ) {
      dispatchSetForeground( parameters );
    } else if( PROPERTY_PATH.equals( operationType ) ) {
      dispatchDrawPath( parameters );
    }
  }

  private void dispatchLineWidth( JsonArray parameters ) {
    int width = parameters.get( 0 ).asInt();
    gc.setLineWidth( width );
  }

  private void dispatchSetForeground( JsonArray parameters ) {
    int r = parameters.get( 0 ).asInt();
    int g = parameters.get( 1 ).asInt();
    int b = parameters.get( 2 ).asInt();
    int a = parameters.get( 3 ).asInt();
    gc.setForeground( new Color( gc.getDevice(), new RGB( r, g, b ) ) );
    gc.setAlpha( a );
  }

  private void dispatchDrawPath( JsonArray parameters ) {
    if( !parameters.isEmpty() ) {
      Path path = new Path( gc.getDevice() );
      createWayPoints( parameters, path );
      gc.drawPath( path );
    }
  }

  private static void createWayPoints( JsonArray parameters, Path path ) {
    path.moveTo( getFloatAtIndex( parameters, 0 ), getFloatAtIndex( parameters, 1 ) );
    for( int i = 2; i < parameters.size() - 4; i += 4 ) {
      path.quadTo( getFloatAtIndex( parameters, i ),
                   getFloatAtIndex( parameters, i + 1 ),
                   getFloatAtIndex( parameters, i + 2 ),
                   getFloatAtIndex( parameters, i + 3 ) );
    }
  }

  private static float getFloatAtIndex( JsonArray parameters, int index ) {
    return Double.valueOf( parameters.get( index ).asDouble() ).floatValue();
  }

}
