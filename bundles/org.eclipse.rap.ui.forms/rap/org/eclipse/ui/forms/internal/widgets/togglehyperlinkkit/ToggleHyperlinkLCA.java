/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenDefaultSelection;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.*;


/* (intentionally non-JavaDoc'ed)
 * This class serves as the LCA for org.eclipse.ui.forms.widgets.TreeNode and
 * org.eclipse.ui.forms.widgets.Twistie.
 */
@SuppressWarnings("restriction")
public final class ToggleHyperlinkLCA extends WidgetLCA<ToggleHyperlink> {

  public static final ToggleHyperlinkLCA INSTANCE = new ToggleHyperlinkLCA();

  private static final String TYPE = "forms.widgets.ToggleHyperlink"; //$NON-NLS-1$

  private static final String PROP_IMAGES = "images"; //$NON-NLS-1$
  private static final String PROP_EXPANDED = "expanded"; //$NON-NLS-1$

  private static final String PREFIX = "resource/widget/rap/hyperlink/"; //$NON-NLS-1$
  private static final String MINUS_GIF = PREFIX + "minus.gif"; //$NON-NLS-1$
  private static final String PLUS_GIF = PREFIX + "plus.gif"; //$NON-NLS-1$

  private static final String TWISTIE_COLLAPSE_GIF = PREFIX + "twistie_collapse.gif"; //$NON-NLS-1$
  private static final String TWISTIE_COLLAPSE_HOVER_GIF
    = PREFIX + "twistie_collapse_hover.gif"; //$NON-NLS-1$
  private static final String TWISTIE_EXPAND_GIF = PREFIX + "twistie_expand.gif"; //$NON-NLS-1$
  private static final String TWISTIE_EXPAND_HOVER_GIF
    = PREFIX + "twistie_expand_hover.gif"; //$NON-NLS-1$

  private static final Image[] DEFAULT_IMAGES = new Image[] { null, null, null, null };

  @Override
  public void preserveValues( ToggleHyperlink hyperlink ) {
    preserveProperty( hyperlink, PROP_EXPANDED, hyperlink.isExpanded() );
  }

  @Override
  public void renderInitialization( ToggleHyperlink hyperlink ) throws IOException {
    RemoteObject remoteObject = createRemoteObject( hyperlink, TYPE );
    remoteObject.setHandler( new ToggleHyperlinkOperationHandler( hyperlink ) );
    remoteObject.set( "parent", WidgetUtil.getId( hyperlink.getParent() ) ); //$NON-NLS-1$
    renderProperty( hyperlink, PROP_IMAGES, getImages( hyperlink ), DEFAULT_IMAGES );
  }

  @Override
  public void renderChanges( ToggleHyperlink hyperlink ) throws IOException {
    ControlLCAUtil.renderChanges( hyperlink );
    WidgetLCAUtil.renderCustomVariant( hyperlink );
    renderProperty( hyperlink, PROP_EXPANDED, hyperlink.isExpanded(), false );
    renderListenDefaultSelection( hyperlink );
  }

  private static Image[] getImages( ToggleHyperlink hyperlink ) {
    Display display = hyperlink.getDisplay();
    Image[] result;
    if( hyperlink instanceof TreeNode ) {
      result = new Image[] {
        getImage( display, MINUS_GIF ),
        null,
        getImage( display, PLUS_GIF ),
        null
      };
    } else if( hyperlink instanceof Twistie ) {
      result = new Image[] {
        getImage( display, TWISTIE_COLLAPSE_GIF ),
        getImage( display, TWISTIE_COLLAPSE_HOVER_GIF ),
        getImage( display, TWISTIE_EXPAND_GIF ),
        getImage( display, TWISTIE_EXPAND_HOVER_GIF )
      };
    } else {
      result = new Image[] { null, null, null, null };
    }
    return result;
  }

  private static Image getImage( Device device, String path ) {
    ClassLoader classLoader = ToggleHyperlinkLCA.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( path );
    Image result = null;
    if( inputStream != null ) {
      try {
        result = new Image( device, inputStream );
      } finally {
        try {
          inputStream.close();
        } catch( IOException e ) {
          // ignore
        }
      }
    }
    return result;
  }

}
