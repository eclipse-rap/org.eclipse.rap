/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


class TextSizeRecalculation {
  static final String TEMPORARY_RESIZE = TextSizeRecalculation.class.getName() + "#temporaryResize";
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
    boolean isPacked = ControlUtil.getControlAdapter( shell ).isPacked();
    Rectangle boundsBuffer = shell.getBounds();
    bufferScrolledCompositeOrigins( shell );
    clearLayoutBuffers( shell );
    setTemporaryResize( true );
    enlargeShell( shell );
    enlargeScrolledCompositeContent( shell );
    setTemporaryResize( false );
    clearLayoutBuffers( shell );
    markLayoutNeeded( shell );
    rePack( shell );
    restoreShellSize( shell, boundsBuffer, isPacked );
    restoreScrolledCompositeOrigins( shell );
  }

  private void rePack( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new RePackVisitor() );
  }

  private void clearLayoutBuffers( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new ClearLayoutBuffersVisitor() );
  }

  private void markLayoutNeeded( Shell shell ) {
    WidgetTreeVisitor.accept( shell, new MarkLayoutNeededVisitor() );
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

  private void restoreShellSize( Shell shell, Rectangle bufferedBounds, boolean isPacked ) {
    if( isPacked ) {
      shell.pack();
    } else {
      setShellSize( shell, bufferedBounds );
    }
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
    shell.getAdapter( IShellAdapter.class ).setBounds( bounds );
  }

  private void setTemporaryResize( boolean value ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    if( value ) {
      serviceStore.setAttribute( TEMPORARY_RESIZE, Boolean.TRUE );
    } else {
      serviceStore.removeAttribute( TEMPORARY_RESIZE );
    }
  }
}