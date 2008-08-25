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

public class DateTimeTimeLCA extends AbstractDateTimeLCADelegate {

  static final String TYPE_POOL_ID = DateTimeTimeLCA.class.getName();
  // Property names for preserveValues
  static final String PROP_HOURS = "hours";
  static final String PROP_MINUTES = "minutes";
  static final String PROP_SECONDS = "seconds";

  // Property names for preserveValues
  void preserveValues( final DateTime dateTime ) {
    ControlLCAUtil.preserveValues( dateTime );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    boolean hasListeners = SelectionEvent.hasListener( dateTime );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_HOURS,
                      new Integer( dateTime.getHours() ) );
    adapter.preserve( PROP_MINUTES,
                      new Integer( dateTime.getMinutes() ) );
    adapter.preserve( PROP_SECONDS,
                      new Integer( dateTime.getSeconds() ) );
    preserveSubWidgetsBounds( dateTime );
  }

  void readData( final DateTime dateTime ) {
    String value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_HOURS );
    if( value != null ) {
      dateTime.setHours( Integer.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_MINUTES );
    if( value != null ) {
      dateTime.setMinutes( Integer.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_SECONDS );
    if( value != null ) {
      dateTime.setSeconds( Integer.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( dateTime, null, true );
  }

  void renderInitialization( final DateTime dateTime )
    throws IOException
  {
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
      style
    };
    writer.newWidget( "org.eclipse.swt.widgets.DateTimeTime", args );
    WidgetLCAUtil.writeCustomVariant( dateTime );
    ControlLCAUtil.writeStyleFlags( dateTime );
  }

  void renderChanges( final DateTime dateTime ) throws IOException {
    ControlLCAUtil.writeChanges( dateTime );
    writeHours( dateTime );
    writeMinutes( dateTime );
    writeSeconds( dateTime );
    DateTimeLCAUtil.writeListener( dateTime );
    writeSubWidgetsBounds( dateTime );
  }

  void renderDispose( final DateTime dateTime ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( dateTime );
    writer.dispose();
  }

  void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  String getTypePoolId( final DateTime dateTime ) {
    return null;
  }
  // ////////////////////////////////////
  // Helping methods to write properties
  private void writeHours( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getHours() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_HOURS, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_HOURS, newValue );
    }
  }

  private void writeMinutes( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getMinutes() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_MINUTES, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_MINUTES, newValue );
    }
  }

  private void writeSeconds( final DateTime dateTime ) throws IOException {
    Integer newValue = new Integer( dateTime.getSeconds() );
    if( WidgetLCAUtil.hasChanged( dateTime, PROP_SECONDS, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( PROP_SECONDS, newValue );
    }
  }

  private void writeSubWidgetsBounds( final DateTime dateTime )
    throws IOException
  {
    // The hours text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.HOURS_TEXTFIELD );
    // The hours minutes separator bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.HOURS_MINUTES_SEPARATOR );
    // The minutes text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MINUTES_TEXTFIELD );
    // The minutes seconds separator bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MINUTES_SECONDS_SEPARATOR );
    // The seconds text field bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SECONDS_TEXTFIELD );
    // The spinner bounds
    DateTimeLCAUtil.writeSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SPINNER );
  }

  private void preserveSubWidgetsBounds( final DateTime dateTime ) {
    // The hours text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.HOURS_TEXTFIELD );
    // The hours minutes separator bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.HOURS_MINUTES_SEPARATOR );
    // The minutes text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MINUTES_TEXTFIELD );
    // The minutes seconds separator bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.MINUTES_SECONDS_SEPARATOR );
    // The seconds text field bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SECONDS_TEXTFIELD );
    // The spinner bounds
    DateTimeLCAUtil.preserveSubWidgetBounds( dateTime,
                    IDateTimeAdapter.SPINNER );
  }
}
