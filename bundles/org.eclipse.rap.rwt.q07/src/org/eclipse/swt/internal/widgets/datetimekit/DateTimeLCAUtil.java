/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.DateTime;

final class DateTimeLCAUtil {

  private DateTimeLCAUtil() {
    // prevent instantiation
  }

  static IDateTimeAdapter getDateTimeAdapter( final DateTime dateTime ) {
    return ( IDateTimeAdapter )dateTime.getAdapter( IDateTimeAdapter.class );
  }

  static void preserveSubWidgetBounds( final DateTime dateTime,
                                       final int subWidgetID )
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
    IDateTimeAdapter dateTimeAdapter = getDateTimeAdapter( dateTime );
    Rectangle subWidgetBounds = dateTimeAdapter.getBounds( subWidgetID );
    adapter.preserve( subWidgetID + "_BOUNDS", subWidgetBounds );
  }

  static void writeSubWidgetBounds( final DateTime dateTime,
                                    final int subWidgetID )
    throws IOException
  {
    IDateTimeAdapter dateTimeAdapter = getDateTimeAdapter( dateTime );
    Rectangle newValue = dateTimeAdapter.getBounds( subWidgetID );
    if( WidgetLCAUtil.hasChanged( dateTime,
                                  subWidgetID + "_BOUNDS", newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.call( "setBounds", new Object[]{
        new Integer( subWidgetID ),
        new Integer( newValue.x ),
        new Integer( newValue.y ),
        new Integer( newValue.width ),
        new Integer( newValue.height )
      });
    }
  }

  static void writeListener( final DateTime dateTime )
    throws IOException
  {
    boolean hasListener = SelectionEvent.hasListener( dateTime );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( dateTime, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( dateTime );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  static void initCellSize( final DateTime dateTime ) throws IOException {
    IDateTimeAdapter dateTimeAdapter
      = DateTimeLCAUtil.getDateTimeAdapter( dateTime );
    JSWriter writer = JSWriter.getWriterFor( dateTime );
    Point cellSize = dateTimeAdapter.getCellSize();
    writer.callFieldAssignment( new JSVar( "org.eclipse.swt.widgets.Calendar" ),
                                "CELL_WIDTH",
                                String.valueOf( cellSize.x ) );
    writer.callFieldAssignment( new JSVar( "org.eclipse.swt.widgets.Calendar" ),
                                "CELL_HEIGHT",
                                String.valueOf( cellSize.y ) );
  }
}
