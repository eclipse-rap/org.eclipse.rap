/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;

/**
 * TODO [fappel]: comment
 */
public final class FontData {

  private final String name;
  private final int height;
  private final int style;
  
  /**
   * TODO [fappel]: comment
   */
  public FontData( final String name, final int height, final int style ) {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.name = name;
    this.height = height;
    this.style = style;
  }

  /**
   * TODO [fappel]: comment
   */
  public FontData( final String string ) {
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int start = 0;
    int end = string.indexOf( '|' );
    if( end == -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    String version1 = string.substring( start, end );
    try {
      if( Integer.parseInt( version1 ) != 1 ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    } catch( final NumberFormatException e ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    start = end + 1;
    end = string.indexOf( '|', start );
    if( end == -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    String name = string.substring( start, end );
    start = end + 1;
    end = string.indexOf( '|', start );
    if( end == -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int height = 0;
    try {
      height = Integer.parseInt( string.substring( start, end ) );
    } catch( NumberFormatException e ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    start = end + 1;
    end = string.indexOf( '|', start );
    if( end == -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int style = 0;
    try {
      style = Integer.parseInt( string.substring( start, end ) );
    } catch( NumberFormatException e ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    start = end + 1;
    end = string.indexOf( '|', start );
    this.name = name;
    this.height = height;
    this.style = style;
  }

  /**
   * Returns a string representation of the receiver which is suitable for
   * constructing an equivalent instance using the <code>FontData(String)</code>
   * constructor.
   * 
   * @return a string representation of the FontData
   * @see FontData
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "1|" ); //$NON-NLS-1$
    buffer.append( getName() );
    buffer.append( "|" ); //$NON-NLS-1$
    buffer.append( getHeight() );
    buffer.append( "|" ); //$NON-NLS-1$
    buffer.append( getStyle() );
    buffer.append( "|" ); //$NON-NLS-1$
    return buffer.toString();
  }
  
  /**
   * TODO [fappel]: comment
   */
  public int getHeight() {
    return height;
  }

  /**
   * TODO [fappel]: comment
   */
  public String getName() {
    return name;
  }

  /**
   * TODO [fappel]: comment
   */
  public int getStyle() {
    return style;
  }
  
  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj != null && obj instanceof FontData ) {
      FontData toCompare = ( FontData )obj;
      result =    name.equals( toCompare.name )
               && height == toCompare.height
               && style == toCompare.style;
    }
    return result;
  }

  public int hashCode() {
    return name.hashCode() ^ height ^ style;
  }
}
