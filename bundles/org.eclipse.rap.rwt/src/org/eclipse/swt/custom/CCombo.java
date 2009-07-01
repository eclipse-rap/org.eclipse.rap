/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;


import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ccombokit.CComboThemeAdapter;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.ListModel;
import org.eclipse.swt.widgets.*;

/**
 * The CCombo class represents a selectable user interface object
 * that combines a text field and a list and issues notification
 * when an item is selected from the list.
 * <p>
 * CCombo was written to work around certain limitations in the native
 * combo box. Specifically, on win32, the height of a CCombo can be set;
 * attempts to set the height of a Combo are ignored. CCombo can be used
 * anywhere that having the increased flexibility is more important than
 * getting native L&F, but the decision should not be taken lightly. 
 * There is no is no strict requirement that CCombo look or behave
 * the same as the native combo box.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
 * </dl>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#ccombo">CCombo snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: CustomControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * @since 1.2
 */
public final class CCombo extends Composite {

  /* Default size for widgets */
  static final int DEFAULT_WIDTH = 64;
  static final int DEFAULT_HEIGHT = 64;
  
  // Must be in sync with appearance "list-item"
  private static final int LIST_ITEM_PADDING = 3;
  private static final int DROP_DOWN_BUTTON_WIDTH = 14;
  
  /**
   * The maximum number of characters that can be entered
   * into a text widget.
   * <p>
   * Note that this value is platform dependent, based upon
   * the native widget implementation.
   * </p>
   */
  public static final int LIMIT = Integer.MAX_VALUE;
  
  private final ListModel model;
  private String text;
  private int textLimit;
  private final Point selection;
  private int visibleCount;
  private boolean editable;
  private boolean dropped;
	
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
   * @see SWT#BORDER
   * @see SWT#READ_ONLY
   * @see SWT#FLAT
   * @see Widget#getStyle()
   */
  public CCombo( final Composite parent, final int style ) {
  	super( parent, checkStyle( style ) );
  	text = "";
  	textLimit = LIMIT;
  	selection = new Point( 0, 0 );
  	visibleCount = 5;
  	dropped = false;
  	editable = ( style & SWT.READ_ONLY ) != 0 ? false : true;
  	model = new ListModel( true );
  }
  
  public int getStyle() {
    int result = super.getStyle();
    result &= ~SWT.READ_ONLY;
    if( !editable ) {
      result |= SWT.READ_ONLY;
    }
    return result;
  }
  
  /**
   * Returns the zero-relative index of the item which is currently
   * selected in the receiver's list, or -1 if no item is selected.
   *
   * @return the index of the selected item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getSelectionIndex() {
    checkWidget();
    return model.getSelectionIndex();
  }
  
  /**
   * Selects the item at the given zero-relative index in the receiver's 
   * list.  If the item at the index was already selected, it remains
   * selected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void select( final int index ) {
    checkWidget();
    model.setSelection( index );
    updateText();
  }
  
  /**
   * Deselects the item at the given zero-relative index in the receiver's 
   * list.  If the item at the index was already deselected, it remains
   * deselected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to deselect
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void deselect( final int index ) {
    checkWidget();
    if( index == model.getSelectionIndex() ) {
      model.setSelection( -1 );
    }
    updateText();
  }
  
  /**
   * Deselects all selected items in the receiver's list.
   * <p>
   * Note: To clear the selection in the receiver's text field,
   * use <code>clearSelection()</code>.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #clearSelection
   */
  public void deselectAll() {
    checkWidget();
    text = "";
    model.deselectAll();
    updateText();
  }
  
