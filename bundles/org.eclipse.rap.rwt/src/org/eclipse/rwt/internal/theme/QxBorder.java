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

package org.eclipse.rwt.internal.theme;

public class QxBorder implements QxType {

  // TODO [rst] Implement properties for left, right, etc.

  private static final String DARKSHADOW_LIGHTSHADOW
    = getBorderColors( "widget.darkshadow", "widget.lightshadow" );
  private static final String LIGHTSHADOW_DARKSHADOW
    = getBorderColors( "widget.lightshadow", "widget.darkshadow" );
  private static final String SHADOW_HIGHLIGHT
    = getBorderColors( "widget.shadow", "widget.highlight" );
  private static final String HIGHLIGHT_SHADOW
    = getBorderColors( "widget.highlight", "widget.shadow" );

  public final int width;

  public final String style;

  // TODO [rst] Color is either a valid color string or a named color from the
  //            color theme. Check for valid colors.
  public final String color;

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

  public QxBorder( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    String[] parts = input.split( "\\s+" );
    if( parts.length > 3 ) {
      throw new IllegalArgumentException( "Illegal number of arguments for border" );
    }
    int width = -1;
    String style = null;
    String color = null;
    for( int i = 0; i < parts.length; i++ ) {
      String part = parts[ i ];
      boolean parsed = "".equals( part );
      // parse style
      if( !parsed && style == null ) {
        String parsedStyle = parseStyle( part );
        if( parsedStyle != null ) {
          style = parsedStyle;
          parsed = true;
        }
      }
      // parse width
      if( !parsed && width == -1 ) {
        Integer parsedWidth = QxDimension.parseLength( part );
        if( parsedWidth != null ) {
          if( parsedWidth.intValue() < 0 ) {
            throw new IllegalArgumentException( "Negative width: " + part );
          }
          width = parsedWidth.intValue();
          parsed = true;
        }
      }
      // parse color
      if( !parsed && color == null ) {
        color = part;
        parsed = true;
      }
      if( !parsed ) {
        throw new IllegalArgumentException( "Illegal parameter for color: "
                                            + part );
      }
    }
    if( "none".equals( style ) ) {
      style = "solid";
      width = 0;
    }
    this.width = width == -1 ? 1 : width;
    this.style = style == null ? "solid" : style;
    this.color = color;
  }

  public String getQxStyle() {
    String result = style;
    if( color == null ) {
      if( ( "outset".equals( style ) || "inset".equals( style ) )
          && ( width == 1 || width == 2 ) )
      {
        result = "solid";
      } else if( ( "ridge".equals( style ) || "groove".equals( style ) )
                 && width == 2 )
      {
        result = "solid";
      }
    }
    return result;
  }

  public String getQxColors() {
    String result = null;
    if( color == null && width == 2 ) {
      if( "outset".equals( style ) ) {
        result = LIGHTSHADOW_DARKSHADOW;
      } else if( "inset".equals( style ) ) {
        result = SHADOW_HIGHLIGHT;
      } else if( "ridge".equals( style ) ) {
        result = HIGHLIGHT_SHADOW;
      } else if( "groove".equals( style ) ) {
        result = SHADOW_HIGHLIGHT;
      }
    } else if( color == null && width == 1 ) {
      if( "outset".equals( style ) ) {
        result = HIGHLIGHT_SHADOW;
      } else if( "inset".equals( style ) ) {
        result = SHADOW_HIGHLIGHT;
      }
    }
    if( result == null ) {
      result = color == null ? null : "\"" + color + "\"";
    }
    return result;
  }

  public String getQxInnerColors() {
    String result = null;
    if( color == null && width == 2 ) {
      if( "outset".equals( style ) ) {
        result = HIGHLIGHT_SHADOW;
      } else if( "inset".equals( style ) ) {
        result = DARKSHADOW_LIGHTSHADOW;
      } else if( "ridge".equals( style ) ) {
        result = SHADOW_HIGHLIGHT;
      } else if( "groove".equals( style ) ) {
        result = HIGHLIGHT_SHADOW;
      }
    }
    return result;
  }

  public String toDefaultString() {
    StringBuffer result = new StringBuffer();
    if( width == 0 ) {
      result.append( "none" );
    } else {
      result.append( width + "px" );
      result.append( " " );
      result.append( style );
      if( color != null ) {
        result.append( " " );
        result.append( color );
      }
    }
    return result.toString();
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
    // TODO [rst] Adapt this method as soon as properties for left, right, etc.
    //            exist
    // TODO [rst] Revise this
    int result = 23;
    result += 37 * result + width;
    result += 37 * result + style.hashCode();
    result += 37 * result + color.hashCode();
    return result;
  }

  public String toString() {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc.
    //            exist
    return "QxBorder {" + width + ", " + style + ", " + color + "}";
  }

  public static boolean isValidStyle( final String string ) {
    boolean result = false;
    for( int i = 0; i < VALID_STYLES.length && !result; i++ ) {
      result |= VALID_STYLES[ i ].equals( string );
    }
    return result;
  }

  private static String getBorderColors( final String color1, final String color2 ) {
    StringBuffer result = new StringBuffer();
    result.append( "[ \"");
    result.append( color1 );
    result.append( "\", \"");
    result.append( color2 );
    result.append( "\", \"");
    result.append( color2 );
    result.append( "\", \"");
    result.append( color1 );
    result.append( "\" ]");
    return result.toString();
  }

  private static String parseStyle( final String part ) {
    String result = null;
    for( int j = 0; j < VALID_STYLES.length && result == null; j++ ) {
      if( VALID_STYLES[ j ].equalsIgnoreCase( part ) ) {
        result = VALID_STYLES[ j ];
      }
    }
    return result;
  }
}
