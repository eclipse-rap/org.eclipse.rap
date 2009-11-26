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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public final class ControlDecorator extends Decorator {

  public static final String KEY_DECORATIONS = "decorations";

  private final Composite parent;
  private Image image;
  private String text;
  private boolean visible;
  private boolean showOnlyOnFocus;
  private boolean showHover;
  private int marginWidth;
  private FocusListener focusListener;

  public ControlDecorator( final Control control,
                           final int style,
                           final Composite composite )
  {
    super( control, style );
    visible = true;
    showHover = true;
    parent = getParent( control, composite );
    addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        removeFocusListener();
      }
    } );
  }

  public Image getImage() {
    checkWidget();
    return image;
  }

  public void setImage( final Image image ) {
    checkWidget();
    this.image = image;
  }

  public String getText() {
    checkWidget();
    return text;
  }

  public void setText( final String text ) {
    checkWidget();
    this.text = text;
  }

  public boolean getShowOnlyOnFocus() {
    checkWidget();
    return showOnlyOnFocus;
  }

  public void setShowOnlyOnFocus( final boolean showOnlyOnFocus ) {
    checkWidget();
    if( this.showOnlyOnFocus != showOnlyOnFocus ) {
      this.showOnlyOnFocus = showOnlyOnFocus;
      if( showOnlyOnFocus ) {
        addFocusListener();
      } else {
        removeFocusListener();
      }
    }
  }

  public boolean getShowHover() {
    checkWidget();
    return showHover;
  }

  public void setShowHover( final boolean showHover ) {
    checkWidget();
    this.showHover = showHover;
  }

  public int getMarginWidth() {
    checkWidget();
    return marginWidth;
  }

  public void setMarginWidth( final int marginWidth ) {
    checkWidget();
    this.marginWidth = marginWidth;
  }

  public void show() {
    checkWidget();
    visible = true;
  }

  public void hide() {
    checkWidget();
    visible = false;
  }

  public boolean isVisible() {
    checkWidget();
    boolean result = true;
    if( !visible ) {
      result = false;
    }
    Control control = ( Control )getDecoratedWidget();
    if( control == null || control.isDisposed() || image == null ) {
      result = false;
    }
    if( control != null && !control.isVisible() ) {
      result = false;
    }
    if( showOnlyOnFocus && getDisplay().getFocusControl() != control ) {
      result = false;
    }
    return result;
  }

  public Rectangle getBounds() {
    checkWidget();
    Rectangle result;
    Control control = ( Control )getDecoratedWidget();
    if( image != null && control != null ) {
      // Compute the bounds first relative to the control's parent.
      Rectangle imageBounds = image.getBounds();
      Rectangle controlBounds = control.getBounds();
      int left;
      if( ( getStyle() & SWT.RIGHT ) == SWT.RIGHT ) {
        left = controlBounds.x + controlBounds.width + marginWidth;
      } else {
        // default is left
        left = controlBounds.x - imageBounds.width - marginWidth;
      }
      int top;
      if( ( getStyle() & SWT.TOP ) == SWT.TOP ) {
        top = controlBounds.y;
      } else if( ( getStyle() & SWT.BOTTOM ) == SWT.BOTTOM ) {
        top = controlBounds.y + controlBounds.height - imageBounds.height;
      } else {
        // default is center
        top 
          = controlBounds.y
          + ( controlBounds.height - imageBounds.height ) / 2;
      }
      // Now convert to coordinates relative to the target control.
      Point globalPoint = control.getParent().toDisplay( left, top );
      Point targetPoint = parent.toControl( globalPoint );
      result = new Rectangle( targetPoint.x,
                              targetPoint.y,
                              imageBounds.width,
                              imageBounds.height );
    } else {
      result = new Rectangle( 0, 0, 0, 0 );
    }
    return result;
  }

  public Control getControl() {
    checkWidget();
    return ( Control )getDecoratedWidget();
  }

  public Composite getParent() {
    checkWidget();
    return parent;
  }

  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods

  private static Composite getParent( final Control control,
                                      final Composite composite )
  {
    Composite result = composite;
    if( composite == null ) {
      result = control.getParent();
    }
    return result;
  }

  private void addFocusListener() {
    if( focusListener == null ) {
      focusListener = new FocusAdapter() {
      };
    }
    Control control = ( Control )getDecoratedWidget();
    if( control != null && !control.isDisposed() ) {
      control.addFocusListener( focusListener );
    }
  }

  private void removeFocusListener() {
    Control control = ( Control )getDecoratedWidget();
    if( focusListener != null && control != null && !control.isDisposed() ) {
      control.removeFocusListener( focusListener );
    }
  }
}
