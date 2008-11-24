/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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
  
  static final String BLANK_HTML = "<html><script></script></html>";

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
    WidgetLCAUtil.preserveCustomVariant( browser );
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
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    // TODO [rh] though implemented in DefaultAppearanceTheme, setting border 
    //      does not work
    ControlLCAUtil.writeChanges( browser );
    writeUrl( browser );
    writeExecute( browser );
    WidgetLCAUtil.writeCustomVariant( browser );
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

  private static void writeUrl( final Browser browser ) 
    throws IOException 
  {
    if( hasUrlChanged( browser ) ) {
      JSWriter writer = JSWriter.getWriterFor( browser );
      writer.set( QX_FIELD_SOURCE, getUrl( browser ) );
    }
  }
  
  static boolean hasUrlChanged( final Browser browser ) {
    boolean initialized = WidgetUtil.getAdapter( browser ).isInitialized();
    return    !initialized
           || WidgetLCAUtil.hasChanged( browser, PROP_TEXT, getText( browser ) )
           || WidgetLCAUtil.hasChanged( browser, PROP_URL, browser.getUrl() );
  }
  
  static String getUrl( final Browser browser ) throws IOException {
    String text = getText( browser );
    String url = browser.getUrl();
    String result;
    if( text != null && !"".equals( text.trim() ) ) {       
      result = registerHtml( text );
    } else if( url != null && !"".equals( url.trim() ) ) {        
      result = url;
    } else {        
      result = registerHtml( BLANK_HTML );
    }
    return result;
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
    ResourceManager.getInstance().register( name, inputStream );
    return ResourceManager.getInstance().getLocation( name );
  }

  private static String createUrlFromHtml( final String html ) {
    StringBuffer result = new StringBuffer();
    result.append( "org.eclipse.swt.browser/text" );
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
