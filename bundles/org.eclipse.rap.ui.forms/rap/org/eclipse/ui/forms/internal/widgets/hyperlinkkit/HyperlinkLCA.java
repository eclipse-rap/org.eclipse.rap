/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH.
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

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenDefaultSelection;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;


@SuppressWarnings("restriction")
public class HyperlinkLCA extends WidgetLCA<Hyperlink> {

  private static final String TYPE = "forms.widgets.Hyperlink"; //$NON-NLS-1$
  private static final String[] ALLOWED_STYLES = new String[] { "WRAP" }; //$NON-NLS-1$

  private static final String PROP_TEXT = "text"; //$NON-NLS-1$
  private static final String PROP_UNDERLINED = "underlined"; //$NON-NLS-1$
  private static final String PROP_UNDERLINE_MODE = "underlineMode"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_FOREGROUND = "activeForeground"; //$NON-NLS-1$
  private static final String PROP_ACTIVE_BACKGROUND = "activeBackground"; //$NON-NLS-1$

  private static final int DEFAULT_UNDERLINE_MODE = 0;

  @Override
  public void preserveValues( Hyperlink hyperlink ) {
    WidgetLCAUtil.preserveCustomVariant( hyperlink );
    preserveProperty( hyperlink, PROP_TEXT, hyperlink.getText() );
    preserveProperty( hyperlink, PROP_UNDERLINED, hyperlink.isUnderlined() );
    preserveProperty( hyperlink,
                      PROP_UNDERLINE_MODE,
                      getUnderlineMode( hyperlink ) );
    preserveProperty( hyperlink,
                      PROP_ACTIVE_FOREGROUND,
                      getActiveForeground( hyperlink ) );
    preserveProperty( hyperlink,
                      PROP_ACTIVE_BACKGROUND,
                      getActiveBackground( hyperlink ) );
  }

  @Override
  public void renderInitialization( Hyperlink hyperlink ) throws IOException {
    RemoteObject remoteObject = createRemoteObject( hyperlink, TYPE );
    remoteObject.setHandler( new HyperlinkOperationHandler( hyperlink ) );
    remoteObject.set( "parent", getId( hyperlink.getParent() ) ); //$NON-NLS-1$
    remoteObject.set( "style", createJsonArray( getStyles( hyperlink, ALLOWED_STYLES ) ) ); //$NON-NLS-1$
  }

  @Override
  public void renderChanges( Hyperlink hyperlink ) throws IOException {
    ControlLCAUtil.renderChanges( hyperlink );
    WidgetLCAUtil.renderCustomVariant( hyperlink );
    renderProperty( hyperlink, PROP_TEXT, hyperlink.getText(), "" ); //$NON-NLS-1$
    renderProperty( hyperlink, PROP_UNDERLINED, hyperlink.isUnderlined(), false );
    renderProperty( hyperlink,
                    PROP_UNDERLINE_MODE,
                    getUnderlineMode( hyperlink ),
                    DEFAULT_UNDERLINE_MODE );
    renderProperty( hyperlink,
                    PROP_ACTIVE_FOREGROUND,
                    getActiveForeground( hyperlink ),
                    null );
    renderProperty( hyperlink,
                    PROP_ACTIVE_BACKGROUND,
                    getActiveBackground( hyperlink ),
                    null );
    renderListenDefaultSelection( hyperlink );
  }

  //////////////////
  // Helping methods

  private static Color getActiveForeground( Hyperlink hyperlink ) {
    return getAdapter( hyperlink ).getActiveForeground();
  }

  private static Color getActiveBackground( Hyperlink hyperlink ) {
    return getAdapter( hyperlink ).getActiveBackground();
  }

  private static int getUnderlineMode( Hyperlink hyperlink ) {
    return getAdapter( hyperlink ).getUnderlineMode();
  }

  private static IHyperlinkAdapter getAdapter( Hyperlink hyperlink ) {
    return hyperlink.getAdapter( IHyperlinkAdapter.class );
  }

}
