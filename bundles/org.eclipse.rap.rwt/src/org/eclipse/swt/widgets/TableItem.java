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
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

/**
 * Instances of this class represent a selectable user interface object
 * that represents an item in a table.
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
public class TableItem extends Item {

  // TODO [rh] make private but accessible from TabelLCA#getDefaultColumnWidth
  public static final int CHECK_WIDTH = 21;
  private static final int CHECK_HEIGHT = 13;

  private static final class Data {
    String text;
    Image image;
  }
  
  boolean cached;
  private final Table parent;
  private Data[] data;
  private boolean checked;
  private boolean grayed;

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
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TableItem( final Table parent, final int style ) {
    this( parent, style, checkNull( parent).getItemCount() );
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
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TableItem( final Table parent, final int style, final int index ) {
    this( parent, style, index, true );
  }
  
  TableItem( final Table parent, 
             final int style, 
             final int index, 
             final boolean cached ) 
  {
    super( parent, style );
    this.parent = parent;
    this.cached = cached;
    this.parent.createItem( this, index );
  }
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
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
  
  ///////////////////////////
  // Methods to get/set texts  
  
  public void setText( final String text ) {
    checkWidget();
    setText( 0, text );
  }
  
  /**
   * Sets the receiver's text at a column
   *
   * @param index the column index
   * @param string the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( final int index, final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      if( data == null ) {
        data = new Data[ count ];
      } else if( data.length < count ) {
        enlargeData( count );
      }
      if( data[ index ] == null ) {
        data[ index ] = new Data();
      }
      data[ index ].text = text;
    }
  }

  public String getText() {
    checkWidget();
    return getText( 0 );
  }
  
  /**
   * Returns the text stored at the given column index in the receiver,
   * or empty string if the text has not been set.
   *
   * @param index the column index
   * @return the text stored at the given column index in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    String result = "";
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null ) 
    {
      result = data[ index ].text;
    }
    return result;
  }

  ////////////////////////////
  // Methods to get/set images  
  
  public void setImage( final Image image ) {
    checkWidget();
    setImage( 0, image );
  }
  
  /**
   * Sets the receiver's image at a column.
   *
   * @param index the column index
   * @param image the new image
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setImage( final int index, final Image image ) {
    checkWidget();
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      if( data == null ) {
        data = new Data[ count ];
      } else if( data.length < count ) {
        enlargeData( count );
      }
      if( data[ index ] == null ) {
        data[ index ] = new Data();
      }
      data[ index ].image = image;
    }
  }
  
  /**
   * Sets the image for multiple columns in the table. 
   * 
   * @param images the array of new images
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if one of the images has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setImage( final Image[] images ) {
    checkWidget();
    if( images == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    for( int i = 0; i < images.length; i++ ) {
      setImage( i, images[ i ] );
    }
  }

  public Image getImage() {
    checkWidget();
    return getImage( 0 );
  }
  
  /**
   * Returns the image stored at the given column index in the receiver,
   * or null if the image has not been set or if the column does not exist.
   *
   * @param index the column index
   * @return the image stored at the given column index in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Image getImage( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    Image result = null;
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null ) 
    {
      result = data[ index ].image;
    }
    return result;
  }

  /**
   * Returns a rectangle describing the size and location
   * relative to its parent of an image at a column in the
   * table.  An empty rectangle is returned if index exceeds
   * the index of the table's last column.
   *
   * @param index the index that specifies the column
   * @return the receiver's bounding image rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getImageBounds( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    Rectangle result;
    Image image = getImage( index );
    if( image != null ) {
      result = image.getBounds();
    } else {
      result = new Rectangle( 0, 0, 0, 0 );
    }
    return result;
  }

  /**
   * Gets the image indent.
   *
   * @return the indent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getImageIndent () {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    // The only method to manipulate the image indent (setImageIndent) is 
    // deprecated and this not implemented, therefore we can safely return 0
    return 0;
  }


  ///////////////////
  // Checked & Grayed
  
  /**
   * Sets the checked state of the checkbox for this item.  This state change 
   * only applies if the Table was created with the SWT.CHECK style.
   *
   * @param checked the new checked state of the checkbox
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setChecked( final boolean checked ) {
    checkWidget();
    if( ( parent.style & SWT.CHECK ) != 0 ) {
      this.checked = checked;
    } 
  }

  /**
   * Returns <code>true</code> if the receiver is checked,
   * and false otherwise.  When the parent does not have
   * the <code>CHECK</code> style, return false.
   *
   * @return the checked state of the checkbox
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getChecked() {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    boolean result = false;
    if( ( parent.style & SWT.CHECK ) != 0 ) {
      result = checked;
    } 
    return result;
  }

  /**
   * Sets the grayed state of the checkbox for this item.  This state change 
   * only applies if the Table was created with the SWT.CHECK style.
   *
   * @param grayed the new grayed state of the checkbox; 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setGrayed( final boolean grayed ) {
    checkWidget();
    if( ( parent.style & SWT.CHECK ) != 0 ) {
      this.grayed = grayed;
    } 
  }

  /**
   * Returns <code>true</code> if the receiver is grayed,
   * and false otherwise. When the parent does not have
   * the <code>CHECK</code> style, return false.
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
    parent.checkData( this, parent.indexOf( this ) );
    boolean result = false;
    if( ( parent.style & SWT.CHECK ) != 0 ) {
      result = grayed;
    }
    return result;
  }

  /////////////////////
  // Dimension methods
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
   public Rectangle getBounds() {
    return getBounds( 0 );
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent at a column in the table.
   *
   * @param index the index that specifies the column
   * @return the receiver's bounding column rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getBounds( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    int itemIndex = parent.indexOf( this );
    int left = 0;
    int top = 0; 
    int width = 0;
    int height = 0;
    if( index == 0 && parent.getColumnCount() == 0 ) {
      left = getCheckWidth( 0 );
      top = getTop( itemIndex );
      Font font = parent.getFont();
      width = FontSizeEstimation.stringExtent( getText(), font ).x;
      Image image = data != null && data.length > 0 ? data[ 0 ].image : null;
      if( image != null ) {
        width += image.getBounds().width;
      }
      height = getHeight();
    } else {
      if( itemIndex != -1 && index < parent.getColumnCount() ) {
        TableColumn[] columns = parent.getColumns();
        left = columns[ index ].getLeft() + getCheckWidth( index );
        top = getTop( itemIndex );
        width = columns[ index ].getWidth();
        height = getHeight();
        width -= getCheckWidth( index );
      } 
    }
    return new Rectangle( left, top, width, height );
  }
  
  final int getTop( final int itemIndex ) {
    int result = 0;
    for( int i = 0; i < itemIndex; i++ ) {
      result += parent.getItem( i ).getHeight();
    }
    return result;
  }
  
  final int getHeight() {
    // TODO [rh] replace with this.getFont() once TableItem supports fonts
    // TODO [rh] preliminary: this is only an approximation for item height
    int result = FontSizeEstimation.getCharHeight( parent.getFont() ) + 4;
    int columnCount = Math.max( parent.getColumnCount(), 1 );
    for( int i = 0; i < columnCount; i++ ) {
      Rectangle imageBounds = getImageBounds( i );
      if( imageBounds.height > result ) {
        result = imageBounds.height;
      }
    }
    if( ( parent.style & SWT.CHECK ) != 0 ) {
      result = Math.max( CHECK_HEIGHT, result );
    }
    return result;
  }
  
  final int getCheckWidth( final int index ) {
    int result = 0;
    if( index == 0 && ( getParent().getStyle() & SWT.CHECK ) != 0 ) {
      result = CHECK_WIDTH;
    }
    return result;
  }
  
  final int getMaxWidth( final int index ) {
    int result = getCheckWidth( index );
    Font font = parent.getFont();
    result += FontSizeEstimation.stringExtent( getText( index ), font ).x;
    Image image = getImage( index );
    if( image != null ) {
      result += image.getBounds().width;
    }
    return result;
  }
  
  ///////////////////////////////////////
  // Clear item data (texts, images, etc)

  final void removeData( final int index ) {
    if( data != null && parent.getColumnCount() > 1 ) {
      Data[] newData = new Data[ data.length - 1 ];
      System.arraycopy( data, 0, newData, 0, index );
      int offSet = data.length - index - 1;
      System.arraycopy( data, index + 1, newData, index, offSet );
      data = newData;
    }
  }
  
  final void clear() {
    data = null;
    if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
      cached = false;
    }
  }

  /////////////////////////////
  // Widget and Item overrides

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    parent.destroyItem( this );
  }

  protected void releaseWidget() {
  }

  String getNameText() {
    if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
      if( !cached ) {
        return "*virtual*";
      }
    }
    return super.getNameText();
  }

  //////////////////
  // helping methods
  

  final boolean isVisible() {
    boolean result = false;
    int visibleItemCount = parent.getVisibleItemCount();
    int index = parent.indexOf( this );
    if( visibleItemCount > 0 ) {
      result = index - parent.getTopIndex() <= visibleItemCount;
    }
    return result;
  }

  private void enlargeData( final int count ) {
    Data[] newData = new Data[ count ];
    System.arraycopy( data, 0, newData, 0, data.length );
    data = newData;
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}