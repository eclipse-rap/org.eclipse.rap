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
import org.eclipse.rap.rwt.graphics.Image;

/**
 * TODO [rh] JavaDoc
 * <p>
 * </p>
 */
public abstract class Item extends Widget {

  private String text;
  private Image image;

  public Item( final Widget parent, final int style ) {
    super( parent, style );
    text = "";
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  public String getText() {
    checkWidget();
    return text;
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    this.image = image;
  }

  public Image getImage() {
    checkWidget();
    return image;
  }
}
