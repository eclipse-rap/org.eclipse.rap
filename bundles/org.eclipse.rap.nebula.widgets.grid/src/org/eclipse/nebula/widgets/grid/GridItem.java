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

import static org.eclipse.rap.rwt.internal.textsize.TextSizeUtil.stringExtent;
import static org.eclipse.swt.internal.widgets.MarkupUtil.isMarkupEnabledFor;
import static org.eclipse.swt.internal.widgets.MarkupValidator.isValidationDisabledFor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.grid.internal.GridItemData;
import org.eclipse.nebula.widgets.grid.internal.GridItemData.CellData;
import org.eclipse.nebula.widgets.grid.internal.IGridItemAdapter;
import org.eclipse.nebula.widgets.grid.internal.griditemkit.GridItemLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IWidgetColorAdapter;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this class represent a selectable user interface object that
 * represents an item in a grid.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 */
@SuppressWarnings("restriction")
public class GridItem extends Item {

  private Grid parent;
  private GridItem parentItem;
  private GridItemData data;
  private boolean hasChildren;
  private boolean visible = true;
  private boolean cached;
  private transient IGridItemAdapter gridItemAdapter;
  int index;

  /**
   * Creates a new instance of this class and places the item at the end of
   * the grid.
   *
   * @param parent
   *            parent grid
   * @param style
   *            item style
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridItem( Grid parent, int style ) {
    this( parent, null, style, -1 );
  }

  /**
   * Creates a new instance of this class and places the item in the grid at
   * the given index.
   *
   * @param parent
   *            parent grid
   * @param style
   *            item style
   * @param index
   *            index where to insert item
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridItem( Grid parent, int style, int index ) {
    this( parent, null, style, index );
  }

  /**
   * Creates a new instance of this class as a child node of the given
   * GridItem and places the item at the end of the parents items.
   *
   * @param parent
   *            parent item
   * @param style
   *            item style
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridItem( GridItem parent, int style ) {
    this( parent == null ? null : parent.parent, parent, style, -1 );
  }

  /**
   * Creates a new instance of this class as a child node of the given Grid
   * and places the item at the given index in the parent items list.
   *
   * @param parent
   *            parent item
   * @param style
   *            item style
   * @param index
   *            index to place item
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the parent</li>
   *             <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *             subclass</li>
   *             </ul>
   */
  public GridItem( GridItem parent, int style, int index ) {
    this( parent == null ? null : parent.parent, parent, style, index );
  }

  GridItem( Grid parent, GridItem parentItem, int style, int index ) {
    super( parent, style, index );
    this.parent = parent;
    this.parentItem = parentItem;
    if( parentItem == null ) {
      parent.newItem( this, index, true );
      parent.newRootItem( this, index );
    } else {
      parent.newItem( this, index, false );
      parentItem.newItem( this, index );
      setVisible( parentItem.isVisible() && parentItem.isExpanded() );
    }
    parent.invalidateDefaultRowHeadersText();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    dispose( SWT.DEFAULT );
  }

  void dispose( int flatIndex ) {
    if( !parent.isDisposing() && !isDisposed() ) {
      if( data != null ) {
        for( int i = 0; i < parent.getColumnCount(); i++ ) {
          CellData itemData = getCellData( i );
          updateColumnImageCount( i, itemData.image, null );
          updateColumnTextCount( i, itemData.text, "" );
        }
      }
      int index = flatIndex == SWT.DEFAULT ? parent.internalIndexOf( this ) : flatIndex;
      if( hasChildren ) {
        List<GridItem> children = getItemData().getChildren();
        while( hasChildren ) {
          children.get( 0 ).dispose( index + 1 );
        }
      }
      parent.removeItem( index );
      if( parentItem != null ) {
        parentItem.removeItem( this.index );
      } else {
        parent.removeRootItem( this.index );
      }
      parent.invalidateDefaultRowHeadersText();
    }
    super.dispose();
  }

