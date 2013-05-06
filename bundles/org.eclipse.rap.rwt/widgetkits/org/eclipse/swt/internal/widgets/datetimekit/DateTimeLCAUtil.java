/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;
import org.eclipse.swt.widgets.DateTime;

final class DateTimeLCAUtil {

  private static final String TYPE = "rwt.widgets.DateTime";
  private static final String[] ALLOWED_STYLES = new String[] {
    "DATE", "TIME", "CALENDAR", "SHORT", "MEDIUM", "LONG", "DROP_DOWN", "BORDER"
  };

  private static final String PROP_CELL_SIZE = "cellSize";
  private static final String PROP_MONTH_NAMES = "monthNames";
  private static final String PROP_WEEKDAY_NAMES = "weekdayNames";
  private static final String PROP_WEEKDAY_SHORT_NAMES = "weekdayShortNames";
  private static final String PROP_DATE_SEPARATOR = "dateSeparator";
  private static final String PROP_DATE_PATTERN = "datePattern";
  private static final String PROP_SUB_WIDGETS_BOUNDS = "subWidgetsBounds";
  private static final String PROP_SELECTION_LISTENER = "Selection";

  private DateTimeLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( DateTime dateTime ) {
    ControlLCAUtil.preserveValues( dateTime );
    WidgetLCAUtil.preserveCustomVariant( dateTime );
    preserveListener( dateTime, PROP_SELECTION_LISTENER, isListening( dateTime, SWT.Selection ) );
  }

  static void renderInitialization( DateTime dateTime ) {
    IClientObject clientObject = getClientObject( dateTime );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( dateTime.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( dateTime, ALLOWED_STYLES ) ) );
  }

  static void renderChanges( DateTime dateTime ) {
    ControlLCAUtil.renderChanges( dateTime );
    WidgetLCAUtil.renderCustomVariant( dateTime );
    renderListener( dateTime,
                    PROP_SELECTION_LISTENER,
                    isListening( dateTime, SWT.Selection ),
                    false );
  }

  static void renderCellSize( DateTime dateTime ) {
    Point cellSize = getDateTimeAdapter( dateTime ).getCellSize();
    IClientObject clientObject = getClientObject( dateTime );
    clientObject.set( PROP_CELL_SIZE, new JsonArray().add( cellSize.x ).add( cellSize.y ) );
  }

  static void renderMonthNames( DateTime dateTime ) {
    String[] monthNames = getDateTimeAdapter( dateTime ).getMonthNames();
    IClientObject clientObject = getClientObject( dateTime );
    clientObject.set( PROP_MONTH_NAMES, createJsonArray( monthNames ) );
  }

  static void renderWeekdayNames( DateTime dateTime ) {
    String[] weekdayNames = getDateTimeAdapter( dateTime ).getWeekdayNames();
    IClientObject clientObject = getClientObject( dateTime );
    clientObject.set( PROP_WEEKDAY_NAMES, createJsonArray( weekdayNames ) );
  }

  static void renderWeekdayShortNames( DateTime dateTime ) {
    String[] weekdayShortNames = getDateTimeAdapter( dateTime ).getWeekdayShortNames();
    IClientObject clientObject = getClientObject( dateTime );
    clientObject.set( PROP_WEEKDAY_SHORT_NAMES, createJsonArray( weekdayShortNames ) );
  }

  static void renderDateSeparator( DateTime dateTime ) {
    String dateSeparator = getDateTimeAdapter( dateTime ).getDateSeparator();
    getClientObject( dateTime ).set( PROP_DATE_SEPARATOR, dateSeparator );
  }

  static void renderDatePattern( DateTime dateTime ) {
    String datePattern = getDateTimeAdapter( dateTime ).getDatePattern();
    getClientObject( dateTime ).set( PROP_DATE_PATTERN, datePattern );
  }

  static void preserveSubWidgetsBounds( DateTime dateTime, SubWidgetBounds[] subWidgetBounds ) {
    preserveProperty( dateTime, PROP_SUB_WIDGETS_BOUNDS, subWidgetBounds );
  }

  static void renderSubWidgetsBounds( DateTime dateTime, SubWidgetBounds[] subWidgetBounds ) {
    if( hasChanged( dateTime, PROP_SUB_WIDGETS_BOUNDS, subWidgetBounds ) ) {
      JsonArray bounds = new JsonArray();
      for( int i = 0; i < subWidgetBounds.length; i++ ) {
        bounds.add( new JsonArray().add( subWidgetBounds[ i ].id )
                                   .add( subWidgetBounds[ i ].x )
                                   .add( subWidgetBounds[ i ].y )
                                   .add( subWidgetBounds[ i ].width )
                                   .add( subWidgetBounds[ i ].height ) );
      }
      getClientObject( dateTime ).set( PROP_SUB_WIDGETS_BOUNDS, bounds );
    }
  }

  static SubWidgetBounds getSubWidgetBounds( DateTime dateTime, int subWidgetId ) {
    Rectangle bounds = getDateTimeAdapter( dateTime ).getBounds( subWidgetId );
    return new SubWidgetBounds( subWidgetId, bounds );
  }

  private static IDateTimeAdapter getDateTimeAdapter( DateTime dateTime ) {
    return dateTime.getAdapter( IDateTimeAdapter.class );
  }

  static final class SubWidgetBounds {
    public final int id;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public SubWidgetBounds( int id, Rectangle bounds ) {
      ParamCheck.notNull( bounds, "subWidgetBounds" );
      this.id = id;
      this.x = bounds.x;
      this.y = bounds.y;
      this.width = bounds.width;
      this.height = bounds.height;
    }

    @Override
    public boolean equals( Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof SubWidgetBounds ) {
        SubWidgetBounds other = ( SubWidgetBounds )obj;
        result =  other.id == id
               && other.x == x
               && other.y == y
               && other.width == width
               && other.height == height;
      } else {
        result = false;
      }
      return result;
    }

    @Override
    public int hashCode() {
      String msg = "SubWidgetBounds#hashCode() not implemented";
      throw new UnsupportedOperationException( msg );
    }
  }
}
