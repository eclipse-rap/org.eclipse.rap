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

import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.combokit.IComboThemeAdapter;

/**
 * Instances of this class are controls that allow the user
 * to choose an item from a list of items, or optionally
 * enter a new value by typing it into an editable text
 * field. Often, <code>Combo</code>s are used in the same place
 * where a single selection <code>List</code> widget could
 * be used but space is limited. A <code>Combo</code> takes
 * less space than a <code>List</code> widget and shows
 * similar information.
 * <p>
 * Note: Since <code>Combo</code>s can contain both a list
 * and an editable text field, it is possible to confuse methods
 * which access one versus the other (compare for example,
 * <code>clearSelection()</code> and <code>deselectAll()</code>).
 * The API documentation is careful to indicate either "the
 * receiver's list" or the "the receiver's text field" to
 * distinguish between the two cases.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DROP_DOWN, READ_ONLY, SIMPLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles DROP_DOWN and SIMPLE may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see List
 * <hr/>
 * <p>Implementation status:</p>
 * Currently only the DROP_DOWN style is supported.
 */
// TODO [rst] Update Javadoc
// TODO [rh] SWT sends an SWT.Modify event when selection is changed or items
//      are aded/removed
public class Combo extends Composite {

  private static final int DROP_DOWN_BUTTON_WIDTH = 14;
  private final ListModel model;
  private String text = "";

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
   * @see SWT#DROP_DOWN
   * @see SWT#READ_ONLY
   * @see SWT#SIMPLE
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Combo( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( true );
  }

  //////////////////////////////////////
  // Methods to manipulate the selection

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
  public void select( final int selectionIndex ) {
    checkWidget();
    model.setSelection( selectionIndex );
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
    model.deselectAll();
  }

//  /**
//   * Sets the selection in the receiver's text field to an empty
//   * selection starting just before the first character. If the
//   * text field is editable, this has the effect of placing the
//   * i-beam at the start of the text.
//   * <p>
//   * Note: To clear the selected items in the receiver's list, 
//   * use <code>deselectAll()</code>.
//   * </p>
//   *
//   * @exception SWTException <ul>
//   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
//   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
//   * </ul>
//   *
//   * @see #deselectAll
//   */
  public void clearSelection() {
    checkWidget();
    // TODO [rh] IMPLEMENTATION MISSING
  }

  ///////////////////////////////////////
  // Methods to manipulate and get items

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
  public void add( final String string, final int index ) {
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
    model.removeAll();
  }

  /**
   * Sets the text of the item in the receiver's list at the given
   * zero-relative index to the string argument. This is equivalent
   * to removing the old item at the index, and then adding the new
   * item at that index.
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
  public void setItems( final String[] items ) {
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
   * Returns a (possibly empty) array of <code>String</code>s which are
   * the items in the receiver's list.
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

//  /**
//   * Sets the number of items that are visible in the drop
//   * down portion of the receiver's list.
//   * <p>
//   * Note: This operation is a hint and is not supported on
//   * platforms that do not have this concept.
//   * </p>
//   *
//   * @param count the new number of items to be visible
//   *
//   * @exception SWTException <ul>
//   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
//   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
//   * </ul>
//   * 
//   * @since 3.0
//   */
  public void setVisibleItemCount (int count) {
    checkWidget ();
    // TODO [rh] IMPLEMENTATION MISSING
  }

//  /**
//   * Gets the number of items that are visible in the drop
//   * down portion of the receiver's list.
//   * <p>
//   * Note: This operation is a hint and is not supported on
//   * platforms that do not have this concept.
//   * </p>
//   *
//   * @return the number of items that are visible
//   *
//   * @exception SWTException <ul>
//   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
//   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
//   * </ul>
//   * 
//   * @since 3.0
//   */
  public int getVisibleItemCount () {
    checkWidget ();
    // TODO [rh] IMPLEMENTATION MISSING
    return 0;
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
  public int indexOf( final String string, final int start ) {
    checkWidget();
    if( string == null )
      error( SWT.ERROR_NULL_ARGUMENT );
    if( !( 0 <= start && start < model.getItemCount() ) )
      return -1;
    for( int i = start; i < model.getItemCount(); i++ ) {
      if( string.equals( model.getItem( i ) ) )
        return i;
    }
    return -1;
  }

  /**
   * Returns a string containing a copy of the contents of the
   * receiver's text field, or an empty string if there are no
   * contents.
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
   * Sets the contents of the receiver's text field to the given string.
   * <p>
   * Note: The text field in a <code>Combo</code> is typically only capable of
   * displaying a single line of text. Thus, setting the text to a string
   * containing line breaks or other special characters will probably cause it
   * to display incorrectly.
   * </p>
   * 
   * @param string the new text
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setText( final String string ) {
    checkWidget();
    if( string == null )
      error( SWT.ERROR_NULL_ARGUMENT );
    if( ( style & SWT.READ_ONLY ) != 0 ) {
      int index = indexOf( string );
      if( index == -1 )
        return;
      select( index );
    }
    text = string;
    ModifyEvent modifyEvent = new ModifyEvent( this );
    modifyEvent.processEvent();
  }

  // //////////////////
  // Widget dimensions

  // TODO [rst] Revise: In SWT, a width or height hint of 0 does not result in
  //      the DEFAULT_WIDTH/HEIGHT.
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

  /////////////////////////////////////////
  // Listener registration/de-registration

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's selection changes, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the combo's list selection changes.
   * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
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
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
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
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    ModifyEvent.removeListener( this, listener );
  }
  
  // TODO [fappel]: documentation and implementation
  public void addVerifyListener( final VerifyListener verifyListener ) {
  }

  // TODO [fappel]: documentation and implementation
  public void removeVerifyListener( final VerifyListener verifyListener ) {
  }


  boolean isTabGroup() {
    return true;
  }

  String getNameText() {
    return getText();
  }

  //////////////////
  // Helping methods

  private Rectangle getPadding() {
    ThemeManager manager = ThemeManager.getInstance();
    IComboThemeAdapter adapter
      = ( IComboThemeAdapter )manager.getThemeAdapter( getClass() );
    return adapter.getPadding( this );
  }

  private static int checkStyle( final int style ) {
    int result = style;
    /*
     * Feature in Windows.  It is not possible to create
     * a combo box that has a border using Windows style
     * bits.  All combo boxes draw their own border and
     * do not use the standard Windows border styles.
     * Therefore, no matter what style bits are specified,
     * clear the BORDER bits so that the SWT style will
     * match the Windows widget.
     *
     * The Windows behavior is currently implemented on
     * all platforms.
     */
    result &= ~SWT.BORDER;

    /*
     * Even though it is legal to create this widget
     * with scroll bars, they serve no useful purpose
     * because they do not automatically scroll the
     * widget's client area.  The fix is to clear
     * the SWT style.
     */
    result &= ~( SWT.H_SCROLL | SWT.V_SCROLL );
    result = checkBits( result, SWT.DROP_DOWN, SWT.SIMPLE, 0, 0, 0, 0 );
    if( ( result & SWT.SIMPLE ) != 0 ) {
      return result & ~SWT.READ_ONLY;
    }
    result |= SWT.H_SCROLL; // Copied from SWT Combo constructor
    return result;
  }
}
