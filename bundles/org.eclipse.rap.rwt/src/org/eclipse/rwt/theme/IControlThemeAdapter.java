/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.theme;

import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

public interface IControlThemeAdapter extends IThemeAdapter {

  public int getBorderWidth ( Control control );

  public Color getForeground( Control control );

  public Color getBackground( Control control );

  public Font getFont( Control control );
}
