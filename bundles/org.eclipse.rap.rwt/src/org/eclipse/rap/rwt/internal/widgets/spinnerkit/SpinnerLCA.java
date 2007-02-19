/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.spinnerkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.ModifyEvent;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Spinner;
import org.eclipse.rap.rwt.widgets.Widget;


public class SpinnerLCA extends AbstractWidgetLCA {

  private static final String PROP_SELECTION = "selection";
  private static final String PROP_PAGE_INCREMENT = "pageIncrement";
  private static final String PROP_MAXIMUM = "maximum";
  private static final String PROP_MINIMUM = "minimum";
  private static final String PROP_INCREMENT = "increment";
  private static final String PROP_MODIFY_LISTENER = "modifyListener";

  public void preserveValues( final Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.preserveValues( spinner );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_SELECTION, new Integer( spinner.getSelection() ) );
    adapter.preserve( PROP_MINIMUM, new Integer( spinner.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( spinner.getMaximum() ) );
    adapter.preserve( PROP_PAGE_INCREMENT, 
                      new Integer( spinner.getPageIncrement() ) );
    adapter.preserve( PROP_MODIFY_LISTENER, 
                      Boolean.valueOf( ModifyEvent.hasListener( spinner ) ) );
  }

  public void readData( final Widget widget ) {
    Spinner spinner = ( Spinner )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selection" );
    if( value != null ) {
      spinner.setSelection( Integer.parseInt( value ) );
    }
    if( WidgetLCAUtil.wasEventSent( spinner, JSConst.EVENT_MODIFY_TEXT ) ) {
      ModifyEvent event = new ModifyEvent( spinner );
      event.processEvent();
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    JSWriter writer = JSWriter.getWriterFor( spinner );
    boolean readOnly = ( spinner.getStyle() & RWT.READ_ONLY ) != 0;
    boolean border = ( spinner.getStyle() & RWT.BORDER ) != 0;
    Object[] args = { 
      Boolean.valueOf( readOnly ), 
      Boolean.valueOf( border ) 
    };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Spinner", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Spinner spinner = ( Spinner )widget;
    ControlLCAUtil.writeChanges( spinner );
    writeSetInt( spinner, PROP_MINIMUM, "min", spinner.getMinimum(), 0 );
    writeSetInt( spinner, PROP_MAXIMUM, "max", spinner.getMaximum(), 100 );
    writeSetInt( spinner, 
                 PROP_INCREMENT, 
                 "incrementAmount", 
                 spinner.getIncrement(), 
                 1 );
    writeSetInt( spinner, 
                 PROP_PAGE_INCREMENT, 
                 "pageIncrementAmount", 
                 spinner.getPageIncrement(), 
                 10 );
    writeSetInt( spinner, PROP_SELECTION, "value", spinner.getSelection(), 0 );
    writeModifyListener( spinner );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  //////////////////////////////////////
  // Helping methods to write JavaScript
  
  private static void writeSetInt( final Spinner spinner,
                                   final String javaProperty,
                                   final String jsProperty,
                                   final int newValue,
                                   final int defValue ) 
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( spinner );
    writer.set( javaProperty, 
                jsProperty, 
                new Integer( newValue ), 
                new Integer( defValue ) );
  }

  private static void writeModifyListener( final Spinner spinner ) 
    throws IOException 
  {
    if( ( spinner.getStyle() & RWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( spinner );
      String prop = PROP_MODIFY_LISTENER;
      Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( spinner ) );
      Boolean defValue = Boolean.FALSE;
      writer.set( prop, "hasModifyListener", newValue, defValue );
    }
  }
}
