/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;


/**
 * Instances of this class represent a column in a table widget.
 * <p><dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, RIGHT, CENTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd> Move, Resize, Selection</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles LEFT, RIGHT and CENTER may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class TableColumn extends Item {

  // TODO [rh] Fow now: keep in sync with settings in appearance theme, should
  //      be moved to theme adapter or similar
  private static final int SORT_INDICATOR_WIDTH = 10;
  private static final int SPACING = 2;
  private static final int PADDING = 2;

  private final Table parent;
  private int width;
  private String toolTipText;
  private boolean resizable;
  private boolean moveable;

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Table</code>) and a style value
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
   * @see SWT#LEFT
   * @see SWT#RIGHT
   * @see SWT#CENTER
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TableColumn( final Table parent, final int style ) {
    this( parent, checkStyle( style ), checkNull( parent ).getColumnCount() );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Table</code>), a style value
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
   * @see SWT#LEFT
   * @see SWT#RIGHT
   * @see SWT#CENTER
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TableColumn( final Table parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    resizable = true;
    this.parent = parent;
    this.parent.createColumn( this, index );
  }

  /**
   * Returns the receiver's parent, which must be a <code>Table</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Table getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Gets the width of the receiver.
   *
   * @return the width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getWidth() {
    checkWidget();
    return width;
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
   */
  public void setToolTipText( final String string ) {
    checkWidget();
    toolTipText = string;
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
   */
  public String getToolTipText() {
    checkWidget();
    return toolTipText;
  }

  /**
   * Controls how text and images will be displayed in the receiver.
   * The argument should be one of <code>LEFT</code>, <code>RIGHT</code>
   * or <code>CENTER</code>.
   *
   * @param alignment the new alignment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setAlignment( final int alignment ) {
    checkWidget();
    if( ( alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER ) ) != 0 ) {
      style &= ~( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
      style |= alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    }
  }

  /**
   * Returns a value which describes the position of the
   * text or image in the receiver. The value will be one of
   * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>.
   *
   * @return the alignment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getAlignment() {
    checkWidget();
    int result = SWT.LEFT;
    if( ( style & SWT.CENTER ) != 0 ) {
      result = SWT.CENTER;
    }
    if( ( style & SWT.RIGHT ) != 0 ) {
      result = SWT.RIGHT;
    }
    return result;
  }

  /**
   * Sets the width of the receiver.
   *
   * @param width the new width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setWidth( final int width ) {
    checkWidget();
    if( width >= 0 ) {
      this.width = width;
      parent.updateScrollBars();
      int eventId = ControlEvent.CONTROL_RESIZED;
      ControlEvent event = new ControlEvent( this, eventId );
      event.processEvent();
    }
  }


  /**
   * Causes the receiver to be resized to its preferred size.
   * For a composite, this involves computing the preferred size
   * from its layout, if there is one.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void pack() {
    checkWidget();
    int width = getPreferredWidth();
    if( width != getWidth() ) {
      setWidth( width );
    }
  }

  /**
   * Sets the moveable attribute.  A column that is
   * moveable can be reordered by the user by dragging
   * the header. A column that is not moveable cannot be
   * dragged by the user but may be reordered
   * by the programmer.
   *
   * @param moveable the moveable attribute
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#setColumnOrder(int[])
   * @see Table#getColumnOrder()
   * @see TableColumn#getMoveable()
   * @see SWT#Move
   */
  public void setMoveable( final boolean moveable ) {
    checkWidget();
    this.moveable = moveable;
  }

  /**
   * Gets the moveable attribute. A column that is
   * not moveable cannot be reordered by the user
   * by dragging the header but may be reordered
   * by the programmer.
   *
   * @return the moveable attribute
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#getColumnOrder()
   * @see Table#setColumnOrder(int[])
   * @see TableColumn#setMoveable(boolean)
   * @see SWT#Move
   */
  public boolean getMoveable() {
    checkWidget();
    return moveable;
  }

  /**
   * Sets the resizable attribute.  A column that is
   * resizable can be resized by the user dragging the
   * edge of the header.  A column that is not resizable
   * cannot be dragged by the user but may be resized
   * by the programmer.
   *
   * @param resizable the resize attribute
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setResizable( final boolean resizable ) {
    checkWidget();
    this.resizable = resizable;
  }

  /**
   * Gets the resizable attribute. A column that is
   * not resizable cannot be dragged by the user but
   * may be resized by the programmer.
   *
   * @return the resizable attribute
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getResizable() {
    checkWidget();
    return resizable;
  }

  ///////////////////////////////////////
  // Listener registration/deregistration

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is moved or resized, by sending
   * it one of the messages defined in the <code>ControlListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ControlListener
   * @see #removeControlListener
   */
  public void addControlListener( final ControlListener listener ) {
    ControlEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is moved or resized.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ControlListener
   * @see #addControlListener
   */
  public void removeControlListener( final ControlListener listener ) {
    ControlEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is selected, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the column header is selected.
   * <code>widgetDefaultSelected</code> is not called.
   * </p>
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   */
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is selected.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #addSelectionListener
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  ////////////////////
  // Widget dimensions

  final int getPreferredWidth () {
    // Compute width from the column itself
    int width = 0;
    if( parent.getHeaderVisible() ) {
      Font font = parent.getFont();
      width = TextSizeDetermination.stringExtent( font, getText() ).x;
      Image image = getImage();
      if( image != null ) {
        width += image.getBounds().width + SPACING;
      }
      if(    parent.getSortColumn() == this
          && parent.getSortDirection() != SWT.NONE )
      {
        width += SORT_INDICATOR_WIDTH + SPACING;
      }
      width += 2 * PADDING;
    }
    // Mimic Windows behaviour that forces first item to resolve
    if( parent.getItemCount() > 0 && parent.getCachedItems().length == 0 ) {
      parent.checkData( parent.getItem( 0 ), 0 );
    }
    // Extend computed width if there are wider items
    int columnIndex = parent.indexOf( this );
    int itemsPreferredWidth = parent.getItemsPreferredWidth( columnIndex );
    width = Math.max( width, itemsPreferredWidth );
    return width;
  }

  ////////////////////////////
  // Widget and Item overrides

  void releaseParent() {
    parent.destroyColumn( this );
  }

  String getNameText() {
    return getText();
  }

  //////////////
  // Left offset

  final int getLeft() {
    int result = 0;
    TableColumn[] columns = parent.getColumns();
    int[] columnOrder = parent.getColumnOrder();
    int orderedIndex = -1;
    for( int i = 0; orderedIndex == -1 && i < columnOrder.length; i++ ) {
      if( columnOrder[ i ] == parent.indexOf( this ) ) {
        orderedIndex = i;
      }
    }
    for( int i = 0; i < orderedIndex; i++ ) {
      result += columns[ columnOrder[ i ] ].getWidth();
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    return checkBits( style, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0 );
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}
