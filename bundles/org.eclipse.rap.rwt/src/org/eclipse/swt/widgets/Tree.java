/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.events.SetDataEvent;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;


/**
 * Instances of this class provide a selectable user interface object that
 * displays a hierarchy of items and issues notification when an item in the
 * hierarchy is selected.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>TreeItem</code>.
 * </p>
 * <p>
 * Style <code>VIRTUAL</code> is used to create a <code>Tree</code> whose
 * <code>TreeItem</code>s are to be populated by the client on an on-demand
 * basis instead of up-front. This can provide significant performance
 * improvements for trees that are very large or for which <code>TreeItem</code>
 * population is expensive (for example, retrieving values from an external
 * source).
 * </p>
 * <p>
 * Here is an example of using a <code>Tree</code> with style
 * <code>VIRTUAL</code>: <code><pre>
 *  final Tree tree = new Tree(parent, SWT.VIRTUAL | SWT.BORDER);
 *  tree.setItemCount(20);
 *  tree.addListener(SWT.SetData, new Listener() {
 *      public void handleEvent(Event event) {
 *          TreeItem item = (TreeItem)event.item;
 *          TreeItem parentItem = item.getParentItem();
 *          String text = null;
 *          if (parentItem == null) {
 *              text = "node " + tree.indexOf(item);
 *          } else {
 *              text = parentItem.getText() + " - " + parentItem.indexOf(item);
 *          }
 *          item.setText(text);
 *          System.out.println(text);
 *          item.setItemCount(10);
 *      }
 *  });
 * </pre></code>
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not make sense to add <code>Control</code> children to it, or set a
 * layout on it.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK<!--, FULL_SELECTION-->, VIRTUAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, Collapse, Expand, SetData<!--, MeasureItem,
 * EraseItem, PaintItem--></dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles SINGLE and MULTI may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @since 1.0
 */
public class Tree extends Composite {

  private static final TreeItem[] EMPTY_SELECTION = new TreeItem[ 0 ];
  // This values must be kept in sync with appearance of list items
  private static final int CHECK_HEIGHT = 13;
  private static final int VERTICAL_ITEM_MARGIN = 3;
  private static final int SCROLL_SIZE = 16;

  /* package */final ItemHolder itemHolder;
  /* package */final ItemHolder columnHolder;
  private TreeItem[] selection;
  private boolean linesVisible;
  private int[] columnOrder;
  private TreeColumn sortColumn;
  private int sortDirection = SWT.NONE;
  private boolean headerVisible = false;
  private TreeItem showItem;
  private final ResizeListener resizeListener;
  private final TreeListener expandListener;
  private TreeItem currentItem;
  private final ITreeAdapter treeAdapter;
  /* package */int scrollTop, scrollLeft;

  private final class CompositeItemHolder implements IItemHolderAdapter {

    public void add( final Item item ) {
      if( item instanceof TreeItem ) {
        itemHolder.add( item );
      } else {
        columnHolder.add( item );
      }
    }

    public void insert( final Item item, final int index ) {
      if( item instanceof TreeItem ) {
        itemHolder.insert( item, index );
      } else {
        columnHolder.insert( item, index );
      }
    }

    public void remove( final Item item ) {
      if( item instanceof TreeItem ) {
        itemHolder.remove( item );
      } else {
        columnHolder.remove( item );
      }
    }

    public Item[] getItems() {
      TreeItem[] items = ( TreeItem[] )itemHolder.getItems();
      Item[] columns = columnHolder.getItems();
      Item[] result = new Item[ items.length + columns.length ];
      System.arraycopy( columns, 0, result, 0, columns.length );
      System.arraycopy( items, 0, result, columns.length, items.length );
      return result;
    }
  }
  private final class InternalTreeAdapter implements ITreeAdapter {

    public TreeItem getShowItem() {
      return Tree.this.showItem;
    }

    public void clearShowItem() {
      Tree.this.showItem = null;
    }

    public void setScrollLeft( final int left ) {
      Tree.this.scrollLeft = left;
    }

    public void setScrollTop( final int top ) {
      Tree.this.scrollTop = top;
    }

    public int getScrollTop() {
      return Tree.this.scrollTop;
    }

    public int getScrollLeft() {
      return Tree.this.scrollLeft;
    }

    public boolean isCached( final TreeItem item ) {
      return item.isCached();
    }

