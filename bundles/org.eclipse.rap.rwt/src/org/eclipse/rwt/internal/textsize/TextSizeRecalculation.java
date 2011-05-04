/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


class TextSizeRecalculation {
  static final String KEY_SCROLLED_COMPOSITE_CONTENT_SIZE = "org.eclipse.rap.content-size";
  static final String KEY_SCROLLED_COMPOSITE_ORIGIN = "org.eclipse.rap.sc-origin";
  static final int RESIZE_OFFSET = 1000;

  void execute() {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      forceShellRecalculations( shells[ i ] );
    }
  }

  private void forceShellRecalculations( Shell shell ) {
    Rectangle boundsBuffer = shell.getBounds();
    bufferScrolledCompositeOrigins( shell );
    clearLayoutBuffers( shell );
    enlargeShell( shell );
    enlargeScrolledCompositeContent( shell );
    clearLayoutBuffers( shell );
    restoreShellSize( shell, boundsBuffer );
    restoreScrolledCompositeOrigins( shell );
  }

  private void clearLayoutBuffers( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new ClearLayoutBuffersVisitor() );
  }

  private void bufferScrolledCompositeOrigins( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new BufferScrolledCompositeOriginsVisitor() );
  }

  private void enlargeScrolledCompositeContent( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new EnlargeScrolledCompositeContentVisitor() );
  }

  private void restoreScrolledCompositeOrigins( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new RestoreScrolledCompositeOriginsVisitor() );
  }
  
  private void restoreShellSize( Shell shell, Rectangle bufferedBounds ) {
    setShellSize( shell, bufferedBounds );
  }

  private void enlargeShell( Shell shell ) {
    Rectangle bounds = shell.getBounds();
    int xPos = bounds.x;
    int yPos = bounds.y;
    int width = bounds.width + RESIZE_OFFSET;
    int height = bounds.height + RESIZE_OFFSET;
    setShellSize( shell, new Rectangle( xPos, yPos, width, height ) );
  }

  private Shell[] getShells() {
    return getShells( getDisplay() );
  }

  private Display getDisplay() {
    return LifeCycleUtil.getSessionDisplay();
  }

  private Shell[] getShells( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    return displayAdapter.getShells();
  }
  
  private void setShellSize( Shell shell, Rectangle bounds ) {
    getShellAdapter( shell ).setBounds( bounds );
  }

  private IShellAdapter getShellAdapter( Shell shell ) {
    return ( IShellAdapter )shell.getAdapter( IShellAdapter.class );
  }
}