  /**
   * Sets the selection in the receiver's text field to the
   * range specified by the argument whose x coordinate is the
   * start of the selection and whose y coordinate is the end
   * of the selection. 
   *
   * @param selection a point representing the new selection start and end
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final Point selection ) {
    checkWidget();
    if( selection == null ) SWT.error ( SWT.ERROR_NULL_ARGUMENT );
    int validatedStart = this.selection.x;
    int validatedEnd = this.selection.y;
    int start = selection.x;
    int end = selection.y;
    if( start >= 0 && end >= start ) {
      validatedStart = Math.min( start, text.length() );
      validatedEnd = Math.min( end, text.length() );
    } else if ( end >= 0 && start > end ) {
      validatedStart = Math.min( end, text.length() );
      validatedEnd = Math.min( start, text.length() );
    }
    this.selection.x = validatedStart;
    this.selection.y = validatedEnd;
  }
  
  /**
   * Returns a <code>Point</code> whose x coordinate is the start
   * of the selection in the receiver's text field, and whose y
   * coordinate is the end of the selection. The returned values
   * are zero-relative. An "empty" selection as indicated by
   * the the x and y coordinates having the same value.
   *
   * @return a point representing the selection start and end
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Point getSelection() {
    checkWidget();
    return new Point( selection.x, selection.y );
  }
  
  /**
   * Sets the maximum number of characters that the receiver's
   * text field is capable of holding to be the argument.
   *
   * @param limit new text limit
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setTextLimit( final int limit ) {
    checkWidget();
    if( limit == 0 ) {
      SWT.error ( SWT.ERROR_CANNOT_BE_ZERO );
    }
    textLimit = limit;
  }
  
  /**
   * Returns the maximum number of characters that the receiver's
   * text field is capable of holding. If this has not been changed
   * by <code>setTextLimit()</code>, it will be the constant
   * <code>Combo.LIMIT</code>.
   * 
   * @return the text limit
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getTextLimit() {
    checkWidget();
    return textLimit;
  }
  
  /**
   * Sets the selection in the receiver's text field to an empty
   * selection starting just before the first character. If the
   * text field is editable, this has the effect of placing the
   * i-beam at the start of the text.
   * <p>
   * Note: To clear the selected items in the receiver's list, 
   * use <code>deselectAll()</code>.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #deselectAll
   */
  public void clearSelection() {
    checkWidget();
    selection.x = 0;
    selection.y = 0;
  }
  
  /**
   * Adds the argument to the end of the receiver's list.
   *
   * @param string the new item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #add(String,int)
   */
  public void add( final String string ) {
  	checkWidget();
  	model.add( string );
  }
  
  /**
   * Adds the argument to the receiver's list at the given
   * zero-relative index.
   * <p>
   * Note: To add an item at the end of the list, use the
   * result of calling <code>getItemCount()</code> as the
   * index or use <code>add(String)</code>.
   * </p>
   *
   * @param string the new item
   * @param index the index for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #add(String)
   */
  public void add( final String string, final int index) {
  	checkWidget();
  	model.add( string, index );
  }
  
  /**
   * Removes the item from the receiver's list at the given
   * zero-relative index.
   *
   * @param index the index for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final int index ) {
    checkWidget();
    int selectionIndex = getSelectionIndex();
    if( selectionIndex == index ) {
      deselect( index );
    }
    model.remove( index );
  }
  
  /**
   * Removes the items from the receiver's list which are
   * between the given zero-relative start and end 
   * indices (inclusive).
   *
   * @param start the start of the range
   * @param end the end of the range
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final int start, final int end ) {
    checkWidget();
    int selectionIndex = getSelectionIndex();
    String[] items = model.getItems();
    for( int i = start; i <= end; i++ ) {
      int indexTemp = indexOf( items[i] );
      if( selectionIndex == indexTemp ) {
        deselect( indexTemp );
      }
    }
    model.remove( start, end );
  }
  
  /**
   * Searches the receiver's list starting at the first item
   * until an item is found that is equal to the argument, 
   * and removes that item from the list.
   *
   * @param string the item to remove
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final String string ) {
    checkWidget();
    int indexOfThisString = indexOf( string );
    int selectionIndex = getSelectionIndex();
    if( selectionIndex == indexOfThisString ) {
      deselect( indexOfThisString );
    }
    model.remove( string );
  }
  
  /**
   * Removes all of the items from the receiver's list and clear the
   * contents of receiver's text field.
   * <p>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void removeAll() {
    checkWidget();
    deselectAll();
    model.removeAll();
  }
  
  /**
   * Sets the text of the item in the receiver's list at the given
   * zero-relative index to the string argument. This is equivalent
   * to <code>remove</code>'ing the old item at the index, and then
   * <code>add</code>'ing the new item at that index.
   *
   * @param index the index for the item
   * @param string the new text for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setItem( final int index, final String string ) {
    checkWidget();
    model.setItem( index, string );
  }
  
  /**
   * Sets the receiver's list to be the given array of items.
   *
   * @param items the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if an item in the items array is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setItems( final String [] items ) {
    checkWidget();
    model.setItems( items );
  }
  
  /**
   * Returns the item at the given, zero-relative index in the
   * receiver's list. Throws an exception if the index is out
   * of range.
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
   */
  public String getItem( final int index ) {
    checkWidget();
    return model.getItem( index );
  }
  
