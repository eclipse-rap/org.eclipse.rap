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
package org.eclipse.swt.internal.browser.browserkit;

import java.io.*;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Widget;


public final class BrowserLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.browser.Browser";
  private static final String QX_FIELD_SOURCE = "source";

  private static final String PARAM_EXECUTE_RESULT = "executeResult";

//  private static final String TYPE_POOL_ID = BrowserLCA.class.getName();

  private static final String PROP_URL = "url";
  private static final String PROP_TEXT = "text";


  public void preserveValues( final Widget widget ) {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.preserveValues( browser );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( browser );
    adapter.preserve( PROP_URL, browser.getUrl() );
    adapter.preserve( PROP_TEXT, getText( browser ) );
  }

  public void readData( final Widget widget ) {
    Browser browser = ( Browser )widget;
    String value 
      = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_RESULT );
    if( value != null ) {
      boolean executeResult = Boolean.valueOf( value ).booleanValue();
      getAdapter( browser ).setExecuteResult( executeResult );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    JSWriter writer = JSWriter.getWriterFor( browser );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( browser );
    WidgetLCAUtil.writeCustomAppearance( browser );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    // TODO [rh] though implemented in DefaultAppearanceTheme, setting border 
    //      does not work
    ControlLCAUtil.writeChanges( browser );
    writeUrlAndText( browser );
    writeExecute( browser );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( QX_FIELD_SOURCE );
    ControlLCAUtil.resetStyleFlags();
  }

  public String getTypePoolId( final Widget widget ) {
    // TODO [rh] Disabled pooling. In IE7, using Browser#setText() does not 
    //      work when widget was pooled. The previous content is displayed.
//    return TYPE_POOL_ID;
    return null;
  }

  private static void writeUrlAndText( final Browser browser ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( browser );
    String text = getText( browser );
    String url = browser.getUrl();
    if(    WidgetLCAUtil.hasChanged( browser, PROP_TEXT, text, null )
        || WidgetLCAUtil.hasChanged( browser, PROP_URL, url, null ) )
    {
      if( text != null ) {
        String textUrl = registerHtml( text );
        writer.set( QX_FIELD_SOURCE, textUrl );
      } else if( !"".equals( url ) ) {
        writer.set( QX_FIELD_SOURCE, url );
      } else {
        writer.set( QX_FIELD_SOURCE, "" );
      }
    }
  }

  private static void writeExecute( final Browser browser ) throws IOException {
    IBrowserAdapter adapter = getAdapter( browser );
    String executeScript = adapter.getExecuteScript();
    if( executeScript != null ) {
      JSWriter writer = JSWriter.getWriterFor( browser );
      writer.call( "execute", new Object[] { executeScript } );
    }
  }

  private static String registerHtml( final String html ) throws IOException {
    String name = createUrlFromHtml( html );
    byte[] bytes = html.getBytes( "UTF-8" );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    try {
      // TODO [rh] ResourceManager should be able to deregister a resource,
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

  private static String getText( final Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }

  private static IBrowserAdapter getAdapter( final Browser browser ) {
    return ( IBrowserAdapter )browser.getAdapter( IBrowserAdapter.class );
  }
}
