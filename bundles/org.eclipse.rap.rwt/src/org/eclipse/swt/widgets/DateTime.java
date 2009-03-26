/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.text.*;
import java.util.*;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;

/**
 * Instances of this class are selectable user interface objects that allow the
 * user to enter and modify date or time values.
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DATE, TIME, CALENDAR, SHORT, MEDIUM, LONG</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles DATE, TIME, or CALENDAR may be specified, and
 * only one of the styles SHORT, MEDIUM, or LONG may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @since 1.1.1
 */
public class DateTime extends Composite {

  private final class DateTimeAdapter implements IDateTimeAdapter {

    public Rectangle getBounds( final int widget ) {
      Rectangle result = new Rectangle( 0, 0, 0, 0);
      switch( widget ) {
        case WEEKDAY_TEXTFIELD:
          result = weekdayTextFieldBounds;
        break;
        case DAY_TEXTFIELD:
          result = dayTextFieldBounds;
        break;
        case MONTH_TEXTFIELD:
          result = monthTextFieldBounds;
        break;
        case YEAR_TEXTFIELD:
          result = yearTextFieldBounds;
        break;
        case WEEKDAY_MONTH_SEPARATOR:
          result = separator0Bounds;
        break;
        case MONTH_DAY_SEPARATOR:
          result = separator1Bounds;
        break;
        case DAY_YEAR_SEPARATOR:
          result = separator2Bounds;
        break;
        case SPINNER:
          result = spinnerBounds;
        break;
        case HOURS_TEXTFIELD:
          result = hoursTextFieldBounds;
        break;
        case MINUTES_TEXTFIELD:
          result = minutesTextFieldBounds;
        break;
        case SECONDS_TEXTFIELD:
          result = secondsTextFieldBounds;
        break;
        case HOURS_MINUTES_SEPARATOR:
          result = separator3Bounds;
        break;
        case MINUTES_SECONDS_SEPARATOR:
          result = separator4Bounds;
        break;
      }
      return result;
    }

    public String[] getMonthNames() {
      return monthNames;
    }

    public String[] getWeekdayNames() {
      return weekdayNames;
    }

    public String[] getWeekdayShortNames() {
      return weekdayShortNames;
    }

    public String getDateSeparator() {
      return dateSeparator;
    }

    public String getDatePattern() {
      return datePattern;
    }
  }

  private int V_PADDING = 6;
  private int H_PADDING = 6;
  private int PADDING = 1;

  private String[] monthNames;
  private String[] weekdayNames;
  private String[] weekdayShortNames;
  private String dateSeparator;
  private String datePattern;

  private final IDateTimeAdapter dateTimeAdapter;
  private Calendar rightNow;

