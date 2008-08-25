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
import org.eclipse.swt.widgets.DateTime;

public class DateTimeCalendarLCA extends AbstractDateTimeLCADelegate {

  static final String TYPE_POOL_ID = DateTimeCalendarLCA.class.getName();

  // Property names for preserveValues
  void preserveValues( final DateTime dateTime ) {
    ControlLCAUtil.preserveValues( dateTime );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dateTime );
  }

  void readData( final DateTime dateTime ) {
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
    writer.newWidget( "org.eclipse.swt.widgets.DateTimeCalendar", args );
    WidgetLCAUtil.writeCustomVariant( dateTime );
    ControlLCAUtil.writeStyleFlags( dateTime );
  }

  void renderChanges( final DateTime dateTime ) throws IOException {
    ControlLCAUtil.writeChanges( dateTime );
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
}
