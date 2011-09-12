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
package org.eclipse.swt.internal.widgets.scalekit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;


public final class ScaleLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Scale";

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  // Default values
  static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  static final Integer DEFAULT_SELECTION = new Integer( 0 );
  static final Integer DEFAULT_INCREMENT = new Integer( 1 );
  static final Integer DEFAULT_PAGE_INCREMENT = new Integer( 10 );

  public void preserveValues( Widget widget ) {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.preserveValues( scale );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    adapter.preserve( PROP_MINIMUM, new Integer( scale.getMinimum() ) );
    adapter.preserve( PROP_MAXIMUM, new Integer( scale.getMaximum() ) );
    adapter.preserve( PROP_SELECTION, new Integer( scale.getSelection() ) );
    adapter.preserve( PROP_INCREMENT, new Integer( scale.getIncrement() ) );
    adapter.preserve( PROP_PAGE_INCREMENT, new Integer( scale.getPageIncrement() ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( scale ) ) );
    WidgetLCAUtil.preserveCustomVariant( scale );
  }

  public void readData( Widget widget ) {
    Scale scale = ( Scale )widget;
    String value = WidgetLCAUtil.readPropertyValue( scale, PROP_SELECTION );
    if( value != null ) {
      scale.setSelection( NumberFormatUtil.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( scale, null, true );
    ControlLCAUtil.processKeyEvents( scale );
    ControlLCAUtil.processMenuDetect( scale );
    WidgetLCAUtil.processHelp( scale );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( scale );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( scale.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( scale ) );
  }


  public void renderChanges( Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.renderChanges( scale );
    WidgetLCAUtil.renderCustomVariant( scale );
    renderMinimum( scale );
    renderMaximum( scale );
    renderSelection( scale );
    renderIncrement( scale );
    renderPageIncrement( scale );
    renderListenSelection( scale );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ////////////////////////////////////////
  // Helping methods to render the changes

  private static void renderMinimum( Scale scale ) {
    renderProperty( scale, PROP_MINIMUM, new Integer( scale.getMinimum() ), DEFAULT_MINIMUM );
  }

  private static void renderMaximum( Scale scale ) {
    renderProperty( scale, PROP_MAXIMUM, new Integer( scale.getMaximum() ), DEFAULT_MAXIMUM );
  }

  private static void renderSelection( Scale scale ) {
    Integer defValue = DEFAULT_SELECTION;
    renderProperty( scale, PROP_SELECTION, new Integer( scale.getSelection() ), defValue );
  }

  private static void renderIncrement( Scale scale ) {
    Integer defValue = DEFAULT_INCREMENT;
    renderProperty( scale, PROP_INCREMENT, new Integer( scale.getIncrement() ), defValue );
  }

  private static void renderPageIncrement( Scale scale ) {
    String prop = PROP_PAGE_INCREMENT;
    Integer defValue = DEFAULT_PAGE_INCREMENT;
    renderProperty( scale, prop, new Integer( scale.getPageIncrement() ), defValue );
  }

  private static void renderListenSelection( Scale scale ) {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( scale ) );
    if( WidgetLCAUtil.hasChanged( scale, PROP_SELECTION_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( scale, "selection", newValue.booleanValue() );
    }
  }

  //////////////////
  // Helping methods

  private static void renderProperty( Scale scale,
                                      String property,
                                      Object newValue,
                                      Object defValue )
  {
    if( WidgetLCAUtil.hasChanged( scale, property, newValue, defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( scale );
      clientObject.setProperty( property, newValue );
    }
  }

  private static void renderListen( Scale scale, String eventType, boolean hasListener ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( scale );
    if( hasListener ) {
      clientObject.addListener( eventType );
    } else {
      clientObject.removeListener( eventType );
    }
  }

}
