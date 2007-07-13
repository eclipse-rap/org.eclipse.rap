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

package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.graphics.FontSizeCalculator;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.widgets.*;

/**
 * Instances of this class implement the notebook user interface
 * metaphor.  It allows the user to select a notebook page from
 * set of pages.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>CTabItem</code>.
 * <code>Control</code> children are created and then set into a
 * tab item using <code>CTabItem#setControl</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to set a layout on it.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed. 
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>CLOSE, TOP, BOTTOM, FLAT, BORDER, SINGLE, MULTI</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * <dd>"CTabFolder2"</dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles TOP and BOTTOM 
 * may be specified.
 * </p>
 * <hr/>
 * <p>Implementation Status: </p>
 * <p>The SWT.SINGLE style is implemented but not fully functional.</p>
 * <p>Attributes, found in SWT, that are not supported</p>
 * <ul><li>unselectedImageVisible (treated as if <code>true</code>)</li>
 * <li>showUnselectedClose (treated as if <code>true</code>)</li>
 * <li><em>simple</em> style (treated as <code>true</code>)</li>
 * <li>MRUVisible (treated as <code>false</code>)</li>
 * <li>SWT.BORDER and SWT.FLAT styles are not fully implemented</li>
 * </ul>
 */
public class CTabFolder extends Composite {

  // width and height of minimize/maximize button
  static final int BUTTON_SIZE = 18; 

  private static final int DEFAULT_SELECTION_BG = SWT.COLOR_LIST_BACKGROUND;
  private static final int DEFAULT_SELECTION_FG = SWT.COLOR_LIST_FOREGROUND;

  public int marginWidth = 0;
  public int marginHeight = 0;
  
