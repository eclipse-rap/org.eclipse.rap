/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;


@SuppressWarnings("restriction")
public class HyperlinkLCA extends AbstractWidgetLCA {

  private static final String TYPE = "forms.widgets.Hyperlink"; //$NON-NLS-1$
  private static final String[] ALLOWED_STYLES = new String[] { "WRAP" }; //$NON-NLS-1$

  private static final String PROP_TEXT = "text"; //$NON-NLS-1$
  private static final String PROP_UNDERLINED = "underlined"; //$NON-NLS-1$
  private static final String PROP_UNDERLINE_MODE = "underlineMode"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_FOREGROUND = "activeForeground"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_BACKGROUND = "activeBackground"; //$NON-NLS-1$
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection"; //$NON-NLS-1$

  private static final int DEFAULT_UNDERLINE_MODE = 0;

  public void readData( Widget widget ) {
    ControlLCAUtil.processSelection( widget, null, false );
    ControlLCAUtil.processDefaultSelection( widget, null );
    WidgetLCAUtil.processHelp( widget );
  }

  @Override
  public void preserveValues( Widget widget ) {
    Hyperlink hyperlink = ( Hyperlink )widget;
    ControlLCAUtil.preserveValues( hyperlink );
    WidgetLCAUtil.preserveCustomVariant( hyperlink );
    WidgetLCAUtil.preserveProperty( hyperlink, PROP_TEXT, hyperlink.getText() );
    WidgetLCAUtil.preserveProperty( hyperlink, PROP_UNDERLINED, hyperlink.isUnderlined() );
    WidgetLCAUtil.preserveProperty( hyperlink, PROP_UNDERLINE_MODE, getUnderlineMode( hyperlink ) );
    WidgetLCAUtil.preserveProperty( hyperlink,
                                    PROP_ACTIVE_FOREGROUND,
                                    getActiveForeground( hyperlink ) );
    WidgetLCAUtil.preserveProperty( hyperlink,
                                    PROP_ACTIVE_BACKGROUND,
                                    getActiveBackground( hyperlink ) );
    boolean hasListener = hyperlink.isListening( SWT.DefaultSelection );
    WidgetLCAUtil.preserveListener( hyperlink, PROP_DEFAULT_SELECTION_LISTENER, hasListener );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Hyperlink hyperlink = ( Hyperlink )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( hyperlink );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( hyperlink.getParent() ) ); //$NON-NLS-1$
    clientObject.set( "style", WidgetLCAUtil.getStyles( hyperlink, ALLOWED_STYLES ) ); //$NON-NLS-1$
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Hyperlink hyperlink = ( Hyperlink )widget;
    ControlLCAUtil.renderChanges( hyperlink );
    WidgetLCAUtil.renderCustomVariant( widget );
    WidgetLCAUtil.renderProperty( hyperlink, PROP_TEXT, hyperlink.getText(), "" ); //$NON-NLS-1$
    WidgetLCAUtil.renderProperty( hyperlink, PROP_UNDERLINED, hyperlink.isUnderlined(), false );
    WidgetLCAUtil.renderProperty( hyperlink,
                                  PROP_UNDERLINE_MODE,
                                  getUnderlineMode( hyperlink ),
                                  DEFAULT_UNDERLINE_MODE );
    WidgetLCAUtil.renderProperty( hyperlink,
                                  PROP_ACTIVE_FOREGROUND,
                                  getActiveForeground( hyperlink ),
                                  null );
    WidgetLCAUtil.renderProperty( hyperlink,
                                  PROP_ACTIVE_BACKGROUND,
                                  getActiveBackground( hyperlink ),
                                  null );
    boolean hasListener = hyperlink.isListening( SWT.DefaultSelection );
    WidgetLCAUtil.renderListener( hyperlink, PROP_DEFAULT_SELECTION_LISTENER, hasListener, false );
  }

  //////////////////
  // Helping methods

  private static Color getActiveForeground( Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    return hyperlinkAdapter.getActiveForeground();
  }

  private static Color getActiveBackground( Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    return hyperlinkAdapter.getActiveBackground();
  }

  private static int getUnderlineMode( Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    IHyperlinkAdapter hyperlinkAdapter = ( IHyperlinkAdapter )adapter;
    return hyperlinkAdapter.getUnderlineMode();
  }

}
