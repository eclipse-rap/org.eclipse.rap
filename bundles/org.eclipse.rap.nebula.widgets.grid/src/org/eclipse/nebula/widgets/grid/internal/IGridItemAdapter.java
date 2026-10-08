/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
