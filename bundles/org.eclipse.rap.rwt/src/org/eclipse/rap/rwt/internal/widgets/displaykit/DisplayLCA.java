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

package org.eclipse.rap.rwt.internal.widgets.displaykit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.rap.rwt.events.RWTEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.ResourceManager;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.*;
import com.w4t.engine.requests.RequestParams;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

public class DisplayLCA implements IDisplayLifeCycleAdapter {

  private static final class RenderVisitor extends AllWidgetTreeVisitor {

    private IOException ioProblem = null;

    public boolean doVisit( final Widget widget ) {
      boolean result = true;
      try {
        render( widget );
        runRenderRunnable( widget );
      } catch( final IOException ioe ) {
        ioProblem = ioe;
        result = false;
      }
      return result;
    }

    private void reThrowProblem() throws IOException {
      if( ioProblem != null ) {
        throw ioProblem;
      }
    }
    
    private static void render( final Widget widget ) throws IOException {
      AbstractWidgetLCA lca = WidgetUtil.getLCA( widget );
      lca.render( widget );
    }
    
    private static void runRenderRunnable( final Widget widget ) 
      throws IOException 
    {
      WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
      if( adapter.getRenderRunnable() != null ) {
        adapter.getRenderRunnable().afterRender();
        adapter.clearRenderRunnable();
      }
    }
  }

  private static final String CLIENT_LOG_LEVEL 
    = "org.eclipse.rap.rwt.clientLogLevel";

  // Maps Java Level to the closest qooxdoo log level
  private static final Map LOG_LEVEL_MAP = new HashMap( 8 + 1, 1f );

  static {
    // Available qooxdoo log level: 
    //   LEVEL_OFF, LEVEL_ALL, 
    //   LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARN, LEVEL_ERROR, LEVEL_FATAL
    LOG_LEVEL_MAP.put( Level.OFF, "qx.dev.log.Logger.LEVEL_OFF" );
    LOG_LEVEL_MAP.put( Level.ALL, "qx.dev.log.Logger.LEVEL_ALL" );
    LOG_LEVEL_MAP.put( Level.WARNING, "qx.dev.log.Logger.LEVEL_WARN" );
    LOG_LEVEL_MAP.put( Level.INFO, "qx.dev.log.Logger.LEVEL_INFO" );
    LOG_LEVEL_MAP.put( Level.SEVERE, "qx.dev.log.Logger.LEVEL_ERROR" );
    LOG_LEVEL_MAP.put( Level.FINE, "qx.dev.log.Logger.LEVEL_DEBUG" );
    LOG_LEVEL_MAP.put( Level.FINER, "qx.dev.log.Logger.LEVEL_DEBUG" );
    LOG_LEVEL_MAP.put( Level.FINEST, "qx.dev.log.Logger.LEVEL_DEBUG" );
  }
  
  ////////////////////////////////////////////////////////
  // interface implementation of IDisplayLifeCycleAdapter
  
  public void preserveValues( final Display display ) {
  }
  
  public void render( final Display display ) throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    HttpServletResponse response = ContextProvider.getResponse();
    HttpServletRequest request = ContextProvider.getRequest();
    // TODO [rh] should be replaced by requestCounter == 0
    if( request.getParameter( RequestParams.UIROOT ) == null ) {
      response.setContentType( HTML.CONTENT_TEXT_HTML_UTF_8 );  
      QooxdooResourcesUtil.registerResources();
      out.startElement( HTML.HTML, null );
      out.startElement( HTML.HEAD, null );
      out.startElement( HTML.META, null );
      out.writeAttribute( HTML.HTTP_EQUIV, HTML.CONTENT_TYPE, null );
      out.writeAttribute( HTML.CONTENT, HTML.CONTENT_TEXT_HTML_UTF_8, null );
      out.startElement( HTML.TITLE, null );
  //    out.writeText( shell.getTitle(), null );
      out.endElement( HTML.TITLE );    
      String[] libraries = out.getJSLibraries();
      IResourceManager manager = ResourceManager.getInstance();
      for( int i = 0; i < libraries.length; i++ ) {      
        String location = manager.getLocation( libraries[ i ] );
        writeScriptTag( out, location );
      }
      out.endElement( HTML.HEAD );
      out.startElement( HTML.BODY, null );
      out.startElement( HTML.SCRIPT, null );
      out.writeAttribute( HTML.TYPE, HTML.CONTENT_TEXT_JAVASCRIPT, null );
      StringBuffer initScript = new StringBuffer();
      initScript.append( jsConfigureLogger( getClientLogLevel() ) );
      initScript.append( jsAppInitialization() );
      IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
      initScript.append( jsSetUIRoot( adapter.getId() ) );
      out.writeText( initScript.toString(), null );
      out.endElement( HTML.SCRIPT );    
      out.endElement( HTML.BODY );
      out.endElement( HTML.HTML );
    } else {
      response.setContentType( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
      IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
      jsSetUIRoot( adapter.getId() );
      
      out.write( "org.eclipse.rap.rwt.EventUtil.suspendEventHandling();" );
      disposeWidgets();
      RenderVisitor visitor = new RenderVisitor();
      Composite[] shells = display.getShells();
      for( int i = 0; i < shells.length; i++ ) {
        Composite shell = shells[ i ];
        WidgetTreeVisitor.accept( shell, visitor );
        visitor.reThrowProblem();
      }
      out.write( "qx.ui.core.Widget.flushGlobalQueues();" );
      out.write( "org.eclipse.rap.rwt.EventUtil.resumeEventHandling();" );
      markInitialized( display );
    }
  }
  
