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

package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public interface IControlAdapter {

  public abstract int getZIndex();

  public abstract int getTabIndex();

  public abstract Font getUserFont();

  public abstract Color getUserForeground();

  public abstract Color getUserBackground();

  public abstract boolean getBackgroundTransparency();

  public abstract void setTabIndex( int index );
}
