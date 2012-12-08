/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;

final class DateTimeCalendarLCA extends AbstractDateTimeLCADelegate {

  private static final String PROP_YEAR = "year";
  private static final String PROP_MONTH = "month";
  private static final String PROP_DAY = "day";

  void preserveValues( DateTime dateTime ) {
    DateTimeLCAUtil.preserveValues( dateTime );
    preserveProperty( dateTime, PROP_YEAR, dateTime.getYear() );
    preserveProperty( dateTime, PROP_MONTH, dateTime.getMonth() );
    preserveProperty( dateTime, PROP_DAY, dateTime.getDay() );
  }

  void readData( DateTime dateTime ) {
    String year = WidgetLCAUtil.readPropertyValue( dateTime, PROP_YEAR );
    String month = WidgetLCAUtil.readPropertyValue( dateTime, PROP_MONTH );
    String day = WidgetLCAUtil.readPropertyValue( dateTime, PROP_DAY );
    if( day != null && month != null && year != null ) {
      dateTime.setDate( NumberFormatUtil.parseInt( year ),
                        NumberFormatUtil.parseInt( month ),
                        NumberFormatUtil.parseInt( day ) );
    }
    ControlLCAUtil.processSelection( dateTime, null, true );
    ControlLCAUtil.processDefaultSelection( dateTime, null );
    ControlLCAUtil.processEvents( dateTime );
    ControlLCAUtil.processKeyEvents( dateTime );
    ControlLCAUtil.processMenuDetect( dateTime );
    WidgetLCAUtil.processHelp( dateTime );
  }

  void renderInitialization( DateTime dateTime ) throws IOException {
    DateTimeLCAUtil.renderInitialization( dateTime );
    DateTimeLCAUtil.renderCellSize( dateTime );
    DateTimeLCAUtil.renderMonthNames( dateTime );
    DateTimeLCAUtil.renderWeekdayShortNames( dateTime );
  }

  void renderChanges( DateTime dateTime ) throws IOException {
    DateTimeLCAUtil.renderChanges( dateTime );
    renderProperty( dateTime, PROP_YEAR, dateTime.getYear(), SWT.DEFAULT );
    renderProperty( dateTime, PROP_MONTH, dateTime.getMonth(), SWT.DEFAULT );
    renderProperty( dateTime, PROP_DAY, dateTime.getDay(), SWT.DEFAULT );
  }
}
