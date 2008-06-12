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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.spinnerkit.SpinnerThemeAdapter;

/**
 * Instances of this class are selectable user interface
 * objects that allow the user to enter and modify numeric
 * values.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>READ_ONLY, WRAP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, Modify</dd>
 * </dl>
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @since 1.0
 */
// TODO cut/copy/past not implemented
// TODO SelectionListener: widgetSelected is fired whenever the value changes
public class Spinner extends Composite {

  private static final int UP_DOWN_MIN_HEIGHT = 16;
  private static final int UP_DOWN_WIDTH = 16;

  private int digits = 0;
  private int increment = 1;
  private int maximum = 100;
  private int minimum = 0;
  private int pageIncrement = 10;
  private int selection = 0;

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
   * @see SWT#READ_ONLY
   * @see SWT#WRAP
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Spinner( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  void initState() {
    state &= ~( /* CANVAS | */ THEME_BACKGROUND );
  }

  /**
   * Returns the number of decimal places used by the receiver.
   *
   * @return the digits
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] the qooxdoo Spinner widget does not provide decimal places
  public int getDigits () {
    checkWidget ();
    return digits;
  }

//  public void setDigits( final int value ) {
//    checkWidget();
//  }

  /////////////////////////////////////////
  // Methods to control range and increment

  /**
   * Returns the amount that the receiver's value will be
   * modified by when the up/down arrows are pressed.
   *
   * @return the increment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getIncrement () {
    checkWidget();
    return increment;
  }

  /**
   * Sets the amount that the receiver's value will be
   * modified by when the up/down arrows are pressed to
   * the argument, which must be at least one.
   *
   * @param value the new increment (must be greater than zero)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setIncrement( final int value ) {
    checkWidget();
    if( value >= 1 ) {
      increment = value;
    }
  }

  /**
   * Returns the minimum value which the receiver will allow.
   *
   * @return the minimum
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getMinimum () {
    checkWidget();
    return minimum;
  }

  /**
   * Sets the minimum value that the receiver will allow.  This new
   * value will be ignored if it is negative or is not less than the receiver's
   * current maximum value.  If the new minimum is applied then the receiver's
   * selection value will be adjusted if necessary to fall within its new range.
   *
   * @param value the new minimum, which must be nonnegative and less than the current maximum
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMinimum( final int value ) {
    checkWidget();
    if( value >= 0 && value <= maximum ) {
      minimum = value;
      if( selection < minimum ) {
        selection = minimum;
      }
    }
  }

  /**
   * Returns the maximum value which the receiver will allow.
   *
   * @return the maximum
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getMaximum () {
    checkWidget();
    return maximum;
  }

  /**
   * Sets the maximum value that the receiver will allow.  This new
   * value will be ignored if it is not greater than the receiver's current
   * minimum value.  If the new maximum is applied then the receiver's
   * selection value will be adjusted if necessary to fall within its new range.
   *
   * @param value the new maximum, which must be greater than the current minimum
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMaximum( final int value ) {
    checkWidget();
    if( value >= 0 && value >= minimum ) {
      maximum = value;
      if( selection > maximum ) {
        selection = maximum;
      }
    }
  }

  /**
   * Returns the amount that the receiver's position will be
   * modified by when the page up/down keys are pressed.
   *
   * @return the page increment
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getPageIncrement () {
    checkWidget();
    return pageIncrement;
  }

  /**
   * Sets the amount that the receiver's position will be
   * modified by when the page up/down keys are pressed
   * to the argument, which must be at least one.
   *
   * @param value the page increment (must be greater than zero)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setPageIncrement( final int value ) {
    checkWidget();
    if( value >= 1 ) {
      pageIncrement = value;
    }
  }

  /**
   * Returns the <em>selection</em>, which is the receiver's position.
   *
   * @return the selection
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getSelection () {
    checkWidget();
    return selection;
  }

  /**
   * Sets the <em>selection</em>, which is the receiver's
   * position, to the argument. If the argument is not within
   * the range specified by minimum and maximum, it will be
   * adjusted to fall within this range.
   *
   * @param value the new selection (must be zero or greater)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final int value ) {
    checkWidget();
    selection = Math.min( Math.max( minimum, value ), maximum );
    ModifyEvent modifyEvent = new ModifyEvent( this );
    modifyEvent.processEvent();
  }

  /**
   * Sets the receiver's selection, minimum value, maximum
   * value, digits, increment and page increment all at once.
   * <p>
   * Note: This is similar to setting the values individually
   * using the appropriate methods, but may be implemented in a
   * more efficient fashion on some platforms.
   * </p>
   *
   * @param selection the new selection value
   * @param minimum the new minimum value
   * @param maximum the new maximum value
   * @param digits the new digits value
   * @param increment the new increment value
   * @param pageIncrement the new pageIncrement value
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public void setValues( final int selection,
                         final int minimum,
                         final int maximum,
                         final int digits,
                         final int increment,
                         final int pageIncrement )
  {
    setMinimum( minimum );
    setMaximum( maximum );
    // setDigits( digits ) - ignore since we cannot (yet) handle digits
    setIncrement( increment );
    setPageIncrement( pageIncrement );
    setSelection( selection );
  }

  ///////////////////
  // Size calculation

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = 0;
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      String string = String.valueOf( maximum );
      if( digits > 0 ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( string );
        buffer.append( "," );
        int count = digits - string.length();
        while( count >= 0 ) {
          buffer.append( "0" );
          count--;
        }
        string = buffer.toString();
      }
      Point textSize = TextSizeDetermination.stringExtent( getFont(), string );
      Rectangle padding = getPadding();
      width = textSize.x + UP_DOWN_WIDTH + padding.width;
      height = textSize.y + padding.height;
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
    Rectangle trim = computeTrim( 0, 0, width, height );
    if( hHint == SWT.DEFAULT ) {
      int upDownHeight = UP_DOWN_MIN_HEIGHT + 2 * getBorderWidth();
      trim.height = Math.max( trim.height, upDownHeight );
    }
    return new Point( trim.width, trim.height );
  }

  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height )
  {
    checkWidget();
    Rectangle result = new Rectangle( x, y, width, height );
    if( ( style & SWT.BORDER ) != 0 ) {
      int border = getBorderWidth();
      result.x -= border ;
      result.y -= border;
      result.width += 2 * border;
      result.height += 2 * border;
    }
    result.width += UP_DOWN_WIDTH;
    return result;
  }

  /////////////////////////////////////////////
  // Event listener registration/deregistration

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
    ModifyEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods

  private Rectangle getPadding() {
    ThemeManager manager = ThemeManager.getInstance();
    SpinnerThemeAdapter adapter
      = ( SpinnerThemeAdapter )manager.getThemeAdapter( getClass() );
    return adapter.getPadding( this );
  }

  private static int checkStyle( final int style ) {
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return style & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }
}
