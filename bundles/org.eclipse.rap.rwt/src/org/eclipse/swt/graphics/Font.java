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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;

// TODO [rh] font property (according and LCA functinality) for the following
//      widget missing: TableItem, TreeColumn
public final class Font {
  
  private static final Map fonts = new HashMap();
  
  private final String name;
  private final int size;
  private final int style;
  
  private Font( final String name, final int size, final int style ) {
    this.name = name;
    this.size = size;
    this.style = style;
  }
  
  public static Font getFont( final String name, 
                              final int size, 
                              final int style ) 
  {
    validate( name, size );
    int checkedStyle = checkStyle( style );
    Integer hashCode = new Integer( getHashCode( name, size, checkedStyle ) );
    Font result;
    synchronized( Font.class ) {
      result = ( Font )fonts.get( hashCode );
      if( result == null ) {
        result = new Font( name, size, checkedStyle );
        fonts.put( hashCode, result );
      }
    }
    return result;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }

  public int getStyle() {
    return style;
  }

  //////////////////
  // Helping methods
  
  private static void validate( final String name, final int size ) {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( size < 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
  }
  
  private static int checkStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }
  
  private static int getHashCode( final String name, 
                                  final int size, 
                                  final int style ) 
  {
    int result = 1;
    result = 31 * result + name.hashCode();
    result = 31 * result + size;
    result = 31 * result + style;
    return result;
  }
}