  /**
   * Fires the given event type on the parent Grid instance. This method
   * should only be called from within a cell renderer. Any other use is not
   * intended.
   *
   * @param eventId
   *            SWT event constant
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void fireEvent( int eventId ) {
    checkWidget();
    Event event = new Event();
    event.display = getDisplay();
    event.widget = this;
    event.item = this;
    event.type = eventId;
    getParent().notifyListeners( eventId, event );
  }

  /**
   * Fires the appropriate events in response to a user checking/unchecking an
   * item. Checking an item fires both a selection event (with event.detail of
   * SWT.CHECK) if the checkbox is in the first column and the seperate check
   * listener (all columns). This method manages that behavior. This method
   * should only be called from within a cell renderer. Any other use is not
   * intended.
   *
   * @param column
   *            the column where the checkbox resides
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void fireCheckEvent( int column ) {
    checkWidget();
    Event event = new Event();
    event.display = getDisplay();
    event.widget = this;
    event.item = this;
    event.type = SWT.Selection;
    event.detail = SWT.CHECK;
    event.index = column;
    getParent().notifyListeners( SWT.Selection, event );
  }

  /**
   * Returns the receiver's parent, which must be a <code>Grid</code>.
   *
   * @return the receiver's parent
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
   * Returns the receiver's parent item, which must be a <code>GridItem</code>
   * or null when the receiver is a root.
   *
   * @return the receiver's parent item
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public GridItem getParentItem() {
    checkWidget();
    return parentItem;
  }

  /**
   * Returns the number of items contained in the receiver that are direct
   * item children of the receiver.
   *
   * @return the number of items
   *
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public int getItemCount() {
    checkWidget();
    return hasChildren ? getItemData().getChildren().size() : 0;
  }

  /**
   * Returns a (possibly empty) array of <code>GridItem</code>s which are the
   * direct item children of the receiver.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain
   * its list of items, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return the receiver's items
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public GridItem[] getItems() {
    checkWidget();
    if( hasChildren ) {
      return getItemData().getChildren().toArray( new GridItem[ 0 ] );
    }
    return new GridItem[ 0 ];
  }

  /**
   * Returns the item at the given, zero-relative index in the receiver.
   * Throws an exception if the index is out of range.
   *
   * @param index
   *            the index of the item to return
   * @return the item at the given index
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *             the number of elements in the list minus 1 (inclusive)</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public GridItem getItem( int index ) {
    checkWidget();
    if( !hasChildren ) {
      throw new IllegalArgumentException( "GridItem has no children!" );
    }
    return getItemData().getChildren().get( index );
  }

  /**
   * Searches the receiver's list starting at the first item (index 0) until
   * an item is found that is equal to the argument, and returns the index of
   * that item. If no item is found, returns -1.
   *
   * @param item
   *            the search item
   * @return the index of the item
   *
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed
   *                </li>
   *                </ul>
   * @exception org.eclipse.swt.SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public int indexOf( GridItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( !hasChildren ) {
      throw new IllegalArgumentException( "GridItem has no children!" );
    }
    return item.getParentItem() == this ? item.index : -1;
  }

  /**
   * Returns true if this item has children.
   *
   * @return true if this item has children
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean hasChildren() {
    checkWidget();
    return hasChildren;
  }

  /**
   * Returns <code>true</code> if the receiver is expanded, and false
   * otherwise.
   * <p>
   *
   * @return the expanded state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li> <li>ERROR_THREAD_INVALID_ACCESS - if not called from
   *             the thread that created the receiver</li>
   *             </ul>
   */
  public boolean isExpanded() {
    checkWidget();
    return data == null ? false : data.expanded;
  }

  /**
   * Sets the expanded state of the receiver.
   * <p>
   *
   * @param expanded
   *            the new expanded state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li> <li>ERROR_THREAD_INVALID_ACCESS - if not called from
   *             the thread that created the receiver</li>
   *             </ul>
   */
  public void setExpanded( boolean expanded ) {
    checkWidget();
    if( getItemData().expanded != expanded ) {
      getItemData().expanded = expanded;
      boolean unselected = false;
      if( hasChildren ) {
        for( GridItem item : getItemData().getChildren() ) {
          item.setVisible( expanded && visible );
          if( !expanded ) {
            if( parent.isSelected( item ) ) {
              parent.deselect( parent.internalIndexOf( item ) );
              unselected = true;
            }
            if( deselectChildren( item ) ) {
              unselected = true;
            }
          }
        }
      }
      parent.invalidateDefaultRowHeadersText();
      parent.scheduleRedraw();
      if( unselected ) {
        Event event = new Event();
        event.item = this;
        parent.notifyListeners( SWT.Selection, event );
      }
      if( parent.getFocusItem() != null && !parent.getFocusItem().isVisible() ) {
        parent.setFocusItem( this );
      }
      markCached();
    }
  }