  /**
   * Returns the number of items contained in the receiver's list.
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
    return model.getItemCount();
  }
  
  /**
   * Returns the height of the area which would be used to
   * display <em>one</em> of the items in the receiver's list.
   *
   * @return the height of one item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getItemHeight() {
    checkWidget();
    int charHeight = TextSizeDetermination.getCharHeight( getFont() );
    int padding = 2 * LIST_ITEM_PADDING;
    return charHeight + padding;
  }
  
  /**
   * Returns an array of <code>String</code>s which are the items
   * in the receiver's list. 
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the items in the receiver's list
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String[] getItems() {
    checkWidget();
    return model.getItems();
  }
  
  /**
   * Sets the number of items that are visible in the drop
   * down portion of the receiver's list.
   *
   * @param count the new number of items to be visible
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public void setVisibleItemCount( final int count ) {
    checkWidget();
    if( count >= 0 ) {
      visibleCount = count;
    }
  }
  
  /**
   * Gets the number of items that are visible in the drop
   * down portion of the receiver's list.
   *
   * @return the number of items that are visible
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public int getVisibleItemCount() {
    checkWidget();
    return visibleCount;
  }
  
  /**
   * Marks the receiver's list as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, marking
   * it visible may not actually cause it to be displayed.
   * </p>
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public void setListVisible( final boolean visible ) {
    checkWidget();
    dropped = visible;
  }
  
  /**
   * Returns <code>true</code> if the receiver's list is visible,
   * and <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, this method
   * may still indicate that it is considered visible even though
   * it may not actually be showing.
   * </p>
   *
   * @return the receiver's list's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public boolean getListVisible() {
    checkWidget();
    return dropped;
  }
  
  /**
   * Searches the receiver's list starting at the first item
   * (index 0) until an item is found that is equal to the 
   * argument, and returns the index of that item. If no item
   * is found, returns -1.
   *
   * @param string the search item
   * @return the index of the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf( final String string ) {
    checkWidget();
    if( string == null ) SWT.error ( SWT.ERROR_NULL_ARGUMENT );
    return indexOf( string, 0 );
  }
  
  /**
   * Searches the receiver's list starting at the given, 
   * zero-relative index until an item is found that is equal
   * to the argument, and returns the index of that item. If
   * no item is found or the starting index is out of range,
   * returns -1.
   *
   * @param string the search item
   * @param start the zero-relative index at which to begin the search
   * @return the index of the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf ( final String string, final int start ) {
    checkWidget();
    if( string == null ) SWT.error ( SWT.ERROR_NULL_ARGUMENT );
    if( !( 0 <= start && start < model.getItemCount() ) )
      return -1;
    for( int i = start; i < model.getItemCount(); i++ ) {
      if( string.equals( model.getItem( i ) ) )
        return i;
    }
    return -1;
  }
  
  /**
   * Sets the contents of the receiver's text field to the
   * given string.
   * <p>
   * Note: The text field in a <code>Combo</code> is typically
   * only capable of displaying a single line of text. Thus,
   * setting the text to a string containing line breaks or
   * other special characters will probably cause it to 
   * display incorrectly.
   * </p>
   *
   * @param string the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( final String string ) {
    checkWidget();
    if( string == null ) {
      SWT.error ( SWT.ERROR_NULL_ARGUMENT );
    }
    int style = super.getStyle();
    if( ( style & SWT.READ_ONLY ) != 0 ) {
      int index = indexOf( string );
      if( index == -1 ) {
        return;
      }
      select( index );
    }
    String verifiedText = verifyText( string, 0, text.length() );
    if( verifiedText != null ) {
      model.deselectAll();
      String[] items = model.getItems();
      for( int i = 0; i < items.length; i++ ) {
        if( verifiedText.equals( items[i] ) ) {
          model.setSelection( i );
          break;
        }
      }
      text = verifiedText;
      fireModifyEvent();
    }
  }
  
  /**
   * Returns a string containing a copy of the contents of the
   * receiver's text field.
   *
   * @return the receiver's text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText() {
    checkWidget();
    String result = "";
    int style = super.getStyle();
    if( ( style & SWT.READ_ONLY ) != 0 ) {
      int idx = model.getSelectionIndex();
      if( idx != -1 ) {
        result = model.getItem( idx );
      }
    } else {
      result = text;
    }
    return result;
  }
  
  /**
   * Sets the editable state.
   *
   * @param editable the new editable state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public void setEditable( final boolean editable ) {
    checkWidget();
    this.editable = editable;
  }
  
  /**
   * Gets the editable state.
   *
   * @return whether or not the receiver is editable
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   */
  public boolean getEditable() {
    checkWidget();
    return editable;
  }
  
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = TextSizeDetermination.getCharHeight( getFont() );
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      String[] items = model.getItems();
      for( int i = 0; i < items.length; i++ ) {
        if( !"".equals( items[ i ] ) ) {
          Point extent
            = TextSizeDetermination.stringExtent( getFont(), items[ i ] );
          width = Math.max( width, extent.x + 10 );
        }
      }
    }
    Rectangle padding = getPadding();
    if( width != 0 ) {
      width += padding.width + DROP_DOWN_BUTTON_WIDTH;
    }
    if( height != 0 ) {
      height += padding.height;
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
    height += 2 * border;
    width += 2 * border;
    return new Point( width, height );
  }
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the user changes the receiver's selection, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the combo's list selection changes.
   * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
   * </p>
   *
   * @param listener the listener which should be notified when the user changes the receiver's selection
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
   * be notified when the user changes the receiver's selection.
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
   * be notified when the receiver's text is modified, by sending
   * it one of the messages defined in the <code>ModifyListener</code>
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
   * @see ModifyListener
   * @see #removeModifyListener
   */
  public void addModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.addListener( this, listener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver's text is modified.
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
   * @see ModifyListener
   * @see #addModifyListener
   */
  public void removeModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.removeListener( this, listener );
  }
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's text is verified, by sending
   * it one of the messages defined in the <code>VerifyListener</code>
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
   * @see VerifyListener
   * @see #removeVerifyListener
   * 
   */
  public void addVerifyListener( final VerifyListener verifyListener ) {
    VerifyEvent.addListener( this, verifyListener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is verified.
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
   * @see VerifyListener
   * @see #addVerifyListener
   * 
   */
  public void removeVerifyListener( final VerifyListener verifyListener ) {
    VerifyEvent.removeListener( this, verifyListener );
  }
  
  public Control[] getChildren() {
  	checkWidget();
  	return new Control[ 0 ];
  }
  
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
  	return;
  }
  
  //////////////////
  // Helping methods

  // Direct copy from Combo.java
  private String verifyText( final String text, final int start, final int end )
  {
    VerifyEvent event = new VerifyEvent( this );
    event.text = text;
    event.start = start;
    event.end = end;
    event.processEvent();
    /*
     * It is possible (but unlikely), that application code could have disposed
     * the widget in the verify event. If this happens, answer null to cancel
     * the operation.
     */
    String result;
    if( event.doit && !isDisposed() ) {
      result = event.text;
    } else {
      return null;
    }
    return result;
  }
  
  private void updateText() {
    int style = super.getStyle();
    if( ( style & SWT.READ_ONLY ) == 0 ) {
      int selectionIndex = getSelectionIndex();
      if( selectionIndex != -1 ) {
        setText( getItem( selectionIndex ) );
      } else {
        setText( "" );
      }
    } else {
      // Covers "SWT.READ_ONLY selection" use case
      fireModifyEvent();
    }
  }
  
  private void fireModifyEvent() {
    ModifyEvent modifyEvent = new ModifyEvent( this );
    modifyEvent.processEvent();
  }
  
  // Made in the same way as in Combo.java
  private Rectangle getPadding() {
    ThemeManager manager = ThemeManager.getInstance();
    CComboThemeAdapter adapter
      = ( CComboThemeAdapter )manager.getThemeAdapter( CCombo.class );
    return adapter.getPadding( this );
  }

  private static int checkStyle( final int style ) {
    int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT;
    return style & mask;
  }
}