  private final ICTabFolderAdapter tabFolderAdapter = new CTabFolderAdapter();
  private final ItemHolder itemHolder = new ItemHolder( CTabItem.class );
  private final ControlListener resizeListener;
  private FocusListener focusListener;
  private Menu showListMenu;
  private int selectedIndex = -1;
  private int firstIndex = -1; // index of the left most visible tab
//  private final boolean simple = true;  // curvy tab style
  private boolean mru;
  private int[] priority = new int[ 0 ];
  private boolean showUnselectedClose = true;
  private int minimumCharacters = 20;
  private boolean maximizeVisible;
  private boolean minimizeVisible;
  private boolean inDispose;
  private boolean minimized;
  private boolean maximized;
  private boolean onBottom;
  private boolean single;
  private final Rectangle maxRect = new Rectangle( 0, 0, 0, 0 );
  private final Rectangle minRect = new Rectangle( 0, 0, 0, 0 );
  // Chevron
  private final Rectangle chevronRect = new Rectangle( 0, 0, 0, 0 );
  private boolean showChevron;
  // Tab bar
  private int fixedTabHeight = SWT.DEFAULT;
  private int tabHeight = 0;
  // TopRight control
  private Control topRight;
  private int topRightAlignment = SWT.RIGHT;
  private final Rectangle topRightRect = new Rectangle( 0, 0, 0, 0 );
  // Client origin and border dimensions
  private int xClient;
  private int yClient;
  private final int highlight_margin;
  private final int highlight_header;
  private int borderRight;
  private int borderLeft;
  private int borderBottom;
  private int borderTop;
  // Colors
  private Color selectionBackground;
  private Color selectionForeground;

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
   * @param parent a widget which will be the parent of the new instance (cannot be null)
   * @param style the style of widget to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT#TOP
   * @see SWT#BOTTOM
   * @see SWT#FLAT  (not fully implemented yet)
   * @see SWT#BORDER  (not fully implemented yet)
   * @see SWT#SINGLE (not fully implemented yet)
   * @see SWT#MULTI
   * @see #getStyle()
   */
  public CTabFolder( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    super.setLayout( new CTabFolderLayout() );
    onBottom = ( super.getStyle() & SWT.BOTTOM ) != 0;
    single = ( super.getStyle() & SWT.SINGLE ) != 0;
    borderRight = ( style & SWT.BORDER ) != 0 ? 1 : 0;
    borderLeft = borderRight;
    borderTop = onBottom ? borderLeft : 0;
    borderBottom = onBottom ? 0 : borderLeft;
    highlight_header = ( style & SWT.FLAT ) != 0 ? 1 : 3;
    highlight_margin = ( style & SWT.FLAT ) != 0 ? 0 : 2;
    selectionBackground = getDisplay().getSystemColor( DEFAULT_SELECTION_BG );
    selectionForeground = getDisplay().getSystemColor( DEFAULT_SELECTION_FG );
    resizeListener = new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        updateItems();
      }
    };
    addControlListener( resizeListener );
    registerDisposeListener();
    updateTabHeight( false );
  }

  //////////////////
  // Item management
  
  /**
   * Return the tab items.
   * 
   * @return the tab items
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public CTabItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.custom.CTabItem[] )itemHolder.getItems();
  }

  /**
   * Return the tab that is located at the specified index.
   * 
   * @param index the index of the tab item
   * @return the item at the specified index
   * 
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is out of range</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   */
  public CTabItem getItem( final int index ) {
    checkWidget();
    return ( CTabItem )itemHolder.getItem( index );
  }

  /**
   * Return the number of tabs in the folder.
   * 
   * @return the number of tabs in the folder
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Return the index of the specified tab or -1 if the tab is not 
   * in the receiver.
   * 
   * @param item the tab item for which the index is required
   * 
   * @return the index of the specified tab item or -1
   * 
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   */
  public int indexOf( final CTabItem item ) {
    checkWidget();
    return itemHolder.indexOf( item );
  }

  ///////////////////////
  // Selection management
  
  /**
   * Set the selection to the tab at the specified index.
   * 
   * @param index the index of the tab item to be selected
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final int index ) {
    checkWidget();
    if( index >= 0 && index <= itemHolder.size() - 1 ) {
      if( selectedIndex != index ) {
        int oldSelectionIndex = selectedIndex;
        selectedIndex = index;
        getItem( selectedIndex ).showing = false;
        Control control = getItem( selectedIndex ).getControl();
        // Adjust bounds of selected control and make it visible (if any)
        if( control != null && !control.isDisposed() ) {
          control.setBounds( getClientArea() );
          control.setVisible( true );
        }
        // Hide control of previous selection (if any)
        if( oldSelectionIndex >= 0 && oldSelectionIndex < getItemCount() ) {
          Control oldControl = getItem( oldSelectionIndex ).getControl();
          if( oldControl != null && !oldControl.isDisposed() ) {
            oldControl.setVisible( false );
          }
        }
      }
      showItem( getSelection() );
    }
  }

  /**
   * Return the index of the selected tab item, or -1 if there
   * is no selection.
   * 
   * @return the index of the selected tab item or -1
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public int getSelectionIndex() {
    checkWidget();
    return selectedIndex;
  }
  
  /**
   * Set the selection to the tab at the specified item.
   * 
   * @param item the tab item to be selected
   * 
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   */
  public void setSelection( final CTabItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int index = itemHolder.indexOf( item );
    setSelection( index );
  }
  
  /**
   * Return the selected tab item, or null if there is no selection.
   * 
   * @return the selected tab item, or null if none has been selected
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public CTabItem getSelection() {
    checkWidget();
    CTabItem result = null;
    if( selectedIndex != -1 ) {
      result = ( CTabItem )itemHolder.getItem( selectedIndex );
    }
    return result; 
  }

  /**
   * Shows the selection.  If the selection is already showing in the receiver,
   * this method simply returns.  Otherwise, the items are scrolled until
   * the selection is visible.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see CTabFolder#showItem(CTabItem)
   * 
   * @since 1.0
   */
  public void showSelection () {
    checkWidget (); 
    if( selectedIndex != -1 ) {
      showItem( getSelection() );
    }
  }

  /**
   * Shows the item.  If the item is already showing in the receiver,
   * this method simply returns.  Otherwise, the items are scrolled until
   * the item is visible.
   * 
   * @param item the item to be shown
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
   * @see CTabFolder#showSelection()
   *
   * @since 1.0
   */
  public void showItem( final CTabItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int index = indexOf( item );
    if( index == -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int idx = -1;
    for( int i = 0; idx == -1 && i < priority.length; i++ ) {
      if( priority[ i ] == index ) {
        idx = i;
      }
    }
    if( mru ) {
      // move to front of mru order
      int[] newPriority = new int[ priority.length ];
      System.arraycopy( priority, 0, newPriority, 1, idx );
      System.arraycopy( priority,
                        idx + 1,
                        newPriority,
                        idx + 1,
                        priority.length - idx - 1 );
      newPriority[ 0 ] = index;
      priority = newPriority;
    }
    if( !item.isShowing() ) {
      updateItems( index );
    }
  }
  
  //////////////////////////////
  // Most recently used settings
  
  /**
   * When there is not enough horizontal space to show all the tabs,
   * by default, tabs are shown sequentially from left to right in 
   * order of their index.  When the MRU visibility is turned on,
   * the tabs that are visible will be the tabs most recently selected.
   * Tabs will still maintain their left to right order based on index 
   * but only the most recently selected tabs are visible.
   * <p>
   * For example, consider a CTabFolder that contains "Tab 1", "Tab 2",
   * "Tab 3" and "Tab 4" (in order by index).  The user selects
   * "Tab 1" and then "Tab 3".  If the CTabFolder is now
   * compressed so that only two tabs are visible, by default, 
   * "Tab 2" and "Tab 3" will be shown ("Tab 3" since it is currently 
   * selected and "Tab 2" because it is the previous item in index order).
   * If MRU visibility is enabled, the two visible tabs will be "Tab 1"
   * and "Tab 3" (in that order from left to right).</p>
   *
   * @param show the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setMRUVisible( final boolean show ) {
    checkWidget();
    if( !mru != !show ) {
      mru = show;
      if( !mru ) {
        int idx = firstIndex;
        int next = 0;
        for( int i = firstIndex; i < priority.length; i++ ) {
          priority[ next++ ] = i;
        }
        for( int i = 0; i < idx; i++ ) {
          priority[ next++ ] = i;
        }
        updateItems();
      }
    }
  }
  
  /**
   * Returns <code>true</code> if the receiver displays most
   * recently used tabs and <code>false</code> otherwise.
   * <p>
   * When there is not enough horizontal space to show all the tabs,
   * by default, tabs are shown sequentially from left to right in 
   * order of their index.  When the MRU visibility is turned on,
   * the tabs that are visible will be the tabs most recently selected.
   * Tabs will still maintain their left to right order based on index 
   * but only the most recently selected tabs are visible.
   * <p>
   * For example, consider a CTabFolder that contains "Tab 1", "Tab 2",
   * "Tab 3" and "Tab 4" (in order by index).  The user selects
   * "Tab 1" and then "Tab 3".  If the CTabFolder is now
   * compressed so that only two tabs are visible, by default, 
   * "Tab 2" and "Tab 3" will be shown ("Tab 3" since it is currently 
   * selected and "Tab 2" because it is the previous item in index order).
   * If MRU visibility is enabled, the two visible tabs will be "Tab 1"
   * and "Tab 3" (in that order from left to right).</p>
   *
   * @return the receiver's header's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getMRUVisible() {
    checkWidget();
    return mru;
  }


  ////////////////////////////////
  // Minimize / Maximize / Restore
  
  /**
   * Marks the receiver's maximize button as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setMaximizeVisible( final boolean maximizeVisible ) {
    checkWidget();
    if( this.maximizeVisible != maximizeVisible ) {
      this.maximizeVisible = maximizeVisible;
      updateItems();
    }
  }
  
  /**
   * Returns <code>true</code> if the maximize button
   * is visible.
   *
   * @return the visibility of the maximized button
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getMaximizeVisible() {
    checkWidget();
    return maximizeVisible;
  }
  
  /**
   * Marks the receiver's minimize button as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setMinimizeVisible( final boolean minimizeVisible ) {
    checkWidget();
    if( this.minimizeVisible != minimizeVisible ) {
      this.minimizeVisible = minimizeVisible;
      updateItems();
    }
  }

  /**
   * Returns <code>true</code> if the minimize button
   * is visible.
   *
   * @return the visibility of the minimized button
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getMinimizeVisible() {
    checkWidget();
    return minimizeVisible;
  }
  
  /**
   * Marks the receiver's minimize button as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setMinimized( final boolean minimized ) {
    checkWidget();
    if( this.minimized != minimized ) {
      if( minimized && this.maximized ) {
        setMaximized( false );
      }
      this.minimized = minimized;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is minimized.
   *
   * @return the receiver's minimized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getMinimized() {
    checkWidget();
    return minimized;
  }
  
  /**
   * Sets the maximized state of the receiver.
   *
   * @param maximize the new maximized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public void setMaximized( final boolean maximized ) {
    checkWidget();
    if( this.maximized != maximized ) {
      if( maximized && this.minimized ) {
        setMinimized( false );
      }
      this.maximized = maximized;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is maximized.
   * <p>
   *
   * @return the receiver's maximized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getMaximized() {
    checkWidget();
    return maximized;
  }

  //////////////////////////////////////
  // Appearance and dimension properties 
  
  /**
   * Sets the layout which is associated with the receiver to be
   * the argument which may be null.
   * <p>
   * Note: No Layout can be set on this Control because it already
   * manages the size and position of its children.
   * </p>
   *
   * @param layout the receiver's new layout or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setLayout( final Layout layout ) {
    checkWidget();
    // ignore - CTabFolder manages its own layout
  }

  /**
   * Specify a fixed height for the tab items.  If no height is specified,
   * the default height is the height of the text or the image, whichever 
   * is greater. Specifying a height of -1 will revert to the default height.
   * 
   * @param height the pixel value of the height or -1
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - if called with a height of less than 0</li>
   * </ul>
   */
  public void setTabHeight( final int height ) {
    checkWidget();
    if( height < -1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    fixedTabHeight = height;
    updateTabHeight( false );
  }

  /**
   * Returns the height of the tab
   * 
   * @return the height of the tab
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public int getTabHeight() {
    checkWidget();
    int result;
    if( fixedTabHeight != SWT.DEFAULT ) {
      result = fixedTabHeight;
    } else {
      result = tabHeight - 1; // -1 for line drawn across top of tab
    }
    return result;
  }

  /** 
   * Returns the number of characters that will
   * appear in a fully compressed tab.
   * 
   * @return number of characters that will appear in a fully compressed tab
   * 
   * @since 1.0
   */
  public int getMinimumCharacters() {
    checkWidget();
    return minimumCharacters;
  }

  /**
   * Sets the minimum number of characters that will 
   * be displayed in a fully compressed tab.
   * 
   * @param count the minimum number of characters that will be displayed in a fully compressed tab
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_RANGE - if the count is less than zero</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setMinimumCharacters( final int minimumCharacters ) {
    checkWidget();
    if( minimumCharacters < 0 ) {
      SWT.error( SWT.ERROR_INVALID_RANGE );
    }
    if( this.minimumCharacters != minimumCharacters ) {
      this.minimumCharacters = minimumCharacters;
      updateItems();
    }
  }
  
  public int getStyle() {
    checkWidget();
    int result = super.getStyle();
    result &= ~( SWT.TOP | SWT.BOTTOM );
    result |= onBottom ? SWT.BOTTOM : SWT.TOP;
    result &= ~( SWT.SINGLE | SWT.MULTI );
    result |= single ? SWT.SINGLE : SWT.MULTI;
    if( borderLeft != 0 ) {
      result |= SWT.BORDER;
    }
    return result;
  }

  /**
   * Returns <code>true</code> if the CTabFolder only displays the selected tab
   * and <code>false</code> if the CTabFolder displays multiple tabs.
   * 
   * @return <code>true</code> if the CTabFolder only displys the selected tab and <code>false</code> if the CTabFolder displays multiple tabs
   * 
   * @since 1.0
   */
  public boolean getSingle() {
    checkWidget();
    return single;
  }

  /**
   * Sets the number of tabs that the CTabFolder should display
   * 
   * @param single <code>true</code> if only the selected tab should be displayed otherwise, multiple tabs will be shown.
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setSingle( final boolean single ) {
    checkWidget();
    if( this.single != single ) {
      this.single = single;
      updateItemsWithResizeEvent();
    }
  }

  /**
   * Returns the position of the tab.  Possible values are SWT.TOP or SWT.BOTTOM.
   * 
   * @return the position of the tab
   * 
   * @exception SWTException <ul>
   *		<li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *		<li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   *	</ul>
   */
  public int getTabPosition(){
    checkWidget();
    return onBottom ? SWT.BOTTOM : SWT.TOP;
  }
  
  /**
   * Specify whether the tabs should appear along the top of the folder 
   * or along the bottom of the folder.
   * 
   * @param position <code>SWT.TOP</code> for tabs along the top or <code>SWT.BOTTOM</code> for tabs along the bottom
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the position value is not either SWT.TOP or SWT.BOTTOM</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setTabPosition( final int position ) {
    checkWidget();
    if( position != SWT.TOP && position != SWT.BOTTOM ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( onBottom != ( position == SWT.BOTTOM ) ) {
      onBottom = position == SWT.BOTTOM;
      borderTop = onBottom ? borderLeft : 0;
      borderBottom = onBottom ? 0 : borderRight;
      updateTabHeight( true );
      updateItemsWithResizeEvent();
    }
  }

  /**
   * Returns <code>true</code> if the receiver's border is visible.
   *
   * @return the receiver's border visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getBorderVisible() {
    checkWidget();
    return borderLeft == 1;
  }

  /**
   * Toggle the visibility of the border
   * 
   * @param show true if the border should be displayed
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] not yet evaluated in LCA 
  public void setBorderVisible( final boolean show ) {
    checkWidget();
    if( ( borderLeft != 1 ) != !show ) {
      borderLeft = borderRight = show ? 1 : 0;
      borderTop = onBottom ? borderLeft : 0;
      borderBottom = onBottom ? 0 : borderLeft;
      updateItemsWithResizeEvent();
    }
  }

  /**
   * Returns <code>true</code> if the close button appears 
   * when the user hovers over an unselected tabs.
   * 
   * @return <code>true</code> if the close button appears on unselected tabs
   * 
   * @since 1.0
   */
  public boolean getUnselectedCloseVisible() {
    checkWidget();
    return showUnselectedClose;
  }

  /**
   * Specify whether the close button appears 
   * when the user hovers over an unselected tabs.
   * 
   * @param visible <code>true</code> makes the close button appear
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setUnselectedCloseVisible( final boolean visible ) {
    checkWidget();
    if( showUnselectedClose != visible ) {
      showUnselectedClose = visible;
      updateItems();
    } 
  }

  public Rectangle computeTrim( final int x, 
                                final int y, 
                                final int width, 
                                final int height ) 
  {
    checkWidget();
    Rectangle result;
    int trimX = x - marginWidth - highlight_margin - borderLeft;
    int trimWidth = width
                  + borderLeft
                  + borderRight
                  + 2
                  * marginWidth
                  + 2
                  * highlight_margin;
    if( minimized ) {
      int trimY = onBottom
                ? y - borderTop
                : y - highlight_header - tabHeight - borderTop;
      int trimHeight = borderTop + borderBottom + tabHeight + highlight_header;
      result = new Rectangle( trimX, trimY, trimWidth, trimHeight );
    } else {
      int trimY = onBottom
                ? y - marginHeight - highlight_margin - borderTop
                : y - marginHeight - highlight_header - tabHeight - borderTop;
      int trimHeight = height
                     + borderTop
                     + borderBottom
                     + 2
                     * marginHeight
                     + tabHeight
                     + highlight_header
                     + highlight_margin;
      result = new Rectangle( trimX, trimY, trimWidth, trimHeight );
    }
    return result;
  }


  ///////////////////
  // Selection colors
  
  /**
   * Sets the receiver's selection background color to the color specified
   * by the argument, or to the default system color for the control
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
   * @since 1.0
   */
  public void setSelectionBackground ( final Color color) {
    checkWidget();
    if( selectionBackground != color ) {
      // if (color == null) color = getDisplay().getSystemColor( SELECTION_BACKGROUND );
      if( color == null ) {
        Color defaultBg = getDisplay().getSystemColor( DEFAULT_SELECTION_BG );
        selectionBackground = defaultBg;
      } else {
        selectionBackground = color;
      }
    } 
  }
  
  /**
   * Returns the receiver's selection background color.
   *
   * @return the selection background color of the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Color getSelectionBackground() {
    checkWidget();
    return selectionBackground;
  }
  
  /**
   * Set the foreground color of the selected tab.
   * 
   * @param color the color of the text displayed in the selected tab
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] not yet rendered in LCA
  public void setSelectionForeground( final Color color ) {
    checkWidget();
    if( selectionForeground != color ) {
      if( color == null ) {
        Color defaultFg = getDisplay().getSystemColor( DEFAULT_SELECTION_FG );
        selectionForeground = defaultFg;
      } else {
        selectionForeground = color;
      }
    }
  }

  /**
   * Returns the receiver's selection foreground color.
   *
   * @return the selection foreground color of the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Color getSelectionForeground() {
    checkWidget();
    return selectionForeground;
  }

  
  //////////////////////////////////
  // Manipulation of topRight control 

  /**
   * Set the control that appears in the top right corner of the tab folder.
   * Typically this is a close button or a composite with a Menu and close button. 
   * The topRight control is optional.  Setting the top right control to null will 
   * remove it from the tab folder.
   * 
   * @param control the control to be displayed in the top right corner or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the control is not a child of this CTabFolder</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setTopRight( final Control control ) {
    checkWidget();
    setTopRight( control, SWT.RIGHT );
  }

  /**
   * Set the control that appears in the top right corner of the tab folder.
   * Typically this is a close button or a composite with a Menu and close button. 
   * The topRight control is optional.  Setting the top right control to null 
   * will remove it from the tab folder.
   * <p>
   * The alignment parameter sets the layout of the control in the tab area.
   * <code>SWT.RIGHT</code> will cause the control to be positioned on the far 
   * right of the folder and it will have its default size.  <code>SWT.FILL</code> 
   * will size the control to fill all the available space to the right of the
   * last tab.  If there is no available space, the control will not be visible.
   * </p>
   *
   * @param control the control to be displayed in the top right corner or null
   * @param alignment <code>SWT.RIGHT</code> or <code>SWT.FILL</code> 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the control is not a child of this CTabFolder</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setTopRight( final Control control, final int alignment ) {
    checkWidget();
    if( alignment != SWT.RIGHT && alignment != SWT.FILL ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( control != null && control.getParent() != this ) {
      SWT.error( SWT.ERROR_INVALID_PARENT );
    }
    if( topRight != control || topRightAlignment != alignment ) {
      topRight = control;
      topRightAlignment = alignment;
      updateItems();
    }
  }

  public Control getTopRight() {
    checkWidget();
    return topRight;
  }
  
  ///////////////////////////
  // Adaptable implementation
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else if( adapter == ICTabFolderAdapter.class ) {
      result = tabFolderAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ////////////////////
  // Control overrides
  
  public void setFont( final Font font ) {
    if( font != getFont() ) {
      super.setFont( font );
      if( !updateTabHeight( false ) ) {
        updateItems();
      }
    }
  }
  
  //////////////////////
  // Composite overrides
  
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle result;
    if( minimized ) {
      result = new Rectangle( xClient, yClient, 0, 0 );
    } else {
      Point size = getSize();
      int width =   size.x
                  - borderLeft
                  - borderRight
                  - 2
                  * marginWidth
                  - 2
                  * highlight_margin;
      int height =   size.y
                   - borderTop
                   - borderBottom
                   - 2
                   * marginHeight
                   - highlight_margin
                   - highlight_header;
      height -= tabHeight;
      result = new Rectangle( xClient, yClient, width, height );
    }
    return result;
  }

  protected void releaseChildren() {
    CTabItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  
  protected void releaseWidget() {
    removeControlListener( resizeListener );
    unregisterFocusListener();
    if( showListMenu != null ) {
      showListMenu.dispose();
    }
    super.releaseWidget();
  }

  ///////////////////////////////////////
  // Listener registration/deregistration 

  /**	 
   * Adds the listener to receive events.
   * <p>
   *
   * @param listener the listener
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   */
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  /**	 
   * Removes the listener.
   *
   * @param listener the listener
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  /**
   * 
   * Adds the listener to the collection of listeners who will
   * be notified when a tab item is closed, minimized, maximized,
   * restored, or to show the list of items that are not 
   * currently visible.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *
   * @see CTabFolder2Listener
   * @see #removeCTabFolder2Listener(CTabFolder2Listener)
   * 
   * @since 1.0
   */
  public void addCTabFolder2Listener( final CTabFolder2Listener listener ) {
    CTabFolderEvent.addListener( this, listener );
  }

  /**	 
   * Removes the listener.
   *
   * @param listener the listener
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   * 
   * @see #addCTabFolder2Listener(CTabFolder2Listener)
   * 
   * @since 1.0
   */
  public void removeCTabFolder2Listener( final CTabFolder2Listener listener ) {
    CTabFolderEvent.removeListener( this, listener );
  }
  
  ///////////////////////////////////
  // Helping mothods to arrange items
  
  boolean updateTabHeight( final boolean force ){
    int oldHeight = tabHeight;
    if( fixedTabHeight != SWT.DEFAULT ) {
      // +1 for line drawn across top of tab
      tabHeight = fixedTabHeight == 0 ? 0 : fixedTabHeight + 1; 
    } else {
      CTabItem[] items = getItems();
      if( items.length == 0 ) {
        int charHeight
          = Math.max( 20, FontSizeCalculator.getCharHeight( this.getFont() ) );
        tabHeight = charHeight + CTabItem.TOP_MARGIN + CTabItem.BOTTOM_MARGIN;
      } else {
        int maxHeight = 0;
        for( int i = 0; i < items.length; i++ ) {
          maxHeight = Math.max( maxHeight, items[ i ].preferredHeight() );
        }
        tabHeight = maxHeight;
      }
    }
    boolean result;
    if( force || tabHeight != oldHeight ) {
      ControlEvent evt = new ControlEvent( this, ControlEvent.CONTROL_RESIZED );
      evt.processEvent();
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  void updateItems() {
    updateItems( selectedIndex );
  }

  void updateItems( final int showIndex ) {
    CTabItem[] items = getItems();
    if( !single && !mru && showIndex != -1 ) {
      // make sure selected item will be showing
      int firstIndex = showIndex;
      if( priority[ 0 ] < showIndex ) {
        int maxWidth = getRightItemEdge() - borderLeft;
//        if( !simple ) {
//          maxWidth -= curveWidth - 2 * curveIndent;
//        }
        int width = 0;
        int[] widths = new int[ items.length ];
        for( int i = priority[ 0 ]; i <= showIndex; i++ ) {
          widths[ i ] = items[ i ].preferredWidth( i == selectedIndex, true );
          width += widths[ i ];
          if( width > maxWidth ) {
            break;
          }
        }
        if( width > maxWidth ) {
          width = 0;
          for( int i = showIndex; i >= 0; i-- ) {
            if( widths[ i ] == 0 ) {
              widths[ i ] = items[ i ].preferredWidth( i == selectedIndex,
                                                       true );
            }
            width += widths[ i ];
            if( width > maxWidth ) {
              break;
            }
            firstIndex = i;
          }
        } else {
          firstIndex = priority[ 0 ];
          for( int i = showIndex + 1; i < items.length; i++ ) {
            widths[ i ] = items[ i ].preferredWidth( i == selectedIndex,
                                                     true );
            width += widths[ i ];
            if( width >= maxWidth ) {
              break;
            }
          }
          if( width < maxWidth ) {
            for( int i = priority[ 0 ] - 1; i >= 0; i-- ) {
              if( widths[ i ] == 0 ) {
                widths[ i ] = items[ i ].preferredWidth( i == selectedIndex,
                                                         true );
              }
              width += widths[ i ];
              if( width > maxWidth ) {
                break;
              }
              firstIndex = i;
            }
          }
        }
      }
      if( firstIndex != priority[ 0 ] ) {
        int index = 0;
        for( int i = firstIndex; i < items.length; i++ ) {
          priority[ index++ ] = i;
        }
        for( int i = 0; i < firstIndex; i++ ) {
          priority[ index++ ] = i;
        }
      }
    }
    setItemSize();
    setItemLocation();
    // 
    if( !single ) {
      boolean needChevron = false;
      for( int i = 0; i < items.length; i++ ) {
        if( !items[i].isShowing() ) {
          needChevron = true;
        }
      }
      showChevron = needChevron;
    }
    //
    setButtonBounds();
    // TODO [rh] changing tabHeight does not change the clientArea of the 
    //      control that belongs to the current selection.
    //      The code below ensures this but does not exist in SWT. Couldn't 
    //      figure out how SWT does this.       
    if( selectedIndex != -1 ) {
      Control control = getItem( selectedIndex ).getControl();
      if( control != null ) {
        control.setBounds( getClientArea() );
      }
    }
  }

  private void setButtonBounds() {
    Point size = getSize();
//    int oldX, oldY, oldWidth, oldHeight;
    // max button
//    oldX = maxRect.x;
//    oldY = maxRect.y;
//    oldWidth = maxRect.width;
//    oldHeight = maxRect.height;
    clearRectangle( maxRect );
    if( maximizeVisible ) {
      maxRect.x = size.x - borderRight - BUTTON_SIZE - 3;
      if( borderRight > 0 ) {
        maxRect.x += 1;
      }
      maxRect.y = onBottom
                ?   size.y
                  - borderBottom
                  - tabHeight
                  + ( tabHeight - BUTTON_SIZE )
                  / 2
                : borderTop + ( tabHeight - BUTTON_SIZE ) / 2;
      maxRect.width = BUTTON_SIZE;
      maxRect.height = BUTTON_SIZE;
    }
//    if( oldX != maxRect.x
//        || oldWidth != maxRect.width
//        || oldY != maxRect.y
//        || oldHeight != maxRect.height )
//    {
//      int left = Math.min( oldX, maxRect.x );
//      int right = Math.max( oldX + oldWidth, maxRect.x + maxRect.width );
//      int top = onBottom
//                        ? size.y - borderBottom - tabHeight
//                        : borderTop + 1;
//      redraw( left, top, right - left, tabHeight, false );
//    }
    // min button
//    oldX = minRect.x;
//    oldY = minRect.y;
//    oldWidth = minRect.width;
//    oldHeight = minRect.height;
    clearRectangle( minRect );
    if( minimizeVisible ) {
      minRect.x = size.x - borderRight - maxRect.width - BUTTON_SIZE - 3;
      if( borderRight > 0 ) {
        minRect.x += 1;
      }
      minRect.y = onBottom
                ?   size.y 
                  - borderBottom 
                  - tabHeight 
                  + ( tabHeight - BUTTON_SIZE )
                  / 2
                : borderTop + ( tabHeight - BUTTON_SIZE ) / 2;
      minRect.width = BUTTON_SIZE;
      minRect.height = BUTTON_SIZE;
    }
//    if( oldX != minRect.x
//        || oldWidth != minRect.width
//        || oldY != minRect.y
//        || oldHeight != minRect.height )
//    {
//      int left = Math.min( oldX, minRect.x );
//      int right = Math.max( oldX + oldWidth, minRect.x + minRect.width );
//      int top = onBottom
//                        ? size.y - borderBottom - tabHeight
//                        : borderTop + 1;
//      redraw( left, top, right - left, tabHeight, false );
//    }
    
    // Fake some variables to leave the code below unchanged
    // those variables are fields in original SWT code
//    Rectangle maxRect = new Rectangle( 0, 0, 0, 0 );
//    if( getMaximizeVisible() ) {
//      maxRect.width = BUTTON_SIZE;
//      maxRect.height = tabHeight;
//    }
//    Rectangle minRect = new Rectangle( 0, 0, 0, 0 );
//    if( getMinimizeVisible() ) {
//      minRect.width = BUTTON_SIZE;
//      minRect.height = tabHeight;
//    }
    CTabItem[] items = getItems();
    // top right control
    clearRectangle( topRightRect );
    if( topRight != null ) {
      switch( topRightAlignment ) {
        case SWT.FILL: {
          int rightEdge = size.x
                          - borderRight
                          - 3
                          - maxRect.width
                          - minRect.width;
//          if(    !simple && borderRight > 0 
//              && !getMaximizeVisible() 
//              && !getMinimizeVisible() ) 
//          {
//            rightEdge -= 2;
//          }
          if( single ) {
            if( items.length == 0 || selectedIndex == -1 ) {
              topRightRect.x = borderLeft + 3;
              topRightRect.width = rightEdge - topRightRect.x;
            } else {
              // fill size is 0 if item compressed
              CTabItem item = items[ selectedIndex ];
              if( item.x + item.width + 7 + 3 * BUTTON_SIZE / 2 >= rightEdge ) {
                break;
              }
              topRightRect.x = item.x + item.width + 7 + 3 * BUTTON_SIZE / 2;
              topRightRect.width = rightEdge - topRightRect.x;
            }
          } else {
            // fill size is 0 if chevron showing
            if( showChevron ) {
              break;
            }
            if( items.length == 0 ) {
              topRightRect.x = borderLeft + 3;
            } else {
              CTabItem item = items[ items.length - 1 ];
              topRightRect.x = item.x + item.width;
//              if( !simple && items.length - 1 == selectionIndex ) {
//                topRightRect.x += curveWidth - curveIndent;
//              }
            }
            topRightRect.width = Math.max( 0, rightEdge - topRightRect.x );
          }
          topRightRect.y = onBottom 
                         ? size.y - borderBottom - tabHeight
                         : borderTop + 1;
          topRightRect.height = tabHeight - 1;
          break;
        }
        case SWT.RIGHT: {
          Point topRightSize 
            = topRight.computeSize( SWT.DEFAULT, tabHeight, false );
          int rightEdge 
            = size.x - borderRight - 3 - maxRect.width - minRect.width;
//          if(    !simple 
//              && borderRight > 0 
//              && !getMaximizeVisible() 
//              && !getMinimizeVisible() ) 
//          {
//            rightEdge -= 2;
//          }
          topRightRect.x = rightEdge - topRightSize.x;
          topRightRect.width = topRightSize.x;
          topRightRect.y = onBottom
                         ? size.y - borderBottom - tabHeight
                         : borderTop + 1;
          topRightRect.height = tabHeight - 1;
        }
      }
      topRight.setBounds( topRightRect );
    }

    // chevron button
    if( showChevron ) {
      chevronRect.width = BUTTON_SIZE;
      chevronRect.height = tabHeight;
    }
    clearRectangle( chevronRect );
    if( single ) {
      if( selectedIndex == -1 || items.length > 1 ) {
        chevronRect.width = 3 * BUTTON_SIZE / 2;
        chevronRect.height = BUTTON_SIZE;
        chevronRect.y = onBottom
                                ? size.y
                                  - borderBottom
                                  - tabHeight
                                  + ( tabHeight - chevronRect.height )
                                  / 2
                                : borderTop
                                  + ( tabHeight - chevronRect.height )
                                  / 2;
        if( selectedIndex == -1 ) {
          chevronRect.x = size.x
                        - borderRight
                        - 3
                        - minRect.width
                        - maxRect.width
                        - topRightRect.width
                        - chevronRect.width;
        } else {
          CTabItem item = items[ selectedIndex ];
          int width = size.x
                    - borderRight
                    - 3
                    - minRect.width
                    - maxRect.width
                    - chevronRect.width;
          if( topRightRect.width > 0 ) {
            width -= topRightRect.width + 3;
          }
          chevronRect.x = Math.min( item.x + item.width + 3, width );
        }
        if( borderRight > 0 ) {
          chevronRect.x += 1;
        }
      }
    } else {
      if( showChevron ) {
        chevronRect.width = 3 * BUTTON_SIZE / 2;
        chevronRect.height = BUTTON_SIZE;
        int i = 0, lastIndex = -1;
        while( i < priority.length && items[ priority[ i ] ].showing ) {
          lastIndex = Math.max( lastIndex, priority[ i++ ] );
        }
        if( lastIndex == -1 ) {
          lastIndex = firstIndex;
        }
        CTabItem lastItem = items[ lastIndex ];
        int w = lastItem.x + lastItem.width + 3;
//        if( !simple && lastIndex == selectionIndex ) {
//          w += curveWidth - 2 * curveIndent;
//        }
        chevronRect.x = Math.min( w, getRightItemEdge() );
        chevronRect.y = onBottom
                                ? size.y
                                  - borderBottom
                                  - tabHeight
                                  + ( tabHeight - chevronRect.height )
                                  / 2
                                : borderTop
                                  + ( tabHeight - chevronRect.height )
                                  / 2;
      }
    }
  }

  private boolean setItemLocation() {
    boolean changed = false;
    CTabItem[] items = getItems();
    if( items.length == 0 ) {
      return false;
    }
    Point size = getSize();
    int y = onBottom 
        ? Math.max( borderBottom, size.y - borderBottom - tabHeight ) 
        : borderTop;
    if( single ) {
//      int defaultX = getDisplay().getBounds().width + 10; // off screen
      
      int defaultX = 12000;
      
      for( int i = 0; i < items.length; i++ ) {
        CTabItem item = items[ i ];
        if( i == selectedIndex ) {
          firstIndex = selectedIndex;
          int oldX = item.getBounds().x;
          int oldY = item.getBounds().y;
          item.x = borderLeft;
          item.y = y;
          item.showing = true;
          if( item.x != oldX || item.y != oldY ) {
            changed = true; 
          }
        } else {
          item.x = defaultX;
          item.showing = false;
        }
      }
    } else {
      int rightItemEdge = getRightItemEdge();
      int maxWidth = rightItemEdge - borderLeft;
      int width = 0;
      for( int i = 0; i < priority.length; i++ ) {
        CTabItem item = items[ priority[ i ] ];
        width += item.width;
        item.showing = i == 0
                     ? true
                     : item.width > 0 && width <= maxWidth;
//        if( !simple && priority[ i ] == selectedIndex ) {
//          width += curveWidth - 2 * curveIndent;
//        }
      }
      int x = 0;
//      int defaultX = getDisplay().getBounds().width + 10; // off screen

      int defaultX = 12000;
      
      firstIndex = items.length - 1;
      for( int i = 0; i < items.length; i++ ) {
        CTabItem item = items[ i ];
        if( !item.showing ) {
          if( item.x != defaultX ) {
            changed = true;
          }
          item.x = defaultX;
        } else {
          firstIndex = Math.min( firstIndex, i );
          if( item.x != x || item.y != y ) {
            changed = true;
          }
          item.x = x;
          item.y = y;
//          if( i == selectionIndex ) {
//            int edge = Math.min( item.x + item.width, rightItemEdge );
//          } else {
//          }
          x = x + item.width;
//          if( !simple && i == selectionIndex ) {
//            x += curveWidth - 2 * curveIndent;
//          }
        }
      }
    }
    return changed;
  }

  private boolean setItemSize() {
    boolean changed = false;
    if( isDisposed() ) {
      return changed;
    }
    Point size = getSize();
    if( size.x <= 0 || size.y <= 0 ) {
      return changed;
    }
    xClient = borderLeft + marginWidth + highlight_margin;
    if( onBottom ) {
      yClient = borderTop + highlight_margin + marginHeight;
    } else {
      yClient = borderTop + tabHeight + highlight_header + marginHeight;
    }
    CTabItem[] items = getItems();
    showChevron = false;
    if( single ) {
      showChevron = true;
      if( selectedIndex != -1 ) {
        CTabItem item = items[ selectedIndex ];
        int width = item.preferredWidth( true, false );
        width = Math.min( width, getRightItemEdge() - borderLeft );
        Rectangle oldBounds = item.getBounds();
        if( oldBounds.height != tabHeight || oldBounds.width != width ) {
          changed = true;
          item.shortenedText = null;
          item.shortenedTextWidth = 0;
          item.width = addCloseButtonWidth( item, width );
          item.height = tabHeight;
          item.shortenedText = null;
          item.shortenedTextWidth = 0;
        }
      }
      return changed;
    }
    if( items.length == 0 ) {
      return changed;
    }
    int[] widths;
    int tabAreaWidth = size.x - borderLeft - borderRight - 3;
    if( minimizeVisible ) {
      tabAreaWidth -= BUTTON_SIZE;
    }
    if( maximizeVisible ) {
      tabAreaWidth -= BUTTON_SIZE;
    }
    if( topRightAlignment == SWT.RIGHT && topRight != null ) {
      Point rightSize = topRight.computeSize( SWT.DEFAULT, SWT.DEFAULT, false );
      tabAreaWidth -= rightSize.x + 3;
    }
    tabAreaWidth = Math.max( 0, tabAreaWidth );
    // First, try the minimum tab size at full compression.
    int minWidth = 0;
    int[] minWidths = new int[ items.length ];
    for( int i = 0; minWidth <= tabAreaWidth && i < priority.length; i++ ) {
      int index = priority[ i ];
      boolean selected = index == selectedIndex;
      minWidths[ index ] = items[ index ].preferredWidth( selected, true );
      minWidth += minWidths[ index ];
    }
    if( minWidth > tabAreaWidth ) {
      // full compression required and a chevron
      showChevron = items.length > 1;
      if( showChevron ) {
        tabAreaWidth -= 3 * BUTTON_SIZE / 2;
      }
      widths = minWidths;
      int index = selectedIndex != -1 ? selectedIndex : 0;
      if( tabAreaWidth < widths[ index ] ) {
        widths[ index ] = Math.max( 0, tabAreaWidth );
      }
    } else {
      int maxWidth = 0;
      int[] maxWidths = new int[ items.length ];
      for( int i = 0; i < items.length; i++ ) {
        maxWidths[ i ] = items[ i ].preferredWidth( i == selectedIndex, 
                                                    false );
        maxWidth += maxWidths[ i ];
      }
      if( maxWidth <= tabAreaWidth ) {
        // no compression required
        widths = maxWidths;
      } else {
        // determine compression for each item
        int extra = ( tabAreaWidth - minWidth ) / items.length;
        while( true ) {
          int large = 0, totalWidth = 0;
          for( int i = 0; i < items.length; i++ ) {
            if( maxWidths[ i ] > minWidths[ i ] + extra ) {
              totalWidth += minWidths[ i ] + extra;
              large++;
            } else {
              totalWidth += maxWidths[ i ];
            }
          }
          if( totalWidth >= tabAreaWidth ) {
            extra--;
            break;
          }
          if( large == 0 || tabAreaWidth - totalWidth < large ) {
            break;
          }
          extra++;
        }
        widths = new int[ items.length ];
        for( int i = 0; i < items.length; i++ ) {
          widths[ i ] = Math.min( maxWidths[ i ], minWidths[ i ] + extra );
        }
      }
    }
    for( int i = 0; i < items.length; i++ ) {
      CTabItem item = items[ i ];
      int width = widths[ i ];
      Rectangle oldBounds = item.getBounds();
      if( oldBounds.height != tabHeight || oldBounds.width != width ) {
        changed = true;
        item.shortenedText = null;
        item.shortenedTextWidth = 0;
        item.width = addCloseButtonWidth( item, width );
        item.height = tabHeight;
      }
    }
    return changed;
  }
  
  private int addCloseButtonWidth( final CTabItem item, final int width ) {
    int result = width;
    if( item.canClose() ) { 
      result += BUTTON_SIZE;
    }
    return result;
  }

  private int getRightItemEdge() {
    int result = getSize().x - borderRight - 3;
    if( minimizeVisible ) {
      result -= BUTTON_SIZE;
    }
    if( maximizeVisible ) {
      result -= BUTTON_SIZE;
    }
    if( showChevron ) {
      result -= 3 * BUTTON_SIZE / 2;
    }
    if( topRight != null && topRightAlignment != SWT.FILL ) {
      Point rightSize = topRight.computeSize( SWT.DEFAULT, SWT.DEFAULT );
      result -= rightSize.x + 3;
    }
    return Math.max( 0, result );
  }

  private static void clearRectangle( final Rectangle rectangle ) {
    rectangle.x = 0;
    rectangle.y = 0;
    rectangle.width = 0;
    rectangle.height = 0;
  }
  
  private void updateItemsWithResizeEvent() {
    Rectangle rectBefore = getClientArea();
    updateItems();
    Rectangle rectAfter = getClientArea();
    if( !rectBefore.equals( rectAfter ) ) {
      // TODO [rh] revise this: are all event fields set correclty?
      ControlEvent event 
        = new ControlEvent( this, ControlEvent.CONTROL_RESIZED );
      event.processEvent();
    }
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int mask 
      = SWT.CLOSE 
      | SWT.TOP 
      | SWT.BOTTOM 
      | SWT.FLAT 
//      | SWT.LEFT_TO_RIGHT 
//      | SWT.RIGHT_TO_LEFT 
      | SWT.SINGLE 
      | SWT.MULTI;
    int result = style & mask;
    // TOP and BOTTOM are mutually exlusive, TOP is the default
    if( ( result & SWT.TOP ) != 0 ) {
      result = result & ~SWT.BOTTOM;
    }
    // SINGLE and MULTI are mutually exlusive, MULTI is the default
    if( ( result & SWT.MULTI ) != 0 ) {
      result = result & ~SWT.SINGLE;
    }
    return result;
  }
  
  private void registerDisposeListener() {
    addDisposeListener( new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        inDispose = true;
        CTabFolder.this.removeDisposeListener( this );
      }
    } );
  }
  
  private void registerFocusListener() {
    if( focusListener == null ) {
      focusListener = new FocusListener( ) {
        public void focusGained( final FocusEvent event ) {
          onFocus();
        }
        public void focusLost( final FocusEvent event ) {
          onFocus();
        }
      };
      addFocusListener( focusListener );
    }
  }
  
  private void onFocus() {
    if( selectedIndex < 0 ) {
      setSelection( 0, true );
    }
    unregisterFocusListener();
  }

  private void unregisterFocusListener() {
    if( focusListener != null ) {
      addFocusListener( focusListener );
      focusListener = null;
    }    
  }

  private void showListMenu() {
    CTabItem[] items = getItems();
    if( items.length == 0 || !showChevron ) {
      return;
    }
    if( showListMenu == null || showListMenu.isDisposed() ) {
      showListMenu = new Menu( this );
    } else {
      // TODO [rh] optimize: reuse existing menuItems if possible 
      MenuItem[] menuItems = showListMenu.getItems();
      for( int i = 0; i < menuItems.length; i++ ) {
        menuItems[ i ].dispose();
      }
    }
    final String id = "CTabFolder_showList_Index"; //$NON-NLS-1$
    for( int i = 0; i < items.length; i++ ) {
      CTabItem tab = items[ i ];
      if( !tab.showing ) {
        MenuItem item = new MenuItem( showListMenu, SWT.NONE );
        item.setText( tab.getText() );
        item.setImage( tab.getImage() );
        item.setData( id, tab );
        item.addSelectionListener( new SelectionAdapter() {
          public void widgetSelected( final SelectionEvent event ) {
            MenuItem menuItem = ( MenuItem )event.getSource();
            int index = indexOf( ( CTabItem )menuItem.getData( id ) );
            setSelection( index, true );
          }
        } );
      } 
    }
    // show menu if it contains any item
    if( showListMenu.getItemCount() > 0 ) {
      int x = chevronRect.x;
      int y = chevronRect.y + chevronRect.height + 1;
      Point location = getDisplay().map( this, null, x, y );
      showListMenu.setLocation( location.x, location.y );
      showListMenu.setVisible( true );
    }    
  }

  private void setSelection( final int index, final boolean notify ) {
    int oldSelectedIndex = selectedIndex;
    setSelection( index );
    if( notify && selectedIndex != oldSelectedIndex && selectedIndex != -1 ) {
      CTabItem selection = getSelection();
      SelectionEvent event 
        = new SelectionEvent( this, selection, SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
  }
  
  void createItem( final CTabItem item, final int index ) {
    itemHolder.insert( item, index );
    if( selectedIndex >= index ) {
      selectedIndex++;
    }
    int[] newPriority = new int[ priority.length + 1 ];
    int next = 0, priorityIndex = priority.length;
    for( int i = 0; i < priority.length; i++ ) {
      if( !mru && priority[ i ] == index ) {
        priorityIndex = next++;
      }
      newPriority[ next++ ] = priority[ i ] >= index
                            ? priority[ i ] + 1
                            : priority[ i ];
    }
    newPriority[ priorityIndex ] = index;
    priority = newPriority;
    updateItems();
    if( getItemCount() == 1 ) {
      registerFocusListener();
    }
  }
  
  void destroyItem( final CTabItem item ) {
    int index = indexOf( item );
    if( !inDispose && index != -1  ) {
      CTabItem[] items = getItems();
      if( items.length == 1 ) {
        items = new CTabItem[ 0 ];
        priority = new int[ 0 ];
        firstIndex = -1;
        selectedIndex = -1;
        Control control = item.getControl();
        if( control != null && !control.isDisposed() ) {
          control.setVisible( false );
        }
        setToolTipText( null );
        setButtonBounds();
        itemHolder.remove( item );
      } else {
        itemHolder.remove( item );
        int[] newPriority = new int[ priority.length - 1 ];
        int next = 0;
        for( int i = 0; i < priority.length; i++ ) {
          if( priority[ i ] == index ) {
            continue;
          }
          newPriority[ next++ ] = priority[ i ] > index
                                ? priority[ i ] - 1
                                : priority[ i ];
        }
        priority = newPriority;
        // move the selection if this item is selected
        if( selectedIndex == index ) {
          Control control = item.getControl();
          selectedIndex = -1;
          int nextSelection = mru ? priority[ 0 ] : Math.max( 0, index - 1 );
          setSelection( nextSelection, true );
          if( control != null && !control.isDisposed() ) {
            control.setVisible( false );
          }
        } else if( selectedIndex > index ) {
          selectedIndex--;
        }
      }
      updateItems();
      if( getItemCount() == 0 ) {
        unregisterFocusListener();
      }
    }    
  }
  
  ////////////////
  // Inner classes
  
  private final class CTabFolderAdapter implements ICTabFolderAdapter {

    public Rectangle getChevronRect() {
      Rectangle rect = CTabFolder.this.chevronRect;
      return new Rectangle( rect.x, rect.y, rect.width, rect.height );
    }

    public boolean getChevronVisible() {
      return CTabFolder.this.showChevron;
    }
    
    public Rectangle getMinimizeRect() {
      Rectangle rect = CTabFolder.this.minRect;
      return new Rectangle( rect.x, rect.y, rect.width, rect.height );
    }

    public Rectangle getMaximizeRect() {
      Rectangle rect = CTabFolder.this.maxRect;
      return new Rectangle( rect.x, rect.y, rect.width, rect.height );
    }
    
    public void showListMenu() {
      CTabFolder.this.showListMenu();
    }
    
    public Menu getShowListMenu() {
      if( CTabFolder.this.showListMenu == null ) {
        CTabFolder.this.showListMenu = new Menu( getShell(), SWT.POP_UP );
      }
      return CTabFolder.this.showListMenu;
    }    
  }
}
