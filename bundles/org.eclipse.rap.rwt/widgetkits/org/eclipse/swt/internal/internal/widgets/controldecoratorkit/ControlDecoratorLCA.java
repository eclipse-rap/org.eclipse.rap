/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

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
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.Widget;


public class ControlDecoratorLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ControlDecorator";
  private static final String[] ALLOWED_STYLES = new String[] {
    "TOP", "BOTTOM", "LEFT", "RIGHT", "CENTER"
  };

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_VISIBLE = "visible";
  private static final String PROP_SHOW_HOVER = "showHover";
  private static final String PROP_SELECTION_LISTENER = "Selection";
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";

  @Override
  public void preserveValues( Widget widget ) {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.preserveBounds( decorator, decorator.getBounds() );
    preserveProperty( decorator, PROP_TEXT, decorator.getText() );
    preserveProperty( decorator, PROP_IMAGE, decorator.getImage() );
    preserveProperty( decorator, PROP_VISIBLE, decorator.isVisible() );
    preserveProperty( decorator, PROP_SHOW_HOVER, decorator.getShowHover() );
    preserveListener( decorator, PROP_SELECTION_LISTENER, isListening( decorator, SWT.Selection ) );
    preserveListener( decorator,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      isListening( decorator, SWT.DefaultSelection ) );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processSelection( widget, null, false );
    ControlLCAUtil.processDefaultSelection( widget, null );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( decorator );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( decorator.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( decorator, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.renderBounds( decorator, decorator.getBounds() );
    renderProperty( decorator, PROP_TEXT, decorator.getText(), "" );
    renderProperty( decorator, PROP_IMAGE, decorator.getImage(), null );
    renderProperty( decorator, PROP_VISIBLE, decorator.isVisible(), false );
    renderProperty( decorator, PROP_SHOW_HOVER, decorator.getShowHover(), true );
    renderListener( decorator,
                    PROP_SELECTION_LISTENER,
                    isListening( decorator, SWT.Selection ),
                    false );
    renderListener( decorator,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    isListening( decorator, SWT.DefaultSelection ),
                    false );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

}
