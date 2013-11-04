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

import static org.eclipse.rap.rwt.internal.util.ParamCheck.notNull;
import static org.eclipse.rap.rwt.internal.util.ParamCheck.notNullOrEmpty;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public abstract class Cell<T extends Cell> implements Adaptable, Serializable  {

  public static enum CellAlignment {
    LEFT, RIGHT, H_CENTER, V_CENTER, TOP, BOTTOM
  }

  static final String PROPERTY_LEFT = "left";
  static final String PROPERTY_TOP = "top";
  static final String PROPERTY_RIGHT = "right";
  static final String PROPERTY_BOTTOM = "bottom";
  static final String PROPERTY_WIDTH = "width";
  static final String PROPERTY_HEIGHT = "height";
  static final String PROPERTY_BINDING_INDEX = "bindingIndex";
  static final String PROPERTY_SELECTABLE = "selectable";
  static final String PROPERTY_NAME = "name";
  static final String PROPERTY_FOREGROUND = "foreground";
  static final String PROPERTY_BACKGROUND = "background";
  static final String PROPERTY_FONT = "font";
  static final String PROPERTY_ALIGNMENT = "alignment";

  private final CellData data;
  private final String type;

  public Cell( RowTemplate template, String type ) {
    notNull( template, "template" );
    notNullOrEmpty( type, "type" );
    this.type = type;
    this.data = new CellData();
    template.addCell( this );
  }

  public String getType() {
    return type;
  }

  public T setName( String name ) {
    checkNotNullOrEmpty( name );
    data.addAttribute( PROPERTY_NAME, name );
    return getThis();
  }

  public T setBindingIndex( int index ) {
    ensurePositive( index, "BindingIndex" );
    data.addAttribute( PROPERTY_BINDING_INDEX, Integer.valueOf( index ) );
    return getThis();
  }

  public T setSelectable( boolean selectable ) {
    data.addAttribute( PROPERTY_SELECTABLE, Boolean.valueOf( selectable ) );
    return getThis();
  }

  public T setForeground( Color foreground ) {
    checkNotNull( foreground, "Foreground" );
    data.addAttribute( PROPERTY_FOREGROUND, foreground );
    return getThis();
  }

  public T setBackground( Color background ) {
    checkNotNull( background, "Background" );
    data.addAttribute( PROPERTY_BACKGROUND, background );
    return getThis();
  }

  public T setFont( Font font ) {
    checkNotNull( font, "Font" );
    data.addAttribute( PROPERTY_FONT, font );
    return getThis();
  }

  public T setLeft( int offset ) {
    checkHorizontalParameters( PROPERTY_RIGHT, PROPERTY_WIDTH );
    data.addAttribute( PROPERTY_LEFT, Integer.valueOf( offset ) );
    return getThis();
  }

  public T setRight( int offset ) {
    checkHorizontalParameters( PROPERTY_LEFT, PROPERTY_WIDTH );
    data.addAttribute( PROPERTY_RIGHT, Integer.valueOf( offset ) );
    return getThis();
  }

  public T setTop( int offset ) {
    checkVerticalParameters( PROPERTY_BOTTOM, PROPERTY_HEIGHT );
    data.addAttribute( PROPERTY_TOP, Integer.valueOf( offset ) );
    return getThis();
  }

  public T setBottom( int offset ) {
    checkVerticalParameters( PROPERTY_TOP, PROPERTY_HEIGHT );
    data.addAttribute( PROPERTY_BOTTOM, Integer.valueOf( offset ) );
    return getThis();
  }

  public T setWidth( int width ) {
    ensurePositive( width, "Width" );
    checkHorizontalParameters( PROPERTY_LEFT, PROPERTY_RIGHT );
    data.addAttribute( PROPERTY_WIDTH, Integer.valueOf( width ) );
    return getThis();
  }

  public T setHeight( int height ) {
    ensurePositive( height, "Height" );
    checkVerticalParameters( PROPERTY_TOP, PROPERTY_BOTTOM );
    data.addAttribute( PROPERTY_HEIGHT, Integer.valueOf( height ) );
    return getThis();
  }

  public T setAlignment( CellAlignment... alignment ) {
    checkNotNull( alignment, "Alignment" );
    String[] values = new String[ alignment.length ];
    for( int i = 0; i < alignment.length; i++ ) {
      CellAlignment cellAlignment = alignment[ i ];
      checkNotNull( cellAlignment, "Alignment" );
      values[ i ] = cellAlignment.toString();
    }
    addAttribute( PROPERTY_ALIGNMENT, values );
    return getThis();
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

  private void checkHorizontalParameters( String key1, String key2 ) {
    Map<String, Object> attributes = data.getAttributes();
    if( attributes.get( key1 ) != null && attributes.get( key2 ) != null ) {
      throw new IllegalArgumentException( "Can only set two horizontal attributes" );
    }
  }

  private void checkVerticalParameters( String key1, String key2 ) {
    Map<String, Object> attributes = data.getAttributes();
    if( attributes.get( key1 ) != null && attributes.get( key2 ) != null ) {
      throw new IllegalArgumentException( "Can only set two vertical attributes" );
    }
  }

  protected void addAttribute( String name, Object attribute ) {
    checkAttribute( name, attribute );
    data.addAttribute( name, attribute );
  }

  private void checkAttribute( String name, Object attribute ) {
    checkNotNullOrEmpty( name );
    checkNotNull( attribute, "Attribute" );
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

  @SuppressWarnings( "unchecked" )
  public <S> S getAdapter( Class<S> adapter ) {
    if( adapter == CellData.class ) {
      return ( S )data;
    }
    return null;
  }
}
