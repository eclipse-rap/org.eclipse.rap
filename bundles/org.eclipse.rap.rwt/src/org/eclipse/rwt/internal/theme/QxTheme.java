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


/**
 * Instances of this class represent a qooxdoo theme of a certain type. Used to
 * assemble the Javascript code for qooxdoo themes.
 */
public class QxTheme {

  private final String id;

  private final String title;

  private final int type;

  private final String base;

  private final StringBuffer code;

  private boolean headWritten;

  private boolean tailWritten;

  private boolean valueWritten;

  /** Type for qooxdoo meta themes */
  public static final int META = 1;

  /** Type for qooxdoo font themes */
  public static final int FONT = 2;

  /** Type for qooxdoo color themes */
  public static final int COLOR = 3;

  /** Type for qooxdoo border themes */
  public static final int BORDER = 4;

  /** Type for qooxdoo icon themes */
  public static final int ICON = 5;

  /** Type for qooxdoo widget themes */
  public static final int WIDGET = 6;

  /** Type for qooxdoo apearance themes */
  public static final int APPEARANCE = 7;

  /**
   * Creates a new qooxdoo theme with the given, id, name, and type.
   *
   * @param id the fully qualified qx class name for the theme
   * @param title the name of the theme
   * @param type the type of the theme
   */
  public QxTheme( final String id, final String title, final int type ) {
    this( id, title, type, null );
  }

  /**
   * Creates a new qooxdoo theme with the given, id, name, type, and base class.
   *
   * @param id the fully qualified qx class name for the theme
   * @param title the name of the theme
   * @param type the type of the theme
   * @param base the fully qualified name of the qx theme to extend
   */
  public QxTheme( final String id,
                  final String title,
                  final int type,
                  final String base )
  {
    this.id = id;
    this.title = title;
    this.type = checkType( type );
    this.base = base;
    this.code = new StringBuffer();
    headWritten = false;
    tailWritten = false;
    valueWritten = false;
  }

  /**
   * Appends a number of key-value pairs to the generated theme. The given
   * Javascript code is appended as is, without any checks being performed.
   *
   * @param values Javascript code that adds the additional values
   */
  public void appendValues( final String values ) {
    beforeWriteValue();
    code.append( values );
    afterWriteValue();
  }

  /**
   * Appends a key-value pair to the generated font theme. Only applicable for
   * instances with type FONT.
   *
   * @param key the key to append
   * @param font the value for the key
   */
  public void appendFont( final String key, final QxFont font ) {
    beforeWriteValue();
    code.append( "    \"" + key + "\" : { " );
    code.append( "family: [" );
    for( int i = 0; i < font.family.length; i++ ) {
      if( i > 0 ) {
        code.append( " ," );
      }
      code.append( "\"" );
      code.append( font.family[ i ] );
      code.append( "\"" );
    }
    code.append( "]" );
    code.append( ", size: " );
    code.append( font.size );
    if( font.bold ) {
      code.append( ", bold: true" );
    }
    if( font.italic ) {
      code.append( ", italic: true" );
    }
    code.append( " }" );
    afterWriteValue();
  }

  /**
   * Appends a key-value pair to the generated color theme. Only applicable for
   * instances with type COLOR.
   *
   * @param key the key to append
   * @param color the value for the key
   */
  public void appendColor( final String key, final QxColor color ) {
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    code.append( "[ " );
    code.append( color.red );
    code.append( ", " );
    code.append( color.green );
    code.append( ", " );
    code.append( color.blue );
    code.append( " ]" );
    afterWriteValue();
  }

  /**
   * Appends a key-value pair to the generated border theme. Only applicable for
   * instances with type BORDER.
   *
   * @param key the key to append
   * @param border the value for the key
   */
  public void appendBorder( final String key, final QxBorder border ) {
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    // none
    code.append( "{ width : " );
    code.append( border.width );
    String style = border.getQxStyle();
    if( style != null && !"solid".equals( style ) ) {
      code.append( ", style : \"" );
      code.append( style );
      code.append( "\"" );
    }
    String colors = border.getQxColors();
    if( colors != null ) {
      code.append( ", color : " );
      code.append( colors );
    }
    String innerColor = border.getQxInnerColors();
    if( innerColor != null ) {
      code.append( ", innerColor : " );
      code.append( innerColor );
    }
    code.append( " }" );
    afterWriteValue();
  }

  /**
   * Appends the single uri entry to the generated widget or icon theme. Only
   * applicable for instances with type WIDGET or ICON.
   *
   * @param pathPrefix the prefix to map "widget/" or "icon/" to
   */
  public void appendUri( final String pathPrefix ) {
    beforeWriteValue();
    code.append( "    \"uri\" : \"" );
    code.append( pathPrefix );
    code.append( "\"" );
    afterWriteValue();
  }

  /**
   * Appends a key-value pair to the generated theme. Only applicable for
   * META theme writers.
   *
   * @param key the key to append
   * @param theme the value for the key
   */
  public void appendTheme( final String key, final String theme ) {
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    code.append( theme );
    afterWriteValue();
  }

  /**
   * Returns the Javascript code that represents this theme. Once this method
   * has been called, no values can be appended anymore.
   *
   * @return the generated theme code.
   */
  public String getJsCode() {
    if( !headWritten ) {
      writeHead();
    }
    if( !tailWritten ) {
      writeTail();
    }
    return code.toString();
  }

  private void beforeWriteValue() {
    if( !headWritten ) {
      writeHead();
    }
    if( tailWritten ) {
      throw new IllegalStateException( "Tail already written" );
    }
    if( valueWritten ) {
      code.append( ",\n" );
    }
  }

  private void afterWriteValue() {
    valueWritten = true;
  }

  private void writeHead() {
    code.append( "/* RAP theme file generated by QxTheme. */\n" );
    code.append( "qx.Theme.define( \"" + id + getNameSuffix() + "\",\n" );
    code.append( "{\n" );
    code.append( "  title : \"" + title + "\",\n" );
    if( base != null ) {
      code.append( "  extend : " + base + ",\n" );
    }
    code.append( "  " + getThemeKey() + " : {\n" );
    headWritten = true;
  }

  private void writeTail() {
    code.append( "\n" );
    code.append( "  }\n" );
    code.append( "} );\n" );
    tailWritten = true;
  }

  private int checkType( final int type ) {
    if( type != META
        && type != FONT
        && type != COLOR
        && type != BORDER
        && type != ICON
        && type != WIDGET
        && type != APPEARANCE )
    {
      throw new IllegalArgumentException( "illegal type" );
    }
    return type;
  }

  private String getNameSuffix() {
    String result = "";
    if( type == FONT ) {
      result = "Fonts";
    } else if( type == COLOR ) {
      result = "Colors";
    } else if( type == BORDER ) {
      result = "Borders";
    } else if( type == ICON ) {
      result = "Icons";
    } else if( type == WIDGET ) {
      result = "Widgets";
    } else if( type == APPEARANCE ) {
      result = "Appearances";
    }
    return result;
  }

  private String getThemeKey() {
    String result = null;
    if( type == META ) {
      result = "meta";
    } else if( type == FONT ) {
      result = "fonts";
    } else if( type == COLOR ) {
      result = "colors";
    } else if( type == BORDER ) {
      result = "borders";
    } else if( type == ICON ) {
      result = "icons";
    } else if( type == WIDGET ) {
      result = "widgets";
    } else if( type == APPEARANCE ) {
      result = "appearances";
    }
    return result;
  }
}
