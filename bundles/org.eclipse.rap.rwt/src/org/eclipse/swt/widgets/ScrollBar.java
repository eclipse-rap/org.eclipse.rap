/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;

/**
 * TODO [rh] JavaDoc
 * <p>(current) limitations:</p>
 * <ul>
 * <li>minimum, maximum, thumb, increment and pageIncrement properties are not 
 * rendered (no corresponding client-side property)</li>
 * <li>size (width when V_SCROLL, height when H_SCROLL) is hard-coded and may 
 * not match what the browser actually shows</li>
 * </ul>  
 */
// TODO [rh] include ScrollBar in widget hierarchy (child of Scrollable)?
public class ScrollBar extends Widget {

  // TODO [rh] scroll bar size could be determined in index.html and be held 
  //      individually per session
  static final int SCROLL_BAR_WIDTH = 16;
  static final int SCROLL_BAR_HEIGHT = 16;

  private final Scrollable parent;
  private int thumb;
  private int selection;
  private int minimum;
  private int maximum;

  // TODO [rh] make package-private as in SWT (not possible since currently
  //      used by ScrolledComposite, see there)
  public ScrollBar( final Scrollable parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
  }

  public Scrollable getParent() {
    checkWidget();
    return parent;
  }
  
  /////////////
  // Visibility
  
  public void setVisible( final boolean visible ) {
    checkWidget();
    boolean isVisible = ( state & HIDDEN ) == 0;
    if( isVisible != visible ) {
      state = visible ? state & ~HIDDEN : state | HIDDEN;
    }
  }

  public boolean getVisible() {
    checkWidget();
    return ( state & HIDDEN ) == 0;
  }
  
  public boolean isVisible() {
    checkWidget();
    return getVisible() && parent.isVisible();
  }
  
  /////////////
  // Enablement
  
  public void setEnabled( boolean enabled ) {
    checkWidget();
    if( enabled ) {
      state &= ~DISABLED;
    } else {
      state |= DISABLED;
    }
  }

  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }

  public boolean isEnabled () {
    checkWidget();
    // TODO [rh] complete implementation when isEnabled is available
//    return getEnabled(); 
    return getEnabled () && parent.isEnabled ();
  }

  public Point getSize() {
    Point result = parent.getSize();
    if( ( style & SWT.HORIZONTAL ) != 0 ) {
      result.y = SCROLL_BAR_WIDTH;
    } else {
      result.x = SCROLL_BAR_HEIGHT;
    }
    return result; 
  }

  public int getThumb() {
    checkWidget();
    return thumb;
  }

  public void setThumb( final int thumb ) {
    checkWidget();
    this.thumb = thumb;
  }
  
  public int getMaximum() {
    checkWidget();
    return maximum;
  }

  public void setMaximum( final int maximum ) {
    checkWidget();
    this.maximum = maximum;
  }

  public int getMinimum() {
    checkWidget();
    return minimum;
  }

  public void setMinimum( final int minimum ) {
    checkWidget();
    this.minimum = minimum;
  }

  public void setSelection( final int selection ) {
    checkWidget();
    this.selection = selection;
  }
  
  public int getSelection() {
    checkWidget();
    return selection;
  }
  
  ////////////////////
  // SelectionListener
  
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }
  
  // /////////////////
  // Widget overrides
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    // TODO [rh] set references to 'this' scrollBar in parent to null
  }

  protected void releaseWidget() {
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    return checkBits( style, SWT.HORIZONTAL, SWT.VERTICAL, 0, 0, 0, 0 );
  }
}
