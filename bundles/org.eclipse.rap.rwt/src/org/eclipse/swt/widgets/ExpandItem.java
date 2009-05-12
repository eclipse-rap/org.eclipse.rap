/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     Tom Schindl<tom.schindl@bestsolution.at> - fix for issue 272674
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;

/**
 * Instances of this class represent a selectable user interface object that
 * represents a expandable item in a expand bar.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see ExpandBar
 * @since 1.2
 */
public class ExpandItem extends Item {

  static final int LEFT_MARGIN = 4;
  static final int RIGHT_MARGIN = 24;
  static final int INTERNAL_SPACING = 4;
  static final int BORDER = 1;
  static final int CHEVRON_SIZE = 24;
  ExpandBar parent;
  Control control;
  boolean expanded;
  int x, y, width, height;
  int imageHeight, imageWidth;

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ExpandItem( final ExpandBar parent, final int style ) {
    this( parent, style, checkNull( parent ).getItemCount() );
  }

  /**
   * Constructs a new instance of this class given its parent, a style value
   * describing its behavior and appearance, and the index at which to place it
   * in the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the parent (inclusive)</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ExpandItem( final ExpandBar parent, final int style, final int index )
  {
    super( parent, style );
    this.parent = parent;
    parent.createItem( this, style, index );
  }

  static ExpandBar checkNull( final ExpandBar control ) {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return control;
  }

  ///////////////////
  // Widget overrides

  public void dispose() {
    if( !isDisposed() ) {
      parent.destroyItem( this );
      if( control != null ) {
        control.dispose();
        control = null;
      }
      super.dispose();
    }
  }

  /**
   * Returns the control that is shown when the item is expanded. If no control
   * has been set, return <code>null</code>.
   *
   * @return the control
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public Control getControl() {
    checkWidget();
    return control;
  }

  /**
   * Returns <code>true</code> if the receiver is expanded, and false
   * otherwise.
   *
   * @return the expanded state
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public boolean getExpanded() {
    checkWidget();
    return expanded;
  }

  /**
   * Returns the height of the receiver's header
   *
   * @return the height of the header
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getHeaderHeight() {
    checkWidget();
    return Math.max( parent.getBandHeight(), imageHeight );
  }

  /**
   * Gets the height of the receiver.
   *
   * @return the height
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getHeight() {
    checkWidget();
    return height;
  }

  /**
   * Returns the receiver's parent, which must be a <code>ExpandBar</code>.
   *
   * @return the receiver's parent
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public ExpandBar getParent() {
    checkWidget();
    return parent;
  }

  int getPreferredWidth() {
    int result = 0;
    if( !isDisposed() ) {
      Image image = getImage();
      int w = ( image == null )
                               ? 0
                               : image.getBounds().width;
      String text = getText();
      if( text != null ) {
        if( w > 0 ) {
          w += INTERNAL_SPACING;
        }
        Font parentFont = getParent().getFont();
        w += TextSizeDetermination.stringExtent( parentFont, text ).x;
      }
      result = w + LEFT_MARGIN + RIGHT_MARGIN;
    }
    return result;
  }

  Rectangle getBounds() {
    Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
    if( this.expanded ) {
      bounds = new Rectangle( this.x,
                              this.y,
                              this.width,
                              getHeaderHeight() + height );
    } else {
      bounds = new Rectangle( this.x, this.y, this.width, getHeaderHeight() );
    }
    return bounds;
  }

  void setBounds( final int x,
                  final int y,
                  final int width,
                  final int height,
                  final boolean move,
                  final boolean size )
  {
    int headerHeight = getHeaderHeight();
    int aX = x;
    int aY = y;
    int aWidth = width;
    int aHeight = height;
    if( move ) {
      if( imageHeight > headerHeight ) {
        aY += ( imageHeight - headerHeight );
      }
      this.x = aX;
      this.y = aY;
    }
    if( size ) {
      this.width = aWidth;
      this.height = aHeight;
    }
    if( control != null && !control.isDisposed() ) {
      if( !parent.isAppThemed() ) {
        aX += BORDER;
        aWidth = Math.max( 0, aWidth - BORDER * 2 );
        aHeight = Math.max( 0, aHeight - BORDER );
      }
      if( move && size ) {
        control.setBounds( aX, aY + headerHeight, aWidth, aHeight );
      }
      if( move && !size ) {
        control.setLocation( aX, aY + headerHeight );
      }
      if( !move && size ) {
        control.setSize( aWidth, aHeight );
      }
    }
  }

  /**
   * Sets the control that is shown when the item is expanded.
   *
   * @param control the new control (or null)
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
   *              <li>ERROR_INVALID_PARENT - if the control is not in the same
   *              widget tree</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
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
    this.control = control;
    if( control != null ) {
      int headerHeight = getHeaderHeight();
      control.setVisible( expanded );
      if( !parent.isAppThemed() ) {
        int width = Math.max( 0, this.width - BORDER * 2 );
        int height = Math.max( 0, this.height - BORDER );
        control.setBounds( x + BORDER, y + headerHeight, width, height );
      } else {
        control.setBounds( x, y + headerHeight, width, height );
      }
    }
  }

  /**
   * Sets the expanded state of the receiver.
   *
   * @param expanded the new expanded state
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setExpanded( final boolean expanded ) {
    checkWidget();
    this.expanded = expanded;
    parent.showItem( this );
  }

  /**
   * Sets the height of the receiver. This is height of the item when it is
   * expanded, excluding the height of the header.
   *
   * @param height the new height
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setHeight( final int height ) {
    checkWidget();
    if( height >= 0 ) {
      setBounds( 0, 0, width, height, false, true );
      if( expanded ) {
        parent.layoutItems( parent.indexOf( this ) + 1, true );
      }
    }
  }

  public void setImage( final Image image ) {
    checkWidget();
    if( image != getImage() ) {
      super.setImage( image );
      int parentWidth = parent.computeSize( SWT.DEFAULT, SWT.DEFAULT, false ).x;
      int availableWidth = parentWidth - 2 * parent.spacing - parent.v_scroll;
      width = Math.max( getPreferredWidth(), availableWidth );
      setBounds( 0, 0, width, height, false, true );
    }
    int oldImageHeight = imageHeight;
    if( image != null ) {
      Rectangle bounds = image.getBounds();
      imageHeight = bounds.height;
      imageWidth = bounds.width;
    } else {
      imageHeight = imageWidth = 0;
    }
    if( oldImageHeight != imageHeight ) {
      parent.layoutItems( parent.indexOf( this ), true );
    }
  }

  public void setText( final String string ) {
    checkWidget();
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !string.equals( getText() ) ) {
      super.setText( string );
      int parentWidth = parent.computeSize( SWT.DEFAULT, SWT.DEFAULT, false ).x;
      int availableWidth = parentWidth - 2 * parent.spacing - parent.v_scroll;
      width = Math.max( getPreferredWidth(), availableWidth );
      setBounds( 0, 0, width, height, false, true );
    }
  }
}
