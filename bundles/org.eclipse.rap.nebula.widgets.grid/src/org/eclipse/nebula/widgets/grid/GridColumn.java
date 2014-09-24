/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import static org.eclipse.swt.internal.widgets.MarkupUtil.isToolTipMarkupEnabledFor;
import static org.eclipse.swt.internal.widgets.MarkupValidator.isValidationDisabledFor;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;


/**
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * Instances of this class represent a column in a grid widget.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.LEFT, SWT.RIGHT, SWT.CENTER<!--, SWT.CHECK--></dd>
 * <dt><b>Events:</b></dt>
 * <dd>Move, Resize, Selection, Show, Hide</dd>
 * </dl>
 */
@SuppressWarnings( "restriction" )
public class GridColumn extends Item {

  static final String FOOTER_SPAN = "footerSpan";
  private static final int SORT_INDICATOR_WIDTH = 10;
  private static final int MARGIN_IMAGE = 3;
  private static final int DEFAULT_WIDTH = 10;

  private int width = DEFAULT_WIDTH;
  private int minimumWidth;
  private Grid parent;
  private GridColumnGroup group;
  private int sortStyle = SWT.NONE;
  private boolean check;
  private boolean tableCheck;
  private boolean moveable;
  private boolean resizeable = true;
  private boolean checkable = true;
  private boolean detail = true;
  private boolean summary = true;
  private boolean visible = true;
  private int alignment = SWT.LEFT;
  private Font headerFont;
  private String headerTooltip;
  private String footerText = "";
  private Image footerImage;
  private Font footerFont;
  private int footerSpan = 1;
  private boolean packed;
  private boolean wordWrap;
  private boolean headerWordWrap;
  int imageCount;
  int textCount;

  /**
   * Constructs a new instance of this class given its parent (which must be a
   * <code>Grid</code>) and a style value describing its behavior and
   * appearance. The item is added to the end of the items maintained by its
   * parent.
   *
   * @param parent
   *            an Grid control which will be the parent of the new instance
   *            (cannot be null)
   * @param style
   *            the style of control to construct
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>
   *             ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridColumn( Grid parent, int style ) {
    this( parent, style, -1 );
  }

  /**
   * Constructs a new instance of this class given its parent (which must be a
   * <code>Grid</code>), a style value describing its behavior and appearance,
   * and the index at which to place it in the items maintained by its parent.
   *
   * @param parent
   *            an Grid control which will be the parent of the new instance
   *            (cannot be null)
   * @param style
   *            the style of control to construct
   * @param index
   *            the index to store the receiver in its parent
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>
   *             ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridColumn( Grid parent, int style, int index ) {
    super( parent, style, index );
    init( parent, style, index );
  }

  /**
   * Constructs a new instance of this class given its parent column group
   * (which must be a <code>GridColumnGroup</code>), a style value describing
   * its behavior and appearance, and the index at which to place it in the
   * items maintained by its parent.
   *
   * @param parent
   *            an Grid control which will be the parent of the new instance
   *            (cannot be null)
   * @param style
   *            the style of control to construct
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>
   *             ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridColumn( GridColumnGroup parent, int style ) {
    super( parent.getParent(), style, parent.getNewColumnIndex() );
    init( parent.getParent(), style, parent.getNewColumnIndex() );
    group = parent;
    group.newColumn( this );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    if( !parent.isDisposing() && !isDisposed() ) {
      parent.removeColumn( this );
      if( group != null ) {
        group.removeColumn( this );
      }
    }
    super.dispose();
  }

  /**
   * Returns the parent grid.
   *
   * @return the parent grid.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Grid getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Returns the column group if this column was created inside a group, or
   * {@code null} otherwise.
   *
   * @return the column group.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public GridColumnGroup getColumnGroup() {
    checkWidget();
    return group;
  }

  /**
   * Adds the listener to the collection of listeners who will be notified
   * when the receiver's is pushed, by sending it one of the messages defined
   * in the <code>SelectionListener</code> interface.
   *
   * @param listener
   *            the listener which should be notified
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void addSelectionListener( SelectionListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    TypedListener typedListener = new TypedListener( listener );
    addListener( SWT.Selection, typedListener );
    addListener( SWT.DefaultSelection, typedListener );
  }

  /**
   * Removes the listener from the collection of listeners who will be
   * notified when the receiver's selection changes.
   *
   * @param listener
   *            the listener which should no longer be notified
   * @see SelectionListener
   * @see #addSelectionListener(SelectionListener)
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void removeSelectionListener( SelectionListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    removeListener( SWT.Selection, listener );
    removeListener( SWT.DefaultSelection, listener );
  }

  /**
   * Adds a listener to the list of listeners notified when the column is
   * moved or resized.
   *
   * @param listener
   *            listener
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void addControlListener( ControlListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    TypedListener typedListener = new TypedListener( listener );
    addListener( SWT.Move, typedListener );
    addListener( SWT.Resize, typedListener );
  }

  /**
   * Removes the given control listener.
   *
   * @param listener
   *            listener.
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void removeControlListener( ControlListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    removeListener( SWT.Move, listener );
    removeListener( SWT.Resize, listener );
  }

  /**
   * Sets the width of the column.
   *
   * @param width
   *            new width
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setWidth( int width ) {
    checkWidget();
    internalSetWidth( width );
  }

  /**
   * Returns the width of the column.
   *
   * @return width of column
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public int getWidth() {
    checkWidget();
    return width;
  }

  /**
   * Set the minimum width of the column
   *
   * @param minimumWidth
   *            the minimum width
   */
  public void setMinimumWidth( int minimumWidth ) {
    checkWidget();
    this.minimumWidth = Math.max( 0, minimumWidth );
    if( minimumWidth > width ) {
      internalSetWidth( minimumWidth );
    }
  }

