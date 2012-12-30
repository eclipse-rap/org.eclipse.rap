/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.internal.widgets.IDateTimeAdapter;
import org.eclipse.swt.internal.widgets.datetimekit.DateTimeLCAUtil.SubWidgetBounds;
import org.eclipse.swt.widgets.DateTime;

final class DateTimeTimeLCA extends AbstractDateTimeLCADelegate {

  private static final String PROP_HOURS = "hours";
  private static final String PROP_MINUTES = "minutes";
  private static final String PROP_SECONDS = "seconds";

  void preserveValues( DateTime dateTime ) {
    DateTimeLCAUtil.preserveValues( dateTime );
    preserveProperty( dateTime, PROP_HOURS, dateTime.getHours() );
    preserveProperty( dateTime, PROP_MINUTES, dateTime.getMinutes() );
    preserveProperty( dateTime, PROP_SECONDS, dateTime.getSeconds() );
    DateTimeLCAUtil.preserveSubWidgetsBounds( dateTime, getSubWidgetsBounds( dateTime ) );
  }

  void readData( DateTime dateTime ) {
    String value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_HOURS );
    if( value != null ) {
      dateTime.setHours( NumberFormatUtil.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_MINUTES );
    if( value != null ) {
      dateTime.setMinutes( NumberFormatUtil.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( dateTime, PROP_SECONDS );
    if( value != null ) {
      dateTime.setSeconds( NumberFormatUtil.parseInt( value ) );
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
  }

  void renderChanges( DateTime dateTime ) throws IOException {
    DateTimeLCAUtil.renderChanges( dateTime );
    renderProperty( dateTime, PROP_HOURS, dateTime.getHours(), SWT.DEFAULT );
    renderProperty( dateTime, PROP_MINUTES, dateTime.getMinutes(), SWT.DEFAULT );
    renderProperty( dateTime, PROP_SECONDS, dateTime.getSeconds(), SWT.DEFAULT );
    DateTimeLCAUtil.renderSubWidgetsBounds( dateTime, getSubWidgetsBounds( dateTime ) );
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static SubWidgetBounds[] getSubWidgetsBounds( DateTime dateTime ) {
    return new SubWidgetBounds[] {
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.HOURS_TEXTFIELD ),
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.HOURS_MINUTES_SEPARATOR ),
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.MINUTES_TEXTFIELD ),
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.MINUTES_SECONDS_SEPARATOR ),
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.SECONDS_TEXTFIELD ),
      DateTimeLCAUtil.getSubWidgetBounds( dateTime, IDateTimeAdapter.SPINNER )
    };
  }
}
