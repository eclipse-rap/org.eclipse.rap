/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Widget;


public final class DateTimeLCA extends WidgetLCA<DateTime> {

  public static final DateTimeLCA INSTANCE = new DateTimeLCA();

  @Override
  public void preserveValues( DateTime dateTime ) {
    getDelegate( dateTime ).preserveValues( dateTime );
  }

  @Override
  public void renderInitialization( DateTime dateTime ) throws IOException {
    getDelegate( dateTime ).renderInitialization( dateTime );
  }

  @Override
  public void renderChanges( DateTime dateTime ) throws IOException {
    getDelegate( dateTime ).renderChanges( dateTime );
  }

  private static AbstractDateTimeLCADelegate getDelegate( Widget widget ) {
    if( ( widget.getStyle() & SWT.DATE ) != 0 ) {
      return DateTimeDateLCA.INSTANCE;
    }
    if( ( widget.getStyle() & SWT.TIME ) != 0 ) {
      return DateTimeTimeLCA.INSTANCE;
    }
    return DateTimeCalendarLCA.INSTANCE;
  }

  private DateTimeLCA() {
    // prevent instantiation
  }

}
