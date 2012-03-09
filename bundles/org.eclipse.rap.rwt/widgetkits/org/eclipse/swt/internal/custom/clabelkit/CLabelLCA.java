/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;


public final class CLabelLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Label";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SHADOW_IN", "SHADOW_OUT", "SHADOW_NONE", "BORDER"
  };

  static final String PROP_TEXT = "text";
  static final String PROP_IMAGE = "image";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_LEFT_MARGIN = "leftMargin";
  static final String PROP_TOP_MARGIN = "topMargin";
  static final String PROP_RIGHT_MARGIN = "rightMargin";
  static final String PROP_BOTTOM_MARGIN = "bottomMargin";

  private static final String DEFAULT_ALIGNMENT = "left";

  public void preserveValues( Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.preserveValues( label );
    WidgetLCAUtil.preserveCustomVariant( label );
    preserveProperty( label, PROP_TEXT, label.getText() );
    preserveProperty( label, PROP_IMAGE, label.getImage() );
    preserveProperty( label, PROP_ALIGNMENT, getAlignment( label ) );
    preserveProperty( label, PROP_LEFT_MARGIN, label.getLeftMargin() );
    preserveProperty( label, PROP_TOP_MARGIN, label.getTopMargin() );
    preserveProperty( label, PROP_RIGHT_MARGIN, label.getRightMargin() );
    preserveProperty( label, PROP_BOTTOM_MARGIN, label.getBottomMargin() );
    WidgetLCAUtil.preserveBackgroundGradient( label );
  }

  public void readData( Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    CLabel clabel = ( CLabel )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( clabel );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( clabel.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( clabel, ALLOWED_STYLES ) );
    // NOTE : This is consistent with Tree and Table, but might change - See Bug 373764
    clientObject.set( "appearance", "clabel" );
  }

  public void renderChanges( Widget widget ) throws IOException {
    CLabel clabel = ( CLabel )widget;
    ControlLCAUtil.renderChanges( clabel );
    WidgetLCAUtil.renderCustomVariant( clabel );
    renderProperty( clabel, PROP_TEXT, clabel.getText(), null );
    renderProperty( clabel, PROP_IMAGE, clabel.getImage(), null );
    renderProperty( clabel, PROP_ALIGNMENT, getAlignment( clabel ), DEFAULT_ALIGNMENT );
    renderMargins( clabel );
    WidgetLCAUtil.renderBackgroundGradient( clabel );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderMargins( CLabel clabel ) {
    Rectangle padding = getThemeAdapter( clabel ).getPadding( clabel );
    renderProperty( clabel, PROP_LEFT_MARGIN, clabel.getLeftMargin(), padding.x );
    renderProperty( clabel, PROP_TOP_MARGIN, clabel.getTopMargin(), padding.y );
    int defRightMargin = padding.width - padding.x;
    renderProperty( clabel, PROP_RIGHT_MARGIN, clabel.getRightMargin(), defRightMargin );
    int defBottomMargin = padding.height - padding.y;
    renderProperty( clabel, PROP_BOTTOM_MARGIN, clabel.getBottomMargin(), defBottomMargin );
  }

  //////////////////
  // Helping methods

  private static String getAlignment( CLabel clabel ) {
    int alignment = clabel.getAlignment();
    String result;
    if( ( alignment & SWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    } else {
      result = "left";
    }
    return result;
  }

  private static CLabelThemeAdapter getThemeAdapter( CLabel clabel ) {
    Object adapter = clabel.getAdapter( IThemeAdapter.class );
    return ( CLabelThemeAdapter )adapter;
  }
}
