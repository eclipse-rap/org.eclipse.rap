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

package org.eclipse.swt.internal.widgets.shellkit;

import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.widgets.Shell;

public interface IShellThemeAdapter extends IControlThemeAdapter {

  abstract public QxBoxDimensions getPadding( Shell shell );

  abstract public int getTitleBarHeight( Shell shell );

  abstract public QxBoxDimensions getTitleBarMargin( Shell shell );

  abstract public int getMenuBarHeight( Shell shell );

}
