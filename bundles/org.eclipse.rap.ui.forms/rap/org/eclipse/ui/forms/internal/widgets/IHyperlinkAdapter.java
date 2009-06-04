/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets;

import org.eclipse.swt.graphics.Color;


public interface IHyperlinkAdapter {

  void setActiveForeground( Color activeForeground );
  Color getActiveForeground();

  void setActiveBackground( Color activeBackground );
  Color getActiveBackground();

  void setUnderlineMode( int underlineMode );
  int getUnderlineMode();
}
