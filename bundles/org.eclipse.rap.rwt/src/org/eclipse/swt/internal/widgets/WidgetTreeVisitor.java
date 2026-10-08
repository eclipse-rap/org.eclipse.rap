/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
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

import org.eclipse.swt.widgets.Widget;


public interface WidgetTreeVisitor {

  /**
   * Visit a widget.
   *
   * @param widget the widget that is visited
   * @return whether children and sub-widgets should be visited
   */
  public boolean visit( Widget widget );

}
