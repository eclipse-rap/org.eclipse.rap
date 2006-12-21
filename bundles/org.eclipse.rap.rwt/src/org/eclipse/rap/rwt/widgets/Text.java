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

  private String text;

  public Text( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public int getStyle() {
    return style;
  }

  public String getLineDelimiter() {
    return "\\n";
  }

  // taken as sample from org.eclipse.swt.widgets.Text, but without scrolling
  // (so far)
  static int checkStyle( int style ) {
    if( ( style & RWT.SINGLE ) != 0 && ( style & RWT.MULTI ) != 0 ) {
      style &= ~RWT.MULTI;
    }
    style = checkBits( style, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    if( ( style & RWT.SINGLE ) != 0 )
      style &= ~( /* RWT.H_SCROLL | RWT.V_SCROLL | */RWT.WRAP   );
    if( ( style & RWT.WRAP ) != 0 ) {
      style |= RWT.MULTI;
      /* style &= ~RWT.H_SCROLL; */
    }
    if( ( style & RWT.MULTI ) != 0 )
      style &= ~RWT.PASSWORD;
    if( ( style & ( RWT.SINGLE | RWT.MULTI ) ) != 0 )
      return style;
    // if ((style & (RWT.H_SCROLL | RWT.V_SCROLL)) != 0) return style |
    // RWT.MULTI;
    return style | RWT.SINGLE;
  }
}
