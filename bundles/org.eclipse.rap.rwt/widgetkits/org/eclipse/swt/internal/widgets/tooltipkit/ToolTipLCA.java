/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tooltipkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readPropertyValue;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IToolTipAdapter;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Widget;


public final class ToolTipLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ToolTip";
  private static final String[] ALLOWED_STYLES = new String[] {
    "BALLOON", "ICON_ERROR", "ICON_INFORMATION", "ICON_WARNING"
  };

  private static final String PROP_AUTO_HIDE = "autoHide";
  private static final String PROP_TEXT = "text";
  private static final String PROP_MESSAGE = "message";
  private static final String PROP_LOCATION = "location";
  private static final String PROP_VISIBLE = "visible";
  private static final String PROP_SELECTION_LISTENER = "Selection";

  private static final Point DEFAULT_LOCATION = new Point( 0, 0 );

  @Override
  public void preserveValues( Widget widget ) {
    ToolTip toolTip = ( ToolTip )widget;
    WidgetLCAUtil.preserveCustomVariant( widget );
    WidgetLCAUtil.preserveRoundedBorder( widget );
    WidgetLCAUtil.preserveBackgroundGradient( widget );
    preserveProperty( toolTip, PROP_AUTO_HIDE, toolTip.getAutoHide() );
    preserveProperty( toolTip, PROP_TEXT, toolTip.getText() );
    preserveProperty( toolTip, PROP_MESSAGE, toolTip.getMessage() );
    preserveProperty( toolTip, PROP_LOCATION, getLocation( toolTip ) );
    preserveProperty( toolTip, PROP_VISIBLE, toolTip.isVisible() );
    preserveListener( toolTip, PROP_SELECTION_LISTENER, isListening( toolTip, SWT.Selection ) );
  }

  public void readData( Widget widget ) {
    ToolTip toolTip = ( ToolTip )widget;
    ControlLCAUtil.processSelection( toolTip, null, false );
    ControlLCAUtil.processDefaultSelection( toolTip, null );
    readVisible( toolTip );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    ToolTip toolTip = ( ToolTip )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( toolTip );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( toolTip.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( toolTip, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ToolTip toolTip = ( ToolTip )widget;
    WidgetLCAUtil.renderCustomVariant( widget );
    WidgetLCAUtil.renderRoundedBorder( widget );
    WidgetLCAUtil.renderBackgroundGradient( widget );
    renderProperty( toolTip, PROP_AUTO_HIDE, toolTip.getAutoHide(), false );
    renderProperty( toolTip, PROP_TEXT, toolTip.getText(), "" );
    renderProperty( toolTip, PROP_MESSAGE, toolTip.getMessage(), "" );
    renderProperty( toolTip, PROP_LOCATION, getLocation( toolTip ), DEFAULT_LOCATION );
    renderProperty( toolTip, PROP_VISIBLE, toolTip.isVisible(), false );
    renderListener( toolTip,
                    PROP_SELECTION_LISTENER,
                    isListening( toolTip, SWT.Selection ),
                    false );
  }

  private static void readVisible( ToolTip toolTip ) {
    String value = readPropertyValue( toolTip, PROP_VISIBLE );
    if( value != null ) {
      toolTip.setVisible( new Boolean( value ).booleanValue() );
    }
  }

  private static Point getLocation( ToolTip toolTip ) {
    return toolTip.getAdapter( IToolTipAdapter.class ).getLocation();
  }
}
