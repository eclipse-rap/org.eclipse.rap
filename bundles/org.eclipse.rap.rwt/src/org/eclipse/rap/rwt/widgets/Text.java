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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;

public class Text extends Control {

  private String text = "";

  public Text( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public String getLineDelimiter() {
    return "\n";
  }

  // taken as sample from org.eclipse.swt.widgets.Text, but without scrolling
  // (so far)
  private static int checkStyle( final int style ) {
    int result = style;
    if( ( result & RWT.SINGLE ) != 0 && ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.MULTI;
    }
    result = checkBits( result, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    if( ( result & RWT.SINGLE ) != 0 ) {
      result &= ~( /* RWT.H_SCROLL | RWT.V_SCROLL | */RWT.WRAP    );
    }
    if( ( result & RWT.WRAP ) != 0 ) {
      result |= RWT.MULTI;
      /* style &= ~RWT.H_SCROLL; */
    }
    if( ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.PASSWORD;
    }
    if( ( result & ( RWT.SINGLE | RWT.MULTI ) ) != 0 ) {
      return result;
    }
    // if ((style & (RWT.H_SCROLL | RWT.V_SCROLL)) != 0) return style |
    // RWT.MULTI;
    return result | RWT.SINGLE;
  }
}
