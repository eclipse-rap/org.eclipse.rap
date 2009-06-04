/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public interface ITreeAdapter {

  TreeItem getShowItem();
  void clearShowItem();
  
  void setScrollTop( final int top );
  void setScrollLeft( final int left );
  int getScrollTop();
  int getScrollLeft();
  
  boolean isCached( final TreeItem item );
  void checkAllData( Tree tree );
}