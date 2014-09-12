/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class CoolBarThemeAdapter extends ControlThemeAdapterImpl {

  @Override
  public Rectangle getBorder( Control control ) {
    return new Rectangle( 0, 0, 0, 0 );
  }

  public int getHandleWidth( Control control ) {
    return getCssDimension( "CoolItem-Handle", "width", control );
  }

}
