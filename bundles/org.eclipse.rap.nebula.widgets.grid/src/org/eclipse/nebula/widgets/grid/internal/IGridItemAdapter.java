/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;


public interface IGridItemAdapter {

  String[] getTexts();
  Image[] getImages();
  Color[] getCellBackgrounds();
  Color[] getCellForegrounds();
  Font[] getCellFonts();
  boolean[] getCellChecked();
  boolean[] getCellGrayed();
  boolean[] getCellCheckable();
  int[] getColumnSpans();
  boolean isParentDisposed();
  boolean isCached();

}
