/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;


public class Decorator extends Widget {
  
  public static final String KEY_DECORATIONS = "decorations";

  private Widget decoratedWidget;
  private DisposeListener disposeListener;
  
  public Decorator( final Widget widget, final int style ) {
    super( widget, style );
    this.decoratedWidget = widget;
    registerDisposeListeners();
    bindDecoration();
  }

  protected final Widget getDecoratedWidget() {
    return decoratedWidget;
  }
  
  //////////////////
  // Helping methods

  private void registerDisposeListeners() {
    disposeListener = new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        release();
      }
    };
    addDisposeListener( disposeListener );
    this.decoratedWidget.addDisposeListener( disposeListener );
  }
  
  private void release() {
    if( decoratedWidget != null && !decoratedWidget.isDisposed() ) {
      removeDisposeListener( disposeListener );
      decoratedWidget.removeDisposeListener( disposeListener );
      disposeListener = null;
      unbindDecoration();
      decoratedWidget = null;
      dispose();
    }
  }
  
  private void bindDecoration() {
    List decorations = ( List )decoratedWidget.getData( KEY_DECORATIONS );
    if( decorations == null ) {
      decorations = new ArrayList();
    }
    decorations.add( this );
    decoratedWidget.setData( KEY_DECORATIONS, decorations );
  }

  private void unbindDecoration() {
    List decorations = ( List )decoratedWidget.getData( KEY_DECORATIONS );
    if( decorations != null ) {
      decorations.remove( this );
      if( decorations.size() == 0 ) {
        decorations = null;
      }
      decoratedWidget.setData( KEY_DECORATIONS, decorations );
    }
  }
}
