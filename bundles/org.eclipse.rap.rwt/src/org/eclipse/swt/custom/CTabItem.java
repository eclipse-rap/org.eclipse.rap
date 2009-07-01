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

package org.eclipse.swt.custom;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

/**
 * Instances of this class represent a selectable user interface object
 * that represent a page in a notebook widget.
 * 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.CLOSE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class CTabItem extends Item {

  static final int TOP_MARGIN = 2;
  static final int BOTTOM_MARGIN = 2;
  static final int LEFT_MARGIN = 4;
  static final int RIGHT_MARGIN = 4;
  static final int INTERNAL_SPACING = 4;
  
  static final String ELLIPSIS = "..."; 
  
  private final IWidgetFontAdapter widgetFontAdapter;
  final CTabFolder parent;
  private Control control;
  private String toolTipText;
  private Font font;
  // TODO [rh] shortenedText is not yet calculated
  String shortenedText;
  int shortenedTextWidth;
  
  boolean showing = false;
  final boolean showClose;
  int x;
  int y;
  int width;
  int height;
  Rectangle closeRect = new Rectangle( 0, 0, 0, 0 );
  
  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>CTabFolder</code>) and a style value
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
   * @param parent a CTabFolder which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#getStyle()
   */
  public CTabItem( final CTabFolder parent, final int style ) {
    this( parent, style, checkNull( parent ).getItemCount() );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>CTabFolder</code>), a style value
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
   * @param parent a CTabFolder which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#getStyle()
   */
  public CTabItem( final CTabFolder parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    showClose = ( style & SWT.CLOSE ) != 0;
    this.parent = parent;
    widgetFontAdapter = new IWidgetFontAdapter() {
      public Font getUserFont() {
        return font;
      }
    };
    parent.createItem( this, index );
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IWidgetFontAdapter.class ) {
      result = widgetFontAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /**
   * Returns the receiver's parent, which must be a <code>CTabFolder</code>.
   *
   * @return the receiver's parent
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public CTabFolder getParent() {
    checkWidget();
    return parent;
  }
  
  /////////////////////////////////////////
  // Getter/setter for principal properties
  
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !text.equals( getText() ) ) {
      super.setText( text );
      shortenedText = null;
      shortenedTextWidth = 0;
      parent.updateItems();
    }
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    if( image != getImage() ) {
      super.setImage( image );
      if( !parent.updateTabHeight( false ) ) {
        parent.updateItems();
      }
    }
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * for this item to the font specified by the argument, or to the default font
   * for that kind of control if the argument is null.
   * 
   * <p>Changing font works, but tab size is not adjusted accordingly.</p>
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
    if( font != this.font ) {
      this.font = font;
      if( !parent.updateTabHeight( false ) ) {
        parent.updateItems();
      }
    }
  }

  /**
   * Returns the font that the receiver will use to paint textual information.
   *
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   *  @since 1.0
   */
  public Font getFont() {
    checkWidget();
    Font result = font;
    if( font == null ) {
      result = parent.getFont();
    }
    return result;
  }

  /**
  * Gets the control that is displayed in the content area of the tab item.
  *
  * @return the control
  *
  * @exception SWTException <ul>
  *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
  *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
  * </ul>
  */
  public Control getControl() {
    checkWidget();
    return control;
  }

  /**
   * Sets the control that is used to fill the client area of
   * the tab folder when the user selects the tab item.
   *
   * @param control the new control (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        SWT.error( SWT.ERROR_INVALID_PARENT );
      }
    }
    if( this.control != null && !this.control.isDisposed() ) {
      this.control.setVisible( false );
    }
    this.control = control;
    if( this.control != null ) {
      int index = parent.indexOf( this );
      if( index == parent.getSelectionIndex() ) {
        this.control.setBounds( parent.getClientArea() );
        this.control.setVisible( true );
      } else {
        this.control.setVisible( false );
      }
    }
  }
  
  /**
   * Sets the receiver's tool tip text to the argument, which
   * may be null indicating that no tool tip text should be shown.
   *
   * @param toolTipText the new tool tip text (or null)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setToolTipText( final String toolTipText ) {
    checkWidget();
    this.toolTipText = toolTipText;
  }
  
  /**
   * Returns the receiver's tool tip text, or null if it has
   * not been set.
   *
   * @return the receiver's tool tip text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getToolTipText () {
    checkWidget();
    String result = toolTipText;
    if( result == null && shortenedText != null ) {
      String text = getText();
      if( !shortenedText.equals( text ) ) {
        result = text;
      }
    }
    return result;
  }
  
  ////////////////////////
  // Bounds and visibility
  
  /**
  * Returns <code>true</code> if the item will be rendered in the visible area of the CTabFolder. Returns false otherwise.
  * 
  *  @return <code>true</code> if the item will be rendered in the visible area of the CTabFolder. Returns false otherwise.
  * 
  *  @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
  * @since 1.0
  */
  public boolean isShowing() {
    checkWidget();
    return showing;
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding column rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getBounds() {
    checkWidget();
    return new Rectangle( x, y, width, height );
  }
  
  ///////////////////
  // Widget overrides

  public void dispose() {
    if( !isDisposed() ) {
      // if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
      parent.destroyItem( this );
      super.dispose();
    } 
  }
  
  ///////////////////////////////////////////////////////////////////////
  // Helping methods used by CTabFolder to control item size and location

  int preferredHeight() {
    Image image = getImage();
    int h = ( image == null ) ? 0 : image.getBounds().height;
    String text = getText();
    // TODO [fappel]: check gc height calculation alternative 
    if( font == null ) {
      Font parentFont = getParent().getFont();
      h = Math.max( h, TextSizeDetermination.textExtent( parentFont, text, 0 ).y );
    } else {
      h = Math.max( h, TextSizeDetermination.textExtent( font, text, 0 ).y );
    }
    return h + TOP_MARGIN + BOTTOM_MARGIN;
  }
  
  int preferredWidth( final boolean isSelected, final boolean minimum ) {
    // NOTE: preferred width does not include the "dead space" caused
    // by the curve.
    if (isDisposed()) return 0;
    int w = 0;
    Image image = getImage();
    if (image != null && (isSelected || parent.showUnselectedImage)) {
      w += image.getBounds().width;
    }
    String text = null;
    if (minimum) {
      int minChars = parent.minChars;
      text = minChars == 0 ? null : getText();
      if (text != null && text.length() > minChars) {
        if (useEllipses()) {
          int end = minChars < ELLIPSIS.length() + 1 ? minChars : minChars - ELLIPSIS.length();
          text = text.substring(0, end);
          if (minChars > ELLIPSIS.length() + 1) text += ELLIPSIS;
        } else {
          int end = minChars;
          text = text.substring(0, end);
        }
      }
    } else {
      text = getText();
    }
    if (text != null) {
      if (w > 0) w += INTERNAL_SPACING;
      if (font == null) {
//        w += gc.textExtent(text, FLAGS).x;
        w += TextSizeDetermination.stringExtent( getFont(), text ).x;
      } else {
//        Font gcFont = gc.getFont();
//        gc.setFont(font);
//        w += gc.textExtent(text, FLAGS).x;
//        gc.setFont(gcFont);
        w += TextSizeDetermination.stringExtent( getFont(), text ).x;
      }
    }
    if (parent.showClose || showClose) {
      if (isSelected || parent.showUnselectedClose) {
        if (w > 0) w += INTERNAL_SPACING;
        w += CTabFolder.BUTTON_SIZE;
      }
    }
    return w + LEFT_MARGIN + RIGHT_MARGIN;
  }

  /*
   * Return whether to use ellipses or just truncate labels
   */
  boolean useEllipses() {
    return parent.simple;
  }

  /* (intentionally non-JavaDoc'ed)
   * Return whether the image (if any) should be shown or omitted if there is 
   * not enough space. The code is copied from the drawSelected and 
   * drawUnselected methods ins SWT.
   */
  boolean showImage() {
    boolean result = false;
    if( parent.getSelection() == this ) {
      int rightEdge = Math.min (x + width, parent.getRightItemEdge());
      // draw Image
      int xDraw = x + LEFT_MARGIN;
      if (parent.single && (parent.showClose || showClose)) {
        xDraw += CTabFolder.BUTTON_SIZE;
      } 
      Image image = getImage();
      if (image != null) {
        Rectangle imageBounds = image.getBounds();
        // only draw image if it won't overlap with close button
        int maxImageWidth = rightEdge - xDraw - RIGHT_MARGIN;
        if (!parent.single && closeRect.width > 0) {
          maxImageWidth -= closeRect.width + INTERNAL_SPACING;
        }
        if (imageBounds.width < maxImageWidth) {
          result = true;
        }
      }
    } else {
      int xDraw = x + LEFT_MARGIN;
      Image image = getImage();
      if (image != null && parent.showUnselectedImage) {
        Rectangle imageBounds = image.getBounds();
        // only draw image if it won't overlap with close button
        int maxImageWidth = x + width - xDraw - RIGHT_MARGIN;
        if (parent.showUnselectedClose && (parent.showClose || showClose)) {
          maxImageWidth -= closeRect.width + INTERNAL_SPACING;
        }
        if (imageBounds.width < maxImageWidth) {
          result = true;
        }
      }
    }
    return result;
  }
  
  /* (intentionally non-JavaDoc'ed)
   * The code of this methods is taken from SWT's CTabItem#drawSelected() and 
   * modified to suit the specifics of RWT.
   */
  String getShortenedText() {
    if( shortenedText == null ) {
      int xDraw = x + LEFT_MARGIN;
      if( showImage() ) {
        Rectangle imageBounds = getImage().getBounds();
        xDraw += imageBounds.width + INTERNAL_SPACING;
      }
      int rightEdge = Math.min( x + width, parent.getRightItemEdge() );
      int textWidth = rightEdge - xDraw - RIGHT_MARGIN;
      if( !parent.single && closeRect.width > 0 ) {
        textWidth -= closeRect.width + INTERNAL_SPACING;
      }
      if( textWidth > 0 ) {
        Font itemFont = font == null ? parent.getFont() : font;
        if( shortenedText == null || shortenedTextWidth != textWidth ) {
          shortenedText = shortenText( itemFont, getText(), textWidth );
          shortenedTextWidth = textWidth;
        }
      }
    }
    return shortenedText == null ? "" : shortenedText;
  }
  
  String shortenText( final Font font, final String text, final int width ) {
    return useEllipses()
      ? shortenText( font, text, width, ELLIPSIS )
      : shortenText( font, text, width, "" );
  }

  static String shortenText( final Font font, 
                             String text, 
                             final int width, 
                             final String ellipses ) 
  {
    if( Graphics.stringExtent( font, text ).x <= width ) {
      return text;
    } 
    int ellipseWidth = Graphics.stringExtent( font, ellipses ).x;
    int length = text.length();
    int end = length - 1;
    while( end > 0 ) {
      text = text.substring( 0, end );
      int l = Graphics.stringExtent( font, text ).x;
      if( l + ellipseWidth <= width ) {
        return text + ellipses;
      }
      end--;
    }
    return text.substring( 0, 1 );
  }
  
  //////////////////
  // Helping methods
  
  private static CTabFolder checkNull( final CTabFolder parent ) {
    if( parent == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return parent;
  }

  private static int checkStyle( final int style ) {
    int result = SWT.NONE;
    if( ( style & SWT.CLOSE ) != 0 ) {
      result = SWT.CLOSE;
    }
    return result;
  }
}
