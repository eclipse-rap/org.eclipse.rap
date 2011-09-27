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

package org.eclipse.swt.internal.widgets.sliderkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;


public class SliderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Slider";

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_THUMB = "thumb";
  static final String PROP_SELECTION_LISTENER = "selection";

  // Default values
  static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  static final Integer DEFAULT_SELECTION = new Integer( 0 );
  static final Integer DEFAULT_INCREMENT = new Integer( 1 );
  static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );
  static final Integer DEFAULT_THUMB = new Integer( 10 );

  public void preserveValues( Widget widget ) {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.preserveValues( slider );
    WidgetLCAUtil.preserveCustomVariant( slider );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( slider );
    adapter.preserve( PROP_MINIMUM, new Integer( slider.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( slider.getMaximum() ) );
    adapter.preserve( PROP_SELECTION, new Integer( slider.getSelection() ) );
    adapter.preserve( PROP_INCREMENT, new Integer( slider.getIncrement() ) );
    adapter.preserve( PROP_PAGE_INCREMENT, new Integer( slider.getPageIncrement() ) );
    adapter.preserve( PROP_THUMB, new Integer( slider.getThumb() ) );
    preserveListener( slider, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( slider ) );
  }

  public void readData( Widget widget ) {
    Slider slider = ( Slider )widget;
    String value = WidgetLCAUtil.readPropertyValue( slider, PROP_SELECTION );
    if( value != null ) {
      slider.setSelection( NumberFormatUtil.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( slider, null, true );
    ControlLCAUtil.processMenuDetect( slider );
    WidgetLCAUtil.processHelp( slider );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Slider slider = ( Slider )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( slider );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( slider.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( slider ) );
  }


  public void renderChanges( Widget widget ) throws IOException {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.renderChanges( slider );
    WidgetLCAUtil.renderCustomVariant( widget );
    renderMinimum( slider );
    renderMaximum( slider );
    renderSelection( slider );
    renderIncrement( slider );
    renderPageIncrement( slider );
    renderThumb( slider );
    renderListenSelection( slider );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderMinimum( Slider slider ) {
    renderProperty( slider, PROP_MINIMUM, new Integer( slider.getMinimum() ), DEFAULT_MINIMUM );
  }

  private static void renderMaximum( Slider slider ) {
    renderProperty( slider, PROP_MAXIMUM, new Integer( slider.getMaximum() ), DEFAULT_MAXIMUM );
  }

  private static void renderSelection( Slider slider ) {
    Integer defValue = DEFAULT_SELECTION;
    renderProperty( slider, PROP_SELECTION, new Integer( slider.getSelection() ), defValue );
  }

  private static void renderIncrement( Slider slider ) {
    Integer defValue = DEFAULT_INCREMENT;
    renderProperty( slider, PROP_INCREMENT, new Integer( slider.getIncrement() ), defValue );
  }

  private static void renderPageIncrement( Slider slider ) {
    String prop = PROP_PAGE_INCREMENT;
    Integer defValue = DEFAULT_PAGE_INCREMENT;
    renderProperty( slider, prop, new Integer( slider.getPageIncrement() ), defValue );
  }

  private static void renderThumb( Slider slider ) {
    renderProperty( slider, PROP_THUMB, new Integer( slider.getThumb() ), DEFAULT_THUMB );
  }

  private static void renderListenSelection( Slider slider ) {
    renderListener( slider, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( slider ), false );
  }
}