    public void checkAllData( final Tree tree ) {
      Tree.checkAllData( tree );
    }
  }

  private static final class ResizeListener extends ControlAdapter {
    public void controlResized( final ControlEvent event ) {
      checkAllData( ( Tree )event.widget );
    }
  }

  private static final class ExpandListener extends TreeAdapter {

    public void treeExpanded( final TreeEvent event ) {
      Tree tree = ( Tree )event.widget;
      tree.updateFlatIndices();
      // TreeItem item = ( TreeItem )event.item;
      // TODO: [bm] only update this subtree
      checkAllData( tree );
    }

    public void treeCollapsed( final TreeEvent event ) {
      Tree tree = ( Tree )event.widget;
      tree.updateFlatIndices();
    }
  }

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
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
   * @see SWT#SINGLE
   * @see SWT#MULTI
   * @see SWT#CHECK
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Tree( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TreeItem.class );
    columnHolder = new ItemHolder( TreeColumn.class );
    treeAdapter = new InternalTreeAdapter();
    selection = EMPTY_SELECTION;
    if( ( this.style & SWT.VIRTUAL ) != 0 ) {
      resizeListener = new ResizeListener();
      addControlListener( resizeListener );
      expandListener = new ExpandListener();
      addTreeListener( expandListener );
    } else {
      resizeListener = null;
      expandListener = null;
    }
  }

  void initState() {
    state &= ~( /* CANVAS | */THEME_BACKGROUND );
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = new CompositeItemHolder();
    } else if( adapter == ITreeAdapter.class ) {
      result = treeAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  // /////////////////////////
  // Methods to manage items
  /**
   * Sets the number of root-level items contained in the receiver.
   *
   * @param itemCount the number of items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setItemCount( final int itemCount ) {
    checkWidget();
    setItemCount( itemCount, null );
    redraw();
  }

  void setItemCount( final int itemCount, final TreeItem parent ) {
    int oldItemCount;
    if( parent == null ) {
      oldItemCount = getItemCount();
    } else {
      oldItemCount = parent.getItemCount();
    }
    int newItemCount = Math.max( 0, itemCount );
    if( newItemCount != oldItemCount ) {
      TreeItem[] items;
      if( parent == null ) {
        items = getItems();
      } else {
        items = parent.getItems();
      }
      int index = newItemCount;
      while( index < oldItemCount ) {
        TreeItem item = items[ index ];
        if( item != null && !item.isDisposed() ) {
          item.dispose();
        }
        index++;
      }
      for( int i = oldItemCount; i < newItemCount; i++ ) {
        if( parent == null ) {
          new TreeItem( this, SWT.NONE, i );
        } else {
          new TreeItem( parent, SWT.NONE, i );
        }
      }
    }
  }

  /**
   * Returns the number of items contained in the receiver that are direct item
   * children of the receiver. The number that is returned is the number of
   * roots in the tree.
   *
   * @return the number of items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Returns a (possibly empty) array of items contained in the receiver that
   * are direct item children of the receiver. These are the roots of the tree.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain its
   * list of items, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return the items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public TreeItem[] getItems() {
    checkWidget();
    return ( org.eclipse.swt.widgets.TreeItem[] )itemHolder.getItems();
  }

  /**
   * Returns the item at the given, zero-relative index in the receiver. Throws
   * an exception if the index is out of range.
   *
   * @param index the index of the item to return
   * @return the item at the given index
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the list minus 1 (inclusive)</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public TreeItem getItem( final int index ) {
    checkWidget();
    return ( TreeItem )itemHolder.getItem( index );
  }

  /**
   * Searches the receiver's list starting at the first item (index 0) until an
   * item is found that is equal to the argument, and returns the index of that
   * item. If no item is found, returns -1.
   *
   * @param item the search item
   * @return the index of the item
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the tool item is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if the tool item has been
   *              disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int indexOf( final TreeItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int index = itemHolder.indexOf( item );
    return index;
  }

  /**
   * Returns the receiver's parent item, which must be a <code>TreeItem</code>
   * or null when the receiver is a root.
   *
   * @return the receiver's parent item
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public TreeItem getParentItem() {
    checkWidget();
    return null;
  }

  /**
   * Removes all of the items from the receiver.
   *
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void removeAll() {
    checkWidget();
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    selection = EMPTY_SELECTION;
  }

  /**
   * Shows the item. If the item is already showing in the receiver, this method
   * simply returns. Otherwise, the items are scrolled and expanded until the
   * item is visible.
   *
   * @param item the item to be shown
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#showSelection()
   */
  public void showItem( final TreeItem item ) {
    checkWidget();
    if( item == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( item.getParent() != this ) {
      return;
    }
    TreeItem parent = item.getParentItem();
    while( parent != null ) {
      parent.setExpanded( true );
      parent = parent.getParentItem();
    }
    showItem = item;
  }

  /**
   * Shows the selection. If the selection is already showing in the receiver,
   * this method simply returns. Otherwise, the items are scrolled until the
   * selection is visible.
   *
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#showItem(TreeItem)
   */
  public void showSelection() {
    checkWidget();
    if( selection.length == 0 ) {
      return;
    }
    showItem( selection[ 0 ] );
  }

  // ///////////////////////////////////
  // Methods to get/set/clear selection
  /**
   * Returns an array of <code>TreeItem</code>s that are currently selected in
   * the receiver. The order of the items is unspecified. An empty array
   * indicates that no items are selected.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain its
   * selection, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return an array representing the selection
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public TreeItem[] getSelection() {
    checkWidget();
    TreeItem[] result = new TreeItem[ selection.length ];
    System.arraycopy( selection, 0, result, 0, selection.length );
    return result;
  }

  /**
   * Returns the number of selected items contained in the receiver.
   *
   * @return the number of selected items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getSelectionCount() {
    checkWidget();
    return selection.length;
  }

  /**
   * Sets the receiver's selection to the given item. The current selection is
   * cleared before the new item is selected.
   * <p>
   * If the item is not in the receiver, then it is ignored.
   * </p>
   *
   * @param selection the item to select
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setSelection( final TreeItem selection ) {
    checkWidget();
    if( selection == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( new TreeItem[]{
      selection
    } );
  }

  /**
   * Sets the receiver's selection to be the given array of items. The current
   * selection is cleared before the new items are selected.
   * <p>
   * Items that are not in the receiver are ignored. If the receiver is
   * single-select and multiple items are specified, then all items are ignored.
   * </p>
   *
   * @param selection the array of items
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if one of the items has been
   *              disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#deselectAll()
   */
  public void setSelection( final TreeItem[] selection ) {
    checkWidget();
    if( selection == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int length = selection.length;
    if( ( style & SWT.SINGLE ) != 0 ) {
      if( length == 0 || length > 1 ) {
        deselectAll();
      } else {
        TreeItem item = selection[ 0 ];
        if( item != null ) {
          if( item.isDisposed() ) {
            SWT.error( SWT.ERROR_INVALID_ARGUMENT );
          }
          this.selection = new TreeItem[]{
            item
          };
        }
      }
    } else {
      if( length == 0 ) {
        deselectAll();
      } else {
        // Construct an array that contains all non-null items to be selected
        TreeItem[] validSelection = new TreeItem[ length ];
        int validLength = 0;
        for( int i = 0; i < length; i++ ) {
          if( selection[ i ] != null ) {
            if( selection[ i ].isDisposed() ) {
              SWT.error( SWT.ERROR_INVALID_ARGUMENT );
            }
            validSelection[ validLength ] = selection[ i ];
            validLength++;
          }
        }
        if( validLength > 0 ) {
          // Copy the above created array to its 'final destination'
          this.selection = new TreeItem[ validLength ];
          System.arraycopy( validSelection, 0, this.selection, 0, validLength );
        }
      }
    }
  }

  /**
   * Selects all of the items in the receiver.
   * <p>
   * If the receiver is single-select, do nothing.
   * </p>
   *
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void selectAll() {
    checkWidget();
    if( ( style & SWT.MULTI ) != 0 ) {
      final java.util.List allItems = new ArrayList();
      WidgetTreeVisitor.accept( this, new AllWidgetTreeVisitor() {

        public boolean doVisit( final Widget widget ) {
          if( widget instanceof TreeItem ) {
            allItems.add( widget );
          }
          return true;
        }
      } );
      selection = new TreeItem[ allItems.size() ];
      allItems.toArray( selection );
    }
  }

  /**
   * Deselects all selected items in the receiver.
   *
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void deselectAll() {
    checkWidget();
    this.selection = EMPTY_SELECTION;
  }

  /**
   * Marks the receiver's lines as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some other condition
   * makes the receiver not visible, marking it visible may not actually cause
   * it to be displayed.
   * </p>
   *
   * @param value the new visibility state
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setLinesVisible( final boolean value ) {
    checkWidget();
    if( linesVisible == value ) {
      return; /* no change */
    }
    linesVisible = value;
  }

  /**
   * Returns <code>true</code> if the receiver's lines are visible, and
   * <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some other condition
   * makes the receiver not visible, this method may still indicate that it is
   * considered visible even though it may not actually be showing.
   * </p>
   *
   * @return the visibility state of the lines
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public boolean getLinesVisible() {
    checkWidget();
    return linesVisible;
  }

  /**
   * Clears the item at the given zero-relative index in the receiver. The text,
   * icon and other attributes of the item are set to the default value. If the
   * tree was created with the <code>SWT.VIRTUAL</code> style, these attributes
   * are requested again as needed.
   *
   * @param index the index of the item to clear
   * @param recursive <code>true</code> if all child items of the indexed item
   *          should be cleared recursively, and <code>false</code> otherwise
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the list minus 1 (inclusive)</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   */
  public void clear( final int index, final boolean recursive ) {
    checkWidget();
    if( !( 0 <= index && index < itemHolder.size() ) ) {
      error( SWT.ERROR_INVALID_RANGE );
    }
    TreeItem item = ( TreeItem )itemHolder.getItem( index );
    /* clear the item(s) */
    item.clear();
    if( recursive ) {
      item.clearAll( true, false );
    }
    if( ( style & SWT.VIRTUAL ) == 0 ) {
      checkData( item, index );
    }
  }

  /**
   * Returns the item at the given point in the receiver or null if no such item
   * exists. The point is in the coordinate system of the receiver.
   * <p>
   * The item that is returned represents an item that could be selected by the
   * user. For example, if selection only occurs in items in the first column,
   * then null is returned if the point is outside of the item. Note that the
   * SWT.FULL_SELECTION style hint, which specifies the selection policy,
   * determines the extent of the selection.
   * </p>
   *
   * @param point the point used to locate the item
   * @return the item at the given point, or null if the point is not in a
   *         selectable item
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public TreeItem getItem( final Point point ) {
    checkWidget();
    if( point == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int index = ( point.y - getHeaderHeight() + scrollTop ) / getItemHeight();
    // collect all visible items
    List visibleItems = collectVisibleItems( null );
    if( !( 0 <= index && index < visibleItems.size() ) ) {
      return null; /* below the last item */
    }
    TreeItem result = ( TreeItem )visibleItems.get( index );
    // TODO [bm] consider the x value and columns
    // if (!result.getHitBounds ().contains (point)) return null; /* considers
    // the x value */
    return result;
  }

  private List collectVisibleItems( final TreeItem parent ) {
    List result = new ArrayList();
    TreeItem[] children;
    if( parent == null ) {
      children = getItems();
    } else {
      children = parent.getItems();
    }
    for( int i = 0; i < children.length; i++ ) {
      TreeItem item = children[ i ];
      result.add( item );
      if( item.getExpanded() ) {
        result.addAll( collectVisibleItems( item ) );
      }
    }
    return result;
  }

  /**
   * Returns the height of the area which would be used to display <em>one</em>
   * of the items in the tree.
   *
   * @return the height of one item
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  // TODO: [bm] improve and offer as API in next release
  private int getItemHeight() {
    checkWidget();
    int charHeight = TextSizeDetermination.getCharHeight( getFont() );
    int result = charHeight + VERTICAL_ITEM_MARGIN;
    if( ( style & SWT.CHECK ) != 0 ) {
      result = Math.max( CHECK_HEIGHT, result );
    }
    return result;
  }

  /**
   * Clears all the items in the receiver. The text, icon and other attributes
   * of the items are set to their default values. If the tree was created with
   * the <code>SWT.VIRTUAL</code> style, these attributes are requested again as
   * needed.
   *
   * @param recursive <code>true</code> if all child items should be cleared
   *          recursively, and <code>false</code> otherwise
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   */
  public void clearAll( final boolean recursive ) {
    checkWidget();
    if( itemHolder.size() == 0 ) {
      return;
    }
    /* clear the item(s) */
    for( int i = 0; i < itemHolder.size(); i++ ) {
      ( ( TreeItem )itemHolder.getItem( i ) ).clear();
      if( recursive ) {
        ( ( TreeItem )itemHolder.getItem( i ) ).clearAll( true, false );
      }
    }
    if( ( style & SWT.VIRTUAL ) != 0 ) {
      checkAllData( this );
    }
  }

  /**
   * Returns the number of columns contained in the receiver. If no
   * <code>TreeColumn</code>s were created by the programmer, this value is
   * zero, despite the fact that visually, one column of items may be visible.
   * This occurs when the programmer uses the tree like a list, adding items but
   * never creating a column.
   *
   * @return the number of columns
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getColumnCount() {
    checkWidget();
    return columnHolder.size();
  }

  void createItem( final TreeColumn column, final int index ) {
    columnHolder.insert( column, index );
    if( columnOrder == null ) {
      columnOrder = new int[]{
        index
      };
    } else {
      int length = columnOrder.length;
      for( int i = index; i < length; i++ ) {
        columnOrder[ i ]++;
      }
      int[] newColumnOrder = new int[ length + 1 ];
      System.arraycopy( columnOrder, 0, newColumnOrder, 0, index );
      System.arraycopy( columnOrder, index, newColumnOrder, index + 1, length
                                                                       - index );
      columnOrder = newColumnOrder;
      columnOrder[ index ] = index;
    }
    /* allow all items to update their internal structures accordingly */
    for( int i = 0; i < itemHolder.size(); i++ ) {
      TreeItem child = ( TreeItem )itemHolder.getItem( i );
      child.addColumn( column );
    }
  }

  final void destroyColumn( final TreeColumn column ) {
    int index = indexOf( column );
    // Remove data from TreeItems
    // TreeItem[] items = getItems();
    // TODO [bm] dipose unneccesary data
    // for( int i = 0; i < items.length; i++ ) {
    // items[ i ].removeData( index );
    // }
    // Reset sort column if necessary
    if( column == sortColumn ) {
      sortColumn = null;
    }
    // Remove from column holder
    columnHolder.remove( column );
    // Remove from column order
    int length = columnOrder.length;
    int[] newColumnOrder = new int[ length - 1 ];
    int count = 0;
    for( int i = 0; i < length; i++ ) {
      if( columnOrder[ i ] != index ) {
        int newOrder = columnOrder[ i ];
        if( index < newOrder ) {
          newOrder--;
        }
        newColumnOrder[ count ] = newOrder;
        count++;
      }
    }
    columnOrder = newColumnOrder;
  }

  /**
   * Returns the height of the receiver's header
   *
   * @return the height of the header or zero if the header is not visible
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getHeaderHeight() {
    checkWidget();
    int result = 0;
    if( headerVisible ) {
      int textHeight = TextSizeDetermination.getCharHeight( getFont() );
      int imageHeight = 0;
      for( int i = 0; i < getColumnCount(); i++ ) {
        Image image = getColumn( i ).getImage();
        int height = image == null ? 0 : image.getBounds().height;
        if( height > imageHeight ) {
          imageHeight = height;
        }
      }
      result = Math.max( textHeight, imageHeight ) + 4;
    }
    return result;
  }

  /**
   * Marks the receiver's header as visible if the argument is <code>true</code>
   * , and marks it invisible otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some other condition
   * makes the receiver not visible, marking it visible may not actually cause
   * it to be displayed.
   * </p>
   *
   * @param value the new visibility state
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setHeaderVisible( final boolean value ) {
    checkWidget();
    if( headerVisible == value ) {
      return; /* no change */
    }
    headerVisible = value;
  }

  /**
   * Returns <code>true</code> if the receiver's header is visible, and
   * <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some other condition
   * makes the receiver not visible, this method may still indicate that it is
   * considered visible even though it may not actually be showing.
   * </p>
   *
   * @return the receiver's header's visibility state
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public boolean getHeaderVisible() {
    checkWidget();
    return headerVisible;
  }

  /**
   * Searches the receiver's list starting at the first column (index 0) until a
   * column is found that is equal to the argument, and returns the index of
   * that column. If no column is found, returns -1.
   *
   * @param column the search column
   * @return the index of the column
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the column is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int indexOf( final TreeColumn column ) {
    checkWidget();
    if( column == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( column.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return columnHolder.indexOf( column );
  }

  TreeColumn[] getOrderedColumns() {
    // TODO [bm] proper implementation
    return getColumns();
  }

  /**
   * Returns the column at the given, zero-relative index in the receiver.
   * Throws an exception if the index is out of range. Columns are returned in
   * the order that they were created. If no <code>TreeColumn</code>s were
   * created by the programmer, this method will throw
   * <code>ERROR_INVALID_RANGE</code> despite the fact that a single column of
   * data may be visible in the tree. This occurs when the programmer uses the
   * tree like a list, adding items but never creating a column.
   *
   * @param index the index of the column to return
   * @return the column at the given index
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the list minus 1 (inclusive)</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#getColumnOrder()
   * @see Tree#setColumnOrder(int[])
   * @see TreeColumn#getMoveable()
   * @see TreeColumn#setMoveable(boolean)
   * @see SWT#Move
   */
  public TreeColumn getColumn( final int index ) {
    checkWidget();
    if( !( 0 <= index && index < columnHolder.size() ) ) {
      error( SWT.ERROR_INVALID_RANGE );
    }
    return ( TreeColumn )columnHolder.getItem( index );
  }

  /**
   * Returns an array of <code>TreeColumn</code>s which are the columns in the
   * receiver. Columns are returned in the order that they were created. If no
   * <code>TreeColumn</code>s were created by the programmer, the array is
   * empty, despite the fact that visually, one column of items may be visible.
   * This occurs when the programmer uses the tree like a list, adding items but
   * never creating a column.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain its
   * list of items, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return the items in the receiver
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#getColumnOrder()
   * @see Tree#setColumnOrder(int[])
   * @see TreeColumn#getMoveable()
   * @see TreeColumn#setMoveable(boolean)
   * @see SWT#Move
   */
  public TreeColumn[] getColumns() {
    checkWidget();
    return ( TreeColumn[] )columnHolder.getItems();
  }

  /**
   * Sets the order that the items in the receiver should be displayed in to the
   * given argument which is described in terms of the zero-relative ordering of
   * when the items were added.
   *
   * @param order the new order to display the items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if the item order is not the same
   *              length as the number of items</li>
   *              </ul>
   * @see Tree#getColumnOrder()
   * @see TreeColumn#getMoveable()
   * @see TreeColumn#setMoveable(boolean)
   * @see SWT#Move
   */
  public void setColumnOrder( final int[] order ) {
    checkWidget();
    if( order == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int columnCount = getColumnCount();
    if( order.length != columnCount ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( columnCount > 0 ) {
      int[] oldOrder = new int[ columnCount ];
      System.arraycopy( columnOrder, 0, oldOrder, 0, columnOrder.length );
      boolean reorder = false;
      boolean[] seen = new boolean[ columnCount ];
      for( int i = 0; i < order.length; i++ ) {
        int index = order[ i ];
        if( index < 0 || index >= columnCount ) {
          error( SWT.ERROR_INVALID_RANGE );
        }
        if( seen[ index ] ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        seen[ index ] = true;
        if( index != oldOrder[ i ] ) {
          reorder = true;
        }
      }
      if( reorder ) {
        System.arraycopy( order, 0, columnOrder, 0, columnOrder.length );
        for( int i = 0; i < seen.length; i++ ) {
          if( oldOrder[ i ] != columnOrder[ i ] ) {
            TreeColumn column = getColumn( columnOrder[ i ] );
            int controlMoved = ControlEvent.CONTROL_MOVED;
            ControlEvent controlEvent = new ControlEvent( column, controlMoved );
            controlEvent.processEvent();
          }
        }
      }
    }
  }

  /**
   * Sets the column used by the sort indicator for the receiver. A null value
   * will clear the sort indicator. The current sort column is cleared before
   * the new column is set.
   *
   * @param column the column used by the sort indicator or <code>null</code>
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_INVALID_ARGUMENT - if the column is disposed</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setSortColumn( final TreeColumn column ) {
    checkWidget();
    if( column != null && column.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( column == sortColumn ) {
      return;
    }
    if( sortColumn != null && !sortColumn.isDisposed() ) {
      sortColumn.setSortDirection( SWT.NONE );
    }
    sortColumn = column;
    if( sortColumn != null ) {
      sortColumn.setSortDirection( sortDirection );
    }
  }

  /**
   * Sets the direction of the sort indicator for the receiver. The value can be
   * one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
   *
   * @param direction the direction of the sort indicator
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setSortDirection( final int direction ) {
    checkWidget();
    if( direction != SWT.UP && direction != SWT.DOWN && direction != SWT.NONE )
    {
      return;
    }
    sortDirection = direction;
    if( sortColumn == null || sortColumn.isDisposed() ) {
      return;
    }
    sortColumn.setSortDirection( sortDirection );
  }

  /**
   * Returns the column which shows the sort indicator for the receiver. The
   * value may be null if no column shows the sort indicator.
   *
   * @return the sort indicator
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see #setSortColumn(TreeColumn)
   */
  public TreeColumn getSortColumn() {
    checkWidget();
    return sortColumn;
  }

  /**
   * Returns the direction of the sort indicator for the receiver. The value
   * will be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
   *
   * @return the sort direction
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see #setSortDirection(int)
   */
  public int getSortDirection() {
    checkWidget();
    return sortDirection;
  }

  /**
   * Returns an array of zero-relative integers that map the creation order of
   * the receiver's items to the order in which they are currently being
   * displayed.
   * <p>
   * Specifically, the indices of the returned array represent the current
   * visual order of the items, and the contents of the array represent the
   * creation order of the items.
   * </p>
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain its
   * list of items, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return the current visual order of the receiver's items
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Tree#setColumnOrder(int[])
   * @see TreeColumn#getMoveable()
   * @see TreeColumn#setMoveable(boolean)
   * @see SWT#Move
   */
  public int[] getColumnOrder() {
    checkWidget();
    int[] result;
    if( columnHolder.size() == 0 ) {
      result = new int[ 0 ];
    } else {
      result = new int[ columnOrder.length ];
      System.arraycopy( columnOrder, 0, result, 0, columnOrder.length );
    }
    return result;
  }

  ///////////////////////////////////////
  // Listener registration/deregistration

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * the receiver's selection changes, by sending it one of the messages defined
   * in the <code>SelectionListener</code> interface.
   * <p>
   * When <code>widgetSelected</code> is called, the item field of the event
   * object is valid. If the receiver has <code>SWT.CHECK</code> style set and
   * the check selection changes, the event object detail field contains the
   * value <code>SWT.CHECK</code>. <code>widgetDefaultSelected</code> is
   * typically called when an item is double-clicked. The item field of the
   * event object is valid for default selection, but the detail field is not
   * used.
   * </p>
   *
   * @param listener the listener which should be notified
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
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when the receiver's selection changes.
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
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * an item in the receiver is expanded or collapsed by sending it one of the
   * messages defined in the <code>TreeListener</code> interface.
   *
   * @param listener the listener which should be notified
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see TreeListener
   * @see #removeTreeListener
   */
  public void addTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when items in the receiver are expanded or collapsed.
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
   * @see TreeListener
   * @see #addTreeListener
   */
  public void removeTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.removeListener( this, listener );
  }

  /////////////////////////////////
  // Methods to cleanup on dispose

  void releaseWidget() {
    super.releaseWidget();
    if( resizeListener != null ) {
      removeControlListener( resizeListener );
    }
  }

  void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    TreeColumn[] cols = ( TreeColumn[] )columnHolder.getItems();
    for( int c = 0; c < cols.length; c++ ) {
      cols[ c ].dispose();
    }
    super.releaseChildren();
  }

  void removeFromSelection( final TreeItem item ) {
    int index = -1;
    for( int i = 0; index == -1 && i < selection.length; i++ ) {
      if( selection[ i ] == item ) {
        index = i;
      }
    }
    if( index != -1 ) {
      TreeItem[] newSelection = new TreeItem[ selection.length - 1 ];
      System.arraycopy( selection, 0, newSelection, 0, index );
      if( index < selection.length - 1 ) {
        int length = selection.length - index - 1;
        System.arraycopy( selection, index + 1, newSelection, index, length );
      }
      selection = newSelection;
    }
  }

  /////////////////////
  // Widget dimensions

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0, height = 0;
    if( getColumnCount() > 0 ) {
      for( int i = 0; i < getColumnCount(); i++ ) {
        // TODO: [if] TreeColumn#pack() does not respect
        // inner items width if expanded
        width += getColumn( i ).getWidth();
      }
    } else {
      for( int i = 0; i < getItemCount(); i++ ) {
        TreeItem item = getItem( i );
        if( item.isCached() ) {
          int itemWidth = item.getPreferredWidth( 0, false );
          width = Math.max( width, itemWidth );
          if( item.getExpanded() ) {
            // TODO: [if] Recursively get the max inner item width
          }
        }
      }
    }
    height += getHeaderHeight();
    height += getItemCount() * getItemHeight();
    for( int i = 0; i < getItemCount(); i++ ) {
      TreeItem item = getItem( i );
      if( item.getExpanded() ) {
        height += item.getInnerHeight();
      }
    }
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    if( ( style & SWT.V_SCROLL ) != 0 ) {
      width += SCROLL_SIZE;
    }
    if( ( style & SWT.H_SCROLL ) != 0 ) {
      height += SCROLL_SIZE;
    }
    return new Point( width, height );
  }

  ///////////////////
  // Helping methods

  final int getCheckWidth() {
    int result = 0;
    if( ( style & SWT.CHECK ) != 0 ) {
      result = CHECK_HEIGHT + 4;
    }
    return result;
  }

  static void checkAllData( final Tree tree ) {
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {

      int flatIndex = 0;

      public boolean doVisit( final Widget widget ) {
        boolean result = true;
        if( widget instanceof TreeItem ) { // ignore tree
          TreeItem item = ( TreeItem )widget;
          result = item.getExpanded();
          int index;
          TreeItem parentItem = item.getParentItem();
          if( parentItem != null ) {
            index = parentItem.indexOf( item );
          } else {
            index = tree.indexOf( item );
          }
          item.flatIndex = flatIndex;
          flatIndex++;
          if( !item.isCached() && tree.isItemVisible( item ) ) {
            tree.checkData( item, index );
          }
        }
        return result;
      }
    };
    WidgetTreeVisitor.accept( tree, visitor );
  }

  private boolean isItemVisible( final TreeItem item ) {
    boolean result = false;
    int itemPosition = item.getBounds( 0, false ).y;
    if( itemPosition >= 0 && itemPosition <= getSize().y ) {
      TreeItem parentItem = item.getParentItem();
      if( parentItem != null ) {
        if( parentItem.getExpanded() ) {
          result = true;
        }
      } else {
        result = true;
      }
    }
    return result;
  }

  // TODO [bm]: performance impact - replace this with logic to only partly
  // update the flat indices when there are changes in the visibility hierarchy
  // like new items, removed items, expand/
  /* package */void updateFlatIndices() {
    int flatIndex = 0;
    TreeItem[] uItems = this.getItems();
    for( int i = 0; i < uItems.length; i++ ) {
      TreeItem treeItem = uItems[ i ];
      treeItem.flatIndex = flatIndex;
      flatIndex++;
      flatIndex = updateFlatIndicesSub( treeItem, flatIndex );
    }
  }

  /* package */int updateFlatIndicesSub( final TreeItem item,
                                         final int flatIndex )
  {
    int newFlatIndex = flatIndex;
    if( item.getExpanded() ) {
      TreeItem[] subItems = item.getItems();
      for( int i = 0; i < subItems.length; i++ ) {
        TreeItem subItem = subItems[ i ];
        subItem.flatIndex = newFlatIndex;
        newFlatIndex++;
        newFlatIndex = updateFlatIndicesSub( subItem, newFlatIndex );
      }
    }
    return newFlatIndex;
  }

  final void checkData( final TreeItem item, final int index ) {
    if( ( style & SWT.VIRTUAL ) != 0 && !item.cached ) {
      if( currentItem == null ) {
        currentItem = item;
      }
      try {
        if( currentItem == item || item.getParentItem() == currentItem ) {
          ProcessActionRunner.add( new Runnable() {

            public void run() {
              item.cached = true;
              SetDataEvent event = new SetDataEvent( Tree.this, item, index );
              event.processEvent();
              // widget could be disposed at this point
              if( isDisposed() || item.isDisposed() ) {
                SWT.error( SWT.ERROR_WIDGET_DISPOSED );
              }
            }
          } );
        }
      } finally {
        if( currentItem == item ) {
          currentItem = null;
        }
      }
    }
  }

  private static int checkStyle( final int style ) {
    int result = style | SWT.H_SCROLL | SWT.V_SCROLL;
    return checkBits( result, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }
}
