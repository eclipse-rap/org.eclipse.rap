/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

public class QxBorder implements QxType {

  // TODO [rst] Implement properties for left, right, etc.
  
  public final int width;
  
  public final String style;
  
  public final QxColor color;
  
  public static final String[] VALID_STYLES = new String[] {
    "groove",
    "ridge",
    "inset",
    "outset",
    "solid",
    "dotted",
    "dashed",
    "double",
    "none"
  };
  
  public QxBorder( final int width, final String style, final QxColor qxColor ) {
    this.width = width;
    this.style = style;
    this.color = qxColor;
  }
  
  public QxBorder( final String value ) {
    String[] parts = value.split( "\\s+" );
    if( parts.length != 3 ) {
      throw new IllegalArgumentException( "Illegal number of arguments for border" );
    }
    int width;
    String style;
    QxColor color;
    try {
      width = Integer.parseInt( parts[ 0 ] );
      if( width < 0 ) {
        throw new IllegalArgumentException( "negative width for border" );
      }
      style = parts[ 1 ];
      checkStyle( style );
      color = new QxColor( parts[ 2 ] );
      // TODO [rst] Also handle IAE for color here
    } catch( NumberFormatException e ) {
      throw new IllegalArgumentException( "Illegal number format" );
    }
    this.width = width;
    this.style = style;
    this.color = color;
  }
  
  private void checkStyle( final String style ) {
    boolean valid = false;
    for( int i = 0; i < VALID_STYLES.length; i++ ) {
      valid |= VALID_STYLES[ i ].equals( style );
    }
    if( !valid ) {
      throw new IllegalArgumentException( "Illegal style" );
    }
  }

  public static boolean isValidStyle( final String string ) {
    boolean result = false;
    for( int i = 0; i < VALID_STYLES.length && !result; i++ ) {
      result |= VALID_STYLES[ i ].equals( string );
    }
    return result;
  }
  
  public boolean equals( final Object object ) {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc. exist
    boolean result;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxBorder ) {
      QxBorder other = (QxBorder)object;
      result = false;
      result = ( other.width == this.width )
               && ( other.style.equals( this.style ) )
               && ( other.color.equals( this.color ) );
    } else {
      result = false;
    }
    return result;
  }
  
  public int hashCode() {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc. exist
    // TODO [rst] Revise this
    int result = 23;
    result += 37 * result + width;
    result += 37 * result + style.hashCode();
    result += 37 * result + color.hashCode();
    return result;
  }
  
  public String toString() {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc. exist
    return "QxBorder {"
    + width
    + ", "
    + style
    + ", "
    + color.name
    + "}";
  }
}
