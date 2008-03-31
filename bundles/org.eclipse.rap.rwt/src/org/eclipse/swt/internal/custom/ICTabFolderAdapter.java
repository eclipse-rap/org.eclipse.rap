/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;


public interface ICTabFolderAdapter {

  boolean getChevronVisible();
  Rectangle getChevronRect();

  Rectangle getMinimizeRect();
  Rectangle getMaximizeRect();

  void showListMenu();

  boolean showItemImage( CTabItem item );
  boolean showItemClose( CTabItem item );
  String getShortenedItemText( CTabItem item );

  public Color getUserSelectionForeground();

  public Color getUserSelectionBackground();
}