  // Date fields
  private Rectangle weekdayTextFieldBounds;
  private Rectangle dayTextFieldBounds;
  private Rectangle monthTextFieldBounds;
  private Rectangle yearTextFieldBounds;
  private Rectangle separator0Bounds;
  private Rectangle separator1Bounds;
  private Rectangle separator2Bounds;
  private Rectangle spinnerBounds;
  // Time fields
  private Rectangle hoursTextFieldBounds;
  private Rectangle minutesTextFieldBounds;
  private Rectangle secondsTextFieldBounds;
  private Rectangle separator3Bounds;
  private Rectangle separator4Bounds;

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see SWT#DATE
   * @see SWT#TIME
   * @see SWT#CALENDAR
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public DateTime( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    dateTimeAdapter = new DateTimeAdapter();
    rightNow = Calendar.getInstance();
    DateFormatSymbols symbols = new DateFormatSymbols( RWT.getLocale() );
    monthNames = symbols.getMonths();
    weekdayNames = symbols.getWeekdays();
    weekdayShortNames = symbols.getShortWeekdays();
    dateSeparator = getDateSeparator();
    datePattern = getDatePattern( dateSeparator );
    computeSubWidgetsBounds();
  }

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * the control is selected by the user, by sending it one of the messages
   * defined in the <code>SelectionListener</code> interface.
   * <p>
   * <code>widgetSelected</code> is called when the user changes the control's
   * value. <code>widgetDefaultSelected</code> is not called.
   * </p>
   *
   * @param listener the listener which should be notified
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when the control is selected by the user.
   *
   * @param listener the listener which should no longer be notified
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see SelectionListener
   * @see #addSelectionListener
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  /**
   * Returns the receiver's hours.
   * <p>
   * Hours is an integer between 0 and 23.
   * </p>
   *
   * @return an integer between 0 and 23
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getHours() {
    checkWidget();
    return rightNow.get( Calendar.HOUR_OF_DAY );
  }

  /**
   * Returns the receiver's minutes.
   * <p>
   * Minutes is an integer between 0 and 59.
   * </p>
   *
   * @return an integer between 0 and 59
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getMinutes() {
    checkWidget();
    return rightNow.get( Calendar.MINUTE );
  }

  /**
   * Returns the receiver's seconds.
   * <p>
   * Seconds is an integer between 0 and 59.
   * </p>
   *
   * @return an integer between 0 and 59
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getSeconds() {
    checkWidget();
    return rightNow.get( Calendar.SECOND );
  }

  /**
   * Returns the receiver's date, or day of the month.
   * <p>
   * The first day of the month is 1, and the last day depends on the month and
   * year.
   * </p>
   *
   * @return a positive integer beginning with 1
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getDay() {
    checkWidget();
    return rightNow.get( Calendar.DATE );
  }

  /**
   * Returns the receiver's month.
   * <p>
   * The first month of the year is 0, and the last month is 11.
   * </p>
   *
   * @return an integer between 0 and 11
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getMonth() {
    checkWidget();
    return rightNow.get( Calendar.MONTH );
  }

  /**
   * Returns the receiver's year.
   * <p>
   * The first year is 1752 and the last year is 9999.
   * </p>
   *
   * @return an integer between 1752 and 9999
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getYear() {
    checkWidget();
    return rightNow.get( Calendar.YEAR );
  }

  /**
   * Sets the receiver's hours.
   * <p>
   * Hours is an integer between 0 and 23.
   * </p>
   *
   * @param hours an integer between 0 and 23
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setHours( final int hours ) {
    checkWidget();
    if( checkTime( hours, getMinutes(), getSeconds() ) ) {
      rightNow.set( Calendar.HOUR_OF_DAY, hours );
    }
  }

  /**
   * Sets the receiver's minutes.
   * <p>
   * Minutes is an integer between 0 and 59.
   * </p>
   *
   * @param minutes an integer between 0 and 59
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setMinutes( final int minutes ) {
    checkWidget();
    if( checkTime( getHours(), minutes, getSeconds() ) ) {
      rightNow.set( Calendar.MINUTE, minutes );
    }
  }

  /**
   * Sets the receiver's seconds.
   * <p>
   * Seconds is an integer between 0 and 59.
   * </p>
   *
   * @param seconds an integer between 0 and 59
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setSeconds( final int seconds ) {
    checkWidget();
    if( checkTime( getHours(), getMinutes(), seconds ) ) {
      rightNow.set( Calendar.SECOND, seconds );
    }
  }

  /**
   * Sets the receiver's date, or day of the month, to the specified day.
   * <p>
   * The first day of the month is 1, and the last day depends on the month and
   * year.
   * </p>
   *
   * @param day a positive integer beginning with 1
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setDay( final int day ) {
    checkWidget();
    int month = rightNow.get( Calendar.MONTH );
    int year = rightNow.get( Calendar.YEAR );
    if( checkDate( year, month, day ) ) {
      rightNow.set( Calendar.DATE, day );
    }
  }

  /**
   * Sets the receiver's month.
   * <p>
   * The first month of the year is 0, and the last month is 11.
   * </p>
   *
   * @param month an integer between 0 and 11
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setMonth( final int month ) {
    checkWidget();
    int day = rightNow.get( Calendar.DATE );
    int year = rightNow.get( Calendar.YEAR );
    if( checkDate( year, month, day ) ) {
      rightNow.set( Calendar.MONTH, month );
    }
  }

  /**
   * Sets the receiver's year.
   * <p>
   * The first year is 1752 and the last year is 9999.
   * </p>
   *
   * @param year an integer between 1752 and 9999
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setYear( final int year ) {
    checkWidget();
    int day = rightNow.get( Calendar.DATE );
    int month = rightNow.get( Calendar.MONTH );
    if( checkDate( year, month, day ) ) {
      rightNow.set( Calendar.YEAR, year );
    }
  }

  /**
   * Sets the receiver's year, month, and day in a single operation.
   * <p>
   * This is the recommended way to set the date, because setting the year,
   * month, and day separately may result in invalid intermediate dates.
   * </p>
   *
   * @param year an integer between 1752 and 9999
   * @param month an integer between 0 and 11
   * @param day a positive integer beginning with 1
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public void setDate( final int year, final int month, final int day ) {
    checkWidget();
    if( checkDate( year, month, day ) ) {
      // reset
      setYear( 9996 );
      setMonth( 0 );
      setDay( 1 );
      // set
      setYear( year );
      setMonth( month );
      setDay( day );
    }
  }

  /**
   * Sets the receiver's hours, minutes, and seconds in a single operation.
   *
   * @param hours an integer between 0 and 23
   * @param minutes an integer between 0 and 59
   * @param seconds an integer between 0 and 59
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public void setTime( final int hours, final int minutes, final int seconds ) {
    checkWidget();
    if( checkTime( hours, minutes, seconds ) ) {
      setHours( hours );
      setMinutes( minutes );
      setSeconds( seconds );
    }
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * to the font specified by the argument, or to the default font for that
   * kind of control if the argument is null.
   *
   * @param font the new font (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setFont( final Font font ) {
    if( font != getFont() ) {
      super.setFont( font );
    }
    computeSubWidgetsBounds();
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IDateTimeAdapter.class ) {
      result = dateTimeAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed ) {
    checkWidget();
    int width = 0, height = 0;
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      Point size = computeSubWidgetsBounds();
      width = size.x;
      height = size.y;
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
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  public void setBounds( final Rectangle bounds ) {
    super.setBounds( bounds );
    // [if] Recalculate the sub widgets bounds
    // important for using it in FillLayout where the
    // DateTime#computeSize() is not call.
    computeSubWidgetsBounds();
  }

  private Point computeSubWidgetsBounds() {
    Font font = getFont();
    int width = 0, height = 0;
    if( ( style & SWT.CALENDAR ) != 0 ) {
      width = 192;
      height = 136;
    } else if( ( style & SWT.DATE ) != 0 ) {
      Point prefSize = new Point( 0, 0 );
      if( datePattern.equals( "MDY" ) ) {
        prefSize = computeMDYBounds();
      } else if( datePattern.equals( "DMY" ) ) {
        prefSize = computeDMYBounds();
      } else {
        if( ( style & SWT.MEDIUM ) != 0 ) {
          prefSize = computeYMDBounds();
        } else {
          prefSize = computeMDYBounds();
        }
      }
      // Overall widget size
      width = prefSize.x + PADDING * 2;
      height = prefSize.y + PADDING * 2;
    } else if( ( style & SWT.TIME ) != 0 ) {
      // Hours text field
      hoursTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
      hoursTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
      hoursTextFieldBounds.height
        = TextSizeDetermination.stringExtent( font, "88" ).y + V_PADDING;
      // Hours minutes separator
      separator3Bounds = new Rectangle( 0, 0, 0, 0 );
      separator3Bounds.x = hoursTextFieldBounds.x + hoursTextFieldBounds.width;
      separator3Bounds.width
        = TextSizeDetermination.stringExtent( font, ":" ).x;
      separator3Bounds.height = hoursTextFieldBounds.height;
      // Minutes text field
      minutesTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
      minutesTextFieldBounds.x = separator3Bounds.x + separator3Bounds.width;
      minutesTextFieldBounds.width = hoursTextFieldBounds.width;
      minutesTextFieldBounds.height = hoursTextFieldBounds.height;
      // Minutes seconds separator
      separator4Bounds = new Rectangle( 0, 0, 0, 0 );
      separator4Bounds.x = minutesTextFieldBounds.x
                         + minutesTextFieldBounds.width;
      separator4Bounds.width = separator3Bounds.width;
      separator4Bounds.height = hoursTextFieldBounds.height;
      // Seconds text field
      secondsTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
      secondsTextFieldBounds.x = separator4Bounds.x + separator4Bounds.width;
      secondsTextFieldBounds.width = hoursTextFieldBounds.width;
      secondsTextFieldBounds.height = hoursTextFieldBounds.height;
      // The spinner bounds
      spinnerBounds = new Rectangle( 0, 0, 0, 0 );
      spinnerBounds.x = minutesTextFieldBounds.x
                      + minutesTextFieldBounds.width;
      if( ( style & SWT.MEDIUM ) != 0 || ( style & SWT.LONG) != 0 ) {
        spinnerBounds.x = secondsTextFieldBounds.x
                        + secondsTextFieldBounds.width;
      }
      spinnerBounds.width = 17;
      spinnerBounds.height = hoursTextFieldBounds.height;
      // Overall widget size
      width = spinnerBounds.x + spinnerBounds.width + PADDING * 2;
      height = hoursTextFieldBounds.height + PADDING * 2;
    }
    return new Point( width, height );
  }

  private Point computeMDYBounds() {
    Font font = getFont();
    // The weekday text field bounds
    weekdayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    if( ( style & SWT.LONG ) != 0 ) {
      weekdayTextFieldBounds.width
        = getMaxStringLength( weekdayNames ) + H_PADDING + 2;
    }
    weekdayTextFieldBounds.height
      = TextSizeDetermination.stringExtent( font, weekdayNames[1] ).y
        + V_PADDING;
    // The weekday month separator bounds
    separator0Bounds = new Rectangle( 0, 0, 0, 0 );
    separator0Bounds.x
      = weekdayTextFieldBounds.x + weekdayTextFieldBounds.width;
    if( ( style & SWT.LONG ) != 0 ) {
      separator0Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator0Bounds.height = weekdayTextFieldBounds.height;
    // The month text field bounds
    monthTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    monthTextFieldBounds.x = separator0Bounds.x + separator0Bounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      monthTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    } else {
      monthTextFieldBounds.width
        = getMaxStringLength( monthNames ) + H_PADDING + 2;
    }
    monthTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The month date separator bounds
    separator1Bounds = new Rectangle( 0, 0, 0, 0 );
    separator1Bounds.x = monthTextFieldBounds.x + monthTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator1Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    }
    separator1Bounds.height = weekdayTextFieldBounds.height;
    // The date text field bounds
    dayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    dayTextFieldBounds.x = separator1Bounds.x + separator1Bounds.width;
    if( ( style & SWT.SHORT ) == 0 ) {
      dayTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    }
    dayTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The date year separator bounds
    separator2Bounds = new Rectangle( 0, 0, 0, 0 );
    separator2Bounds.x = dayTextFieldBounds.x + dayTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    } else {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator2Bounds.height = weekdayTextFieldBounds.height;
    // The year text field bounds
    yearTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    yearTextFieldBounds.x = separator2Bounds.x + separator2Bounds.width;
    yearTextFieldBounds.width
      = TextSizeDetermination.stringExtent( font, "8888" ).x + H_PADDING;
    yearTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The spinner bounds
    spinnerBounds = new Rectangle( 0, 0, 0, 0 );
    spinnerBounds.x = yearTextFieldBounds.x + yearTextFieldBounds.width;
    spinnerBounds.width = 17;
    spinnerBounds.height = weekdayTextFieldBounds.height;
    // Overall widget size
    int width = spinnerBounds.x + spinnerBounds.width;
    int height = weekdayTextFieldBounds.height;
    return new Point( width, height );
  }

  private Point computeDMYBounds() {
    Font font = getFont();
    // The weekday text field bounds
    weekdayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    if( ( style & SWT.LONG ) != 0 ) {
      weekdayTextFieldBounds.width
        = getMaxStringLength( weekdayNames ) + H_PADDING + 2;
    }
    weekdayTextFieldBounds.height
      = TextSizeDetermination.stringExtent( font, weekdayNames[1] ).y
        + V_PADDING;
    // The weekday day separator bounds
    separator0Bounds = new Rectangle( 0, 0, 0, 0 );
    separator0Bounds.x
      = weekdayTextFieldBounds.x + weekdayTextFieldBounds.width;
    if( ( style & SWT.LONG ) != 0 ) {
      separator0Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator0Bounds.height = weekdayTextFieldBounds.height;
    // The day text field bounds
    dayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    dayTextFieldBounds.x = separator0Bounds.x + separator0Bounds.width;
    if( ( style & SWT.SHORT ) == 0 ) {
      dayTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    }
    dayTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The day month separator bounds
    separator1Bounds = new Rectangle( 0, 0, 0, 0 );
    separator1Bounds.x = dayTextFieldBounds.x + dayTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator1Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    }
    separator1Bounds.height = weekdayTextFieldBounds.height;
    // The month text field bounds
    monthTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    monthTextFieldBounds.x = separator1Bounds.x + separator1Bounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      monthTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    } else {
      monthTextFieldBounds.width
        = getMaxStringLength( monthNames ) + H_PADDING + 2;
    }
    monthTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The month year separator bounds
    separator2Bounds = new Rectangle( 0, 0, 0, 0 );
    separator2Bounds.x = monthTextFieldBounds.x + monthTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    } else {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator2Bounds.height = weekdayTextFieldBounds.height;
    // The year text field bounds
    yearTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    yearTextFieldBounds.x = separator2Bounds.x + separator2Bounds.width;
    yearTextFieldBounds.width
      = TextSizeDetermination.stringExtent( font, "8888" ).x + H_PADDING;
    yearTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The spinner bounds
    spinnerBounds = new Rectangle( 0, 0, 0, 0 );
    spinnerBounds.x = yearTextFieldBounds.x + yearTextFieldBounds.width;
    spinnerBounds.width = 17;
    spinnerBounds.height = weekdayTextFieldBounds.height;
    // Overall widget size
    int width = spinnerBounds.x + spinnerBounds.width;
    int height = weekdayTextFieldBounds.height;
    return new Point( width, height );
  }

  private Point computeYMDBounds() {
    Font font = getFont();
    // The weekday text field bounds
    weekdayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    if( ( style & SWT.LONG ) != 0 ) {
      weekdayTextFieldBounds.width
        = getMaxStringLength( weekdayNames ) + H_PADDING + 2;
    }
    weekdayTextFieldBounds.height
      = TextSizeDetermination.stringExtent( font, weekdayNames[1] ).y
        + V_PADDING;
    // The weekday day separator bounds
    separator0Bounds = new Rectangle( 0, 0, 0, 0 );
    separator0Bounds.x
      = weekdayTextFieldBounds.x + weekdayTextFieldBounds.width;
    if( ( style & SWT.LONG ) != 0 ) {
      separator0Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator0Bounds.height = weekdayTextFieldBounds.height;
    // The year text field bounds
    yearTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    yearTextFieldBounds.x = separator0Bounds.x + separator0Bounds.width;
    yearTextFieldBounds.width
      = TextSizeDetermination.stringExtent( font, "8888" ).x + H_PADDING;
    yearTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The year month separator bounds
    separator1Bounds = new Rectangle( 0, 0, 0, 0 );
    separator1Bounds.x = yearTextFieldBounds.x + yearTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator1Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    }
    // The month text field bounds
    monthTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    monthTextFieldBounds.x = separator1Bounds.x + separator1Bounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      monthTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    } else {
      monthTextFieldBounds.width
        = getMaxStringLength( monthNames ) + H_PADDING + 2;
    }
    monthTextFieldBounds.height = weekdayTextFieldBounds.height;
    // The month day separator bounds
    separator2Bounds = new Rectangle( 0, 0, 0, 0 );
    separator2Bounds.x = monthTextFieldBounds.x + monthTextFieldBounds.width;
    if( ( style & SWT.MEDIUM ) != 0 ) {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, dateSeparator ).x;
    } else {
      separator2Bounds.width
        = TextSizeDetermination.stringExtent( font, "," ).x;
    }
    separator2Bounds.height = weekdayTextFieldBounds.height;
    // The day text field bounds
    dayTextFieldBounds = new Rectangle( 0, 0, 0, 0 );
    dayTextFieldBounds.x = separator2Bounds.x + separator2Bounds.width;
    if( ( style & SWT.SHORT ) == 0 ) {
      dayTextFieldBounds.width
        = TextSizeDetermination.stringExtent( font, "88" ).x + H_PADDING;
    }
    dayTextFieldBounds.height = weekdayTextFieldBounds.height;

    separator1Bounds.height = weekdayTextFieldBounds.height;
    // The spinner bounds
    spinnerBounds = new Rectangle( 0, 0, 0, 0 );
    spinnerBounds.x = dayTextFieldBounds.x + dayTextFieldBounds.width;
    spinnerBounds.width = 17;
    spinnerBounds.height = weekdayTextFieldBounds.height;
    // Overall widget size
    int width = spinnerBounds.x + spinnerBounds.width;
    int height = weekdayTextFieldBounds.height;
    return new Point( width, height );
  }

  private int getDaysInMonth( final int month, final int year ) {
    GregorianCalendar cal = new GregorianCalendar( year, month, 1 );
    return cal.getActualMaximum( Calendar.DAY_OF_MONTH );
  }

  private int getMaxStringLength( final String[] strings ) {
    Font font = getFont();
    int maxLength = 0;
    for( int i = 0; i < strings.length; i++ ) {
      int currentStringWidth
        = TextSizeDetermination.stringExtent( font, strings[i] ).x;
      maxLength = Math.max( maxLength, currentStringWidth );
    }
    return maxLength;
  }

  private String getDateSeparator() {
    DateFormat df
      = DateFormat.getDateInstance( DateFormat.SHORT, RWT.getLocale() );
    String datePattern = ( ( SimpleDateFormat )df ).toPattern();
    String result = "";
    int index = 0;
    while( Character.isLetter( datePattern.charAt( index ) ) ) {
      index++;
    }
    result = Character.toString( datePattern.charAt( index ) );
    return result;
  }

  private String getDatePattern( final String dateSeparator ) {
    DateFormat df
      = DateFormat.getDateInstance( DateFormat.SHORT, RWT.getLocale() );
    String datePattern = ( ( SimpleDateFormat )df ).toPattern();
    String result = "";
    StringTokenizer st = new StringTokenizer( datePattern,
                                              dateSeparator );
    while ( st.hasMoreTokens() ) {
      String token = st.nextToken();
      result += Character.toString( token.charAt( 0 ) );
    }
    return result.toUpperCase();
  }

  String getNameText() {
    return "DateTime";
  }

  private boolean checkDate( final int year,
                             final int month,
                             final int day )
  {
    int daysInMonth = getDaysInMonth( month, year );
    boolean validYear = ( year >= 1752 && year <= 9999 && day <= daysInMonth );
    boolean validMonth = ( month >= 0 && month <= 11 && day <= daysInMonth );
    boolean validDay = ( day >= 1 && day <= daysInMonth );
    return validYear && validMonth && validDay;
  }

  private boolean checkTime( final int hours,
                             final int minutes,
                             final int seconds )
  {
    boolean validHours = ( hours >= 0 && hours <= 23 );
    boolean validMinutes = ( minutes >= 0 && minutes <= 59 );
    boolean validSeconds = ( seconds >= 0 && seconds <= 59 );
    return validHours && validMinutes && validSeconds;
  }

  static int checkStyle( final int value ) {
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    int style = value;
    style &= ~( SWT.H_SCROLL | SWT.V_SCROLL );
    style = checkBits( style, SWT.DATE, SWT.TIME, SWT.CALENDAR, 0, 0, 0 );
    return checkBits( style, SWT.MEDIUM, SWT.SHORT, SWT.LONG, 0, 0, 0 );
  }

  protected void checkSubclass() {
    if( !isValidSubclass() ) {
      error( SWT.ERROR_INVALID_SUBCLASS );
    }
  }
}
