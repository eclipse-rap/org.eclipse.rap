/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;


public class QxFont implements QxType {

  private static final Pattern FONT_DEF_PATTERN
    = Pattern.compile( "(\".+?\"|'.+?'|\\S[^\\s,]+)(\\s*,)?" );

  public final String[] family;

  public final int size;

  public final boolean bold;

  public final boolean italic;

  private String familyAsString;

  private QxFont( final String[] family,
                  final int size,
                  final boolean bold,
                  final boolean italic )
  {
    this.family = family;
    this.size = size;
    this.bold = bold;
    this.italic = italic;
  }

  public static QxFont create( final String[] families,
                               final int size,
                               final boolean bold,
                               final boolean italic )
  {
    if( size < 0 ) {
      throw new IllegalArgumentException( "Negative width: " + size );
    }
    return new QxFont( families, size, bold, italic );
  }

  public static QxFont valueOf( final String input ) {
    if( input == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( input.trim().length() == 0 ) {
      throw new IllegalArgumentException( "Empty font definition" );
    }
    List family = new ArrayList();
    int size = 0;
    boolean bold = false;
    boolean italic = false;

    Matcher matcher = FONT_DEF_PATTERN.matcher( input );
    while( matcher.find() ) {
      String part = matcher.group( 1 );
      char c = part.charAt( 0 );
      if( c == '"' || c == '\'' ) {
        part = part.substring( 1, part.length() - 1 );
      }
//      boolean hasComma = matcher.group( 2 ) != null;
      if( "bold".equalsIgnoreCase( part ) ) {
        bold = true;
      } else if( "italic".equalsIgnoreCase( part ) ) {
        italic = true;
      } else {
        Integer parsedSize = QxDimension.parseLength( part );
        if( parsedSize != null ) {
          size = parsedSize.intValue();
        } else {
          // TODO [rst] Check commas
          family.add( part );
        }
      }
    }
    // TODO [rst] Check for illegal input and throw exception
    String[] familyArr = ( String[] )family.toArray( new String[ family.size() ] );
    return new QxFont( familyArr, size, bold, italic ) ;
  }

  public String getFamilyAsString() {
    if( familyAsString == null ) {
      StringBuffer buffer = new StringBuffer();
      for( int i = 0; i < family.length; i++ ) {
        if( i > 0 ) {
          buffer.append( ", " );
        }
        boolean hasSpace = family[ i ].indexOf( ' ' ) != -1;
        if( hasSpace ) {
          buffer.append( "\"" );
        }
        buffer.append( family[ i ] );
        if( hasSpace ) {
          buffer.append( "\"" );
        }
      }
      familyAsString = buffer.toString();
    }
    return familyAsString;
  }

  public String toDefaultString() {
    StringBuffer result = new StringBuffer();
    if( bold ) {
      result.append( "bold " );
    }
    if( italic ) {
      result.append( "italic " );
    }
    result.append( size );
    result.append( "px " );
    result.append( getFamilyAsString() );
    return result.toString();
  }

  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof QxFont ) {
      QxFont other = ( QxFont )obj;
      result =  Arrays.equals( other.family, family )
             && other.size == size
             && other.bold == bold
             && other.italic == italic;
    }
    return result;
  }

  public int hashCode() {
    int result = 23;
    for( int i = 0; i < family.length; i++ ) {
      result += 37 * result + family[ i ].hashCode();
    }
    result += 37 * result + size;
    result += bold ? 0 : 37 * result + 41;
    result += italic ? 0 : 37 * result + 43;
    return result;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append( "QxFont{ " );
    if( bold ) {
      result.append( "bold " );
    }
    if( italic ) {
      result.append( "italic " );
    }
    result.append( size );
    result.append( "px " );
    result.append( getFamilyAsString() );
    result.append( " }" );
    return result.toString();
  }

  public static Font createFont( final QxFont font ) {
    String name = font.getFamilyAsString();
    int style = SWT.NORMAL;
    if( font.bold ) {
      style |= SWT.BOLD;
    }
    if( font.italic ) {
      style |= SWT.ITALIC;
    }
    FontData data = new FontData( name, font.size, style );
    return Graphics.getFont( data );
  }
}
