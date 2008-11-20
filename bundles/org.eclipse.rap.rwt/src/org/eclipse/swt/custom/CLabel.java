/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A Label which supports aligned text and/or an image and different border styles.
 * <p>
 * If there is not enough space a CLabel uses the following strategy to fit the 
 * information into the available space:
 * <pre>
 * 		ignores the indent in left align mode
 * 		ignores the image and the gap
 * 		shortens the text by replacing the center portion of the label with an ellipsis
 * 		shortens the text by removing the center portion of the label
 * </pre>
 * <p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>LEFT, RIGHT, CENTER, SHADOW_IN, SHADOW_OUT, SHADOW_NONE</dd>
 * <dt><b>Events:</b>
 * <dd></dd>
 * </dl>
 * 
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @since 1.0
 */
public class CLabel extends Canvas {

	/** Gap between icon and text */
  private static final int GAP = 5;
  /** Left and right margins */
  private static final int INDENT = 3;
  /** a string inserted in the middle of text that has been shortened */
//  private static final String ELLIPSIS = "..."; //$NON-NLS-1$ // could use the ellipsis glyph on some platforms "\u2026"
  /** the alignment. Either CENTER, RIGHT, LEFT. Default is LEFT */
  private int align = SWT.LEFT;
  private int hIndent = INDENT;
  private int vIndent = INDENT;
  /** the current text */
  private String text;
  /** the current icon */
  private Image image;
  // The tooltip is used for two purposes - the application can set
  // a tooltip or the tooltip can be used to display the full text when the
  // the text has been truncated due to the label being too short.
  // The appToolTip stores the tooltip set by the application.
  // Control.tooltiptext
  // contains whatever tooltip is currently being displayed.
  private String appToolTipText;

