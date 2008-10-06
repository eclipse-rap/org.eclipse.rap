/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonThemeAdapter;

/**
 * Instances of this class represent a selectable user interface object that
 * issues notification when pressed and released.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd><!--ARROW, -->CHECK, PUSH, RADIO, TOGGLE, FLAT</dd>
 * <dd><!--UP, DOWN, -->LEFT, RIGHT, CENTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles <!--ARROW, -->CHECK, PUSH, RADIO, and TOGGLE
 * may be specified.
 * </p><p>
 * Note: Only one of the styles LEFT, RIGHT, and CENTER may be specified.
 * </p><!--<p>
 * Note: Only one of the styles UP, DOWN, LEFT, and RIGHT may be specified
 * when the ARROW style is specified.
 * </p>--><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
// TODO [rst] Remove comments from javadoc when fully implemented
public class Button extends Control {

  private String text = "";
  private boolean selected;
  private Image image;
  private boolean isDefault;

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
   * <!--@see SWT#ARROW-->
   * @see SWT#CHECK
   * @see SWT#PUSH
   * @see SWT#RADIO
   * @see SWT#TOGGLE
   * @see SWT#FLAT
   * @see SWT#LEFT
   * @see SWT#RIGHT
   * @see SWT#CENTER
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  // TODO [rst] Remove comments from javadoc when fully implemented
  public Button( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  void initState() {
    if( ( style & ( SWT.PUSH | SWT.TOGGLE ) ) == 0 ) {
      state |= THEME_BACKGROUND;
    }
  }

  ////////////////
  // Getter/setter

  /**
   * Sets the receiver's text.
   * <p>
   * This method sets the button label.  The label may include
   * the mnemonic character but must not contain line delimiters.
   * </p>
   * <p>
   * Caution: Mnemonics are not implemented yet.
   *
   * Mnemonics are indicated by an '&amp;' that causes the next
   * character to be the mnemonic.  When the user presses a
   * key sequence that matches the mnemonic, a selection
   * event occurs. On most platforms, the mnemonic appears
   * underlined but may be emphasised in a platform specific
   * manner.  The mnemonic indicator character '&amp;' can be
   * escaped by doubling it in the string, causing a single
   * '&amp;' to be displayed.
   * </p>
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
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & SWT.ARROW ) == 0 ) {
      this.text = text;
    }
  }

  /**
   * Returns the receiver's text, which will be an empty
   * string if it has never been set<!-- or if the receiver is
   * an <code>ARROW</code> button-->.
   *
   * @return the receiver's text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rst] Remove comments from javadoc when fully implemented
  public String getText() {
    checkWidget();
    return text;
  }

  /**
   * Returns <code>true</code> if the receiver is selected,
   * and false otherwise.
   * <p>
   * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
   * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
   * it is selected when it is pushed in. If the receiver is of any other type,
   * this method returns false.
   *
   * @return the selection state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getSelection() {
    checkWidget();
    boolean result = false;
    if( ( style & ( SWT.CHECK | SWT.RADIO | SWT.TOGGLE ) ) != 0 ) {
      result = selected;
    }
    return result;
  }

  /**
   * Sets the selection state of the receiver, if it is of type <code>CHECK</code>,
   * <code>RADIO</code>, or <code>TOGGLE</code>.
   *
   * <p>
   * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
   * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
   * it is selected when it is pushed in.
   *
   * @param selected the new selection state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final boolean selected ) {
    checkWidget();
    if( ( style & ( SWT.CHECK | SWT.RADIO | SWT.TOGGLE ) ) != 0 ) {
      this.selected = selected;
    }
  }

  /**
   * Returns the receiver's image if it has one, or null
   * if it does not.
   *
   * @return the receiver's image
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Image getImage() {
    checkWidget();
    return image;
  }

  /**
   * Sets the receiver's image to the argument, which may be
   * <code>null</code> indicating that no image should be displayed.
   *
   * @param image the image to display on the receiver (may be <code>null</code>)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * <hr/>
   * Not implemented for CHECK and RADIO.
   */
  // TODO [rh] implement handling of images for CHECK and RADIO as SWT does
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & SWT.ARROW ) == 0 ) {
      this.image = image;
    }
  }

  /**
   * Returns a value which describes the position of the
   * text or image in the receiver. The value will be one of
   * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
   * <!--
   * unless the receiver is an <code>ARROW</code> button, in
   * which case, the alignment will indicate the direction of
   * the arrow (one of <code>LEFT</code>, <code>RIGHT</code>,
   * <code>UP</code> or <code>DOWN</code>)
   * -->.
   *
   * @return the alignment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rst] Remove comments from javadoc when fully implemented
  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & SWT.ARROW ) != 0 ) {
      if( ( style & SWT.UP ) != 0 ) {
        result = SWT.UP;
      } else if( ( style & SWT.DOWN ) != 0 ) {
        result = SWT.DOWN;
      } else if( ( style & SWT.LEFT ) != 0 ) {
        result = SWT.LEFT;
      } else if( ( style & SWT.RIGHT ) != 0 ) {
        result = SWT.RIGHT;
      } else {
        result = SWT.UP;
      }
    } else {
      if( ( style & SWT.LEFT ) != 0 ) {
        result = SWT.LEFT;
      } else if( ( style & SWT.CENTER ) != 0 ) {
        result = SWT.CENTER;
      } else if( ( style & SWT.RIGHT ) != 0 ) {
        result = SWT.RIGHT;
      } else {
        result = SWT.LEFT;
      }
    }
    return result;
  }

  /**
   * Controls how text, images and arrows will be displayed
   * in the receiver. The argument should be one of
   * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
   * <!--
   * unless the receiver is an <code>ARROW</code> button, in
   * which case, the argument indicates the direction of
   * the arrow (one of <code>LEFT</code>, <code>RIGHT</code>,
   * <code>UP</code> or <code>DOWN</code>)
   * -->.
   *
   * @param alignment the new alignment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
   // TODO [rst] Remove comments from javadoc when fully implemented
  public void setAlignment( final int alignment ) {
    checkWidget();
    if( ( style & SWT.ARROW ) != 0 ) {
      if( ( style & ( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT ) ) != 0 ) {
        style &= ~( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT );
        style |= alignment & ( SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT );
      }
    } else if( ( alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER ) ) != 0 ) {
      style &= ~( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
      style |= alignment & ( SWT.LEFT | SWT.RIGHT | SWT.CENTER );
    }
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = 0;
    // TODO [rst] Image is currently ignored for CHECK and RADIO buttons,
    //            remove when this restriction disappers
    boolean imageIgnored = ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0;
    boolean hasImage = image != null && !imageIgnored;
    boolean hasText = text.length() > 0;
    if( hasImage ) {
      Rectangle imageBounds = image.getBounds ();
      width = imageBounds.width;
      height = imageBounds.height;
    }
    if( hasText ) {
      Point extent = TextSizeDetermination.stringExtent( getFont(), text );
      width += extent.x;
      height = Math.max( height, extent.y );
    }
    if( width == 0 ) {
      width = 1;
    }
    if( height == 0 ) {
      height = 10;
    }
    ButtonThemeAdapter themeAdapter = getThemeAdapter();
    if( hasText && hasImage ) {
      int spacing = themeAdapter.getSpacing( this );
      width += spacing;
    }
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      Point checkSize = themeAdapter.getCheckSize();
      width += checkSize.x + themeAdapter.getCheckSpacing();
      height = Math.max( height, checkSize.y );
    }
    Rectangle padding = themeAdapter.getPadding( this );
    width += padding.width;
    height += padding.height;
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  ///////////////////////////////////////
  // Listener registration/deregistration

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is selected, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the control is selected.
   * <code>widgetDefaultSelected</code> is not called.
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
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is selected.
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
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////////////
  // Default Button handling

  void setDefault( final boolean isDefault ) {
    this.isDefault = isDefault;
  }

  boolean getDefault() {
    return isDefault;
  }

  ///////////////////
  // Widget overrides

  boolean isTabGroup() {
    return true;
  }

  String getNameText() {
    return getText();
  }

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    int result = checkBits( style,
                            SWT.PUSH,
                            SWT.ARROW,
                            SWT.CHECK,
                            SWT.RADIO,
                            SWT.TOGGLE,
                            0 );
    if( ( result & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      result = checkBits( result, SWT.CENTER, SWT.LEFT, SWT.RIGHT, 0, 0, 0 );
    } else if( ( result & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      result = checkBits( result, SWT.LEFT, SWT.RIGHT, SWT.CENTER, 0, 0, 0 );
    } else if( ( result & SWT.ARROW ) != 0 ) {
      result |= SWT.NO_FOCUS;
      result = checkBits( result, SWT.UP, SWT.DOWN, SWT.LEFT, SWT.RIGHT, 0, 0 );
    }
    return result;
  }

  private static ButtonThemeAdapter getThemeAdapter() {
    ThemeManager themeManager = ThemeManager.getInstance();
    return ( ButtonThemeAdapter )themeManager.getThemeAdapter( Button.class );
  }
}