  /**
   * @return the minimum width
   */
  public int getMinimumWidth() {
    checkWidget();
    return minimumWidth;
  }

  @Override
  public void setText( String text ) {
    super.setText( text );
    parent.layoutCache.invalidateHeaderHeight();
    parent.scheduleRedraw();
  }

  @Override
  public void setImage( Image image ) {
    super.setImage( image );
    parent.layoutCache.invalidateHeaderHeight();
    parent.scheduleRedraw();
  }

  /**
   * Sets the sort indicator style for the column. This method does not actual
   * sort the data in the table. Valid values include: SWT.UP, SWT.DOWN,
   * SWT.NONE.
   *
   * @param style
   *            SWT.UP, SWT.DOWN, SWT.NONE
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setSort( int style ) {
    checkWidget();
    if( style == SWT.UP || style == SWT.DOWN || style == SWT.NONE ) {
      // TODO: [if] Currently, client implementation supports only one sort column
      if( style != SWT.NONE ) {
        for( int i = 0; i < parent.getColumnCount(); i++ ) {
          GridColumn column = parent.getColumn( i );
          if( column != this ) {
            column.setSort( SWT.NONE );
          }
        }
      }
      sortStyle = style;
      parent.redraw();
    }
  }

  /**
   * Returns the sort indicator value.
   *
   * @return SWT.UP, SWT.DOWN, SWT.NONE
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public int getSort() {
    checkWidget();
    return sortStyle;
  }

  /**
   * Sets the column moveable or fixed.
   *
   * @param moveable
   *            true to enable column moving
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setMoveable( boolean moveable ) {
    checkWidget();
    this.moveable = moveable;
    parent.redraw();
  }

  /**
   * Returns true if this column is moveable.
   *
   * @return true if moveable.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getMoveable() {
    checkWidget();
    return moveable;
  }

  /**
   * Sets the column resizeable.
   *
   * @param resizeable
   *            true to make the column resizeable
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setResizeable( boolean resizeable ) {
    checkWidget();
    this.resizeable = resizeable;
  }

  /**
   * Returns true if the column is resizeable.
   *
   * @return true if the column is resizeable.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getResizeable() {
    checkWidget();
    return resizeable;
  }

  /**
   * Sets the checkable state. If false the checkboxes in the column cannot be
   * checked.
   *
   * @param checkable
   *            the new checkable state.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setCheckable(boolean checkable) {
    checkWidget();
    this.checkable = checkable;
  }

  /**
   * Returns the checkable state. If false the checkboxes in the column cannot
   * be checked.
   *
   * @return true if the column is checkable (only applicable when style is
   *         SWT.CHECK).
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getCheckable() {
    checkWidget();
    return checkable;
  }

  /**
   * Sets the column as a detail column in a column group. Detail columns are
   * shown when a column group is expanded. If this column was not created in
   * a column group, this method has no effect.
   *
   * @param detail
   *            true to show this column when the group is expanded.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setDetail( boolean detail ) {
    checkWidget();
    this.detail = detail;
  }

  /**
   * Returns true if this column is set as a detail column in a column group.
   * Detail columns are shown when the group is expanded.
   *
   * @return true if the column is a detail column.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean isDetail() {
    checkWidget();
    return detail;
  }

  /**
   * Sets the column as a summary column in a column group. Summary columns
   * are shown when a column group is collapsed. If this column was not
   * created in a column group, this method has no effect.
   *
   * @param summary
   *            true to show this column when the group is collapsed.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setSummary( boolean summary ) {
    checkWidget();
    this.summary = summary;
  }

  /**
   * Returns true if this column is set as a summary column in a column group.
   * Summary columns are shown when the group is collapsed.
   *
   * @return true if the column is a summary column.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean isSummary() {
    checkWidget();
    return summary;
  }

  /**
   * Sets the column's visibility.
   *
   * @param visible
   *            the visible to set
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setVisible( boolean visible ) {
    checkWidget();
    boolean before = isVisible();
    this.visible = visible;
    if( isVisible() != before ) {
      if( visible ) {
        notifyListeners( SWT.Show, new Event() );
      } else {
        notifyListeners( SWT.Hide, new Event() );
      }
      GridColumn[] orderedColumns = parent.getColumnsInOrder();
      boolean fire = false;
      for( int i = 0; i < orderedColumns.length; i++ ) {
        GridColumn column = orderedColumns[ i ];
        if( column == this ) {
          fire = true;
        } else if( fire && column.isVisible() ) {
          column.fireMoved();
        }
      }
      parent.redraw();
    }
  }

  /**
   * Returns the visibility state as set with {@code setVisible}.
   *
   * @return the visible
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getVisible() {
    checkWidget();
    return visible;
  }

  /**
   * Returns true if the column is visible, false otherwise. If the column is
   * in a group and the group is not expanded and this is a detail column,
   * returns false (and vice versa).
   *
   * @return true if visible, false otherwise
   *
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean isVisible() {
    checkWidget();
    boolean result = visible;
    if( group != null && !group.isDisposed() ) {
      if( ( group.getExpanded() && !isDetail() ) || ( !group.getExpanded() && !isSummary() ) ) {
        result = false;
      }
    }
    return result;
  }

  /**
   * Returns true if the column includes a check box.
   *
   * @return true if the column includes a check box.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean isCheck() {
    checkWidget();
    return check || tableCheck;
  }

  /**
   * Returns true if this column includes a tree toggle.
   *
   * @return true if the column includes the tree toggle.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean isTree() {
    checkWidget();
    return parent.isTreeColumn( parent.indexOf( this ) );
  }

  /**
   * Sets the column alignment.
   *
   * @param alignment
   *            SWT.LEFT, SWT.RIGHT, SWT.CENTER
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setAlignment( int alignment ) {
    checkWidget();
    if( alignment == SWT.LEFT || alignment == SWT.CENTER || alignment == SWT.RIGHT ) {
      this.alignment = alignment;
    }
  }

  /**
   * Returns the column alignment.
   *
   * @return SWT.LEFT, SWT.RIGHT, SWT.CENTER
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public int getAlignment() {
    checkWidget();
    return alignment;
  }

  /**
   * Returns the true if the cells in receiver wrap their text.
   *
   * @return true if the cells wrap their text.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getWordWrap() {
    checkWidget();
    return wordWrap;
  }

  /**
   * If the argument is true, wraps the text in the receiver's cells. This
   * feature will not cause the row height to expand to accommodate the
   * wrapped text. Please use <code>Grid#setItemHeight</code> to change the
   * height of each row.
   *
   * @param wordWrap
   *            true to make cells wrap their text.
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setWordWrap( boolean wordWrap ) {
    checkWidget();
    if( this.wordWrap != wordWrap ) {
      this.wordWrap = wordWrap;
      parent.scheduleRedraw();
    }
  }

  /**
   * Sets the Font to be used when displaying the Header text.
   *
   * @param font
   *            the new header font (or null)
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_INVALID_ARGUMENT - if the argument has been
   *             disposed</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setHeaderFont( Font font ) {
    checkWidget();
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    headerFont = font;
    parent.layoutCache.invalidateHeaderHeight();
    parent.scheduleRedraw();
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for the header.
   *
   * @return the receiver's font
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Font getHeaderFont() {
    checkWidget();
    return headerFont == null ? parent.getFont() : headerFont;
  }

  /**
   * Sets the tooltip text of the column header.
   *
   * @param toolTipText the tooltip text
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setHeaderTooltip( String toolTipText ) {
    checkWidget();
    if(    toolTipText != null
        && isToolTipMarkupEnabledFor( this )
        && !isValidationDisabledFor( this ) )
    {
      MarkupValidator.getInstance().validate( toolTipText );
    }
    headerTooltip = toolTipText;
  }

  /**
   * Returns the tooltip of the column header.
   *
   * @return the tooltip text (or null)
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public String getHeaderTooltip() {
    checkWidget();
    return headerTooltip;
  }

  /**
   * Sets whether or not text is word-wrapped in the header for this column.
   * If Grid.setAutoHeight(true) is set, the row height is adjusted to
   * accommodate word-wrapped text.
   *
   * @param wordWrap
   *            Set to true to wrap the text, false otherwise
   * @see #getHeaderWordWrap()
   */
  public void setHeaderWordWrap( boolean wordWrap ) {
    checkWidget();
    if( headerWordWrap != wordWrap ) {
      headerWordWrap = wordWrap;
      parent.layoutCache.invalidateHeaderHeight();
      parent.layoutCache.invalidateFooterHeight();
      parent.scheduleRedraw();
    }
  }

