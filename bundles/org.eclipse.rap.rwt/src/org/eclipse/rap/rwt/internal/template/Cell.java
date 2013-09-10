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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Cell implements Serializable {

  private final Map<String, Object> attributes;
  private Integer column;
  private Integer top;
  private Integer left;
  private Integer width;
  private Integer height;
  private Integer bottom;
  private Integer right;
  private final String type;

  public Cell( RowTemplate template, String type ) {
    checkType( type );
    checkTemplate( template );
    this.type = type;
    this.attributes = new HashMap<String, Object>();
    template.addCell( this );
  }

  private void checkType( String type ) {
    if( type == null || type.isEmpty() ) {
      throw new IllegalArgumentException( "Invalid type" );
    }
  }

  private void checkTemplate( RowTemplate template ) {
    if( template == null ) {
      throw new IllegalArgumentException( "RowTemplate must not be null" );
    }
  }

  public String getType() {
    return type;
  }

  public Cell setColumn( int index ) {
    ensurePositive( index, "Column" );
    column = Integer.valueOf( index );
    return this;
  }

  public Integer getColumn() {
    return column;
  }

  public Cell setLeft( int offset ) {
    checkHorizontalParameters( right, width );
    left = Integer.valueOf( offset );
    return this;
  }

  public Integer getLeft() {
    return left;
  }

  public Cell setRight( int offset ) {
    checkHorizontalParameters( left, width );
    right = Integer.valueOf( offset );
    return this;
  }

  public Integer getRight() {
    return right;
  }

  public Cell setTop( int offset ) {
    checkVerticalParameters( bottom, height );
    top = Integer.valueOf( offset );
    return this;
  }

  public Integer getTop() {
    return top;
  }

  public Cell setBottom( int offset ) {
    checkVerticalParameters( top, height );
    bottom = Integer.valueOf( offset );
    return this;
  }

  public Integer getBottom() {
    return bottom;
  }

  public Cell setWidth( int width ) {
    ensurePositive( width, "Width" );
    checkHorizontalParameters( left, right );
    this.width = Integer.valueOf( width );
    return this;
  }

  public Integer getWidth() {
    return width;
  }

  public Cell setHeight( int height ) {
    ensurePositive( height, "Height" );
    checkVerticalParameters( top, bottom );
    this.height = Integer.valueOf( height );
    return this;
  }

  public Integer getHeight() {
    return height;
  }

  private void ensurePositive( int value, String valueName ) {
    if( value < 0 ) {
      throw new IllegalArgumentException( valueName + " must be >= 0 but was " + value );
    }
  }

  private void checkHorizontalParameters( Integer value1, Integer value2 ) {
    if( value1 != null && value2 != null ) {
      throw new IllegalArgumentException( "Can only set two horizontal parameters" );
    }
  }

  private void checkVerticalParameters( Integer value1, Integer value2 ) {
    if( value1 != null && value2 != null ) {
      throw new IllegalArgumentException( "Can only set two vertical parameters" );
    }
  }

  public void addAttribute( String name, Object attribute ) {
    checkAttribute( name, attribute );
    attributes.put( name, attribute );
  }

  private void checkAttribute( String name, Object attribute ) {
    if( name == null ) {
      throw new IllegalArgumentException( "Name must not be null" );
    }
    notNullOrEmpty( name, "Attribute name" );
    if( attribute == null ) {
      throw new IllegalArgumentException( "Attribute must not be null" );
    }
  }

  public Map<String, Object> getAttributes() {
    return new HashMap<String, Object>( attributes );
  }
}
