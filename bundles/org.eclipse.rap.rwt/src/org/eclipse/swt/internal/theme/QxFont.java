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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  public QxFont( final String fontDef ) {
    if( fontDef == null ) {
      throw new NullPointerException( "null argument" );
    }
    List family = new ArrayList();
    int size = 0;
    boolean bold = false;
    boolean italic = false;

    Matcher matcher = FONT_DEF_PATTERN.matcher( fontDef );
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
        int parsedSize = parseSize( part );
        if( parsedSize != -1 ) {
          size = parsedSize;
        } else {
          // TODO [rst] Check commas
          family.add( part );
        }
      }
    }
    // TODO [rst] Check for illegal input and throw exception
    this.family = ( String[] )family.toArray( new String[ family.size() ] );
    this.bold = bold;
    this.italic = italic;
    this.size = size;
  }

  public String getFamilyAsString() {
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < family.length; i++ ) {
      if( i > 0 ) {
        result.append( ", " );
      }
      boolean hasSpace = family[ i ].indexOf( ' ' ) != -1;
      if( hasSpace ) {
        result.append( "\"" );
      }
      result.append( family[ i ] );
      if( hasSpace ) {
        result.append( "\"" );
      }
    }
    return result.toString();
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
    result.append( " " );
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
    // TODO [rst] revise hash calculation
    int result = 23;
    for( int i = 0; i < family.length; i++ ) {
      result += 37 * family[ i ].hashCode();
    }
    result += 37 * size;
    result += bold ? 0 : 41;
    result += italic ? 0 : 43;
    return result;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append( "QxFont {" );
    result.append( getFamilyAsString() );
    result.append( " " );
    result.append( size );
    if( bold ) {
      result.append( " bold" );
    }
    if( italic ) {
      result.append( " italic" );
    }
    result.append( "}" );
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
    return Font.getFont( data );
  }

  private static int parseSize( final String string ) {
    int size = -1;
    try {
      size = Integer.parseInt( string );
    } catch( final NumberFormatException e ) {
    }
    return size;
  }
}
