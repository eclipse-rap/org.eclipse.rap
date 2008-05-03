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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QxDimension implements QxType {

  public static final QxDimension ZERO = new QxDimension( 0 );

  private static final Pattern LENGTH_PATTERN
    = Pattern.compile( "((\\+|-)?\\d+)(em|ex|px|pt|pc|in|cm|mm|%)?" );

  public final int value;

  private QxDimension( final int value ) {
    this.value = value;
  }

  public static QxDimension create( final int value ) {
    QxDimension result;
    if( value == 0 ) {
      result = ZERO;
    } else {
      result = new QxDimension( value );
    }
    return result;
  }

  public static QxDimension valueOf( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    Integer parsed = parseLength( input );
    if( parsed == null ) {
      throw new IllegalArgumentException( "Illegal dimension parameter: " + input );
    }
    return create( parsed.intValue() );
  }

  public String toDefaultString() {
    return value + "px";
  }

  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxDimension ) {
      QxDimension other = (QxDimension)object;
      result = ( other.value == this.value );
    }
    return result;
  }

  public int hashCode () {
    return value * 47;
  }

  public String toString () {
    return "QxDimension{ "
           + value
           + " }";
  }

  /**
   * Tries to interpret a string as length parameter.
   *
   * @return the parsed length as integer, or <code>null</code> if the string
   *         could not be parsed.
   * @throws IllegalArgumentException if the string is valid CSS length
   *             parameter that is a percentage value or has an unsupported
   *             unit.
   */
  static Integer parseLength( final String input ) {
    // TODO [rst] Also catch values with fractional digits
    Integer result = null;
    Matcher matcher = LENGTH_PATTERN.matcher( input );
    if( matcher.matches() ) {
      result = Integer.valueOf( matcher.group( 1 ) );
      String unit = matcher.group( 3 );
      if( unit != null && "%".equals( unit ) ) {
        throw new IllegalArgumentException( "Percentages not supported: " + input );
      }
      if( unit != null && !"px".equals( unit ) ) {
        throw new IllegalArgumentException( "Unit not supported: " + input );
      }
    }
    return result;
  }
}
