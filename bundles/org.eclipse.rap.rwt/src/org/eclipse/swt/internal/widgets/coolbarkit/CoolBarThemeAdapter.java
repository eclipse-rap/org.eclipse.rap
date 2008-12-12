/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public class CoolBarThemeAdapter extends ControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    return 0;
  }

  public int getHandleWidth( final Control control ) {
    return getCssDimension( "CoolItem-Handle", "width", control );
  }

}
