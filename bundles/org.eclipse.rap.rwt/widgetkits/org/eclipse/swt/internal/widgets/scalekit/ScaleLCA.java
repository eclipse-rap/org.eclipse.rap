/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Widget;


public final class ScaleLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Scale";
  private static final String[] ALLOWED_STYLES = new String[] {
    "HORIZONTAL", "VERTICAL", "BORDER"
  };

  // Property names for preserveValues
  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_INCREMENT = "increment";
  static final String PROP_PAGE_INCREMENT = "pageIncrement";
  static final String PROP_SELECTION_LISTENER = "Selection";

  // Default values
  private  static final int DEFAULT_MINIMUM = 0;
  private static final int DEFAULT_MAXIMUM = 100;
  private static final int DEFAULT_SELECTION = 0;
  private static final int DEFAULT_INCREMENT = 1;
  private static final int DEFAULT_PAGE_INCREMENT = 10;

  @Override
  public void preserveValues( Widget widget ) {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.preserveValues( scale );
    WidgetLCAUtil.preserveCustomVariant( scale );
    preserveProperty( scale, PROP_MINIMUM, scale.getMinimum() );
    preserveProperty( scale, PROP_MAXIMUM, scale.getMaximum() );
    preserveProperty( scale, PROP_SELECTION, scale.getSelection() );
    preserveProperty( scale, PROP_INCREMENT, scale.getIncrement() );
    preserveProperty( scale, PROP_PAGE_INCREMENT, scale.getPageIncrement() );
    preserveListener( scale, PROP_SELECTION_LISTENER, isListening( scale, SWT.Selection ) );
  }

  public void readData( Widget widget ) {
    Scale scale = ( Scale )widget;
    String value = WidgetLCAUtil.readPropertyValue( scale, PROP_SELECTION );
    if( value != null ) {
      scale.setSelection( NumberFormatUtil.parseInt( value ) );
    }
    ControlLCAUtil.processSelection( scale, null, true );
    ControlLCAUtil.processKeyEvents( scale );
    ControlLCAUtil.processMouseEvents( scale );
    ControlLCAUtil.processMenuDetect( scale );
    WidgetLCAUtil.processHelp( scale );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( scale );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( scale.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( scale, ALLOWED_STYLES ) ) );
  }


  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Scale scale = ( Scale )widget;
    ControlLCAUtil.renderChanges( scale );
    WidgetLCAUtil.renderCustomVariant( scale );
    renderProperty( scale, PROP_MINIMUM, scale.getMinimum(), DEFAULT_MINIMUM );
    renderProperty( scale, PROP_MAXIMUM, scale.getMaximum(), DEFAULT_MAXIMUM );
    renderProperty( scale, PROP_SELECTION, scale.getSelection(), DEFAULT_SELECTION );
    renderProperty( scale, PROP_INCREMENT, scale.getIncrement(), DEFAULT_INCREMENT );
    renderProperty( scale, PROP_PAGE_INCREMENT, scale.getPageIncrement(), DEFAULT_PAGE_INCREMENT );
    renderListener( scale, PROP_SELECTION_LISTENER, isListening( scale, SWT.Selection ), false );
  }

}
