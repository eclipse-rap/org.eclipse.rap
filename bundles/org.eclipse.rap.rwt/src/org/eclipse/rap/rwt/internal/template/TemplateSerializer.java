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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.JsonUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;


public class TemplateSerializer {

  private static final String PROPERTY_ATTRIBUTES = "attributes";
  private static final String PROPERTY_LAYOUT = "layout";
  private static final String PROPERTY_CELLS = "cells";

  private final RowTemplate template;

  public TemplateSerializer( RowTemplate template ) {
    checkTemplate( template );
    this.template = template;
  }

  private void checkTemplate( RowTemplate template ) {
    if( template == null ) {
      throw new IllegalArgumentException( "RowTemplate must not be null" );
    }
  }

  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    List<Cell> cells = template.getCells();
    if( !cells.isEmpty() ) {
      addCells( object, cells );
    }
    return object;
  }

  private void addCells( JsonObject object, List<Cell> cells ) {
    JsonArray serializedCells = new JsonArray();
    for( Cell cell : cells ) {
      addCell( serializedCells, cell );
    }
    object.add( PROPERTY_CELLS, serializedCells );
  }

  private void addCell( JsonArray serializedCells, Cell cell ) {
    JsonObject serializedCell = new JsonObject();
    addLayout( cell, serializedCell );
    addAttributes( cell, serializedCell );
    serializedCells.add( serializedCell );
  }

  private void addLayout( Cell cell, JsonObject serializedCell ) {
    JsonArray layout = new JsonArray();
    layout.add( getJsonValue( cell.getTop() ) );
    layout.add( getJsonValue( cell.getRight() ) );
    layout.add( getJsonValue( cell.getBottom() ) );
    layout.add( getJsonValue( cell.getLeft() ) );
    layout.add( getJsonValue( cell.getWidth() ) );
    layout.add( getJsonValue( cell.getHeight() ) );
    serializedCell.add( PROPERTY_LAYOUT, layout );
  }

  private void addAttributes( Cell cell, JsonObject serializedCell ) {
    Map<String, Object> attributes = cell.getAttributes();
    if( !attributes.isEmpty() ) {
      serializedCell.add( PROPERTY_ATTRIBUTES, getSerializedAttributes( attributes ) );
    }
  }

  private JsonObject getSerializedAttributes( Map<String, Object> attributes ) {
    JsonObject serializedAttributes = new JsonObject();
    for( Entry<String, Object> entry : attributes.entrySet() ) {
      serializedAttributes.add( entry.getKey(), getJsonValue( entry.getValue() ) );
    }
    return serializedAttributes;
  }

  private JsonValue getJsonValue( Object value ) {
    if( value instanceof Image ) {
      return ProtocolUtil.getJsonForImage( ( Image )value );
    }
    if( value instanceof Color ) {
      return ProtocolUtil.getJsonForColor( ( Color )value, false );
    }
    if( value instanceof RGB ) {
      return ProtocolUtil.getJsonForColor( ( RGB )value, false );
    }
    if( value instanceof Font ) {
      return ProtocolUtil.getJsonForFont( ( Font )value );
    }
    if( value instanceof FontData ) {
      return ProtocolUtil.getJsonForFont( ( FontData )value );
    }
    if( value instanceof Point ) {
      return ProtocolUtil.getJsonForPoint( ( Point )value );
    }
    if( value instanceof Rectangle ) {
      return ProtocolUtil.getJsonForRectangle( ( Rectangle )value );
    }
    return JsonUtil.createJsonValue( value );
  }
}
