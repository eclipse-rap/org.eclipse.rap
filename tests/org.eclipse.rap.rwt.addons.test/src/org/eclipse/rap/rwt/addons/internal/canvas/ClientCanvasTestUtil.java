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

import org.eclipse.rap.json.JsonArray;


public class ClientCanvasTestUtil {

  private static final String PROPERTY_FOREGROUND = "foreground";
  private static final String PROPERTY_LINE_WIDTH = "lineWidth";
  private static final String PROPERTY_PATH = "path";

  public static final int LINE_WITH = 3;

  public static String createDrawingsWithoutLineWidth() {
    JsonArray drawings = new JsonArray();
    JsonArray polylineParam = createPathParam();
    drawings.add( polylineParam );
    JsonArray colorParam = createColorParam();
    drawings.add( colorParam );
    JsonArray polylineParam2 = createPathParam();
    drawings.add( polylineParam2 );
    return drawings.toString();
  }

  public static String createDrawings( int lineWidth ) {
    JsonArray drawings = new JsonArray();
    JsonArray polylineParam = createPathParam();
    drawings.add( polylineParam );
    JsonArray lineWidthParam = createLineWidthParam( lineWidth );
    drawings.add( lineWidthParam );
    JsonArray colorParam = createColorParam();
    drawings.add( colorParam );
    JsonArray polylineParam2 = createPathParam();
    drawings.add( polylineParam2 );
    return drawings.toString();
  }

  private static JsonArray createPathParam() {
    JsonArray drawingParam1 = new JsonArray();
    drawingParam1.add( PROPERTY_PATH );
    JsonArray path = createPath();
    drawingParam1.add( path );
    return drawingParam1;
  }

  private static JsonArray createPath() {
    JsonArray polyline = new JsonArray();
    polyline.add( 0 );
    polyline.add( 1 );
    polyline.add( 5 );
    polyline.add( 5 );
    polyline.add( 6 );
    polyline.add( 6 );
    polyline.add( 7 );
    polyline.add( 7 );
    polyline.add( 8 );
    polyline.add( 8 );
    return polyline;
  }

  private static JsonArray createLineWidthParam( int lineWidth ) {
    JsonArray drawingParam2 = new JsonArray();
    drawingParam2.add( PROPERTY_LINE_WIDTH );
    JsonArray lineWidthArray = new JsonArray();
    lineWidthArray.add( lineWidth );
    drawingParam2.add( lineWidthArray );
    return drawingParam2;
  }

  private static JsonArray createColorParam() {
    JsonArray drawingParam3 = new JsonArray();
    drawingParam3.add( PROPERTY_FOREGROUND );
    JsonArray color = new JsonArray();
    color.add( 50 );
    color.add( 100 );
    color.add( 200 );
    color.add( 10 ); // alpha
    drawingParam3.add( color );
    return drawingParam3;
  }

  private ClientCanvasTestUtil() {
    // prevent instantiation
  }

}