  private Image backgroundImage;
  private Color background;
	
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
   * @see SWT#LEFT
   * @see SWT#RIGHT
   * @see SWT#CENTER
   * @see SWT#SHADOW_IN
   * @see SWT#SHADOW_OUT
   * @see SWT#SHADOW_NONE
   * @see #getStyle()
   */
  public CLabel( Composite parent, int style ) {
    super( parent, checkStyle( style ) );
    int result = style;
    if ( (style & (SWT.CENTER | SWT.RIGHT)) == 0 )
      result |= SWT.LEFT;
    if ( (result & SWT.CENTER) != 0 )
      align = SWT.CENTER;
    if ( (result & SWT.RIGHT) != 0 )
      align = SWT.RIGHT;
    if ( (result & SWT.LEFT) != 0 )
      align = SWT.LEFT;

    addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        onDispose( event );
      }
    } );

  }

  /**
   * Check the style bits to ensure that no invalid styles are applied.
   */
  private static int checkStyle( int style ) {
    int result = style;
    if ( (style & SWT.BORDER) != 0 )
      result |= SWT.SHADOW_IN;
    int mask = SWT.SHADOW_IN | SWT.SHADOW_OUT | SWT.SHADOW_NONE | SWT.LEFT_TO_RIGHT /*| SWT.RIGHT_TO_LEFT*/;
    result = style & mask;
    return result |= SWT.NO_FOCUS;
  }

  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    Point e = getTotalSize( image, text );
    if ( wHint == SWT.DEFAULT ) {
      e.x += 2 * hIndent;
    } else {
      e.x = wHint;
    }
    if ( hHint == SWT.DEFAULT ) {
      e.y += 2 * vIndent;
    } else {
      e.y = hHint;
    }
    return e;
  }
  
  /**
   * Returns the alignment.
   * The alignment style (LEFT, CENTER or RIGHT) is returned.
   * 
   * @return SWT.LEFT, SWT.RIGHT or SWT.CENTER
   */
  public int getAlignment() {
    checkWidget();
    return align;
  }

  /**
   * Return the CLabel's image or <code>null</code>.
   * 
   * @return the image of the label or null
   */
  public Image getImage() {
    checkWidget();
    return image;
  }

  /**
   * Compute the minimum size.
   */
  private Point getTotalSize( Image image, String text ) {
    Point size = new Point( 0, 0 );

    if ( image != null ) {
      Rectangle r = image.getBounds();
      size.x += r.width;
      size.y += r.height;
    }

    if ( text != null && text.length() > 0 ) {
      Point e = TextSizeDetermination.stringExtent( getFont(), text );
      size.x += e.x;
      size.y = Math.max( size.y, e.y );
      if ( image != null )
        size.x += GAP;
    } else {
      int charHeight = TextSizeDetermination.getCharHeight( getFont() );
      size.y = Math.max( size.y, charHeight );
    }

    return size;
  }

  public int getStyle() {
    int style = super.getStyle();
    switch (align) {
    case SWT.RIGHT:
      style |= SWT.RIGHT;
      break;
    case SWT.CENTER:
      style |= SWT.CENTER;
      break;
    case SWT.LEFT:
      style |= SWT.LEFT;
      break;
    }
    return style;
  }

  /**
   * Return the Label's text.
   * 
   * @return the text of the label or null
   */
  public String getText() {
    checkWidget();
    return text;
  }

  public String getToolTipText() {
    checkWidget();
    return appToolTipText;
  }

  void onDispose( DisposeEvent event ) {
    backgroundImage = null;
    text = null;
    image = null;
    appToolTipText = null;
  }

  /**
   * Set the alignment of the CLabel.
   * Use the values LEFT, CENTER and RIGHT to align image and text within the available space.
   * 
   * @param align the alignment style of LEFT, RIGHT or CENTER
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the value of align is not one of SWT.LEFT, SWT.RIGHT or SWT.CENTER</li>
   * </ul>
   */
  public void setAlignment( int align ) {
    checkWidget();
    if ( align != SWT.LEFT && align != SWT.RIGHT && align != SWT.CENTER ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if ( this.align != align ) {
      this.align = align;
    }
  }

  public void setBackground( Color color ) {
    super.setBackground( color );
    // Are these settings the same as before?
    if ( backgroundImage == null ) {
      if ( color == null ) {
        if ( background == null )
          return;
      } else {
        if ( color.equals( background ) )
          return;
      }
    }
    background = color;
    backgroundImage = null;
  }

  /**
   * Set the image to be drawn in the background of the label.
   * 
   * @param image the image to be drawn in the background
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO: [bm] review
  public void setBackground( Image image ) {
    checkWidget();
    if ( image == backgroundImage )
      return;
    backgroundImage = image;

  }

  public void setFont( Font font ) {
    super.setFont( font );
  }
  
  /**
   * Set the label's Image.
   * The value <code>null</code> clears it.
   * 
   * @param image the image to be displayed in the label or null
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setImage( Image image ) {
    checkWidget();
    if ( image != this.image ) {
      this.image = image;
    }
  }
  
  /**
   * Set the label's text.
   * The value <code>null</code> clears it.
   * 
   * @param text the text to be displayed in the label or null
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( String text ) {
    checkWidget();
    if ( text == null )
      text = ""; //$NON-NLS-1$
    if ( !text.equals( this.text ) ) {
      this.text = text;
    }
  }

  public void setToolTipText( String string ) {
    super.setToolTipText( string );
    appToolTipText = super.getToolTipText();
  }
  
/**
 * Shorten the given text <code>t</code> so that its length doesn't exceed
 * the given width. The default implementation replaces characters in the
 * center of the original string with an ellipsis ("...").
 * Override if you need a different strategy.
 * 
 * @param gc the gc to use for text measurement
 * @param t the text to shorten
 * @param width the width to shorten the text to, in pixels
 * @return the shortened text
 */
  // TODO: [bm] review
//protected String shortenText(Object gc, String t, int width) {
//	if (t == null) return null;
//	int w = FontSizeEstimation.stringExtent( ELLIPSIS, getFont() ).x;
//	if (width<=w) return t;
//	int l = t.length();
//	int max = l/2;
//	int min = 0;
//	int mid = (max+min)/2 - 1;
//	if (mid <= 0) return t;
//	while (min < mid && mid < max) {
//		String s1 = t.substring(0, mid);
//		String s2 = t.substring(l-mid, l);
//		int l1 = FontSizeEstimation.stringExtent( s1, getFont() ).x;
//		int l2 = FontSizeEstimation.stringExtent( s2, getFont() ).x;
//		if (l1+w+l2 > width) {
//			max = mid;			
//			mid = (max+min)/2;
//		} else if (l1+w+l2 < width) {
//			min = mid;
//			mid = (max+min)/2;
//		} else {
//			min = max;
//		}
//	}
//	if (mid == 0) return t;
// 	return t.substring(0, mid)+ELLIPSIS+t.substring(l-mid, l);
//}
//
//private String[] splitString(String text) {
//    String[] lines = new String[1];
//    int start = 0, pos;
//    do {
//        pos = text.indexOf('\n', start);
//        if (pos == -1) {
//        	lines[lines.length - 1] = text.substring(start);
//        } else {
//            boolean crlf = (pos > 0) && (text.charAt(pos - 1) == '\r');
//            lines[lines.length - 1] = text.substring(start, pos - (crlf ? 1 : 0));
//            start = pos + 1;
//            String[] newLines = new String[lines.length+1];
//            System.arraycopy(lines, 0, newLines, 0, lines.length);
//       		lines = newLines;
//        }
//    } while (pos != -1);
//    return lines;
//}
}