  /**
   * Returns the level of this item in the tree.
   *
   * @return the level of the item in the tree
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public int getLevel() {
    checkWidget();
    return parentItem == null ? 0 : parentItem.getLevel() + 1;
  }

  /**
   * Sets the font that the receiver will use to paint textual information for
   * this item to the font specified by the argument, or to the default font
   * for that kind of control if the argument is null.
   *
   * @param font
   *            the new font (or null)
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
  public void setFont( Font font ) {
    checkWidget();
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getItemData().defaultFont = font;
    markCached();
    parent.scheduleRedraw();
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for this item.
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
  public Font getFont() {
    checkWidget();
    handleVirtual();
    Font defaultFont = getItemData().defaultFont;
    return defaultFont == null ? parent.getFont() : defaultFont;
  }

  /**
   * Sets the font that the receiver will use to paint textual information for
   * the specified cell in this item to the font specified by the argument, or
   * to the default font for that kind of control if the argument is null.
   *
   * @param index
   *            the column index
   * @param font
   *            the new font (or null)
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
  public void setFont( int index, Font font ) {
    checkWidget();
    if( font != null && font.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getCellData( index ).font = font;
    markCached();
    parent.scheduleRedraw();
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for the specified cell in this item.
   *
   * @param index
   *            the column index
   * @return the receiver's font
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Font getFont( int index ) {
    checkWidget();
    handleVirtual();
    Font result = getCellData( index ).font;
    if( result == null ) {
      result = getFont();
    }
    return result;
  }

  /**
   * Sets the receiver's background color to the color specified by the
   * argument, or to the default system color for the item if the argument is
   * null.
   *
   * @param background
   *            the new color (or null)
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
  public void setBackground( Color background ) {
    checkWidget();
    if( background != null && background.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getItemData().defaultBackground = background;
    markCached();
  }

  /**
   * Returns the receiver's background color.
   *
   * @return the background color
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Color getBackground() {
    checkWidget();
    handleVirtual();
    Color defaultBackground = getItemData().defaultBackground;
    return defaultBackground == null ? parent.getBackground() : defaultBackground;
  }

  /**
   * Sets the background color at the given column index in the receiver to
   * the color specified by the argument, or to the default system color for
   * the item if the argument is null.
   *
   * @param index
   *            the column index
   * @param background
   *            the new color (or null)
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
  public void setBackground( int index, Color background ) {
    checkWidget();
    if( background != null && background.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getCellData( index ).background = background;
    markCached();
  }

  /**
   * Returns the background color at the given column index in the receiver.
   *
   * @param index
   *            the column index
   * @return the background color
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Color getBackground( int index ) {
    checkWidget();
    handleVirtual();
    Color result = getCellData( index ).background;
     if( result == null ) {
       result = getBackground();
     }
    return result;
  }

  /**
   * Sets the receiver's foreground color to the color specified by the
   * argument, or to the default system color for the item if the argument is
   * null.
   *
   * @param foreground
   *            the new color (or null)
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
  public void setForeground( Color foreground ) {
    checkWidget();
    if( foreground != null && foreground.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getItemData().defaultForeground = foreground;
    markCached();
  }

  /**
   * Returns the foreground color that the receiver will use to draw.
   *
   * @return the receiver's foreground color
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Color getForeground() {
    checkWidget();
    handleVirtual();
    Color defaultForeground = getItemData().defaultForeground;
    return defaultForeground == null ? parent.getForeground() : defaultForeground;
  }

  /**
   * Sets the foreground color at the given column index in the receiver to
   * the color specified by the argument, or to the default system color for
   * the item if the argument is null.
   *
   * @param index
   *            the column index
   * @param foreground
   *            the new color (or null)
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
  public void setForeground( int index, Color foreground ) {
    checkWidget();
    if( foreground != null && foreground.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    getCellData( index ).foreground = foreground;
    markCached();
  }

  /**
   * Returns the foreground color at the given column index in the receiver.
   *
   * @param index
   *            the column index
   * @return the foreground color
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Color getForeground( int index ) {
    checkWidget();
    handleVirtual();
    Color result = getCellData( index ).foreground;
    if( result == null ) {
      result = getForeground();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setText( String string ) {
    checkWidget();
    setText( 0, string );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText() {
    checkWidget();
    return getText( 0 );
  }

  /**
   * Sets the receiver's text at a column.
   *
   * @param index
   *            the column index
   * @param text
   *            the new text
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setText( int index, String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( isMarkupEnabledFor( parent ) && !isValidationDisabledFor( parent ) ) {
      MarkupValidator.getInstance().validate( text );
    }
    CellData cellData = getCellData( index );
    updateColumnTextCount( index, cellData.text, text );
    cellData.text = text;
    markCached();
  }

  /**
   * Returns the text stored at the given column index in the receiver, or
   * empty string if the text has not been set.
   *
   * @param index
   *            the column index
   * @return the text stored at the given column index in the receiver
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public String getText( int index ) {
    checkWidget();
    handleVirtual();
    return getCellData( index ).text;
  }

  /**
   * Sets the tooltip for the given column index.
   *
   * @param index
   *            the column index
   * @param tooltip
   *            the tooltip text
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setToolTipText( int index, String tooltip ) {
    checkWidget();
    getCellData( index ).tooltip = tooltip;
    if( tooltip != null && tooltip.length() > 0 ) {
      parent.setCellToolTipsEnabled( true );
    }
    markCached();
  }

  /**
   * Returns the tooltip for the given cell.
   *
   * @param index
   *            the column index
   * @return the tooltip
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public String getToolTipText( int index ) {
    checkWidget();
    handleVirtual();
    return getCellData( index ).tooltip;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setImage( Image image ) {
    checkWidget();
    setImage( 0, image );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage() {
    checkWidget();
    return getImage( 0 );
  }

  /**
   * Sets the receiver's image at a column.
   *
   * @param index
   *            the column index
   * @param image
   *            the new image
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setImage( int index, Image image ) {
    checkWidget();
    if( image != null && image.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    CellData cellData = getCellData( index );
    updateColumnImageCount( index, cellData.image, image );
    cellData.image = image;
    parent.imageSetOnItem( image );
    markCached();
  }

  /**
   * Returns the image stored at the given column index in the receiver, or
   * null if the image has not been set or if the column does not exist.
   *
   * @param index
   *            the column index
   * @return the image stored at the given column index in the receiver
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Image getImage( int index ) {
    checkWidget();
    handleVirtual();
    return getCellData( index ).image;
  }

  /**
   * Sets the checked state at the first column in the receiver.
   *
   * @param checked
   *            the new checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setChecked( boolean checked ) {
    checkWidget();
    setChecked( 0, checked );
  }

  /**
   * Returns the checked state at the first column in the receiver.
   *
   * @return the checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getChecked() {
    checkWidget();
    return getChecked( 0 );
  }

  /**
   * Sets the checked state at the given column index in the receiver.
   *
   * @param index
   *            the column index
   * @param checked
   *            the new checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setChecked( int index, boolean checked ) {
    checkWidget();
    // [if] TODO: probably need a check for parent.getColumn( index ).isCheck() ?
    getCellData( index ).checked = checked;
    markCached();
  }

  /**
   * Returns the checked state at the given column index in the receiver.
   *
   * @param index
   *            the column index
   * @return the checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getChecked( int index ) {
    checkWidget();
    handleVirtual();
    return getCellData( index ).checked;
  }

  /**
   * Sets the grayed state of the checkbox for the first column. This state
   * change only applies if the GridColumn was created with the SWT.CHECK
   * style.
   *
   * @param grayed
   *            the new grayed state of the checkbox;
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setGrayed( boolean grayed ) {
    checkWidget();
    setGrayed( 0, grayed );
  }

  /**
   * Returns <code>true</code> if the first column in the receiver is grayed,
   * and false otherwise. When the GridColumn does not have the
   * <code>CHECK</code> style, return false.
   *
   * @return the grayed state of the checkbox
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getGrayed() {
    checkWidget();
    return getGrayed( 0 );
  }

  /**
   * Sets the grayed state of the checkbox for the given column index. This
   * state change only applies if the GridColumn was created with the
   * SWT.CHECK style.
   *
   * @param index
   *            the column index
   * @param grayed
   *            the new grayed state of the checkbox;
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setGrayed( int index, boolean grayed ) {
    checkWidget();
    // [if] TODO: probably need a check for parent.getColumn( index ).isCheck() ?
    getCellData( index ).grayed = grayed;
    markCached();
  }

  /**
   * Returns <code>true</code> if the column at the given index in the
   * receiver is grayed, and false otherwise. When the GridColumn does not
   * have the <code>CHECK</code> style, return false.
   *
   * @param index
   *            the column index
   * @return the grayed state of the checkbox
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getGrayed( int index ) {
    checkWidget();
    handleVirtual();
    return getCellData( index ).grayed;
  }

  /**
   * Sets the checkable state at the given column index in the receiver. A
   * checkbox which is uncheckable will not be modifiable by the user but
   * still make be modified programmatically. If the column at the given index
   * is not checkable then individual cell will not be checkable regardless.
   *
   * @param index
   *            the column index
   * @param checked
   *            the new checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setCheckable( int index, boolean checked ) {
    checkWidget();
    // [if] TODO: probably need a check for parent.getColumn( index ).isCheck() ?
    getCellData( index ).checkable = checked;
    markCached();
  }

  /**
   * Returns the checkable state at the given column index in the receiver. If
   * the column at the given index is not checkable then this will return
   * false regardless of the individual cell's checkable state.
   *
   * @param index
   *            the column index
   * @return the checked state
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public boolean getCheckable( int index ) {
    checkWidget();
    handleVirtual();
    boolean result = getCellData( index ).checkable;
    if( parent.getColumnCount() > 0 && !parent.getColumn( index ).getCheckable() ) {
      result = false;
    }
    return result;
  }

  /**
   * Sets the column spanning for the column at the given index to span the
   * given number of subsequent columns.
   *
   * @param index
   *            column index that should span
   * @param span
   *            number of subsequent columns to span
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.1
   */
  public void setColumnSpan( int index, int span ) {
    checkWidget();
    getCellData( index ).columnSpan = span;
    parent.setHasSpanning( true );
  }

