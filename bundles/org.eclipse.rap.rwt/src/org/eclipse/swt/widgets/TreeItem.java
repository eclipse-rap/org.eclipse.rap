/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
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

  private static final int IMAGE_TEXT_GAP = 2;
  private static final int INDENT_WIDTH = 16;
  
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
  private String[] texts;
  private Image[] images;
  Color[] cellForegrounds, cellBackgrounds;
  Font[] cellFonts;
  int depth;
  private int index;
  protected boolean cached;

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
   * Constructs a new instance of this class given its parent (which must be a
   * <code>Tree</code> or a <code>TreeItem</code>), a style value
   * describing its behavior and appearance, and the index at which to place it
   * in the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   * 
   * @param parent a tree control which will be the parent of the new instance
   *            (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *                the number of elements in the parent (inclusive)</li>
   *                </ul>
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the parent</li>
   *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
   *                allowed subclass</li>
   *                </ul>
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public TreeItem( Tree parent, int style, int index ) {
    this( parent, null, style, index );
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
      this.depth = parentItem.depth+1;
    }
    int newIndex;
    if( parentItem != null ) {
      newIndex = index == -1 ? parentItem.getItemCount() : index;
      ItemHolder.insertItem( parentItem, this, newIndex );
    } else {
      newIndex = index == -1 ? parent.getItemCount() : index;
      ItemHolder.insertItem( parent, this, newIndex );
    }
    this.index = newIndex;
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
    
    int columnCount = parent.columnHolder.size();
    texts = new String[ columnCount ];
    images = new Image[ columnCount ];
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
  public Tree getParent() {
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
    checkWidget();
    return getBounds( 0 );
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent at a column in the tree.
   *
   * @param columnIndex the index that specifies the column
   * @return the receiver's bounding column rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Rectangle getBounds( int columnIndex ) {
    return getBounds( columnIndex, true );
  }
  
  /* package */ Rectangle getBounds( int columnIndex, boolean checkData ) {
    checkWidget();
    Rectangle result;
    int columnCount = parent.getColumnCount();
    if( columnIndex < 0 || ( columnIndex > columnCount ) ) {
      result = new Rectangle( 0, 0, 0, 0 );
    } else if( getParentItem() != null && !getParentItem().getExpanded() ) {
      result = new Rectangle( 0, 0, 0, 0 );
    } else {
      Rectangle imageBounds = getImageBounds( columnIndex );
      Point textWidth = TextSizeDetermination.stringExtent( getFont(),
                                                            getText( 0 , checkData ) );
      int width;
      if( columnIndex == 0 && columnCount == 0 ) {
        int gap = getImageGap( columnIndex );
        width = 2 + imageBounds.width + gap + textWidth.x + 2;
      } else {
        width = parent.getColumn( columnIndex ).getWidth();
      }
      // no support for bigger text/images due to qx bug
      // int height = Math.max( textWidth.y, imageBounds.height ) + 2;
      int height = 16;

      int left = imageBounds.x + ( depth+1 * INDENT_WIDTH );
      int top = 0;
      if( parentItem != null ) {
        final Rectangle parentItemBounds = parentItem.getBounds( columnIndex );
        top += parentItemBounds.y + 16; //parentItemBounds.height;
//      } else {
      }
      top += index * height; // TODO: [bm] go trough all elements to gain height?
      result = new Rectangle( left, top, width, height );
    }
    return result;
  }

	private int getImageGap( final int index ) {
    int result = 0;
    Image image = getImage( index );
    if( image != null ) {
      result = IMAGE_TEXT_GAP;
    }
    return result;
  }
  
  /**
	 * Returns the background color at the given column index in the receiver.
	 * 
	 * @param columnIndex
	 *            the column index
	 * @return the background color
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @since 1.0
	 */
  public Color getBackground( int columnIndex ) {
    checkWidget();
    // if (!parent.checkData (this, true)) error (SWT.ERROR_WIDGET_DISPOSED);
    int validColumnCount = Math.max( 1, parent.columnHolder.size() );
    if( !( 0 <= columnIndex && columnIndex < validColumnCount ) )
      return getBackground();
    if( cellBackgrounds == null || cellBackgrounds[ columnIndex ] == null )
      return getBackground();
    return cellBackgrounds[ columnIndex ];
  }
  
  /**
   * Returns the font that the receiver will use to paint textual information
   * for the specified cell in this item.
   *
   * @param columnIndex the column index
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public Font getFont (int columnIndex) {
    checkWidget ();
    return getFont (columnIndex, true);
  }
  
  Font getFont (int columnIndex, boolean checkData) {
//    if (checkData && !parent.checkData (this, true)) error (SWT.ERROR_WIDGET_DISPOSED);
    int validColumnCount = Math.max (1, parent.columnHolder.size());
    if (!(0 <= columnIndex && columnIndex < validColumnCount)) return getFont (checkData);
    if (cellFonts == null || cellFonts [columnIndex] == null) return getFont (checkData);
    return cellFonts [columnIndex];
  }
  
  /**
   * 
   * Returns the foreground color at the given column index in the receiver.
   *
   * @param columnIndex the column index
   * @return the foreground color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Color getForeground( int columnIndex ) {
    checkWidget();
    // if (!parent.checkData (this, true)) error (SWT.ERROR_WIDGET_DISPOSED);
    int validColumnCount = Math.max( 1, parent.columnHolder.size() );
    if( !( 0 <= columnIndex && columnIndex < validColumnCount ) )
      return getForeground();
    if( cellForegrounds == null || cellForegrounds[ columnIndex ] == null )
      return getForeground();
    return cellForegrounds[ columnIndex ];
  }
  
  /**
   * Sets the background color at the given column index in the receiver 
   * to the color specified by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param columnIndex the column index
   * @param value the new color (or null)
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
   * 
   */
  public void setBackground (int columnIndex, Color value) {
    checkWidget();
    int validColumnCount = Math.max( 1, parent.columnHolder.size() );
    if( !( 0 <= columnIndex && columnIndex < validColumnCount ) )
      return;
    if( cellBackgrounds == null ) {
      cellBackgrounds = new Color[ validColumnCount ];
    }
    if( cellBackgrounds[ columnIndex ] == value )
      return;
    if( cellBackgrounds[ columnIndex ] != null
        && cellBackgrounds[ columnIndex ].equals( value ) )
      return;
    cellBackgrounds[ columnIndex ] = value;
//    if( ( parent.style & SWT.VIRTUAL ) != 0 )
//      cached = true;
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * for the specified cell in this item to the font specified by the 
   * argument, or to the default font for that kind of control if the 
   * argument is null.
   *
   * @param columnIndex the column index
   * @param value the new font (or null)
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
  public void setFont (int columnIndex, Font value) {
    checkWidget ();

    int validColumnCount = Math.max (1, parent.columnHolder.size());
    if (!(0 <= columnIndex && columnIndex < validColumnCount)) return;
    if (cellFonts == null) {
      if (value == null) return;
      cellFonts = new Font [validColumnCount];
    }
    if (cellFonts [columnIndex] == value) return;
    if (cellFonts [columnIndex] != null && cellFonts [columnIndex].equals (value)) return;
    cellFonts [columnIndex] = value;
//    if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }
  
  /**
   * Sets the foreground color at the given column index in the receiver 
   * to the color specified by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param columnIndex the column index
   * @param value the new color (or null)
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
   * 
   */
  public void setForeground( int columnIndex, Color value ) {
    checkWidget();
    int validColumnCount = Math.max( 1, parent.columnHolder.size() );
    if( !( 0 <= columnIndex && columnIndex < validColumnCount ) )
      return;
    if( cellForegrounds == null ) {
      cellForegrounds = new Color[ validColumnCount ];
    }
    if( cellForegrounds[ columnIndex ] == value )
      return;
    if( cellForegrounds[ columnIndex ] != null
        && cellForegrounds[ columnIndex ].equals( value ) )
      return;
    cellForegrounds[ columnIndex ] = value;
    // if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }

  /**
   * Sets the font that the receiver will use to paint textual information for
   * this item to the font specified by the argument, or to the default font for
   * that kind of control if the argument is null.
   * 
   * @param font the new font (or null)
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
   *                disposed</li>
   *                </ul>
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
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

  Font getFont( boolean checkData ) {
//    if( checkData && !parent.checkData( this, true ) )
//      error( SWT.ERROR_WIDGET_DISPOSED );
    if( font != null )
      return font;
    return parent.getFont();
  }
  
  /**
   * Sets the receiver's background color to the color specified
   * by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param value the new color (or null)
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
   * @since 1.0
   *
   */
  public Color getBackground() {
    checkWidget();
    if( isDisposed() ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    if( background != null ) {
      return background;
    }
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
   * @since 1.0
   *
   */
  public Color getForeground () {
    checkWidget ();
    if( isDisposed() ) {
      error( SWT.ERROR_WIDGET_DISPOSED );
    }
    if( foreground != null ) {
      return foreground;
    }
    return parent.getForeground ();
  }

  /**
   * Sets the receiver's foreground color to the color specified
   * by the argument, or to the default system color for the item
   * if the argument is null.
   *
   * @param value the new color (or null)
   *
   * @since 1.0
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
   * @param value the new grayed state of the checkbox
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setGrayed( final boolean value ) {
    checkWidget();
    if( ( parent.getStyle() & SWT.CHECK ) == 0 ) {
      return;
    }
    if( grayed == value ) {
      return;
    }
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

  /**
   * Returns the text stored at the given column index in the receiver,
   * or empty string if the text has not been set.
   *
   * @param columnIndex the column index
   * @return the text stored at the given column index in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public String getText (int columnIndex) {
      checkWidget ();
      return getText (columnIndex, true);
  }
  
  /**
   * Returns the receiver's text, which will be an empty
   * string if it has never been set.
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
    if(!isMaterialized()) {
      parent.checkData( this, this.index );
    }
    return super.getText();
  }
  
  String getText (int columnIndex, boolean checkData) {
    if(checkData && !isMaterialized()) parent.checkData( this, this.index );
    int validColumnCount = Math.max (1, parent.columnHolder.size());
    if (!(0 <= columnIndex && columnIndex < validColumnCount)) return "";   //$NON-NLS-1$
    if (columnIndex == 0) return super.getText ();  /* super is intentional here */
    if (texts [columnIndex] == null) return ""; //$NON-NLS-1$
    return texts [columnIndex];
  }
  
  /**
   * Sets the text for multiple columns in the tree. 
   * 
   * @param value the array of new strings
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setText (String[] value) {
      checkWidget ();
      if (value == null) error (SWT.ERROR_NULL_ARGUMENT);
      // TODO make a smarter implementation of this
      for (int i = 0; i < value.length; i++) {
          if (value [i] != null) setText (i, value [i]);
      }
  }
  
  /**
   * Sets the receiver's text at a column
   *
   * @param columnIndex the column index
   * @param value the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setText (int columnIndex, String value) {
      checkWidget ();
      if (value == null) error (SWT.ERROR_NULL_ARGUMENT);
      int validColumnCount = Math.max (1, parent.columnHolder.size());
      if (!(0 <= columnIndex && columnIndex < validColumnCount)) return;
      if (value.equals (getText (columnIndex, false))) return;
      if (columnIndex == 0) {
          super.setText (value);
      } else {
          texts [columnIndex] = value;        
      }
      if ((parent.style & SWT.VIRTUAL) != 0) markMaterialized();
  }
  
  /**
   * Sets the receiver's text.
   *
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
  public void setText( final String text ) {
    super.setText( text );
    if ((parent.style & SWT.VIRTUAL) != 0) markMaterialized();
  }
  
  /**
   * Returns the image stored at the given column index in the receiver,
   * or null if the image has not been set or if the column does not exist.
   *
   * @param columnIndex the column index
   * @return the image stored at the given column index in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Image getImage (int columnIndex) {
      checkWidget ();
      return getImage (columnIndex, true);
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
//    parent.checkData( this, parent.indexOf( this ) );
    Rectangle result;
    Image image = getImage( index );
    if( image != null ) {
      result = image.getBounds();
    } else {
      result = new Rectangle( 0, 0, 0, 0 );
    }
    return result;
  }
  
  Image getImage (int columnIndex, boolean checkData) {
//      if (checkData && !parent.checkData (this, true)) error (SWT.ERROR_WIDGET_DISPOSED);
      int validColumnCount = Math.max (1, parent.columnHolder.size());
      if (!(0 <= columnIndex && columnIndex < validColumnCount)) return null;
      if (columnIndex == 0) return super.getImage (); /* super is intentional here */
      return images [columnIndex];
  }
  
  /*
   * Returns the receiver's ideal width for the specified columnIndex.
   */
  int getPreferredWidth ( int columnIndex, boolean checkData ) {
    int width = 0;
    width += TextSizeDetermination.stringExtent( parent.getFont(), getText( columnIndex, checkData ) ).x;
    int orderedIndex = parent.columnHolder.size() == 0 ? 0 : ((TreeColumn)parent.columnHolder.getItem( columnIndex )).getOrderIndex ();
    if (orderedIndex == 0) {
      width += 19; // TODO find proper solution
      width += 3; //Tree.MARGIN_IMAGE;
  
      //Image image = getImage (columnIndex, false);
      Image image = getImage ();
      if (image != null) {
          width += image.getBounds ().width;
          width += 3; //Tree.MARGIN_IMAGE;
      }
    }
    return width;
  }
  
  void clear() {
    // TODO: [bm] revisit when columns are available
    checked = grayed = false;
    texts = null;
//    textWidths = new int[ 1 ];
//    fontHeight = 0;
//    fontHeights = null;
    images = null;
    foreground = background = null;
//    displayTexts = null;
    cellForegrounds = cellBackgrounds = null;
    font = null;
    cellFonts = null;
    setText( "" );
    setImage( (Image) null );

    int columnCount = parent.columnHolder.size();
    if ( columnCount > 0 ) {
//      displayTexts = new String[ columnCount ];
      if ( columnCount > 1 ) {
        texts = new String[ columnCount ];
//        textWidths = new int[ columnCount ];
        images = new Image[ columnCount ];
      }
    }
  }

  /**
   * Clears the item at the given zero-relative index in the receiver.
   * The text, icon and other attributes of the item are set to the default
   * value.  If the tree was created with the <code>SWT.VIRTUAL</code> style,
   * these attributes are requested again as needed.
   *
   * @param index the index of the item to clear
   * @param recursive <code>true</code> if all child items of the indexed item should be
   * cleared recursively, and <code>false</code> otherwise
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 1.0
   */
  public void clear( int index, boolean recursive ) {
    checkWidget();
    if( !( 0 <= index && index < itemHolder.size() ) )
      error( SWT.ERROR_INVALID_RANGE );
    TreeItem item = ( TreeItem )itemHolder.getItem( index );
    /* clear the item(s) */
    item.clear();
    if( recursive ) {
      item.clearAll( true, false );
    }
  }
  
  /*
   * Updates internal structures in the receiver and its child items to handle the creation of a new column.
   */
  void addColumn( TreeColumn column ) {
    int index = column.getIndex();
    int columnCount = parent.columnHolder.size();
    if( columnCount > 1 ) {
      if( columnCount == 2 ) {
        texts = new String[ 2 ];
      } else {
        String[] newTexts = new String[ columnCount ];
        System.arraycopy( texts, 0, newTexts, 0, index );
        System.arraycopy( texts, index, newTexts, index + 1, columnCount
                                                             - index
                                                             - 1 );
        texts = newTexts;
      }
      if( index == 0 ) {
        texts[ 1 ] = text;
        text = ""; //$NON-NLS-1$
      }
      
      if( columnCount == 2 ) {
        images = new Image[ 2 ];
      } else {
        Image[] newImages = new Image[ columnCount ];
        System.arraycopy( images, 0, newImages, 0, index );
        System.arraycopy( images, index, newImages, index + 1, columnCount
                                                               - index
                                                               - 1 );
        images = newImages;
      }
      if( index == 0 ) {
        images[ 1 ] = image;
        image = null;
      }
    }
    /* notify all child items as well */
    for( int i = 0; i < itemHolder.size(); i++ ) {
      TreeItem child = ( TreeItem )itemHolder.getItem( i );
      child.addColumn( column );
    }
  }
  
  /**
   * Sets the receiver's image at a column.
   *
   * @param columnIndex the column index
   * @param value the new image
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setImage (int columnIndex, Image value) {
      checkWidget ();

      TreeColumn[] columns = ( TreeColumn[] )parent.columnHolder.getItems();
      int validColumnCount = Math.max (1, columns.length);
      if (!(0 <= columnIndex && columnIndex < validColumnCount)) return;
      Image image = getImage (columnIndex, false);
      if (value == image) return;
      if (value != null && value.equals (image)) return;
      if (columnIndex == 0) {
          super.setImage (value);
      } else {
          images [columnIndex] = value;
      }
//      if ((parent.style & SWT.VIRTUAL) != 0) cached = true;
  }
  
  /**
   * Sets the image for multiple columns in the tree. 
   * 
   * @param value the array of new images
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if one of the images has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setImage (Image[] value) {
      checkWidget ();
      if (value == null) error (SWT.ERROR_NULL_ARGUMENT);
      
      // TODO make a smarter implementation of this
      for (int i = 0; i < value.length; i++) {
          if (value [i] != null) setImage (i, value [i]);
      }
  }
  
  /**
   * Clears all the items in the receiver. The text, icon and other
   * attributes of the items are set to their default values. If the
   * tree was created with the <code>SWT.VIRTUAL</code> style, these
   * attributes are requested again as needed.
   *
   * @param recursive <code>true</code> if all child items should be cleared
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
   * @since 1.0
   */
  public void clearAll( final boolean recursive) {
    clearAll( recursive, true );
  }

  void clearAll( final boolean recursive, final boolean doVisualUpdate ) {
    checkWidget ();
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
  
  /**
   * Sets the number of child items contained in the receiver.
   * 
   * @param count the number of items
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   * @since 1.0
   */
  public void setItemCount( int count ) {
    checkWidget();
    parent.setItemCount( count, this );
  }


  /* package */ void markMaterialized() {
//    Widget parentWidget;
//    if( parentItem != null ) {
//      parentWidget = parentItem;
//    } else {
//      parentWidget = parent;
//    }
//    if((parent.style & SWT.VIRTUAL) != 0) {
//      ItemHolder.insertItem( parentWidget, this, index );
//    }
    setData("materialized", Boolean.TRUE );
  }
  
  /* package */ boolean isMaterialized() {
    boolean result = true;
    if((parent.getStyle() & SWT.VIRTUAL) != 0) {
      result = getData("materialized") != null;
    }
    return result;
  }
  
  /////////////////////////////////
  // Methods to dispose of the item

  final void releaseChildren() {
    TreeItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
  }

  final void releaseParent() {
    if( parentItem != null ) {
      ItemHolder.removeItem( parentItem, this );
    } else {
      ItemHolder.removeItem( parent, this );
    }
    parent.removeFromSelection( this );
  }

  final void releaseWidget() {
    // do nothing
  }

  /* package */ int getInnerHeight() {
    int innerHeight = 0;
    for( int i = 0; i < itemHolder.size(); i++ ) {
      TreeItem item = ( TreeItem )itemHolder.getItem( i );
      if(item.getExpanded()) {
        innerHeight = item.getInnerHeight();
      }
    }
    innerHeight += getItemCount()*16;
    return innerHeight;
  }
}
