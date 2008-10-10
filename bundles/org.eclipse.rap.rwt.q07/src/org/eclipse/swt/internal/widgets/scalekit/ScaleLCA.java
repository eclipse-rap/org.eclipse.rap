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
package org.eclipse.swt.internal.widgets.scalekit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Widget;


public final class ScaleLCA extends AbstractWidgetLCA {

  // Property names for preserveValues
  static final String PROP_SELECTION = "selection";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_INCREMENT = "increment";

  // Default values
  static final Integer DEFAULT_SELECTION = new Integer( 0 );
  static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );
  static final Integer DEFAULT_INCREMENT = new Integer( 1 );

  public void preserveValues( final Widget widget ) {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.preserveValues( scale );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    boolean hasListeners = SelectionEvent.hasListener( scale );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_SELECTION,
                      new Integer( scale.getSelection() ) );
    adapter.preserve( PROP_MAXIMUM,
                      new Integer( scale.getMaximum() ) );
    adapter.preserve( PROP_MINIMUM,
                      new Integer( scale.getMinimum() ) );
    adapter.preserve( PROP_PAGE_INCREMENT,
                      new Integer( scale.getPageIncrement() ) );
    adapter.preserve( PROP_INCREMENT,
                      new Integer( scale.getIncrement() ) );
    WidgetLCAUtil.preserveCustomVariant( scale );
  }

  public void readData( final Widget widget ) {
    Scale scale = ( Scale )widget;
    String value = WidgetLCAUtil.readPropertyValue( scale, PROP_SELECTION );
    if( value != null ) {
      scale.setSelection( Integer.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( scale, null, true );
    ControlLCAUtil.processKeyEvents( scale );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    JSWriter writer = JSWriter.getWriterFor( scale );
    String style;
    if( ( scale.getStyle() & SWT.HORIZONTAL ) != 0 ) {
      style = "horizontal";
    } else {
      style = "vertical";
    }
    Object[] args = new Object[]{
      style
    };
    writer.newWidget( "org.eclipse.swt.widgets.Scale", args );    
    ControlLCAUtil.writeStyleFlags( scale );
  }


  public void renderChanges( final Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.writeChanges( scale );
    writeMaximum( scale );
    writeMinimum( scale );
    writePageIncrement( scale );
    writeSelection( scale );
    writeIncrement( scale );
    writeListener( scale );
    WidgetLCAUtil.writeCustomVariant( scale );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  //////////////////
  // Helping methods

  private void writeMaximum( final Scale scale ) throws IOException {
    Integer newValue = new Integer( scale.getMaximum() );
    String prop = PROP_MAXIMUM;
    Integer defValue = DEFAULT_MAXIMUM;
    if( WidgetLCAUtil.hasChanged( scale, prop, newValue, defValue  ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( prop, newValue );
    }
  }

  private void writeMinimum( final Scale scale ) throws IOException {
    Integer newValue = new Integer( scale.getMinimum() );
    String prop = PROP_MINIMUM;
    Integer defValue = DEFAULT_MINIMUM;
    if( WidgetLCAUtil.hasChanged( scale, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( prop, newValue );
    }
  }

  private void writeSelection( final Scale scale ) throws IOException {
    Integer newValue = new Integer( scale.getSelection() );
    String prop = PROP_SELECTION;
    Integer defValue = DEFAULT_SELECTION;
    if( WidgetLCAUtil.hasChanged( scale, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( prop, newValue );
    }
  }

  private void writeIncrement( final Scale scale ) throws IOException {
    Integer newValue = new Integer( scale.getIncrement() );
    String prop = PROP_INCREMENT;
    Integer defValue = DEFAULT_INCREMENT;
    if( WidgetLCAUtil.hasChanged( scale, prop,  newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( prop, newValue );
    }
  }

  private void writePageIncrement( final Scale scale ) throws IOException {
    Integer newValue = new Integer( scale.getPageIncrement() );
    String prop = PROP_PAGE_INCREMENT;
    Integer defValue = DEFAULT_PAGE_INCREMENT;
    if( WidgetLCAUtil.hasChanged( scale, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( prop, newValue );
    }
  }

  private void writeListener( final Scale scale ) throws IOException {
    boolean hasListener = SelectionEvent.hasListener( scale );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( scale, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( scale );
      writer.set( "hasSelectionListener", newValue );
    }
  }

}
