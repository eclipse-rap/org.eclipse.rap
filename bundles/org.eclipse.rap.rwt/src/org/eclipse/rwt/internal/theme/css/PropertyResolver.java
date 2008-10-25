/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.*;
import org.w3c.css.sac.LexicalUnit;


/**
 * Utility class to read values from LexicalUnits.
 */
public final class PropertyResolver {

  private static final String BOLD = "bold";

  private static final String ITALIC = "italic";

  private static final String NORMAL = "normal";
  // No border; the computed border width is zero.
  private static final String NONE = "none";
  // Same as 'none', except in terms of border conflict resolution for table
  // elements.
  private static final String HIDDEN = "hidden";
  // The border is a series of dots.
  private static final String DOTTED = "dotted";
  // The border is a series of short line segments.
  private static final String DASHED = "dashed";
  // The border is a single line segment.
  private static final String SOLID = "solid";
  // The border is two solid lines. The sum of the two lines and the space
  // between them equals the value of 'border-width'.
  private static final String DOUBLE = "double";
  // The border looks as though it were carved into the canvas.
  private static final String GROOVE = "groove";
  // The opposite of 'groove': the border looks as though it were coming out of
  // the canvas.
  private static final String RIDGE = "ridge";
  // The border makes the box look as though it were embedded in the canvas.
  private static final String INSET = "inset";
  // The opposite of 'inset': the border makes the box look as though it were
  // coming out of the canvas.
  private static final String OUTSET = "outset";

  /** A thin border. */
  private static final String THIN = "thin";

  /** A medium border. */
  private static final String MEDIUM = "medium";

  /** A thick border. */
  private static final String THICK = "thick";

  private static final String TRANSPARENT = "transparent";

  private static final Map NAMED_COLORS = new HashMap();

  private static final List BORDER_STYLES = new ArrayList();

  /** Width value for "thin" identifier. */
  static final int THIN_VALUE = 1;

  /** Width value for "medium" identifier. */
  static final int MEDIUM_VALUE = 3;

  /** Width value for "thick" identifier. */
  static final int THICK_VALUE = 5;

  static {
    // register 16 standard HTML colors
    NAMED_COLORS.put( "black", new int[] { 0, 0, 0 } );
    NAMED_COLORS.put( "gray", new int[] { 128, 128, 128 } );
    NAMED_COLORS.put( "silver", new int[] { 192, 192, 192 } );
    NAMED_COLORS.put( "white", new int[] { 255, 255, 255 } );
    NAMED_COLORS.put( "maroon", new int[] { 128, 0, 0 } );
    NAMED_COLORS.put( "red", new int[] { 255, 0, 0 } );
    NAMED_COLORS.put( "purple", new int[] { 128, 0, 128 } );
    NAMED_COLORS.put( "fuchsia", new int[] { 255, 0, 255 } );
    NAMED_COLORS.put( "green", new int[] { 0, 128, 0 } );
    NAMED_COLORS.put( "lime", new int[] { 0, 255, 0 } );
    NAMED_COLORS.put( "navy", new int[] { 0, 0, 128 } );
    NAMED_COLORS.put( "blue", new int[] { 0, 0, 255 } );
    NAMED_COLORS.put( "olive", new int[] { 128, 128, 0 } );
    NAMED_COLORS.put( "yellow", new int[] { 255, 255, 0 } );
    NAMED_COLORS.put( "teal", new int[] { 0, 128, 128 } );
    NAMED_COLORS.put( "aqua", new int[] { 0, 255, 255 } );
    // register border styles
    BORDER_STYLES.add( NONE );
    BORDER_STYLES.add( HIDDEN );
    BORDER_STYLES.add( DOTTED );
    BORDER_STYLES.add( DASHED );
    BORDER_STYLES.add( SOLID );
    BORDER_STYLES.add( DOUBLE );
    BORDER_STYLES.add( GROOVE );
    BORDER_STYLES.add( RIDGE );
    BORDER_STYLES.add( INSET );
    BORDER_STYLES.add( OUTSET );
  }

