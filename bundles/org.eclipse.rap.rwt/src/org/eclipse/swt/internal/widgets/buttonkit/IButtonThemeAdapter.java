/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.controlkit.IControlThemeAdapter;
import org.eclipse.swt.widgets.Button;

public interface IButtonThemeAdapter extends IControlThemeAdapter {

  abstract public Point getSize( Button button );

}
