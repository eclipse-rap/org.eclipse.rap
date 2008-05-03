/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
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

  public static final QxBorder NONE = new QxBorder( 0, null, null );

  private static final String[] VALID_STYLES = new String[] {
    "none",
    "hidden",
    "dotted",
    "dashed",
    "solid",
    "double",
    "groove",
    "ridge",
    "inset",
    "outset"
  };

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

  private QxBorder( final int width, final String style, final String color ) {
    this.width = width;
    this.style = style;
    this.color = color;
  }

  public static QxBorder create( final int width,
                                 final String style,
                                 final String color )
  {
    QxBorder result;
    if( width == 0 || "none".equals( style ) || "hidden".equals( style ) ) {
      result = NONE;
    } else {
      result = new QxBorder( width, style == null ? "solid" : style, color );
    }
    return result;
  }

  public static QxBorder valueOf( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    String[] parts = input.split( "\\s+" );
    if( input.trim().length() == 0 ) {
      throw new IllegalArgumentException( "Empty border definition" );
    }
    if( parts.length > 3 ) {
      throw new IllegalArgumentException( "Illegal number of arguments for border" );
    }
    int width = -1;
    String style = null;
    String color = null;
    for( int i = 0; i < parts.length; i++ ) {
      String part = parts[ i ];
      boolean consumed = "".equals( part );
      // parse width
      if( !consumed && width == -1 ) {
        Integer parsedWidth = QxDimension.parseLength( part );
        if( parsedWidth != null ) {
          if( parsedWidth.intValue() < 0 ) {
            throw new IllegalArgumentException( "Negative width: " + part );
          }
          width = parsedWidth.intValue();
          consumed = true;
        }
      }
      // parse style
      if( !consumed && style == null ) {
        String parsedStyle = parseStyle( part );
        if( parsedStyle != null ) {
          style = parsedStyle;
          consumed = true;
        }
      }
      // parse color
      if( !consumed && color == null ) {
        color = part;
        consumed = true;
      }
      if( !consumed ) {
        throw new IllegalArgumentException( "Illegal parameter for color: "
                                            + part );
      }
    }
    if( width == -1 ) {
      width = 1;
    }
    return QxBorder.create( width, style, color );
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

  /**
   * Returns the colors to pass to qx for use default widget colors for 3d
   * borders. When a 3d border style is used and no color has been set, this
   * method returns an array of the default widget colors for the style.
   */
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

  /**
   * Returns the inner colors to pass to qx for use default widget colors for 3d
   * borders. When a 3d border style is used and no color has been set, this
   * method returns an array of the default widget colors for the style.
   */
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
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxBorder ) {
      QxBorder other = (QxBorder)object;
      result = other.width == this.width
               && style == null
                  ? other.style == null
                  : style.equals( other.style )
               && color == null
                  ? other.color == null
                  : color.equals( other.color );
    }
    return result;
  }

  public int hashCode() {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc.
    //            exist
    int result = 23;
    result += 37 * result + width;
    if( style != null ) {
      result += 37 * result + style.hashCode();
    }
    if( color != null ) {
      result += 37 * result + color.hashCode();
    }
    return result;
  }

  public String toString() {
    // TODO [rst] Adapt this method as soon as properties for left, right, etc.
    //            exist
    return "QxBorder{ " + width + ", " + style + ", " + color + " }";
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