  public static QxType resolveProperty( final String name,
                                        final LexicalUnit unit,
                                        final ResourceLoader loader )
  {
    QxType result;
    String type = getType( name );
    if( type == null ) {
      throw new IllegalArgumentException( "Unknown property " + name );
    }
    if( ThemeDefinitionReader.TYPE_BOOLEAN.equals( type ) ) {
      throw new IllegalArgumentException( "Currently not supported" );
    } else if( ThemeDefinitionReader.TYPE_BORDER.equals( type ) ) {
      result = readBorder( unit );
    } else if( ThemeDefinitionReader.TYPE_BOXDIMENSIONS.equals( type ) ) {
      result = readBoxDimensions( unit );
    } else if( ThemeDefinitionReader.TYPE_COLOR.equals( type ) ) {
      result = readColor( unit );
    } else if( ThemeDefinitionReader.TYPE_DIMENSION.equals( type ) ) {
      result = readDimension( unit );
    } else if( ThemeDefinitionReader.TYPE_FONT.equals( type ) ) {
      result = readFont( unit );
    } else if( ThemeDefinitionReader.TYPE_IMAGE.equals( type ) ) {
      result = readBackgroundImage( unit, loader );
    } else {
      throw new RuntimeException( "Illegal type " + type );
    }
    if( result == null ) {
      throw new IllegalArgumentException( "Failed to parse value "
                                          + toString( unit ) );
    }
    return result;
  }

  public static String getType( final String property ) {
    // TODO [rst] respect properties declared in theme.xml files
    String result = null;
    if( "padding".equals( property ) || "margin".equals( property ) ) {
      result = ThemeDefinitionReader.TYPE_BOXDIMENSIONS;
    } else if( "color".equals( property )
        || "background-color".equals( property )
        || property.endsWith( "-color" ) )
    {
      result = ThemeDefinitionReader.TYPE_COLOR;
    } else if( "font".equals( property ) ) {
      result = ThemeDefinitionReader.TYPE_FONT;
    } else if( "border".equals( property ) ) {
      result = ThemeDefinitionReader.TYPE_BORDER;
    } else if( "spacing".equals( property )
        || "width".equals( property )
        || "height".equals( property ) )
    {
      result = ThemeDefinitionReader.TYPE_DIMENSION;
    } else if( "background-image".equals( property ) ) {
      result = ThemeDefinitionReader.TYPE_IMAGE;
    }
    return result;
  }

  static QxColor readColor( final LexicalUnit input ) {
    QxColor result = null;
    short type = input.getLexicalUnitType();
    if( type == LexicalUnit.SAC_RGBCOLOR ) {
      LexicalUnit redParam = input.getParameters();
      LexicalUnit greenParam = redParam.getNextLexicalUnit().getNextLexicalUnit();
      LexicalUnit blueParam = greenParam.getNextLexicalUnit().getNextLexicalUnit();
      short valueType = redParam.getLexicalUnitType();
      if( greenParam.getLexicalUnitType() == valueType
          || blueParam.getLexicalUnitType() == valueType )
      {
        if( valueType == LexicalUnit.SAC_INTEGER ) {
          int red = normalizeRGBValue( redParam.getIntegerValue() );
          int green = normalizeRGBValue( greenParam.getIntegerValue() );
          int blue = normalizeRGBValue( blueParam.getIntegerValue() );
          result = QxColor.create( red, green, blue );
        } else if( valueType == LexicalUnit.SAC_PERCENTAGE ) {
          float redPercent = normalizePercentValue( redParam.getFloatValue() );
          float greenPercent = normalizePercentValue( greenParam.getFloatValue() );
          float bluePercent = normalizePercentValue( blueParam.getFloatValue() );
          int red = ( int )( 255 * redPercent / 100 );
          int green = ( int )( 255 * greenPercent / 100 );
          int blue = ( int )( 255 * bluePercent / 100 );
          result = QxColor.create( red, green, blue );
        }
      }
    } else if( type == LexicalUnit.SAC_IDENT ) {
      String string = input.getStringValue();
      if( TRANSPARENT.equals( string ) ) {
        result = QxColor.TRANSPARENT;
      } else if( NAMED_COLORS.containsKey( string.toLowerCase() ) ) {
        int[] values = ( int[] )NAMED_COLORS.get( string.toLowerCase() );
        result = QxColor.create( values[ 0 ], values[ 1 ], values[ 2 ] );
      }
    }
    return result;
  }

  static QxDimension readDimension( final LexicalUnit unit ) {
    QxDimension result = null;
    Integer length = readSingleLengthUnit( unit );
    if( length != null ) {
      result = QxDimension.create( length.intValue() );
    }
    return result;
  }

  static QxBoxDimensions readBoxDimensions( final LexicalUnit unit ) {
    QxBoxDimensions result = null;
    Integer value1 = readSingleLengthUnit( unit );
    if( value1 != null ) {
      int top, right, left, bottom;
      top = right = bottom = left = value1.intValue();
      LexicalUnit nextUnit = unit.getNextLexicalUnit();
      boolean ok = true;
      int pos = 1;
      while( nextUnit != null && ok ) {
        pos++;
        Integer nextValue = readSingleLengthUnit( nextUnit );
        ok &= nextValue != null && pos <= 4;
        if( ok ) {
          if( pos == 2 ) {
            right = left = nextValue.intValue();
          } else if( pos == 3 ) {
            bottom = nextValue.intValue();
          } else if( pos == 4 ) {
            left = nextValue.intValue();
          }
        }
        nextUnit = nextUnit.getNextLexicalUnit();
      }
      ok &= nextUnit == null;
      if( ok  ) {
        result = QxBoxDimensions.create( top, right, bottom, left );
      }
    }
    return result;
  }

