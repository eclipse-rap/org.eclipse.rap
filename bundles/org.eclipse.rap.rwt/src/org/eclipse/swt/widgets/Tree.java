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

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;

/**
 * Instances of this class provide a selectable user interface object
 * that displays a hierarchy of items and issues notification when an
 * item in the hierarchy is selected.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TreeItem</code>.
 * </p><p>
 * Style <code>VIRTUAL</code> is used to create a <code>Tree</code> whose
 * <code>TreeItem</code>s are to be populated by the client on an on-demand basis
 * instead of up-front.  This can provide significant performance improvements for
 * trees that are very large or for which <code>TreeItem</code> population is
 * expensive (for example, retrieving values from an external source).
 * </p><p>
 * Here is an example of using a <code>Tree</code> with style <code>VIRTUAL</code>:
 * <code><pre>
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
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, VIRTUAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, Collapse, Expand, SetData, MeasureItem, EraseItem, PaintItem</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles SINGLE and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class Tree extends Composite {

  private static final TreeItem[] EMPTY_SELECTION = new TreeItem[ 0 ];
  
  private final ItemHolder itemHolder;
  private TreeItem[] selection;


  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
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
   * @see SWT#SINGLE
   * @see SWT#MULTI
   * @see SWT#CHECK
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Tree( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TreeItem.class );
    selection = EMPTY_SELECTION;
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ///////////////////////////
  // Methods to manage items 

  /**
   * Returns the number of items contained in the receiver
   * that are direct item children of the receiver.  The
   * number that is returned is the number of roots in the
   * tree.
   *
   * @return the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Returns a (possibly empty) array of items contained in the
   * receiver that are direct item children of the receiver.  These
   * are the roots of the tree.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public TreeItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.TreeItem[] )itemHolder.getItems();
  }
  
  /**
   * Returns the item at the given, zero-relative index in the
   * receiver. Throws an exception if the index is out of range.
   *
   * @param index the index of the item to return
   * @return the item at the given index
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public TreeItem getItem( final int index ) {
    checkWidget();
    return ( TreeItem )itemHolder.getItem( index );
  }
  
  /**
   * Searches the receiver's list starting at the first item
   * (index 0) until an item is found that is equal to the 
   * argument, and returns the index of that item. If no item
   * is found, returns -1.
   *
   * @param item the search item
   * @return the index of the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the tool item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the tool item has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public int indexOf( final TreeItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }
  
  /**
   * Removes all of the items from the receiver.
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void removeAll() {
    checkWidget();
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    selection = EMPTY_SELECTION;
  }
  
  /////////////////////////////////////
  // Methods to get/set/clear selection
  
  /**
   * Returns an array of <code>TreeItem</code>s that are currently
   * selected in the receiver. The order of the items is unspecified.
   * An empty array indicates that no items are selected. 
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its selection, so modifying the array will
   * not affect the receiver. 
   * </p>
   * @return an array representing the selection
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
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
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getSelectionCount() {
    checkWidget();
    return selection.length;
  }
  
  /**
   * Sets the receiver's selection to the given item.
   * The current selection is cleared before the new item is selected.
   * <p>
   * If the item is not in the receiver, then it is ignored.
   * </p>
   *
   * @param item the item to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setSelection( final TreeItem selection ) {
    checkWidget();
    if( selection == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( new TreeItem[] { selection } );
  }

  /**
   * Sets the receiver's selection to be the given array of items.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Items that are not in the receiver are ignored.
   * If the receiver is single-select and multiple items are specified,
   * then all items are ignored.
   * </p>
   *
   * @param items the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
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
          this.selection = new TreeItem[] { item };
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
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
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
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void deselectAll() {
    checkWidget();
    this.selection = EMPTY_SELECTION;
  }

  //////////////////////////////////////
  // Listener registration/deregistration
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's selection changes, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * When <code>widgetSelected</code> is called, the item field of the event object is valid.
   * If the receiver has <code>SWT.CHECK</code> style set and the check selection changes,
   * the event object detail field contains the value <code>SWT.CHECK</code>.
   * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
   * The item field of the event object is valid for default selection, but the detail field is not used.
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
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver's selection changes.
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
    checkWidget();
    SelectionEvent.removeListener( this, listener );
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
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see TreeListener
   * @see #removeTreeListener
   */
  public void addTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.addListener( this, listener );
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
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see TreeListener
   * @see #addTreeListener
   */
  public void removeTreeListener( final TreeListener listener ) {
    checkWidget();
    TreeEvent.removeListener( this, listener );
  }

  ////////////////////////////////
  // Methods to cleanup on dispose
  
  protected void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
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
  
  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style | SWT.H_SCROLL | SWT.V_SCROLL;
    return checkBits( result, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }
}
