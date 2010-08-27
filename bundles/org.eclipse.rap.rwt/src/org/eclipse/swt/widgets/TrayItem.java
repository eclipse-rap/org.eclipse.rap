/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

/**
 * Instances of this class represent icons that can be placed on the system tray
 * or task bar status area.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, MenuDetect, Selection</dd>
 * </dl>
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @since 1.4
 */
public class TrayItem extends Item {

  private Tray tray;
  private String toolTip;
  private boolean visible = true;

  /**
   * Constructs a new instance of this class given its parent (which must be a
   * <code>Tray</code>) and a style value describing its behavior and
   * appearance. The item is added to the end of the items maintained by its
   * parent.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must be
   * built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
   * constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   * @since 1.4
   */
  public TrayItem( final Tray parent, final int style ) {
    super( parent, style );
    if( parent == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.tray = parent;
    checkWidget();
    parent.addItem( this );
  }

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * the receiver is selected by the user, by sending it one of the messages
   * defined in the <code>SelectionListener</code> interface.
   * <p>
   * <code>widgetSelected</code> is called when the receiver is selected
   * <code>widgetDefaultSelected</code> is called when the receiver is
   * double-clicked
   * </p>
   *
   * @param listener the listener which should be notified when the receiver is
   *          selected by the user
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   * @since 1.4
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Returns the receiver's tool tip text, or null if it has not been set.
   *
   * @return the receiver's tool tip text
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @since 1.4
   */
  public String getToolTipText() {
    checkWidget();
    return toolTip;
  }

  /**
   * Returns <code>true</code> if the receiver is visible and <code>false</code>
   * otherwise.
   *
   * @return the receiver's visibility
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @since 1.4
   */
  public boolean getVisible() {
    checkWidget();
    return visible;
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when the receiver is selected by the user.
   *
   * @param listener the listener which should no longer be notified
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SelectionListener
   * @see #addSelectionListener
   * @since 1.4
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    if( listener == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    SelectionEvent.removeListener( this, listener );
  }

  /**
   * Sets the receiver's image.
   *
   * @param image the new image
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @since 1.4
   */
  public void setImage( final Image image ) {
    checkWidget();
    if( image == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
  }

  /**
   * Sets the receiver's tool tip text to the argument, which may be null
   * indicating that no tool tip text should be shown.
   *
   * @param value the new tool tip text (or null)
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @since 1.4
   */
  public void setToolTipText( final String value ) {
    checkWidget();
  }

  /**
   * Makes the receiver visible if the argument is <code>true</code>, and makes
   * it invisible otherwise.
   *
   * @param visible the new visibility state
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @since 1.4
   */
  public void setVisible( final boolean visible ) {
    checkWidget();
  }

  public void dispose() {
    tray.removeItem( this );
    super.dispose();
  }

  protected void internal_createHandle( final int index ) {
    checkWidget();
  }

  void TrayItemCallback( final int type, final int x, int y ) {
    checkWidget();
  }
}
