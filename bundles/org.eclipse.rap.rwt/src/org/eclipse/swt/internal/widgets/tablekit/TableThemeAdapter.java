/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tablekit;

import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public final class TableThemeAdapter extends ControlThemeAdapter {

  public int getCheckBoxWidth( final Control control ) {
    return getCssDimension( "Table-Checkbox", "width", control );
  }
}