  /**
   * Returns whether or not text is word-wrapped in the header for this
   * column.
   *
   * @return true if the header wraps its text.
   * @see GridColumn#setHeaderWordWrap(boolean)
   */
  public boolean getHeaderWordWrap() {
    checkWidget();
    return headerWordWrap;
  }

  /**
   * Sets the receiver's footer text.
   *
   * @param text
   *            the new text
   *
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   *                </ul>
   * @exception org.eclipse.swt.SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public void setFooterText( String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    footerText = text;
    parent.layoutCache.invalidateFooterHeight();
  }

  /**
   * Returns the receiver's footer text, which will be an empty string if it
   * has never been set.
   *
   * @return the receiver's text
   *
   * @exception org.eclipse.swt.SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public String getFooterText() {
    checkWidget();
    return footerText;
  }

  /**
   * Sets the receiver's footer image to the argument, which may be null
   * indicating that no image should be displayed.
   *
   * @param image
   *            the image to display on the receiver (may be null)
   *
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_INVALID_ARGUMENT - if the image has been
   *                disposed</li>
   *                </ul>
   * @exception org.eclipse.swt.SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public void setFooterImage( Image image ) {
    checkWidget();
    if( image != null && image.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    footerImage = image;
    parent.layoutCache.invalidateFooterHeight();
  }

  /**
   * Returns the receiver's footer image if it has one, or null if it does
   * not.
   *
   * @return the receiver's image
   *
   * @exception org.eclipse.swt.SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public Image getFooterImage() {
    checkWidget();
    return footerImage;
  }

  /**
   * Sets the Font to be used when displaying the Footer text.
   *
   * @param font
   *            the new footer font (or null)
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_INVALID_ARGUMENT - if the argument has been
   *             disposed</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setFooterFont( Font font ) {
    checkWidget();
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    footerFont = font;
    parent.layoutCache.invalidateFooterHeight();
    parent.scheduleRedraw();
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for the footer.
   *
   * @return the receiver's font
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Font getFooterFont() {
    checkWidget();
    return footerFont == null ? parent.getFont() : footerFont;
  }

  /**
   * Causes the receiver to be resized to its preferred size.
   *
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void pack() {
    checkWidget();
    int newWidth = Math.max( getPreferredWidth(), parent.getMaxContentWidth( this ) );
    setWidth( newWidth );
    packed = true;
    parent.redraw();
  }

  @Override
  public void setData( String key, Object value ) {
    handleFooterSpan( key, value );
    if( !RWT.TOOLTIP_MARKUP_ENABLED.equals( key ) || !isToolTipMarkupEnabledFor( this ) ) {
      super.setData( key, value );
    }
  }

  private void handleFooterSpan( String key, Object value ) {
    if( FOOTER_SPAN.equals( key ) ) {
      if( !( value instanceof Integer ) || ( ( Integer )value ).intValue() < 1 ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      footerSpan = ( ( Integer )value ).intValue();
      if( getHeaderWordWrap() ) {
        parent.layoutCache.invalidateFooterHeight();
        parent.scheduleRedraw();
      }
    }
  }

  void repack() {
    if( packed ) {
      pack();
    }
  }

  int getLeft() {
    int result = 0;
    boolean found = false;
    int[] columnOrder = parent.getColumnOrder();
    for( int i = 0; i < columnOrder.length && !found; i++ ) {
      GridColumn currentColumn = parent.getColumn( columnOrder[ i ] );
      if( currentColumn == this ) {
        found = true;
      } else if( currentColumn.isVisible() ) {
        result += currentColumn.getWidth();
      }
    }
    return result;
  }

  int getHeaderWrapWidth() {
    int result = width - parent.getHeaderPadding().width;
    Image headerImage = getImage();
    if( headerImage != null ) {
      result -= headerImage.getBounds().width;
      result -= MARGIN_IMAGE;
    }
    if( sortStyle != SWT.NONE ) {
      result -= SORT_INDICATOR_WIDTH;
      result -= MARGIN_IMAGE;
    }
    return result;
  }

  int getFooterWrapWidth() {
    int result = getFooterWidth() - parent.getHeaderPadding().width;
    if( footerImage != null ) {
      result -= footerImage.getBounds().width;
      result -= MARGIN_IMAGE;
    }
    return result;
  }

  private int getFooterWidth() {
    int result = width;
    if( footerSpan != 1 ) {
      boolean found = false;
      int nextColumns = 0;
      int[] columnOrder = parent.getColumnOrder();
      for( int i = 0; i < columnOrder.length; i++ ) {
        GridColumn currentColumn = parent.getColumn( columnOrder[ i ] );
        if( currentColumn == this ) {
          found = true;
        } else if( found && currentColumn.isVisible() && footerSpan > ++nextColumns ) {
          result += currentColumn.getWidth();
        }
      }
    }
    return result;
  }

  private int getPreferredWidth() {
    int headerWidth = 0;
    String headerText = getText();
    Image headerImage = getImage();
    headerWidth = getContentWidth( getHeaderFont(), headerText, headerImage );
    if( sortStyle != SWT.NONE ) {
      headerWidth += SORT_INDICATOR_WIDTH;
      if( headerText.length() > 0 || headerImage != null ) {
        headerWidth += MARGIN_IMAGE;
      }
    }
    int footerWidth = 0;
    if( parent.getFooterVisible() ) {
      footerWidth = getContentWidth( getFooterFont(), footerText, footerImage );
    }
    return Math.max( headerWidth, footerWidth );
  }

  private int getContentWidth( Font font, String text, Image image ) {
    int contentWidth = 0;
    if( text.length() > 0 ) {
      contentWidth += TextSizeUtil.textExtent( font, text, 0 ).x;
    }
    if( image != null ) {
      contentWidth += image.getBounds().width;
      if( text.length() > 0 ) {
        contentWidth += MARGIN_IMAGE;
      }
    }
    contentWidth += parent.getHeaderPadding().width;
    return contentWidth;
  }

  private void internalSetWidth( int width ) {
    int newWidth = Math.max( minimumWidth, width );
    if( this.width != newWidth ) {
      this.width = newWidth;
      packed = false;
      processControlEvents();
      if( parent.isAutoHeight() && getHeaderWordWrap() ) {
        parent.layoutCache.invalidateHeaderHeight();
        parent.layoutCache.invalidateFooterHeight();
      }
      parent.scheduleRedraw();
    }
  }

  protected boolean isTableCheck() {
    return tableCheck;
  }

  protected void setTableCheck( boolean tableCheck ) {
    this.tableCheck = tableCheck;
  }

  private void init( Grid parent, int style, int index ) {
    this.parent = parent;
    if( ( style & SWT.RIGHT ) == SWT.RIGHT ) {
      alignment = SWT.RIGHT;
    }
    if( ( style & SWT.CENTER ) == SWT.CENTER ) {
      alignment = SWT.CENTER;
    }
    if( ( style & SWT.CHECK ) == SWT.CHECK ) {
      check = true;
    }
    parent.newColumn( this, index );
  }

  private void processControlEvents() {
    int[] columnOrder = parent.getColumnOrder();
    boolean found = false;
    for( int i = 0; i < columnOrder.length; i++ ) {
      GridColumn currentColumn = parent.getColumn( columnOrder[ i ] );
      if( currentColumn == this ) {
        found = true;
        fireResized();
      } else if( found ) {
        currentColumn.fireMoved();
      }
    }
  }

  void fireResized() {
    Event event = new Event();
    event.display = getDisplay();
    event.item = this;
    event.widget = parent;
    notifyListeners( SWT.Resize, event );
  }

  void fireMoved() {
    Event event = new Event();
    event.display = getDisplay();
    event.item = this;
    event.widget = parent;
    notifyListeners( SWT.Move, event );
  }
}
