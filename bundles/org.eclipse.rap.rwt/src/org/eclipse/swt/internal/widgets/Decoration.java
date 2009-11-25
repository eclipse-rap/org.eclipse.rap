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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public final class Decoration extends Widget {

  public static final String KEY_DECORATIONS = "decorations";

  private Control control;
  private final Composite parent;
  private Image image;
  private String descriptionText;
  private final int position;
  private boolean visible = true;
  private boolean showOnlyOnFocus;
  private boolean showHover = true;
  private int marginWidth;
  private boolean hasFocus;
  private FocusListener focusListener;
  private DisposeListener disposeListener;

  public Decoration( final Control control,
                     final int position,
                     final Composite composite )
  {
    super( getParent( control, composite ), SWT.NONE );
    this.control = control;
    this.position = position;
    parent = getParent( control, composite );
    disposeListener = new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event) {
        dispose();
      }
    };
    control.addDisposeListener( disposeListener );
    addDecoration();
  }

  public Image getImage() {
    return image;
  }

  public void setImage( final Image image ) {
    this.image = image;
  }

  public String getDescriptionText() {
    return descriptionText;
  }

  public void setDescriptionText( final String descriptionText ) {
    this.descriptionText = descriptionText;
  }

  public boolean getShowOnlyOnFocus() {
    return showOnlyOnFocus;
  }

  public void setShowOnlyOnFocus( final boolean showOnlyOnFocus ) {
    this.showOnlyOnFocus = showOnlyOnFocus;
    if( showOnlyOnFocus ) {
      addFocusListener();
    } else {
      removeFocusListener();
    }
  }

  public boolean getShowHover() {
    return showHover;
  }

  public void setShowHover( final boolean showHover ) {
    this.showHover = showHover;
  }

  public int getMarginWidth() {
    return marginWidth;
  }

  public void setMarginWidth( final int marginWidth ) {
    this.marginWidth = marginWidth;
  }

  public void show() {
    visible = true;
  }

  public void hide() {
    visible = false;
  }

  public boolean isVisible() {
    boolean result = true;
    if( !visible ) {
      result = false;
    }
    if( control == null || control.isDisposed() || image == null ) {
      result = false;
    }
    if( !control.isVisible() ) {
      result = false;
    }
    if( showOnlyOnFocus ) {
      result = result && hasFocus;
    }
    return result;
  }

  public Rectangle getBounds() {
    return getDecorationRectangle( parent );
  }

  public Control getControl() {
    return control;
  }

  public Composite getParent() {
    return parent;
  }

  public void dispose() {
    if( control != null && !control.isDisposed() ) {
      control.removeDisposeListener( disposeListener );
      disposeListener = null;
      removeFocusListener();
      focusListener = null;
      removeDecoration();
      control = null;
    }
    super.dispose();
  }

  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods

  private static Composite getParent( final Control control,
                                      final Composite composite )
  {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    Composite result = control.getParent();
    if( composite != null ) {
      result = composite;
    }
    return result;
  }

  private void addDecoration() {
    List decorations = ( List )control.getData( KEY_DECORATIONS );
    if( decorations == null ) {
      decorations = new ArrayList();
    }
    decorations.add( this );
    control.setData( KEY_DECORATIONS, decorations );
  }

  private void removeDecoration() {
    List decorations = ( List )control.getData( KEY_DECORATIONS );
    if( decorations != null ) {
      decorations.remove( this );
      if( decorations.size() == 0 ) {
        decorations = null;
      }
      control.setData( KEY_DECORATIONS, decorations );
    }
  }

  private void addFocusListener() {
    if( focusListener == null ) {
      focusListener = new FocusListener() {
        public void focusGained( final FocusEvent event ) {
          hasFocus = true;
        }
        public void focusLost( final FocusEvent event ) {
          hasFocus = false;
        }
      };
    }
    if( control != null && !control.isDisposed() ) {
      control.addFocusListener( focusListener );
    }
  }

  private void removeFocusListener() {
    if( focusListener != null && control != null && !control.isDisposed() ) {
      control.removeFocusListener( focusListener );
    }
  }

  private Rectangle getDecorationRectangle( final Control targetControl ) {
    Rectangle result;
    if( image != null && control != null ) {
      // Compute the bounds first relative to the control's parent.
      Rectangle imageBounds = image.getBounds();
      Rectangle controlBounds = control.getBounds();
      int x, y;
      // Compute x
      if( ( position & SWT.RIGHT ) == SWT.RIGHT ) {
        x = controlBounds.x + controlBounds.width + marginWidth;
      } else {
        // default is left
        x = controlBounds.x - imageBounds.width - marginWidth;
      }
      // Compute y
      if( ( position & SWT.TOP ) == SWT.TOP ) {
        y = controlBounds.y;
      } else if( ( position & SWT.BOTTOM ) == SWT.BOTTOM ) {
        y = controlBounds.y + control.getBounds().height - imageBounds.height;
      } else {
        // default is center
        y = controlBounds.y
          + ( control.getBounds().height - imageBounds.height ) / 2;
      }
      // Now convert to coordinates relative to the target control.
      Point globalPoint = control.getParent().toDisplay( x, y );
      Point targetPoint;
      if( targetControl == null ) {
        targetPoint = globalPoint;
      } else {
        targetPoint = targetControl.toControl( globalPoint );
      }
      result = new Rectangle( targetPoint.x,
                              targetPoint.y,
                              imageBounds.width,
                              imageBounds.height );
    } else {
      result = new Rectangle( 0, 0, 0, 0 );
    }
    return result;
  }
}
