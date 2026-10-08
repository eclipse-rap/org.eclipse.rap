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

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


class EnlargeScrolledCompositeContentVisitor implements WidgetTreeVisitor {

  @Override
  public boolean visit( Widget widget ) {
    if( widget instanceof ScrolledComposite && hasContentControl( widget ) ) {
      enlargeContentControl( widget );
    }
    return true;
  }

  private static void enlargeContentControl( Widget widget ) {
    enlargeContentControl( getContentControl( widget ) );
  }

  private static void enlargeContentControl( Control content ) {
    Point currentSize = content.getSize();
    int width = currentSize.x + TextSizeRecalculation.RESIZE_OFFSET;
    int height = currentSize.y + TextSizeRecalculation.RESIZE_OFFSET;
    content.setSize( width, height );
  }

  private static boolean hasContentControl( Widget widget ) {
    return getContentControl( widget ) != null;
  }

  private static Control getContentControl( Widget widget ) {
    return ( ( ScrolledComposite )widget ).getContent();
  }

}