  /**
   * Returns the column span for the given column index in the receiver.
   *
   * @param index
   *            the column index
   * @return the number of columns spanned (0 equals no columns spanned)
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.1
   */
  public int getColumnSpan( int index ) {
    checkWidget();
    return getCellData( index ).columnSpan;
  }

  /**
   * Sets the height of this <code>GridItem</code>.
   *
   * @param height
   *            new height in pixels
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public void setHeight( int height ) {
    checkWidget();
    if( height < 1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( getItemData().customHeight != height ) {
      getItemData().customHeight = height;
      parent.hasDifferingHeights = true;
      markCached();
      parent.scheduleRedraw();
    }
  }

  /**
   * Returns the height of this <code>GridItem</code>.
   *
   * @return height of this <code>GridItem</code>
   */
  public int getHeight() {
    checkWidget();
    int customHeight = getItemData().customHeight;
    return customHeight != -1 ? customHeight : parent.getItemHeight();
  }

  /**
   * Sets the receiver's row header text. If the text is <code>null</code> the
   * row header will display the row number.
   *
   * @param text
   *            the new text
   * @throws IllegalArgumentException
   *             <ul>
   *             <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   *             </ul>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public void setHeaderText( String text ) {
    checkWidget();
    if( parent.getRowHeadersColumn() != null ) {
      updateColumnTextCount( Integer.MIN_VALUE, internalGetHeaderText(), text );
      getItemData().headerText = text;
    }
  }

  /**
   * Returns the receiver's row header text. If the text is <code>null</code>
   * the row header will display the row number.
   *
   * @return the text stored for the row header or code <code>null</code> if
   *         the default has to be displayed
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public String getHeaderText() {
    checkWidget();
    return getItemData().headerText;
  }

  /**
   * Sets the receiver's row header image. If the image is <code>null</code>
   * none is shown in the header
   *
   * @param image
   *            the new image
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public void setHeaderImage( Image image ) {
    checkWidget();
    if( image != null && image.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( parent.getRowHeadersColumn() != null ) {
      updateColumnImageCount( Integer.MIN_VALUE, getItemData().headerImage, image );
      getItemData().headerImage = image;
      parent.imageSetOnItem( image );
    }
  }

  /**
   * Returns the receiver's row header image.
   *
   * @return the image stored for the header or <code>null</code> if none has
   *         to be displayed
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public Image getHeaderImage() {
    checkWidget();
    return getItemData().headerImage;
  }

  /**
   * Set the new header background
   *
   * @param headerBackground
   *            the color or <code>null</code>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public void setHeaderBackground( Color headerBackground ) {
    checkWidget();
    if( headerBackground != null && headerBackground.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( parent.getRowHeadersColumn() != null ) {
      getItemData().headerBackground = headerBackground;
    }
  }

  /**
   * Returns the receiver's row header background color
   *
   * @return the color or <code>null</code> if none
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public Color getHeaderBackground() {
    checkWidget();
    return getItemData().headerBackground;
  }

  /**
   * Set the new header foreground
   *
   * @param headerForeground
   *            the color or <code>null</code>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public void setHeaderForeground( Color headerForeground ) {
    checkWidget();
    if( headerForeground != null && headerForeground.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( parent.getRowHeadersColumn() != null ) {
      getItemData().headerForeground = headerForeground;
    }
  }

  /**
   * Returns the receiver's row header foreground color
   *
   * @return the color or <code>null</code> if none
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public Color getHeaderForeground() {
    checkWidget();
    return getItemData().headerForeground;
  }

  /**
   * Set the new header font
   *
   * @param headerFont
   *            the font or <code>null</code>
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public void setHeaderFont( Font headerFont ) {
    checkWidget();
    if( headerFont != null && headerFont.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( parent.getRowHeadersColumn() != null ) {
      getItemData().headerFont = headerFont;
    }
  }

  /**
   * Returns the receiver's row header font
   *
   * @return the font or <code>null</code> if none
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   *
   * @since 3.14
   */
  public Font getHeaderFont() {
    checkWidget();
    return getItemData().headerFont;
  }

