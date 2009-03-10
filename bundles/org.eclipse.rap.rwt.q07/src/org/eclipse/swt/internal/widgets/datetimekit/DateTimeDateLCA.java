/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.DateTime;

final class DateTimeDateLCA extends AbstractDateTimeLCADelegate {

  // Property names for preserveValues
  static final String PROP_DAY = "day";
  static final String PROP_MONTH = "month";
  static final String PROP_YEAR = "year";

  // Property names for preserveValues
  void preserveValues( final DateTime dateTime ) {
    ControlLCAUtil.preserveValues( dateTime );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    boolean hasListeners = SelectionEvent.hasListener( dateTime );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_DAY,
                      new Integer( dateTime.getDay() ) );
    adapter.preserve( PROP_MONTH,
                      new Integer( dateTime.getMonth() ) );
    adapter.preserve( PROP_YEAR,
                      new Integer( dateTime.getYear() ) );
    preserveSubWidgetsBounds( dateTime );
  }

  void readData( final DateTime dateTime ) {
    String value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_DAY );
    if( value != null ) {
      dateTime.setDay( Integer.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_MONTH );
    if( value != null ) {
      dateTime.setMonth( Integer.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_YEAR );
    if( value != null ) {
      dateTime.setYear( Integer.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( dateTime, null, true );
    ControlLCAUtil.processKeyEvents( dateTime );
  }

  void renderInitialization( final DateTime dateTime )
    throws IOException
  {
    IDateTimeAdapter dateTimeAdapter
      = DateTimeLCAUtil.getDateTimeAdapter( dateTime );
    JSWriter writer = JSWriter.getWriterFor( dateTime );
    String style = "";
    if( ( dateTime.getStyle() & SWT.SHORT ) != 0 ) {
      style = "short";
    } else if( ( dateTime.getStyle() & SWT.MEDIUM ) != 0 ) {
      style = "medium";
    } else if( ( dateTime.getStyle() & SWT.LONG ) != 0 ) {
      style = "long";
    }
    Object[] args = new Object[]{
      style,
      dateTimeAdapter.getMonthNames(),
      dateTimeAdapter.getWeekdayNames(),
      dateTimeAdapter.getDateSeparator(),
      dateTimeAdapter.getDatePattern()
    };
    writer.newWidget( "org.eclipse.swt.widgets.DateTimeDate", args );
    WidgetLCAUtil.writeCustomVariant( dateTime );
    ControlLCAUtil.writeStyleFlags( dateTime );
  }

  void renderChanges( final DateTime dateTime ) throws IOException {
    ControlLCAUtil.writeChanges( dateTime );
    writeYear( dateTime );
    writeMonth( dateTime );
    writeDay( dateTime );
    DateTimeLCAUtil.writeListener( dateTime );
    writeSubWidgetsBounds( dateTime );
  }

  void renderDispose( final DateTime dateTime ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( dateTime );
    writer.dispose();
  }

  // ////////////////////////////////////
  // Helping methods to write properties

  private void writeDay( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getDay() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_DAY, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_DAY, newValue );
    }
  }

  private void writeMonth( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getMonth() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_MONTH, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_MONTH, newValue );
    }
  }

  private void writeYear( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getYear() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_YEAR, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_YEAR, newValue );
    }
  }

  private void writeSubWidgetsBounds( final DateTime dateTime )
    throws IOException
  {
    // The weekday text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.WEEKDAY_TEXTFIELD );
    // The weekday month separator bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.WEEKDAY_MONTH_SEPARATOR );
    // The month text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MONTH_TEXTFIELD );
    // The month date separator bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MONTH_DAY_SEPARATOR );
    // The date text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.DAY_TEXTFIELD );
    // The date year separator bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.DAY_YEAR_SEPARATOR );
    // The year text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.YEAR_TEXTFIELD );
    // The spinner bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SPINNER );
  }

  private void preserveSubWidgetsBounds( final DateTime dateTime ) {
    // The weekday text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.WEEKDAY_TEXTFIELD );
    // The weekday month separator bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.WEEKDAY_MONTH_SEPARATOR );
    // The month text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MONTH_TEXTFIELD );
    // The month date separator bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MONTH_DAY_SEPARATOR );
    // The date text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.DAY_TEXTFIELD );
    // The date year separator bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.DAY_YEAR_SEPARATOR );
    // The year text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.YEAR_TEXTFIELD );
    // The spinner bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SPINNER );
  }
}
