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
import org.eclipse.swt.internal.graphics.FontSizeCalculator;

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

  private static final int RIGHT_MARGIN = 2;
  private static final int IMAGE_TEXT_GAP = 2;
  
  private static final class Data {
    String text = "";
    Image image;
    Font font;
    Color background;
    Color foreground;
  }
  
  private final Table parent;
  boolean cached;
  private Data[] data;
  private boolean checked;
  private boolean grayed;
  private Color background;
  private Color foreground;
  private Font font;

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
      ensureData( index, count );
      data[ index ].text = text;
      markCached();
      parent.redraw();
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
      ensureData( index, count );
      data[ index ].image = image;
      parent.updateItemImageSize( image );
      markCached();
      parent.redraw();
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

  ////////////////////
  // Colors and Fonts
  
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
   */
  public void setBackground( final Color color ) {
    checkWidget();
    if( background != color ) {
      background = color;
      if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
        cached = true;
      }
      markCached();
      parent.redraw();
    }
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
   */
  public Color getBackground() {
    checkWidget ();
    parent.checkData( this, parent.indexOf( this ) );
    Color result;
    if( background == null ) {
      result = parent.getBackground();
    } else {
      result = background;
    }
    return result;
  }

  /**
   * Sets the background color at the given column index in the receiver 
   * to the color specified by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param index the column index
   * @param color the new color (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setBackground( final int index, final Color color ) {
    checkWidget();
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      ensureData( index, count );
      data[ index ].background = color;
      markCached();
      parent.redraw();
    }
  }

  /**
   * Returns the background color at the given column index in the receiver.
   *
   * @param index the column index
   * @return the background color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Color getBackground( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    Color result = getBackground();
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null 
        && data[ index ].background != null ) 
    {
      result = data[ index ].background;
    }
    return result;
  }

  /**
   * Sets the receiver's foreground color to the color specified by the
   * argument, or to the default system color for the item if the argument is
   * null.
   * 
   * @param color the new color (or null)
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_ARGUMENT - if the argument has been
   *              disposed</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setForeground( final Color color ) {
    checkWidget();
    if( foreground != color ) {
      foreground = color;
      if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
        cached = true;
      }
      markCached();
      parent.redraw();
    }
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
   */
  public Color getForeground() {
    checkWidget ();
    parent.checkData( this, parent.indexOf( this ) );
    Color result;
    if( foreground == null ) {
      result = parent.getForeground();
    } else {
      result = foreground;
    }
    return result;
  }
  
  /**
   * Sets the foreground color at the given column index in the receiver 
   * to the color specified by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param index the column index
   * @param color the new color (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setForeground( final int index, final Color color ) {
    checkWidget();
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      ensureData( index, count );
      data[ index ].foreground = color;
      markCached();
      parent.redraw();
    }
  }

  /**
   * 
   * Returns the foreground color at the given column index in the receiver.
   *
   * @param index the column index
   * @return the foreground color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Color getForeground( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    Color result = getForeground();
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null 
        && data[ index ].foreground != null ) 
    {
      result = data[ index ].foreground;
    }
    return result;
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
   */
  public void setFont( final Font font ) {
    checkWidget();
    if( this.font != font ) {
      this.font = font;
      markCached();
      parent.redraw();
    }
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
   */
  public Font getFont () {
    checkWidget ();
    parent.checkData( this, parent.indexOf( this ) );
    Font result;
    if( font == null ) {
      result = parent.getFont();
    } else {
      result = font;
    }
    return result;
  }
  
  /**
   * Sets the font that the receiver will use to paint textual information
   * for the specified cell in this item to the font specified by the 
   * argument, or to the default font for that kind of control if the 
   * argument is null.
   *
   * @param index the column index
   * @param font the new font (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setFont( final int index, final Font font ) {
    checkWidget();
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      ensureData( index, count );
      data[ index ].font = font;
      markCached();
      parent.redraw();
    }
  }

  /**
   * Returns the font that the receiver will use to paint textual information
   * for the specified cell in this item.
   *
   * @param index the column index
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Font getFont( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    Font result = getFont();
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null 
        && data[ index ].font != null ) 
    {
      result = data[ index ].font;
    }
    return result;
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
    Rectangle result;
    int columnCount = parent.getColumnCount();
    if( columnCount > 0 && ( index < 0 || index >= columnCount ) ) {
      result = new Rectangle( 0, 0, 0, 0 );
    } else {
      Rectangle imageBounds = getImageBounds( index );
      Rectangle textBounds = getTextBounds( index );
      int left = imageBounds.x;
      int top = imageBounds.y;
      int width;
      if( index == 0 && columnCount == 0 ) {
        int gap = getImageGap( index );
        width = 2 + imageBounds.width + gap + textBounds.width + 2;
      } else {
        width = parent.getColumn( index ).getWidth(); 
      }
      int height = imageBounds.height;
      result = new Rectangle( left, top, width, height );
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
    int left = 0;
    int top = 0; 
    int width = 0;
    int height = 0;
    int columnCount = parent.getColumnCount();
    if( index == 0 && columnCount == 0 ) {
      int itemIndex = parent.indexOf( this );
      left = getCheckWidth( index );
      top = getTop( itemIndex );
      width = getImageWidth( index );
      height = parent.getItemHeight();
    } else if( index >= 0 && index < columnCount ) {
      int itemIndex = parent.indexOf( this );
      left = getCheckWidth( index ) + parent.getColumn( index ).getLeft();
      top = getTop( itemIndex );
      width = getImageWidth( index );
      height = parent.getItemHeight();
    } 
    return new Rectangle( left, top, width, height );
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
  
  /**
   * Returns a rectangle describing the size and location
   * relative to its parent of the text at a column in the
   * table.  An empty rectangle is returned if index exceeds
   * the index of the table's last column.
   *
   * @param index the index that specifies the column
   * @return the receiver's bounding text rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getTextBounds( final int index ) {
    checkWidget();
    parent.checkData( this, parent.indexOf( this ) );
    int itemIndex = parent.indexOf( this );
    int left = 0;
    int top = 0; 
    int width = 0;
    int height = 0;
    if( index == 0 && parent.getColumnCount() == 0 ) {
      Rectangle imageBounds = getImageBounds( 0 );
      left = imageBounds.x + imageBounds.width + getImageGap( 0 );
      top = getTop( itemIndex );
      Font font = parent.getFont();
      width = FontSizeCalculator.stringExtent( font, getText( 0 ) ).x;
      height = parent.getItemHeight();
    } else {
      if( itemIndex != -1 && index < parent.getColumnCount() ) {
        Rectangle imageBounds = getImageBounds( index );
        int gap = getImageGap( index );
        left = imageBounds.x + imageBounds.width + gap;
        top = getTop( itemIndex );
        width = getColumnWidth( index ) - ( gap + imageBounds.width );
        if( width < 0 ) {
          width = 0;
        }
        height = parent.getItemHeight();
      } 
    }
    return new Rectangle( left, top, width, height );
  }

  private int getColumnWidth( final int index ) {
    TableColumn column = parent.getColumn( index );
    return column.getWidth() - getCheckWidth( index );
  }

  private int getTop( final int itemIndex ) {
    return itemIndex * parent.getItemHeight();
  }
  
  final int getPackWidth( final int index ) {
    return 
        getImageWidth( index )
      + getImageGap( index )
      + FontSizeCalculator.stringExtent( parent.getFont(), getText( index ) ).x
      + RIGHT_MARGIN;
  }
  
  final int getCheckWidth( final int index ) {
//    return index == 0 ? parent.getCheckWidth() : 0;
    int result = 0;
    if( index == 0 && parent.getColumnCount() == 0 ) {
      result = parent.getCheckWidth();
    } else {
      int[] columnOrder = parent.getColumnOrder();
      if( columnOrder[ 0 ] == index ) {
        result = parent.getCheckWidth();
      }
    }
    return result;
  }
  
  private int getImageWidth( final int index ) {
    int width = 0;
    Image image = getImage( index );
    if( image != null ) {
      width = parent.getItemImageSize().x;
    }
    return width;
  }
  
  private int getImageGap( final int index ) {
    int result = 0;
    Image image = getImage( index );
    if( image != null ) {
      result = IMAGE_TEXT_GAP;
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
    checked = false;
    grayed = false;
    if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
      cached = false;
    }
  }

  /////////////////////////////
  // Widget and Item overrides

  void releaseChildren() {
  }

  void releaseParent() {
    parent.destroyItem( this );
  }

  void releaseWidget() {
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
    if( visibleItemCount > 0 ) {
      int index = parent.indexOf( this );
      result = index - parent.getTopIndex() <= visibleItemCount;
    }
    return result;
  }

  private void markCached() {
    if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
      cached = true;
    }
  }

  private void ensureData( final int index, final int columnCount ) {
    if( data == null ) {
      data = new Data[ columnCount ];
    } else if( data.length < columnCount ) {
      Data[] newData = new Data[ columnCount ];
      System.arraycopy( data, 0, newData, 0, data.length );
      data = newData;
    }
    if( data[ index ] == null ) {
      data[ index ] = new Data();
    }
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}