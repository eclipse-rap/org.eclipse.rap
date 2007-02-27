/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.graphics.Rectangle;

public abstract class Scrollable extends Control {

  Scrollable( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
  }

  public Scrollable( final Composite parent, final int style ) {
    super( parent, style );
  }

  public Rectangle getClientArea() {
    checkWidget();
    Rectangle current = getBounds();
    // TODO [rst] better implementation
    return new Rectangle( 0,
                          0,
                          current.width - getBorderWidth() * 2,
                          current.height - getBorderWidth() * 2 );
  }

  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height )
  {
    checkWidget();
    // TODO: [fappel] reasonable implementation;
    return new Rectangle( x, y, width, height );
  }
}
