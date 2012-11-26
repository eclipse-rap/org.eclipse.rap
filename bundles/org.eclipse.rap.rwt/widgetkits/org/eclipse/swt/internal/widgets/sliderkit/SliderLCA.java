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
package org.eclipse.swt.internal.widgets.sliderkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Widget;


public class SliderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Slider";
  private static final String[] ALLOWED_STYLES = new String[] {
    "HORIZONTAL", "VERTICAL", "BORDER"
  };

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_THUMB = "thumb";
  static final String PROP_SELECTION_LISTENER = "Selection";

  // Default values
  private static final int DEFAULT_MINIMUM = 0;
  private static final int DEFAULT_MAXIMUM = 100;
  private static final int DEFAULT_SELECTION = 0;
  private static final int DEFAULT_INCREMENT = 1;
  private static final int DEFAULT_PINCREMENT = 10;
  private static final int DEFAULT_THUMB = 10;

  public void preserveValues( Widget widget ) {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.preserveValues( slider );
    WidgetLCAUtil.preserveCustomVariant( slider );
    preserveProperty( slider, PROP_MINIMUM, slider.getMinimum() );
    preserveProperty( slider, PROP_MAXIMUM, slider.getMaximum() );
    preserveProperty( slider, PROP_SELECTION, slider.getSelection() );
    preserveProperty( slider, PROP_INCREMENT, slider.getIncrement() );
    preserveProperty( slider, PROP_PAGE_INCREMENT, slider.getPageIncrement() );
    preserveProperty( slider, PROP_THUMB, slider.getThumb() );
    preserveListener( slider, PROP_SELECTION_LISTENER, slider.isListening( SWT.Selection ) );
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
    IClientObject clientObject = ClientObjectFactory.getClientObject( slider );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( slider.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( slider, ALLOWED_STYLES ) );
  }


  public void renderChanges( Widget widget ) throws IOException {
    Slider slider = ( Slider )widget;
    ControlLCAUtil.renderChanges( slider );
    WidgetLCAUtil.renderCustomVariant( widget );
    renderProperty( slider, PROP_MINIMUM, slider.getMinimum(), DEFAULT_MINIMUM );
    renderProperty( slider, PROP_MAXIMUM, slider.getMaximum(), DEFAULT_MAXIMUM );
    renderProperty( slider, PROP_SELECTION, slider.getSelection(), DEFAULT_SELECTION );
    renderProperty( slider, PROP_INCREMENT, slider.getIncrement(), DEFAULT_INCREMENT );
    renderProperty( slider, PROP_PAGE_INCREMENT, slider.getPageIncrement(), DEFAULT_PINCREMENT );
    renderProperty( slider, PROP_THUMB, slider.getThumb(), DEFAULT_THUMB );
    renderListener( slider, PROP_SELECTION_LISTENER, slider.isListening( SWT.Selection ), false );
  }

}
