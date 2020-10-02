/*******************************************************************************
 * Copyright (c) 2012, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.internal.gridcolumngroupkit.GridColumnGroupLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;


/**
 * Instances of this class represent a column group in a grid widget.  A column group header is
 * displayed above grouped columns.  The column group can optionally be configured to expand and
 * collapse.  A column group in the expanded state shows {@code GridColumn}s whose detail property
 * is true.  A column group in the collapsed state shows {@code GridColumn}s whose summary property
 * is true.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.TOGGLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Expand, Collapse</dd>
 * </dl>
 */
public class GridColumnGroup extends Item {

  private static final int CHEVRON_HEIGHT = 16;
  private static final int CHEVRON_WIDTH = 12;
  private static final int MARGIN_IMAGE = 3;

  private Grid parent;
  private List<GridColumn> columns = new ArrayList<GridColumn>();
  private boolean expanded = true;
  private Font headerFont;
  private boolean headerWordWrap;

  /**
   * Constructs a new instance of this class given its parent (which must be a Grid) and a style
   * value describing its behavior and appearance.
   *
   * @param parent the parent table
   * @param style the style of the group
   * @throws IllegalArgumentException
   * <ul>
   * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the parent</li>
   * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public GridColumnGroup( Grid parent, int style ) {
    super( parent, style );
    this.parent = parent;
    parent.newColumnGroup( this );
  }

  @Override
  public void dispose() {
    super.dispose();
    if( !parent.isDisposing() ) {
      GridColumn[] oldColumns = columns.toArray( new GridColumn[ columns.size() ] );
      columns.clear();
      for( int i = 0; i < oldColumns.length; i++ ) {
        oldColumns[ i ].dispose();
      }
      parent.removeColumnGroup( this );
    }
  }

  /**
   * Returns the parent grid.
   *
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the receiver</li>
   * </ul>
   */
  public Grid getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when an item in the receiver is expanded or collapsed
   * by sending it one of the messages defined in the <code>TreeListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception org.eclipse.swt.SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see TreeListener
   * @see #removeTreeListener
   */
  public void addTreeListener( TreeListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    TypedListener typedListener = new TypedListener( listener );
    addListener( SWT.Expand, typedListener );
    addListener( SWT.Collapse, typedListener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when items in the receiver are expanded or collapsed.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception org.eclipse.swt.SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see TreeListener
   * @see #addTreeListener
   */
  public void removeTreeListener( TreeListener listener ) {
    checkWidget();
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    removeListener( SWT.Expand, listener );
    removeListener( SWT.Collapse, listener );
  }

  /**
   * Returns the columns within this group.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain
   * its list of items, so modifying the array will not affect the receiver.
   * </p>
   * @return the columns
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the receiver</li>
   * </ul>
   */
  public GridColumn[] getColumns() {
    checkWidget();
    return columns.toArray( new GridColumn[ columns.size() ] );
  }

  /**
   * Sets the expanded state of the receiver.
   *
   * @param expanded the expanded to set
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the receiver</li>
   * </ul>
   */
  public void setExpanded( boolean expanded ) {
    checkWidget();
    if( this.expanded != expanded ) {
      this.expanded = expanded;
      if( parent.getCellSelectionEnabled() ) {
        List<Integer> collapsedCols = new ArrayList<>();
        for( int j = 0; j < columns.size(); j++ ) {
          GridColumn column = columns.get( j );
          if( expanded && column.isSummary() ) {
            collapsedCols.add( Integer.valueOf( parent.indexOf( column ) ) );
          }
          if( !expanded && !column.isSummary() ) {
            collapsedCols.add( Integer.valueOf( parent.indexOf( column ) ) );
          }
        }
        Point[] selection = parent.getCellSelection();
        for( int i = 0; i < selection.length; i++ ) {
          if( collapsedCols.contains( Integer.valueOf( selection[ i ].x ) ) ) {
            parent.deselectCell( selection[ i ] );
          }
        }
      }
      parent.invalidateScrollBars();
      parent.redraw();
    }
  }

  /**
   * Returns true if the receiver is expanded, false otherwise.
   *
   * @return the expanded attribute
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the receiver</li>
   * </ul>
   */
  public boolean getExpanded() {
    checkWidget();
    return expanded;
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for the header.
   *
   * @return the receiver's font
   * @throws org.eclipse.swt.SWTException
   * <ul>
   * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   * created the receiver</li>
   * </ul>
   */
  public Font getHeaderFont() {
    checkWidget();
    return headerFont == null ? parent.getFont() : headerFont;
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
   * Sets whether or not text is word-wrapped in the header for this column group.
   * If Grid.setAutoHeight(true) is set, the row height is adjusted to accommodate
   * word-wrapped text.
   * @param wordWrap Set to true to wrap the text, false otherwise
   * @see #getHeaderWordWrap()
   */
  public void setHeaderWordWrap( boolean wordWrap ) {
    checkWidget();
    headerWordWrap = wordWrap;
    parent.layoutCache.invalidateHeaderHeight();
    parent.scheduleRedraw();
  }

  /**
   * Returns whether or not text is word-wrapped in the header for this column group.
   * @return true if the header wraps its text.
   * @see GridColumn#setHeaderWordWrap(boolean)
   */
  public boolean getHeaderWordWrap() {
    checkWidget();
    return headerWordWrap;
  }

  @Override
  @SuppressWarnings( "unchecked" )
  public <T> T getAdapter( Class<T> adapter ) {
    if( adapter == WidgetLCA.class ) {
      return ( T )GridColumnGroupLCA.INSTANCE;
    }
    return super.getAdapter( adapter );
  }

  void newColumn( GridColumn column ) {
    columns.add( column );
  }

  void removeColumn( GridColumn column ) {
    columns.remove( column );
  }

  int getNewColumnIndex() {
    int result = -1;
    if( columns.size() != 0 ) {
      GridColumn lastColumn = columns.get( columns.size() - 1 );
      result = parent.indexOf( lastColumn ) + 1;
    }
    return result;
  }

  int getChevronHeight() {
    return ( getStyle() & SWT.TOGGLE ) != 0 ? CHEVRON_HEIGHT : 0;
  }

  int getHeaderWrapWidth() {
    BoxDimensions headerPadding = parent.getHeaderPadding();
    int result = getGroupWidth() - headerPadding.left - headerPadding.right;
    Image headerImage = getImage();
    if( headerImage != null ) {
      result -= headerImage.getBounds().width;
      result -= MARGIN_IMAGE;
    }
    result -= ( getStyle() & SWT.TOGGLE ) != 0 ? CHEVRON_WIDTH : 0;
    return result;
  }

  private int getGroupWidth() {
    int width = 0;
    for( GridColumn column : columns ) {
      if( column.isVisible() ) {
        width += column.getWidth();
      }
    }
    return width;
  }

}