  static String readBorderStyle( final LexicalUnit unit ) {
    String result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_IDENT ) {
      String string = unit.getStringValue();
      if( BORDER_STYLES.contains( string ) ) {
        result = string;
      }
    }
    return result;
  }

  static int readBorderWidth( final LexicalUnit unit ) {
    int result = -1;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_IDENT ) {
      String string = unit.getStringValue();
      if( THIN.equals( string ) ) {
        result = THIN_VALUE;
      } else if( MEDIUM.equals( string ) ) {
        result = MEDIUM_VALUE;
      } else if( THICK.equals( string ) ) {
        result = THICK_VALUE;
      }
    } else if( type == LexicalUnit.SAC_PIXEL ) {
      float value = unit.getFloatValue();
      if( value >= 0f ) {
        result = Math.round( value );
      }
    }
    return result;
  }

  static QxBorder readBorder( final LexicalUnit unit ) {
    QxBorder result = null;
    QxColor color = null;
    String style = null;
    int width = -1;
    LexicalUnit nextUnit = unit;
    boolean consumed = false;
    while( nextUnit != null ) {
      consumed = false;
      if( !consumed && width == -1 ) {
        width = readBorderWidth( nextUnit );
        consumed |= width != -1;
      }
      if( !consumed && style == null ) {
        style = readBorderStyle( nextUnit );
        consumed |= style != null;
      }
      if( !consumed && color == null ) {
        color = readColor( nextUnit );
        consumed |= color != null;
      }
      nextUnit = consumed ? nextUnit.getNextLexicalUnit() : null;
    }
    if( consumed ) {
      // TODO [rst] create should take a QxColor
      result = QxBorder.create( width == -1 ? 0 : width,
                                            style,
                                            color != null ? color.toDefaultString() : null );
    }
    return result;
  }

  static String readFontStyle( final LexicalUnit unit ) {
    String result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_IDENT ) {
      String value = unit.getStringValue();
      if( NORMAL.equals( value ) ) {
        result = value;
      } else if( ITALIC.equals( value ) ) {
        result = value;
      }
    }
    return result;
  }

  static String readFontWeight( final LexicalUnit unit ) {
    String result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_IDENT ) {
      String value = unit.getStringValue();
      if( NORMAL.equals( value ) ) {
        result = value;
      } else if( BOLD.equals( value ) ) {
        result = value;
      }
    }
    return result;
  }

  static int readFontSize( final LexicalUnit unit ) {
    int result = -1;
    Integer length = readSingleLengthUnit( unit );
    if( length != null ) {
      int value = length.intValue();
      if( value >= 0 ) {
        result = value;
      }
    }
    return result;
  }

  static String[] readFontFamily( final LexicalUnit unit ) {
    List list = new ArrayList();
    LexicalUnit nextUnit = unit;
    boolean ok = true;
    String buffer = "";
    while( nextUnit != null && ok ) {
      short type = nextUnit.getLexicalUnitType();
      if( type == LexicalUnit.SAC_STRING_VALUE
          || type == LexicalUnit.SAC_IDENT )
      {
        if( buffer.length() > 0 ) {
          buffer += " ";
        }
        buffer += nextUnit.getStringValue();
      } else if( type == LexicalUnit.SAC_OPERATOR_COMMA ) {
        if( buffer.length() > 0 ) {
          list.add( buffer );
        } else {
          ok = false;
        }
        buffer = "";
      }
      nextUnit = nextUnit.getNextLexicalUnit();
    }
    String[] result = null;
    if( buffer.length() > 0 ) {
      list.add( buffer );
      result = new String[ list.size() ];
      list.toArray( result );
    }
    return result;
  }

  // The format of a URI value is 'url(' followed by optional whitespace
  // followed by an optional single quote (') or double quote (") character
  // followed by the URI itself, followed by an optional single quote (') or
  // double quote (") character followed by optional whitespace followed by ')'.
  // The two quote characters must be the same.

  static QxFont readFont( final LexicalUnit unit ) {
    QxFont result = null;
    String[] family = null;
    String style = null;
    String weight = null;
    int size = -1;
    boolean consumed = false;
    boolean consumedSize = false;
    boolean consumedFamily = false;
    LexicalUnit nextUnit = unit;
    while( nextUnit != null && !consumedFamily ) {
      consumed = false;
      if( !consumed && !consumedSize && style == null ) {
        style = readFontStyle( nextUnit );
        consumed |= style != null;
      }
      if( !consumed && !consumedSize && weight == null ) {
        weight = readFontWeight( nextUnit );
        consumed |= weight != null;
      }
      if( !consumed && !consumedFamily && size == -1 ) {
        size = readFontSize( nextUnit );
        consumedSize = size != -1;
        consumed |= consumedSize;
      }
      if( !consumed && consumedSize && family == null ) {
        family = readFontFamily( nextUnit );
        consumedFamily = family != null;
        consumed |= consumedFamily;
      }
      nextUnit = consumed ? nextUnit.getNextLexicalUnit() : null;
    }
    if( consumed && consumedSize && consumedFamily ) {
      boolean bold = BOLD.equals( weight );
      boolean italic = ITALIC.equals( style );
      result = QxFont.create( family, size, bold, italic );
    }
    return result;
  }

  static String readUrl( final LexicalUnit unit ) {
    String result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_URI ) {
      result = unit.getStringValue();
    }
    return result;
  }

  static QxImage readBackgroundImage( final LexicalUnit unit,
                                      final ResourceLoader loader ) {
    QxImage result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_URI ) {
      String value = unit.getStringValue();
      result = QxImage.valueOf( value, loader );
    } else if( type == LexicalUnit.SAC_IDENT ) {
      String value = unit.getStringValue();
      if( NONE.equals( value ) ) {
        result = QxImage.NONE;
      }
    }
    return result;
  }

  private static Integer readSingleLengthUnit( final LexicalUnit unit ) {
    Integer result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_PIXEL ) {
      result = new Integer( ( int )unit.getFloatValue() );
    }
    return result;
  }

  private static int normalizeRGBValue( final int input ) {
    int result = input;
    if( input < 0 ) {
      result = 0;
    } else if( input > 255 ) {
      result = 255;
    }
    return result;
  }

  private static float normalizePercentValue( final float input ) {
    float result = input;
    if( input < 0f ) {
      result = 0f;
    } else if( input > 100f ) {
      result = 100f;
    }
    return result;
  }

  static String toString( final LexicalUnit value ) {
    StringBuffer buffer = new StringBuffer();
    short type = value.getLexicalUnitType();
    if( type == LexicalUnit.SAC_ATTR ) {
      buffer.append( "ATTR " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_CENTIMETER
        || type == LexicalUnit.SAC_DEGREE
        || type == LexicalUnit.SAC_EM
        || type == LexicalUnit.SAC_EX
        || type == LexicalUnit.SAC_GRADIAN
        || type == LexicalUnit.SAC_HERTZ
        || type == LexicalUnit.SAC_INCH
        || type == LexicalUnit.SAC_KILOHERTZ
        || type == LexicalUnit.SAC_MILLIMETER
        || type == LexicalUnit.SAC_MILLISECOND
        || type == LexicalUnit.SAC_PERCENTAGE
        || type == LexicalUnit.SAC_PICA
        || type == LexicalUnit.SAC_POINT
        || type == LexicalUnit.SAC_PIXEL
        || type == LexicalUnit.SAC_RADIAN
        || type == LexicalUnit.SAC_SECOND
        || type == LexicalUnit.SAC_DIMENSION )
    {
      buffer.append( "DIM "
                     + value.getFloatValue()
                     + value.getDimensionUnitText() );
    } else if( type == LexicalUnit.SAC_RGBCOLOR ) {
      LexicalUnit parameters = value.getParameters();
      buffer.append( "rgb(" + toString( parameters ) + ")" );
    } else if( type == LexicalUnit.SAC_STRING_VALUE ) {
      buffer.append( "\"" + value.getStringValue() + "\"" );
    } else if( type == LexicalUnit.SAC_IDENT ) {
      buffer.append( value.getStringValue() );
    } else if( type == LexicalUnit.SAC_PIXEL ) {
      buffer.append( value.getFloatValue() + "px" );
    } else if( type == LexicalUnit.SAC_INTEGER ) {
      buffer.append( value.getIntegerValue() );
    } else if( type == LexicalUnit.SAC_OPERATOR_COMMA ) {
      buffer.append( "," );
    } else if( type == LexicalUnit.SAC_ATTR ) {
      buffer.append( "ATTR " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_FUNCTION ) {
      buffer.append( "UNKNOWN FUNCTION " + value.getFunctionName() );
    } else if( type == LexicalUnit.SAC_DIMENSION ) {
      buffer.append( "UNKNOWN DIMENSION " + value );
    } else {
      buffer.append( "unsupported unit " + value.getLexicalUnitType() );
    }
    LexicalUnit next = value.getNextLexicalUnit();
    if( next != null ) {
      buffer.append( " " );
      buffer.append( toString( next ) );
    }
    return buffer.toString();
  }
}
