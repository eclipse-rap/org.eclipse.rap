/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.widgets;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ItemHolder;

/**
 * Instances of this class represent a selectable user interface object
 * corresponding to a tab for a page in a tab folder.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class TabItem extends Item {

  // [if] This constants must be kept in sync with AppearancesBase.js
  private final static int TABS_SPACING = 1;
  private final static int IMAGE_TEXT_SPACING = 4;
  private final static int ITEM_BORDER = 1;
  private final static int SELECTED_ITEM_BORDER = 3;
  private final static int SELECTED_ITEM_ADDITIONAL_PADDING = 4;
  private final static Rectangle PADDING = new Rectangle( 5, 2, 11, 4 );

  private final TabFolder parent;
  private Control control;
  private String toolTipText;

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>TabFolder</code>) and a style value
   * describing its behavior and appearance. The item is added
   * to the end of the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TabItem( final TabFolder parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, parent.getItemCount() );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>TabFolder</code>), a style value
   * describing its behavior and appearance, and the index
   * at which to place it in the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TabItem( final TabFolder parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, index );
  }

  /**
   * Returns the receiver's parent, which must be a <code>TabFolder</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public TabFolder getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Returns the control that is used to fill the client area of
   * the tab folder when the user selects the tab item.  If no
   * control has been set, return <code>null</code>.
   * <p>
   * @return the control
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Control getControl() {
    checkWidget();
    return control;
  }

  /**
   * Sets the control that is used to fill the client area of
   * the tab folder when the user selects the tab item.
   * <p>
   * @param control the new control (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.parent != parent ) {
        error( SWT.ERROR_INVALID_PARENT );
      }
    }
    if( this.control != null && this.control.isDisposed() ) {
      this.control = null;
    }
    Control oldControl = this.control;
    Control newControl = control;
    this.control = control;
    int index = parent.indexOf( this );
    if( index != parent.getSelectionIndex() ) {
      if( newControl != null ) {
        newControl.setVisible( false );
      }
    } else {
      if( newControl != null ) {
        newControl.setBounds( parent.getClientArea() );
        newControl.setVisible( true );
      }
      if( oldControl != null ) {
        oldControl.setVisible( false );
      }
    }
  }

  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.3
   */
  public Rectangle getBounds() {
    checkWidget();
    Rectangle result = new Rectangle( 0, 0, 0, 0 );
    int index = parent.indexOf( this );
    if( index != -1 ) {
      int selectionIndex = parent.getSelectionIndex();
      String text = getText();
      if( text != null ) {
        Point extent = Graphics.stringExtent( parent.getFont(), text );
        result.width = extent.x;
        result.height = extent.y;
      }
      Image image = getImage();
      if( image != null ) {
        Rectangle imageSize = image.getBounds();
        result.width += imageSize.width + IMAGE_TEXT_SPACING;
        result.height = Math.max( result.height, imageSize.height );
      }
      result.width += 2 * ITEM_BORDER + PADDING.width;
      result.height += ITEM_BORDER + PADDING.height;
      if( index == selectionIndex ) {
        result.width += SELECTED_ITEM_ADDITIONAL_PADDING;
        result.height += SELECTED_ITEM_BORDER;
      }
      if( selectionIndex != -1 ) {
        if( index + 1 == selectionIndex || index - 1 == selectionIndex ) {
          result.width -= ITEM_BORDER;
        }
      }
      if( isBarTop() ) {
        if( index != selectionIndex ) {
          result.y += SELECTED_ITEM_BORDER;
        }
      } else {
        result.y = parent.getBounds().height
                 - 2 * parent.getBorderWidth()
                 - result.height;
        if( index != selectionIndex ) {
          result.y -= SELECTED_ITEM_BORDER;
        }
      }
      if( index > 0 ) {
        TabItem leftItem = parent.getItem( index - 1 );
        Rectangle leftItemBounds = leftItem.getBounds();
        result.x = leftItemBounds.x + leftItemBounds.width + TABS_SPACING;
        if( index == selectionIndex || index - 1 == selectionIndex ) {
          result.x -= TABS_SPACING;
        }
      }
    }
    return result;
  }

  private boolean isBarTop() {
    return ( parent.getStyle() & SWT.BOTTOM ) == 0;
  }

  public void setImage( final Image image ) {
    checkWidget();
    int index = parent.indexOf( this );
    if( index > -1 ) {
      super.setImage( image );
    }
  }

  /**
   * Sets the receiver's tool tip text to the argument, which
   * may be null indicating that no tool tip text should be shown.
   *
   * @param string the new tool tip text (or null)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public void setToolTipText( final String toolTip ) {
    checkWidget();
    toolTipText = toolTip;
  }

  /**
   * Returns the receiver's tool tip text, or null if it has
   * not been set.
   *
   * @return the receiver's tool tip text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public String getToolTipText() {
    checkWidget();
    return toolTipText;
  }

  ///////////////////////////////////
  // Methods to dispose of the widget

  void releaseParent() {
    super.releaseParent();
    ItemHolder.getItemHolder( parent ).remove( this );
  }

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    int result = SWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }
}
