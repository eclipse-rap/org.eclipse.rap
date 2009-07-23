/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.*;


public interface IControlAdapter {

  int getZIndex();

  int getTabIndex();
  void setTabIndex( int index );

  Font getUserFont();
  Color getUserForeground();
  Color getUserBackground();
  Image getUserBackgroundImage();

  boolean getBackgroundTransparency();
}
