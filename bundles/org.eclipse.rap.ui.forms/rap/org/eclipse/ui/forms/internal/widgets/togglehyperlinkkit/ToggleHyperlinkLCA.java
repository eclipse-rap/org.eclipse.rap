/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.*;


/* (intentionally non-JavaDoc'ed)
 * This class serves as the LCA for org.eclipse.ui.forms.widgets.TreeNode and
 * org.eclipse.ui.forms.widgets.Twistie.
 */
@SuppressWarnings("restriction")
public final class ToggleHyperlinkLCA extends AbstractWidgetLCA {

  private static final String TYPE = "forms.widgets.ToggleHyperlink"; //$NON-NLS-1$

  private static final String PROP_IMAGES = "images"; //$NON-NLS-1$
  private static final String PROP_EXPANDED = "expanded"; //$NON-NLS-1$
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection"; //$NON-NLS-1$

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
  public void preserveValues( Widget widget ) {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    ControlLCAUtil.preserveValues( hyperlink );
    WidgetLCAUtil.preserveCustomVariant( hyperlink );
    WidgetLCAUtil.preserveProperty( hyperlink, PROP_EXPANDED, hyperlink.isExpanded() );
    boolean hasListener = isListening( hyperlink, SWT.DefaultSelection );
    WidgetLCAUtil.preserveListener( hyperlink, PROP_DEFAULT_SELECTION_LISTENER, hasListener );
  }

  public void readData( Widget widget ) {
    // It is not neccessary to read the expanded state as a HyperlinkListener
    // will always be registered (see ToggleHyperlink).
    ControlLCAUtil.processSelection( widget, null, false );
    ControlLCAUtil.processDefaultSelection( widget, null );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( hyperlink );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( hyperlink.getParent() ) ); //$NON-NLS-1$
    WidgetLCAUtil.renderProperty( hyperlink, PROP_IMAGES, getImages( hyperlink ), DEFAULT_IMAGES );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ToggleHyperlink hyperlink = ( ToggleHyperlink )widget;
    ControlLCAUtil.renderChanges( hyperlink );
    WidgetLCAUtil.renderCustomVariant( hyperlink );
    WidgetLCAUtil.renderProperty( hyperlink, PROP_EXPANDED, hyperlink.isExpanded(), false );
    boolean hasListener = isListening( hyperlink, SWT.DefaultSelection );
    WidgetLCAUtil.renderListener( hyperlink, PROP_DEFAULT_SELECTION_LISTENER, hasListener, false );
  }

  //////////////////
  // Helping methods

  /* (intentiaonally non-JavaDoc'ed)
   * Returns four images for:
   *   collapsedNormal, collapsedHover, expandedNormal, expandedHover
   */
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

  public static Image getImage( Device device, String path ) {
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
