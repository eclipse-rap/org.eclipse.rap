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

package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public interface ITreeItemAdapter
  extends IWidgetColorAdapter, IWidgetFontAdapter
{

  public abstract Color[] getCellBackgrounds();

  public abstract Color[] getCellForegrounds();

  public abstract Font[] getCellFonts();
}