  /**
   * Sets this <code>GridItem</code> to its preferred height.
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
    // [if] As different item heights (wordwrap and autoHeight) are not supported,
    // we only invalidate the cache here
    parent.layoutCache.invalidateItemHeight();
  }

  /**
   * Returns a rectangle describing the receiver's size and location relative
   * to its parent at a column in the table.
   *
   * @param columnIndex
   *            the index that specifies the column
   * @return the receiver's bounding column rectangle
   * @throws org.eclipse.swt.SWTException
   *             <ul>
   *             <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed
   *             </li>
   *             <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *             thread that created the receiver</li>
   *             </ul>
   */
  public Rectangle getBounds( int columnIndex ) {
    checkWidget();
    if( isVisible() && parent.isShown( this ) ) {
      Point origin = parent.getOrigin( parent.getColumn( columnIndex ), this );
      Point cellSize = getCellSize( columnIndex );
      return new Rectangle( origin.x, origin.y, cellSize.x, cellSize.y );
    }
    // [if] -1000 is used in the original implementation
    return new Rectangle( -1000, -1000, 0, 0 );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter( Class<T> adapter ) {
    if(    adapter == IWidgetFontAdapter.class
        || adapter == IWidgetColorAdapter.class
        || adapter == IGridItemAdapter.class )
    {
      if( gridItemAdapter == null ) {
        gridItemAdapter = new GridItemAdapter();
      }
      return ( T )gridItemAdapter;
    } else if( adapter == WidgetLCA.class ) {
      return ( T )GridItemLCA.INSTANCE;
    }
    return super.getAdapter( adapter );
  }

  public boolean isVisible() {
    return visible;
  }

  void setVisible( boolean visible ) {
    if( this.visible != visible ) {
      this.visible = visible;
      parent.updateVisibleItems( visible ? 1 : -1 );
      if( hasChildren ) {
        for( GridItem item : getItemData().getChildren() ) {
          item.setVisible( visible && isExpanded() );
        }
      }
    }
  }

  private void newItem( GridItem item, int index ) {
    List<GridItem> children = getItemData().getChildren();
    if( index == -1 ) {
      children.add( item );
      item.index = children.size() - 1;
    } else {
      children.add( index, item );
      item.index = index;
    }
    adjustItemIndices( item.index + 1 );
    hasChildren = true;
  }

  private void removeItem( int index ) {
    if( !hasChildren ) {
      throw new IllegalArgumentException( "GridItem has no children!" );
    }
    List<GridItem> children = getItemData().getChildren();
    children.remove( index );
    adjustItemIndices( index );
    hasChildren = children.size() > 0;
  }

  private void adjustItemIndices( int start ) {
    List<GridItem> children = getItemData().getChildren();
    for( int i = start; i < children.size(); i++ ) {
      children.get( i ).index = i;
    }
  }

  void columnAdded( int index ) {
    if( data != null && parent.getColumnCount() > 1 ) {
      data.addCellData( index );
    }
  }

  void columnRemoved( int index ) {
    if( data != null && parent.getColumnCount() > 0 ) {
      data.removeCellData( index );
    }
  }

  void clear( boolean allChildren ) {
    if( data != null ) {
      for( int i = 0; i < parent.getColumnCount(); i++ ) {
        CellData cellData = getCellData( i );
        updateColumnImageCount( i, cellData.image, null );
        updateColumnTextCount( i, cellData.text, "" );
      }
      data.clear();
    }
    cached = false;
    // Recursively clear children if requested.
    if( allChildren && hasChildren ) {
      List<GridItem> children = getItemData().getChildren();
      for( int i = children.size() - 1; i >= 0; i-- ) {
        children.get( i ).clear( true );
      }
    }
  }

  int getPreferredWidth( int index ) {
    int result = getIndentationWidth( index );
    result += getPaddingWidth( index );
    result += getCheckBoxWidth( index );
    result += getImageWidth( index );
    result += getSpacing( index );
    result += getTextWidth( index );
    return result;
  }

  private int getIndentationWidth( int index ) {
    int result = 0;
    if( parent.isTreeColumn( index ) ) {
      result = ( getLevel() + 1 ) * parent.getIndentationWidth();
    }
    return result;
  }

  private int getPaddingWidth( int index ) {
    BoxDimensions cellPadding = parent.getCellPadding();
    int result = cellPadding.left + cellPadding.right;
    if( parent.isTreeColumn( index ) ) {
      result -= parent.getCellPadding().left;
    }
    return result;
  }

  private int getCheckBoxWidth( int index ) {
    return parent.getColumn( index ).isCheck() ? parent.getCheckBoxImageOuterSize().width : 0;
  }

  private int getImageWidth( int index ) {
    int result = 0;
    if( parent.hasColumnImages( index ) ) {
      result = parent.getItemImageSize().x;
    }
    return result;
  }

  private int getSpacing( int index ) {
    int result = 0;
    String text = getCellData( index ).text;
    if( parent.hasColumnImages( index ) && text.length() > 0 ) {
      result = parent.getCellSpacing();
    }
    return result;
  }

  private int getTextWidth( int index ) {
    String text = getCellData( index ).text;
    if( text.length() > 0 ) {
      return stringExtent( internalGetFont( index ), text, isMarkupEnabledFor( parent ) ).x;
    }
    return 0;
  }

  int getTextOffset( int index ) {
    int result = getIndentationWidth( index );
    if( !parent.isTreeColumn( index ) ) {
      result += parent.getCellPadding().left;
    }
    result += getCheckBoxWidth( index );
    result += getImageWidth( index );
    result += getSpacing( index );
    return result;
  }

  private Font internalGetFont( int index ) {
    Font result = getCellData( index ).font;
    if( result == null ) {
      result = getItemData().defaultFont;
    }
    if( result == null ) {
      result = parent.getFont();
    }
    return result;
  }

  protected Point getCellSize( int index ) {
    int width = 0;
    int span = getColumnSpan( index );
    for( int i = 0; i <= span && i < parent.getColumnCount() - index; i++ ) {
      width += parent.getColumn( index + i ).getWidth();
    }
    GridItem item = this;
    int itemIndex = parent.internalIndexOf( item );
    int height = getHeight();
    span = 0; // getRowSpan( index );
    for( int i = 1; i <= span && i < parent.getItemCount() - itemIndex; i++ ) {
      item = parent.getItem( itemIndex + i );
      if( item.isVisible() ) {
        height += item.getHeight();
      }
    }
    return new Point( width, height );
  }

  private CellData getCellData( int index ) {
    return getItemData().getCellData( index );
  }

  private GridItemData getItemData() {
    ensureItemData();
    return data;
  }

  private String internalGetHeaderText() {
    if( !parent.isRowHeaderVisible() ) {
      return "";
    }
    String text = getItemData().headerText;
    if( text == null ) {
      text = getItemData().defaultHeaderText;
    }
    return text == null ? "" : text;
  }

  void setDefaultHeaderText( String text ) {
    getItemData().defaultHeaderText = text;
  }

  private Color internalGetHeaderBackground() {
    if( !parent.isRowHeaderVisible() ) {
      return null;
    }
    Color background = getItemData().headerBackground;
    if( background == null ) {
      background = getItemData().defaultHeaderBackground;
    }
    return background;
  }

  void ensureItemData() {
    if( data == null ) {
      data = new GridItemData( parent.getColumnCount() );
      data.defaultHeaderBackground = new Color( getDisplay(), 231, 231, 231 );
    }
  }

  void handleVirtual() {
    if( !isCached() ) {
      markCached();
      Event event = new Event();
      event.item = this;
      event.index = index;
      parent.notifyListeners( SWT.SetData, event );
    }
  }

  private boolean deselectChildren( GridItem item ) {
    boolean flag = false;
    for( GridItem child : item.getItems() ) {
      if( parent.isSelected( child ) ) {
        flag = true;
      }
      parent.deselect( parent.internalIndexOf( child ) );
      if( deselectChildren( child ) ) {
        flag = true;
      }
    }
    return flag;
  }

  private void updateColumnImageCount( int index, Image oldImage, Image newImage ) {
    int delta = 0;
    if( oldImage == null && newImage != null ) {
      delta = +1;
    } else if( oldImage != null && newImage == null ) {
      delta = -1;
    }
    if( delta != 0 && index == Integer.MIN_VALUE ) {
      parent.getRowHeadersColumn().imageCount += delta;
    } else if( delta != 0 && index >= 0 && index < parent.getColumnCount() ) {
      parent.getColumn( index ).imageCount += delta;
    }
  }

  private void updateColumnTextCount( int index, String oldText, String newText ) {
    int delta = 0;
    if( oldText.length() == 0 && newText.length() > 0 ) {
      delta = +1;
    } else if( oldText.length() > 0 && newText.length() == 0 ) {
      delta = -1;
    }
    if( delta != 0 && index == Integer.MIN_VALUE ) {
      parent.getRowHeadersColumn().textCount += delta;
    } else if( delta != 0 && index >= 0 && index < parent.getColumnCount() ) {
      parent.getColumn( index ).textCount += delta;
    }
  }

  boolean isCached() {
    return parent.isVirtual() ? cached : true;
  }

  private void markCached() {
    if( parent.isVirtual() ) {
      cached = true;
    }
  }

  boolean isResolved() {
    return parent.isVirtual() ? data != null : true;
  }

  ////////////////
  // Inner classes

  private final class GridItemAdapter
    implements IGridItemAdapter, IWidgetFontAdapter, IWidgetColorAdapter
  {

    @Override
    public boolean isParentDisposed() {
      Widget itemParent = parentItem == null ? parent : parentItem;
      return itemParent.isDisposed();
    }

    @Override
    public boolean isCached() {
      return GridItem.this.isCached();
    }

    @Override
    public Color getUserBackground() {
      return getItemData().defaultBackground;
    }

    @Override
    public Color getUserForeground() {
      return getItemData().defaultForeground;
    }

    @Override
    public Font getUserFont() {
      return getItemData().defaultFont;
    }

    @Override
    public String[] getTexts() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      String[] result = null;
      for( int i = 0; i < columnCount; i++ ) {
        String text = "";
        if( i == 0 && offset == 1 ) {
          text = internalGetHeaderText();
        } else {
          text = getCellData( i - offset ).text;
        }
        if( !"".equals( text ) ) {
          if( result == null ) {
            result = new String[ columnCount ];
            Arrays.fill( result, "" );
          }
          result[ i ] = text;
        }
      }
      return result;
    }

    @Override
    public Image[] getImages() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      Image[] result = null;
      for( int i = 0; i < columnCount; i++ ) {
        Image image = null;
        if( i == 0 && offset == 1 ) {
          image = getItemData().headerImage;
        } else {
          image = getCellData( i - offset ).image;
        }
        if( image != null ) {
          if( result == null ) {
            result = new Image[ columnCount ];
          }
          result[ i ] = image;
        }
      }
      return result;
    }

