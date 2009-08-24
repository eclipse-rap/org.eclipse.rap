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

package org.eclipse.swt.internal.widgets.sliderkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Widget;


public class SliderLCA extends AbstractWidgetLCA {
  
  // Property names for preserveValues
  static final String PROP_SELECTION = "selection";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_THUMB = "thumb";
  
  // Default values
  static final Integer DEFAULT_SELECTION = new Integer( 0 );
  static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );
  static final Integer DEFAULT_INCREMENT = new Integer( 1 );
  static final Integer DEFAULT_THUMB = new Integer( 10 );

  public void preserveValues( final Widget widget ) {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.preserveValues( slider );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( slider );
    boolean hasListeners = SelectionEvent.hasListener( slider );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_SELECTION, 
                      new Integer( slider.getSelection() ) );
    adapter.preserve( PROP_MAXIMUM, 
                      new Integer( slider.getMaximum() ) );
    adapter.preserve( PROP_MINIMUM, 
                      new Integer( slider.getMinimum() ) );
    adapter.preserve( PROP_PAGE_INCREMENT, 
                      new Integer( slider.getPageIncrement() ) );
    adapter.preserve( PROP_INCREMENT, 
                      new Integer( slider.getIncrement() ) );
    adapter.preserve( PROP_THUMB, 
                      new Integer( slider.getThumb() ) );
  }

  public void readData( final Widget widget ) {
    Slider slider = ( Slider )widget;
    String value = WidgetLCAUtil.readPropertyValue( slider, PROP_SELECTION );
    if( value != null ) {
      slider.setSelection( Integer.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( slider, null, true );
    WidgetLCAUtil.processHelp( slider );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Slider slider = ( Slider )widget;
    JSWriter writer = JSWriter.getWriterFor( slider );
    String style = "";
    if( ( slider.getStyle() & SWT.HORIZONTAL ) != 0 ) {
      style = "horizontal";
    } else {
      style = "vertical";
    }
    Object[] args = new Object[]{
      style
    };
    writer.newWidget( "org.eclipse.swt.widgets.Slider", args );
    WidgetLCAUtil.writeCustomVariant( widget );
    ControlLCAUtil.writeStyleFlags( slider );
  }

  
  public void renderChanges( final Widget widget ) throws IOException {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.writeChanges( slider );
    writeMaximum( slider );
    writeMinimum( slider );
    writePageIncrement( slider );
    writeSelection( slider );
    writeIncrement( slider );
    writeThumb( slider );
    writeListener( slider );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////
  // Helping methods
  private void writeMaximum( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getMaximum() );
    if( WidgetLCAUtil.hasChanged( slider, 
                                  PROP_MAXIMUM,
                                  newValue, 
                                  DEFAULT_MAXIMUM  ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_MAXIMUM, newValue );
    }
  }
  
  private void writeMinimum( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getMinimum() );
    String prop = PROP_MINIMUM;
    Integer defValue = DEFAULT_MINIMUM;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_MINIMUM, newValue );
    }
  }
  
  private void writeSelection( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getSelection() );
    String prop = PROP_SELECTION;
    Integer defValue = DEFAULT_SELECTION;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_SELECTION, newValue );
    }
  }
  
  private void writeIncrement( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getIncrement() );
    String prop = PROP_INCREMENT;
    Integer defValue = DEFAULT_INCREMENT;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_INCREMENT, newValue );
    }
  }
  
  private void writePageIncrement( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getPageIncrement() );
    String prop = PROP_PAGE_INCREMENT;
    Integer defValue = DEFAULT_PAGE_INCREMENT;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_PAGE_INCREMENT, newValue );
    }
  }
  
  private void writeThumb( final Slider slider ) throws IOException {
    Integer newValue = new Integer( slider.getThumb() );
    String prop = PROP_THUMB;
    Integer defValue = DEFAULT_THUMB;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( PROP_THUMB, newValue );
    }
  }
  
  private void writeListener( final Slider slider ) throws IOException {  
    boolean hasListener = SelectionEvent.hasListener( slider );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( slider, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( slider );
      writer.set( "hasSelectionListener", newValue );
    }
  }
 
}
