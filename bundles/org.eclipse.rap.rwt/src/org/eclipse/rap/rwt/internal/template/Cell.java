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

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForColor;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForFont;
import static org.eclipse.rap.rwt.internal.util.ParamCheck.notNull;
import static org.eclipse.rap.rwt.internal.util.ParamCheck.notNullOrEmpty;

import java.io.Serializable;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public abstract class Cell<T extends Cell> implements Serializable  {

  public static enum CellAlignment {
    LEFT, RIGHT, H_CENTER, V_CENTER, TOP, BOTTOM
  }

  private static final String PROPERTY_TYPE = "type";
  private static final String PROPERTY_LEFT = "left";
  private static final String PROPERTY_TOP = "top";
  private static final String PROPERTY_RIGHT = "right";
  private static final String PROPERTY_BOTTOM = "bottom";
  private static final String PROPERTY_WIDTH = "width";
  private static final String PROPERTY_HEIGHT = "height";
  private static final String PROPERTY_BINDING_INDEX = "bindingIndex";
  private static final String PROPERTY_SELECTABLE = "selectable";
  private static final String PROPERTY_NAME = "name";
  private static final String PROPERTY_FOREGROUND = "foreground";
  private static final String PROPERTY_BACKGROUND = "background";
  private static final String PROPERTY_FONT = "font";
  private static final String PROPERTY_ALIGNMENT = "alignment";

  private final String type;
  private String name;
  private int bindingIndex;
  private boolean isSelectable;
  private Color foreground;
  private Color background;
  private Font font;
  private Integer left;
  private Integer right;
  private Integer top;
  private Integer bottom;
  private Integer width;
  private Integer height;
  private CellAlignment[] alignment;

  public Cell( RowTemplate template, String type ) {
    notNull( template, "template" );
    notNullOrEmpty( type, "type" );
    this.type = type;
    bindingIndex = -1;
    template.addCell( this );
  }

  String getType() {
    return type;
  }

  public T setName( String name ) {
    checkNotNullOrEmpty( name );
    this.name = name;
    return getThis();
  }

  String getName() {
    return name;
  }

  public T setBindingIndex( int index ) {
    ensurePositive( index, "BindingIndex" );
    bindingIndex = index;
    return getThis();
  }

  int getBindingIndex() {
    return bindingIndex;
  }

  public T setSelectable( boolean selectable ) {
    isSelectable = selectable;
    return getThis();
  }

  boolean isSelectable() {
    return isSelectable;
  }

  public T setForeground( Color foreground ) {
    checkNotNull( foreground, "Foreground" );
    this.foreground = foreground;
    return getThis();
  }

  Color getForeground() {
    return foreground;
  }

  public T setBackground( Color background ) {
    checkNotNull( background, "Background" );
    this.background = background;
    return getThis();
  }

  Color getBackground() {
    return background;
  }

  public T setFont( Font font ) {
    checkNotNull( font, "Font" );
    this.font = font;
    return getThis();
  }

  Font getFont() {
    return font;
  }

  public T setLeft( int offset ) {
    checkHorizontalParameters( right, width );
    left = Integer.valueOf( offset );
    return getThis();
  }

  Integer getLeft() {
    return left;
  }

  public T setRight( int offset ) {
    checkHorizontalParameters( left, width );
    this.right = Integer.valueOf( offset );
    return getThis();
  }

  Integer getRight() {
    return right;
  }

  public T setTop( int offset ) {
    checkVerticalParameters( bottom, height );
    this.top = Integer.valueOf( offset );
    return getThis();
  }

  Integer getTop() {
    return top;
  }

  public T setBottom( int offset ) {
    checkVerticalParameters( top, height );
    this.bottom = Integer.valueOf( offset );
    return getThis();
  }

  Integer getBottom() {
    return bottom;
  }

  public T setWidth( int width ) {
    ensurePositive( width, "Width" );
    checkHorizontalParameters( left, right );
    this.width = Integer.valueOf( width );
    return getThis();
  }

  Integer getWidth() {
    return width;
  }

  public T setHeight( int height ) {
    ensurePositive( height, "Height" );
    checkVerticalParameters( top, bottom );
    this.height = Integer.valueOf( height );
    return getThis();
  }

  Integer getHeight() {
    return height;
  }

  public T setAlignment( CellAlignment... alignment ) {
    checkNotNull( alignment, "Alignment" );
    for( int i = 0; i < alignment.length; i++ ) {
      checkNotNull( alignment[ i ], "Alignment" );
    }
    this.alignment = alignment;
    return getThis();
  }

  CellAlignment[] getAlignment() {
    return alignment;
  }
  @SuppressWarnings( "unchecked" )
  private T getThis() {
    return ( T )this;
  }

  private void ensurePositive( int value, String valueName ) {
    if( value < 0 ) {
      throw new IllegalArgumentException( valueName + " must be >= 0 but was " + value );
    }
  }

  private void checkHorizontalParameters( Integer value1, Integer value2 ) {
    if( value1 != null && value2 != null ) {
      throw new IllegalArgumentException( "Can only set two horizontal attributes" );
    }
  }

  private void checkVerticalParameters( Integer value1, Integer value2 ) {
    if( value1 != null && value2 != null ) {
      throw new IllegalArgumentException( "Can only set two vertical attributes" );
    }
  }

  protected JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add( PROPERTY_TYPE, type );
    if( left != null ) {
      jsonObject.add( PROPERTY_LEFT, left.intValue() );
    }
    if( right != null ) {
      jsonObject.add( PROPERTY_RIGHT, right.intValue() );
    }
    if( top != null ) {
      jsonObject.add( PROPERTY_TOP, top.intValue() );
    }
    if( bottom != null ) {
      jsonObject.add( PROPERTY_BOTTOM, bottom.intValue() );
    }
    if( width != null ) {
      jsonObject.add( PROPERTY_WIDTH, width.intValue() );
    }
    if( height != null ) {
      jsonObject.add( PROPERTY_HEIGHT, height.intValue() );
    }
    if( bindingIndex != -1 ) {
      jsonObject.add( PROPERTY_BINDING_INDEX, bindingIndex );
    }
    if( isSelectable ) {
      jsonObject.add( PROPERTY_SELECTABLE, isSelectable );
    }
    if( name != null ) {
      jsonObject.add( PROPERTY_NAME, name );
    }
    if( foreground != null ) {
      jsonObject.add( PROPERTY_FOREGROUND, getJsonForColor( foreground, false ) );
    }
    if( background != null ) {
      jsonObject.add( PROPERTY_BACKGROUND, getJsonForColor( background, false ) );
    }
    if( font != null ) {
      jsonObject.add( PROPERTY_FONT, getJsonForFont( font ) );
    }
    if( alignment != null ) {
      jsonObject.add( PROPERTY_ALIGNMENT, alignmentToJson( alignment ) );
    }
    return jsonObject;
  }

  private static JsonArray alignmentToJson( CellAlignment[] alignments ) {
    JsonArray result = new JsonArray();
    for( CellAlignment cellAlignment : alignments ) {
      result.add( cellAlignment.toString() );
    }
    return result;
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
