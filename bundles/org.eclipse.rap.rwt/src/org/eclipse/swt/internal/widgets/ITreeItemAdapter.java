/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;


public interface ITreeItemAdapter {

  String[] getTexts();
  Image[] getImages();
  Color[] getCellBackgrounds();
  Color[] getCellForegrounds();
  Font[] getCellFonts();
  boolean isParentDisposed();

}
