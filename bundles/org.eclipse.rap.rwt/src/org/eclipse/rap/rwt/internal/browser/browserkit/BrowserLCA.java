/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.browser.browserkit;

import java.io.*;
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.ResourceManager;
import org.eclipse.rap.rwt.widgets.Widget;


public class BrowserLCA extends AbstractWidgetLCA {

  private static final String QX_FIELD_SOURCE = "source";
  
  private static final String PROP_URL = "url";
  private static final String PROP_TEXT = "text";

  public void preserveValues( final Widget widget ) {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.preserveValues( browser );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( browser );
    adapter.preserve( PROP_URL, browser.getUrl() );
    adapter.preserve( PROP_TEXT, browser.getText() );
  }

  public void readData( final Widget widget ) {
    // TODO Auto-generated method stub
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    JSWriter writer = JSWriter.getWriterFor( browser );
    writer.newWidget( "qx.ui.embed.Iframe" );
    writer.set( "appearance", "browser" );
    // TODO [rh] nice-to-have: prevent popup menu from showing, disable widget
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    // TODO [rh] though implemented in DefaultAppearanceThe, setting border does
    //      not work
    ControlLCAUtil.writeChanges( browser );
    JSWriter writer = JSWriter.getWriterFor( browser );
    if(    WidgetUtil.hasChanged( widget, PROP_TEXT, browser.getText(), null ) 
        || WidgetUtil.hasChanged( widget, PROP_TEXT, browser.getUrl(), null ) ) 
    {
      if( browser.getText() != null ) {
        String url = registerHtml( browser.getText() );
        writer.set( PROP_URL, QX_FIELD_SOURCE, url );
      } else if( !"".equals( browser.getUrl() ) ) {
        writer.set( QX_FIELD_SOURCE, browser.getUrl() );
      } else {
        writer.set( QX_FIELD_SOURCE, "" );
      }
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static String registerHtml( final String html ) throws IOException {
    String name = createUrlFromHtml( html );
    byte[] bytes = html.getBytes( "UTF-8" );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    try {
      // TODO [rh] ResourceManager should be alble to deregister a resource,
      //      thus we could cleanup the here registered resource when text
      //      is changed and in renderDispose
      ResourceManager.getInstance().register( name, inputStream );
    } finally {
      inputStream.close();
    }
    return ResourceManager.getInstance().getLocation( name );
  }

  private static String createUrlFromHtml( final String html ) {
    StringBuffer result = new StringBuffer();
    result.append( "browsertext" );
    result.append( String.valueOf( html.hashCode() ) );
    result.append( ".html" );
    return result.toString();
  }
}
