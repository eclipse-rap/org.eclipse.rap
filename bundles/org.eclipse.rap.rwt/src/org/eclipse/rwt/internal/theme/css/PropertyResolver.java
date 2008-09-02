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

  private static final String NONE = "none";

  private static final String HIDDEN = "hidden";

  private static final String DOTTED = "dotted";

  private static final String DASHED = "dashed";

  private static final String SOLID = "solid";

  private static final String DOUBLE = "double";

  private static final String GROOVE = "groove";

  private static final String RIDGE = "ridge";

  private static final String INSET = "inset";

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
  public static final int THIN_VALUE = 1;

  /** Width value for "medium" identifier. */
  public static final int MEDIUM_VALUE = 3;

  /** Width value for "thick" identifier. */
  public static final int THICK_VALUE = 5;

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
    BORDER_STYLES.add( NONE ); // No border; the computed border width is zero.
    BORDER_STYLES.add( HIDDEN ); // Same as 'none', except in terms of border conflict resolution for table elements.
    BORDER_STYLES.add( DOTTED ); // The border is a series of dots.
    BORDER_STYLES.add( DASHED ); // The border is a series of short line segments.
    BORDER_STYLES.add( SOLID ); // The border is a single line segment.
    BORDER_STYLES.add( DOUBLE ); // The border is two solid lines. The sum of the two lines and the space between them equals the value of 'border-width'.
    BORDER_STYLES.add( GROOVE ); // The border looks as though it were carved into the canvas.
    BORDER_STYLES.add( RIDGE ); // The opposite of 'groove': the border looks as though it were coming out of the canvas.
    BORDER_STYLES.add( INSET ); // The border makes the box look as though it were embedded in the canvas.
    BORDER_STYLES.add( OUTSET ); // The opposite of 'inset': the border makes the box look as though it were coming out of the canvas.
  }

  public static QxColor readColor( final LexicalUnit input ) {
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

  public static QxDimension readDimension( final LexicalUnit unit ) {
    QxDimension result = null;
    Integer length = readSingleLengthUnit( unit );
    if( length != null ) {
      result = QxDimension.create( length.intValue() );
    }
    return result;
  }

  public static QxBoxDimensions readBoxDimensions( final LexicalUnit unit ) {
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

  public static String readBorderStyle( final LexicalUnit unit ) {
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

  public static int readBorderWidth( final LexicalUnit unit ) {
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

  public static QxBorder readBorder( final LexicalUnit unit ) {
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

  public static String readFontStyle( final LexicalUnit unit ) {
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

  public static String readFontWeight( final LexicalUnit unit ) {
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

  public static int readFontSize( final LexicalUnit unit ) {
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

  public static String[] readFontFamily( final LexicalUnit unit ) {
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

  public static QxFont readFont( final LexicalUnit unit ) {
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

  public static String readUrl( final LexicalUnit unit ) {
    String result = null;
    short type = unit.getLexicalUnitType();
    if( type == LexicalUnit.SAC_URI ) {
      result = unit.getStringValue();
    }
    return result;
  }

  public static QxImage readBackgroundImage( final LexicalUnit unit,
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
}