    @Override
    public Color[] getCellBackgrounds() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      Color[] result = null;
      for( int i = 0; i < columnCount; i++ ) {
        Color background = null;
        if( i == 0 && offset == 1 ) {
          background = internalGetHeaderBackground();
        } else {
          background = getCellData( i - offset ).background;
        }
        if( background != null ) {
          if( result == null ) {
            result = new Color[ columnCount ];
          }
          result[ i ] = background;
        }
      }
      return result;
    }

    @Override
    public Color[] getCellForegrounds() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      Color[] result = null;
      for( int i = 0; i < columnCount; i++ ) {
        Color foreground = null;
        if( i == 0 && offset == 1 ) {
          foreground = getItemData().headerForeground;
        } else {
          foreground = getCellData( i - offset ).foreground;
        }
        if( foreground != null ) {
          if( result == null ) {
            result = new Color[ columnCount ];
          }
          result[ i ] = foreground;
        }
      }
      return result;
    }

    @Override
    public Font[] getCellFonts() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      Font[] result = null;
      for( int i = 0; i < columnCount; i++ ) {
        Font font = null;
        if( i == 0 && offset == 1 ) {
          font = getItemData().headerFont;
        } else {
          font = getCellData( i - offset ).font;
        }
        if( font != null ) {
          if( result == null ) {
            result = new Font[ columnCount ];
          }
          result[ i ] = font;
        }
      }
      return result;
    }

    @Override
    public boolean[] getCellChecked() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      boolean[] result = null;
      for( int i = offset; i < columnCount; i++ ) {
        boolean checked = getCellData( i - offset ).checked;
        if( checked ) {
          if( result == null ) {
            result = new boolean[ columnCount ];
          }
          result[ i ] = checked;
        }
      }
      return result;
    }

    @Override
    public boolean[] getCellGrayed() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      boolean[] result = null;
      for( int i = offset; i < columnCount; i++ ) {
        boolean grayed = getCellData( i - offset ).grayed;
        if( grayed ) {
          if( result == null ) {
            result = new boolean[ columnCount ];
          }
          result[ i ] = grayed;
        }
      }
      return result;
    }

    @Override
    public boolean[] getCellCheckable() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      boolean[] result = null;
      for( int i = offset; i < columnCount; i++ ) {
        boolean checkable = getCellData( i - offset ).checkable;
        if( !checkable ) {
          if( result == null ) {
            result = new boolean[ columnCount ];
            Arrays.fill( result, true );
          }
          result[ i ] = checkable;
        }
      }
      return result;
    }

    @Override
    public int[] getColumnSpans() {
      int offset = getColumnOffset();
      int columnCount = Math.max( 1, getParent().getColumnCount() ) + offset;
      int[] result = null;
      for( int i = offset; i < columnCount; i++ ) {
        int span = getCellData( i - offset ).columnSpan;
        if( span != 0 ) {
          if( result == null ) {
            result = new int[ columnCount ];
          }
          result[ i ] = span;
        }
      }
      return result;
    }

    private int getColumnOffset() {
      return parent.getRowHeadersColumn() != null ? 1 : 0;
    }

  }

}
