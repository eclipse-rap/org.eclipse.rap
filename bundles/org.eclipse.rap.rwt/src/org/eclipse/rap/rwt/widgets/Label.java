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

public class Label extends Control {

  private String text = "";

  public Label( final Composite parent, final int style ) {
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

  private static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    result |= RWT.NO_FOCUS;
    if ((style & RWT.SEPARATOR) != 0) {
      result = checkBits (result, RWT.VERTICAL, RWT.HORIZONTAL, 0, 0, 0, 0);
      // result = checkBits (style, RWT.SHADOW_OUT, RWT.SHADOW_IN, RWT.SHADOW_NONE, 0, 0, 0);
    }
    result = checkBits (style, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0);
    return result;
  } 

}
