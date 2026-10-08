/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets;

public interface ICellToolTipAdapter {

  ICellToolTipProvider getCellToolTipProvider();
  void setCellToolTipProvider( ICellToolTipProvider provider );

  String getCellToolTipText();
  void setCellToolTipText( String toolTipText );
}
