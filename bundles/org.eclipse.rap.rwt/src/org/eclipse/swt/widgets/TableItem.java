/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.*;


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

  private final class TableItemAdapter implements ITableItemAdapter {

    public Color[] getCellBackgrounds() {
      int columnCount = Math.max( 1, getParent().getColumnCount() );
      Color[] result = new Color[ columnCount ];
      for( int i = 0; i < result.length; i++ ) {
        if( data != null && data[ i ] != null ) {
          result[ i ] = data[ i ].background;
        }
        if( result[ i ] == null ) {
          result[ i ] = background;
        }
      }
      return result;
    }

    public Color[] getCellForegrounds() {
      int columnCount = Math.max( 1, getParent().getColumnCount() );
      Color[] result = new Color[ columnCount ];
      for( int i = 0; i < result.length; i++ ) {
        if( data != null && data[ i ] != null ) {
          result[ i ] = data[ i ].foreground;
        }
        if( result[ i ] == null ) {
          result[ i ] = foreground;
        }
      }
      return result;
    }

    public Font[] getCellFonts() {
      int columnCount = Math.max( 1, getParent().getColumnCount() );
      Font[] result = new Font[ columnCount ];
      for( int i = 0; i < result.length; i++ ) {
        if( data != null && data[ i ] != null ) {
          result[ i ] = data[ i ].font;
        }
        if( result[ i ] == null ) {
          result[ i ] = font;
        }
      }
      return result;
    }
  }

  private static final class Data {
    String text = "";
    Image image;
    Font font;
    Color background;
    Color foreground;
  }

  private final TableItemAdapter tableItemAdapter;
  final Table parent;
  boolean cached;
  int index;
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
             final boolean create )
  {
    super( parent, style );
    this.parent = parent;
    this.index = index;
    tableItemAdapter = new TableItemAdapter();
    if( create ) {
      this.parent.createItem( this, index );
    }
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if(    adapter == IWidgetFontAdapter.class
        || adapter == IWidgetColorAdapter.class
        || adapter == ITableItemAdapter.class )
    {
      result = tableItemAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
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
   * @param text the new text
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
      if( !text.equals( data[ index ].text ) ) {
        data[ index ].text = text;
        markCached();
        if( parent.getColumnCount() == 0 ) {
          parent.updateScrollBars();
        }
        parent.redraw();
      }
    }
  }

  /**
   * Sets the text for multiple columns in the table.
   *
   * @param strings the array of new strings
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public void setText( final String[] strings ) {
    checkWidget();
    if( strings == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    for( int i = 0; i < strings.length; i++ ) {
      String string = strings[ i ];
      if( string != null ) {
        setText( i, string );
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
      if( !equals( data[ index].image, image ) ) {
        parent.updateColumnImageCount( index, data[ index ].image, image );
        data[ index ].image = image;
        parent.updateItemImageSize( image );
        markCached();
        if( parent.getColumnCount() == 0 ) {
          parent.updateScrollBars();
        }
        parent.redraw();
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    return getImageInternal( index );
  }

  Image getImageInternal( final int index ) {
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
    if( !equals( background, color ) ) {
      background = color;
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
      if( !equals( data[ index ].background, color ) ) {
        data[ index ].background = color;
        markCached();
        parent.redraw();
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
    if( !equals( foreground, color ) ) {
      foreground = color;
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
    checkWidget();
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
      if( !equals( data[ index ].foreground, color ) ) {
        data[ index ].foreground = color;
        markCached();
        parent.redraw();
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
    if( !equals( this.font, font ) ) {
      this.font = font;
      markCached();
      if( parent.getColumnCount() == 0 ) {
        parent.updateScrollBars();
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
      if( !equals( font, data[ index ].font ) ) {
        data[ index ].font = font;
        markCached();
        parent.redraw();
      }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    Rectangle result;
    int columnCount = parent.getColumnCount();
    if( columnCount > 0 && ( index < 0 || index >= columnCount ) ) {
      result = new Rectangle( 0, 0, 0, 0 );
    } else {
      Rectangle textBounds = getTextBounds( index );
      int left = getLeft( index );
      int itemIndex = parent.indexOf( this );
      int top = getTop( itemIndex );
      int width = 0;
      if( index == 0 && columnCount == 0 ) {
        Rectangle imageBounds = getImageBounds( index );
        int spacing = getSpacing( index );
        int paddingWidth = parent.getCellPadding().width;
        width = imageBounds.width + spacing + textBounds.width + paddingWidth;
      } else if( index >= 0 && index < columnCount ) {
        width = parent.getColumn( index ).getWidth();
      }
      int height = getHeight( index );
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
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    int itemIndex = parent.indexOf( this );
    Rectangle cellPadding = parent.getCellPadding();
    int left = getLeft( index ) + cellPadding.x;
    int top = getTop( itemIndex );
    int width = getImageWidth( index );
    int height = getHeight( index );
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
  public int getImageIndent() {
    checkWidget();
    if( !parent.checkData( this, parent.indexOf( this ) ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    // [rh] The only method to manipulate the image indent (setImageIndent) is
    // deprecated and thus not implemented, therefore we can safely return 0
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
    int itemIndex = parent.indexOf( this );
    if( !parent.checkData( this, itemIndex ) ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    int left = 0;
    int top = 0;
    int width = 0;
    Rectangle cellPadding = parent.getCellPadding();
    if( index == 0 && parent.getColumnCount() == 0 ) {
      int imageWidth = 0;
      int spacing = 0;
      if( parent.hasColumnImages( 0 ) ) {
        imageWidth = parent.getItemImageSize().x;
        spacing = getSpacing( 0 );
      }
      left =   getCheckWidth( 0 )
             + cellPadding.x
             + imageWidth
             + spacing;
      top = getTop( itemIndex );
      Font font = getFont();
      width = TextSizeDetermination.stringExtent( font, getText( 0 ) ).x;
    } else if( itemIndex != -1 && index < parent.getColumnCount() ) {
      int imageWidth = 0;
      if( parent.hasColumnImages( index ) ) {
        imageWidth = parent.getItemImageSize().x;
      }
      int spacing = getSpacing( index );
      left =   parent.getColumn( index ).getLeft()
             + getCheckWidth( index )
             + cellPadding.x
             + imageWidth
             + spacing;
      top = getTop( itemIndex );
      width =   getColumnWidth( index )
              - cellPadding.width
              - imageWidth
              - spacing;
      if( width < 0 ) {
        width = 0;
      }
    }
    int height = getHeight( index );
    return new Rectangle( left, top, width, height );
  }

  private int getColumnWidth( final int index ) {
    TableColumn column = parent.getColumn( index );
    return column.getWidth() - getCheckWidth( index );
  }

  private int getLeft( final int index ) {
    int result = 0;
    int columnCount = parent.getColumnCount();
    if( index == 0 && columnCount == 0 ) {
      result = getCheckWidth( index ) - parent.leftOffset;
    } else if( index >= 0 && index < columnCount ) {
      // TODO [rh] consider applying the leftOffset already in Column#getLeft()
      int columnLeft = parent.getColumn( index ).getLeft();
      result = getCheckWidth( index ) + columnLeft - parent.leftOffset;
    }
    return result;
  }

  private int getTop( final int itemIndex ) {
    int relativeItemIndex = itemIndex - parent.getTopIndex();
    int headerHeight = parent.getHeaderHeight();
    int itemHeight = parent.getItemHeight();
    return headerHeight + relativeItemIndex * itemHeight;
  }

  private int getHeight( final int index ) {
    int result = 0;
    int columnCount = parent.getColumnCount();
    boolean singleColumn = index == 0 && columnCount == 0;
    boolean columnInRange = index >= 0 && index < columnCount;
    if( singleColumn || columnInRange ) {
      result = parent.getItemHeight();
    }
    return result;
  }

  final int getPackWidth( final int index ) {
    return
        getImageWidth( index )
      + getSpacing( index )
      + getTextWidth( index )
      + parent.getCellPadding().width;
  }

  final int getCheckWidth( final int index ) {
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
    int result = 0;
    Image image = getImage( index );
    if( image != null ) {
      result = parent.getItemImageSize().x;
    }
    return result;
  }

  private int getTextWidth( final int index ) {
    int result = 0;
    String text = getText( index );
    if( text.length() > 0 ) {
      result = TextSizeDetermination.stringExtent( parent.getFont(), text ).x;
    }
    return result;
  }

  private int getSpacing( final int index ) {
    int result = 0;
    if( parent.hasColumnImages( index ) ) {
      result = parent.getCellSpacing();
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
    parent.updateScrollBars();
    if( ( parent.style & SWT.VIRTUAL ) != 0 ) {
      cached = false;
      parent.redraw();
    }
  }

  /////////////////////////////
  // Widget and Item overrides

  void releaseParent() {
    parent.destroyItem( this, parent.indexOf( this ) );
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
  // Helping methods

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

  private static boolean equals( final Object object1, final Object object2 ) {
    boolean result;
    if( object1 == object2 ) {
      result = true;
    } else if( object1 == null ) {
      result = false;
    } else {
      result = object1.equals( object2 );
    }
    return result;
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}