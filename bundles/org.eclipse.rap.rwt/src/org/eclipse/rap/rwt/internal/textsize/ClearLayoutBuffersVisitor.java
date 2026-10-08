/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


class ClearLayoutBuffersVisitor implements WidgetTreeVisitor {

  @Override
  public boolean visit( Widget widget ) {
    if( widget instanceof Composite ) {
      Composite composite = ( Composite )widget;
      composite.changed( composite.getChildren() );
    }
    return true;
  }

}