  public void readData( final Display display ) {
    readBounds( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        IWidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
        adapter.readData( widget );
        return true;
      }
    };
    Composite[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      Composite shell = shells[ i ];
      WidgetTreeVisitor.accept( shell, visitor );
    }
  }
  
  public void processAction( final Display display ) {
//    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
//      public boolean doVisit( final Widget widget ) {
//        IWidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
//        adapter.processAction( widget );
//        return true;
//      }
//    };
//    Composite[] shells = display.getShells();
//    for( int i = 0; i < shells.length; i++ ) {
//      Composite shell = shells[ i ];
//      WidgetTreeVisitor.accept( shell, visitor );
//    }
    RWTEvent.processScheduledEvents();
  }

  /////////////////////////////
  // Helping methods for render

  private static String jsConfigureLogger( final Level level ) {
    String jsLevel = ( String )LOG_LEVEL_MAP.get( level );
    String code = "qx.dev.log.Logger.ROOT_LOGGER.setMinLevel( {0} );";
    return MessageFormat.format( code, new Object[] { jsLevel } );
  }

  private static String jsAppInitialization() {
    // TODO [rh] change org_eclipse_rap_rwt_requesthandler to something like
    //      request.set(ServerSide)Handler (see also request.js)
    String code =   "var org_eclipse_rap_rwt_requesthandler = \"{0}\";"
                  + "var app = new org.eclipse.rap.rwt.Application();" 
                  + "qx.core.Init.getInstance().setApplication( app );";
    Object[] param = new Object[] { 
      ContextProvider.getRequest().getServletPath().substring( 1 )
    };
    return MessageFormat.format( code, param );
  }
  
  private static String jsSetUIRoot( final String id ) {
    Object[] args = new Object[] { id };
    String code 
      = "var req = org.eclipse.rap.rwt.Request.getInstance();" 
      + "req.setUIRootId( \"{0}\" );";
    return MessageFormat.format( code, args );
  }
  
  private static void disposeWidgets() throws IOException {
    Widget[] disposedWidgets = AbstractWidgetLCA.getDisposedWidgets();
    for( int i = 0; i < disposedWidgets.length; i++ ) {
      AbstractWidgetLCA lca = WidgetUtil.getLCA( disposedWidgets[ i ] );
      lca.renderDispose( disposedWidgets[ i ] );
    }
  }
  
  private static void writeScriptTag( final HtmlResponseWriter out, 
                                      final String library ) 
    throws IOException 
  {
    out.startElement( HTML.SCRIPT, null );
    out.writeAttribute( HTML.TYPE, HTML.CONTENT_TEXT_JAVASCRIPT, null );
    out.writeAttribute( HTML.SRC, library, null );
    out.writeAttribute( HTML.CHARSET, HTML.CHARSET_NAME_UTF_8, null );
    out.endElement( HTML.SCRIPT );
  }

  private static void markInitialized( final Display display ) {
    WidgetAdapter adapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    adapter.setInitialized( true );
  }

  private static Level getClientLogLevel() {
    Level result = Level.OFF;
    String logLevel = System.getProperty( CLIENT_LOG_LEVEL );
    if( logLevel != null ) {
      logLevel = logLevel.toUpperCase();
      Level[] knownLogLevels = new Level[ LOG_LEVEL_MAP.size() ];
      LOG_LEVEL_MAP.keySet().toArray( knownLogLevels );
      for( int i = 0; i < knownLogLevels.length; i++ ) {
        if( knownLogLevels[ i ].getName().equals( logLevel ) ) {
          result = knownLogLevels[ i ];
        }
      }
    }
    return result;
  }

  private static void readBounds( final Display display ) {
    Rectangle bounds 
      = new Rectangle( 0, 
                       0, 
                       readIntPropertyValue( display, "bounds.width", 0 ), 
                       readIntPropertyValue( display, "bounds.height", 0 ) );
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( bounds );
  }
  
  private static String readPropertyValue( final Display display, 
                                           final String propertyName ) 
  {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer key = new StringBuffer();
    key.append( DisplayUtil.getId( display ) );
    key.append( "." );
    key.append( propertyName );
    return request.getParameter( key.toString() );
  }

  private static int readIntPropertyValue( final Display display, 
                                           final String propertyName,
                                           final int defaultValue ) 
  {
    String value = readPropertyValue( display, propertyName );
    int result;
    if( value == null ) {
      result = defaultValue;
    } else {
      result = Integer.parseInt( value );
    }
    return result;
  }
}