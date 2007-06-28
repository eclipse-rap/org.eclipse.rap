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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.*;


/**
 * Instances of this class represent a selectable user interface object
 * that represents a hierarchy of tree items in a tree widget.
 *
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
public class TreeItem extends Item {

  private final TreeItem parentItem;
  private final Tree parent;
  private final ItemHolder itemHolder;
  private final IWidgetFontAdapter widgetFontAdapter;
  private final IWidgetColorAdapter widgetColorAdapter;
  private Font font;
  private boolean expanded;
  private boolean checked;
  private Color background, foreground;
  private boolean grayed;

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Tree</code> or a <code>TreeItem</code>)
   * and a style value describing its behavior and appearance.
   * The item is added to the end of the items maintained by its parent.
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
   * @param parent a tree control which will be the parent of the new instance (cannot be null)
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
  public TreeItem( final Tree parent, final int style ) {
    this( parent, null, style, -1 );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Tree</code> or a <code>TreeItem</code>)
   * and a style value describing its behavior and appearance.
   * The item is added to the end of the items maintained by its parent.
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
   * @param parentItem a tree control which will be the parent of the new instance (cannot be null)
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
  public TreeItem( final TreeItem parentItem, final int style ) {
    this( parentItem == null ? null : parentItem.parent,
          parentItem,
          style,
          -1 );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Tree</code> or a <code>TreeItem</code>),
   * a style value describing its behavior and appearance, and the index
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
   * @param parentItem a tree control which will be the parent of the new instance (cannot be null)
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
  public TreeItem( final TreeItem parentItem, final int style, final int index )
  {
    this( parentItem == null ? null : parentItem.parent,
          parentItem,
          style,
          -1 );
  }

  private TreeItem( final Tree parent,
                    final TreeItem parentItem,
                    final int style,
                    final int index )
  {
    super( parent, style );
    this.parent = parent;
    this.parentItem = parentItem;
    if( parentItem != null ) {
      int newIndex = index == -1 ? parentItem.getItemCount() : index;
      ItemHolder.insertItem( parentItem, this, newIndex );
    } else {
      int newIndex = index == -1 ? parent.getItemCount() : index;
      ItemHolder.insertItem( parent, this, newIndex );
    }
    itemHolder = new ItemHolder( TreeItem.class );
    widgetFontAdapter = new IWidgetFontAdapter() {
      public Font getUserFont() {
        return font;
      }
    };
    widgetColorAdapter = new IWidgetColorAdapter() {
      public Color getUserForegound() {
        return foreground;
      }
      public Color getUserBackgound() {
        return background;
      }
    };
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else if( adapter == IWidgetFontAdapter.class ) {
      result = widgetFontAdapter;
    } else if( adapter == IWidgetColorAdapter.class ) {
      result = widgetColorAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /////////////////
  // Item overrides

  public final Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  /////////////////////////
  // Parent/child relations

  /**
   * Returns the receiver's parent, which must be a <code>Tree</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public final Tree getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Returns the receiver's parent item, which must be a
   * <code>TreeItem</code> or null when the receiver is a
   * root.
   *
   * @return the receiver's parent item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public TreeItem getParentItem() {
    checkWidget();
    return parentItem;
  }

  ////////////////
  // Getter/Setter

  /**
   * Sets the expanded state of the receiver.
   * <p>
   *
   * @param expanded the new expanded state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setExpanded( final boolean expanded ) {
    checkWidget();
    if( !expanded || getItemCount() > 0 ) {
      this.expanded = expanded;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is expanded,
   * and false otherwise.
   * <p>
   *
   * @return the expanded state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getExpanded() {
    checkWidget();
    return expanded;
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * for this item to the font specified by the argument, or to the default font
   * for that kind of control if the argument is null.
   *
   * @param font the new font (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public void setFont( final Font font ) {
    checkWidget();
    this.font = font;
  }

  /**
   * Returns the font that the receiver will use to paint textual information for this item.
   *
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public Font getFont() {
    checkWidget();
    Font result;
    if( font == null ) {
      result = getParent().getFont();
    } else {
      result = font;
    }
    return result;
  }

  /**
   * Sets the receiver's background color to the color specified
   * by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param color the new color (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 2.0
   *
   */
  public void setBackground( final Color value ) {
    checkWidget();
    if( background == value ) {
      return;
    }
    if( background != null && background.equals( value ) ) {
      return;
    }
    background = value;
    // if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }

  /**
   * Returns the receiver's background color.
   *
   * @return the background color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 2.0
   *
   */
  public Color getBackground() {
    checkWidget();
    if ( isDisposed() )
      error( SWT.ERROR_WIDGET_DISPOSED );
    if ( background != null )
      return background;
    return parent.getBackground();
  }

  /**
   * Returns the foreground color that the receiver will use to draw.
   *
   * @return the receiver's foreground color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 2.0
   *
   */
  public Color getForeground () {
    checkWidget ();
    if (isDisposed()) error (SWT.ERROR_WIDGET_DISPOSED);
    if (foreground != null) return foreground;
    return parent.getForeground ();
  }

  /**
   * Sets the receiver's foreground color to the color specified
   * by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param color the new color (or null)
   *
   * @since 2.0
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 2.0
   *
   */
  public void setForeground( final Color value ) {
    checkWidget ();
    if( foreground == value ) {
      return;
    }
    if( foreground != null && foreground.equals( value ) ) {
      return;
    }
    foreground = value;
//    if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }

  /**
   * Sets the checked state of the receiver.
   * <p>
   *
   * @param checked the new checked state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setChecked( final boolean checked ) {
    checkWidget();
    if( ( parent.getStyle() & SWT.CHECK ) != 0 ) {
      this.checked = checked;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is checked,
   * and false otherwise.  When the parent does not have
   * the <code>CHECK style, return false.
   * <p>
   *
   * @return the checked state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getChecked() {
    checkWidget();
    return checked;
  }
  
  /**
   * Sets the grayed state of the checkbox for this item.  This state change 
   * only applies if the Tree was created with the SWT.CHECK style.
   *
   * @param grayed the new grayed state of the checkbox
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setGrayed( boolean value ) {
    checkWidget();
    if ( ( parent.getStyle() & SWT.CHECK ) == 0 )
      return;
    if ( grayed == value )
      return;
    grayed = value;
// if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }
  
  /**
   * Returns <code>true</code> if the receiver is grayed,
   * and false otherwise. When the parent does not have
   * the <code>CHECK style, return false.
   * <p>
   *
   * @return the grayed state of the checkbox
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getGrayed() {
    checkWidget();
//    if ( !parent.checkData( this, true ) )
//      error( SWT.ERROR_WIDGET_DISPOSED );
    return grayed;
  }
  
  void clear() {
    // TODO: [bm] revisit when columns are available
//    checked = grayed = false;
    checked = false;
//    texts = null;
//    textWidths = new int[ 1 ];
//    fontHeight = 0;
//    fontHeights = null;
//    images = null;
    foreground = background = null;
//    displayTexts = null;
//    cellForegrounds = cellBackgrounds = null;
    font = null;
//    cellFonts = null;
//    cached = false;
//    text = "";
//    image = null;
    setText( "" );
    setImage( null );

//    int columnCount = parent.columns.length;
//    if ( columnCount > 0 ) {
//      displayTexts = new String[ columnCount ];
//      if ( columnCount > 1 ) {
//        texts = new String[ columnCount ];
//        textWidths = new int[ columnCount ];
//        images = new Image[ columnCount ];
//      }
//    }
  }

  /**
   * Clears all the items in the receiver. The text, icon and other
   * attributes of the items are set to their default values. If the
   * tree was created with the <code>SWT.VIRTUAL</code> style, these
   * attributes are requested again as needed.
   * 
   * @param all <code>true</code> if all child items should be cleared
   * recursively, and <code>false</code> otherwise
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 3.2
   */
  public void clearAll (boolean recursive) {
    clearAll (recursive, true);
  }
  void clearAll (boolean recursive, boolean doVisualUpdate) {
    checkWidget ();
    if (itemHolder.size() == 0) return;

    /* clear the item(s) */
    for (int i = 0; i < itemHolder.size(); i++) {
      ( ( TreeItem ) itemHolder.getItem( i ) ).clear ();
      if (recursive) ( ( TreeItem )itemHolder.getItem( i )).clearAll (true, false);
    }
  }
  ///////////////////////////////////////
  // Methods to maintain (sub-) TreeItems

  /**
   * Returns a (possibly empty) array of <code>TreeItem</code>s which
   * are the direct item children of the receiver.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver.
   * </p>
   *
   * @return the receiver's items
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
   * Returns the number of items contained in the receiver
   * that are direct item children of the receiver.
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
   * <p>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public void removeAll() {
    checkWidget();
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  /////////////////////////////////
  // Methods to dispose of the item

  protected final void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  protected final void releaseParent() {
    if( parentItem != null ) {
      ItemHolder.removeItem( parentItem, this );
    } else {
      ItemHolder.removeItem( parent, this );
    }
    parent.removeFromSelection( this );
  }

  protected final void releaseWidget() {
    // do nothing
  }
}
