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
import org.eclipse.rap.rwt.internal.engine.ResourceRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.IResource;
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
      WidgetUtil.getLCA( widget ).render( widget );
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

  private static final String PROP_FOCUS_CONTROL = "focusControl";

  static {
    // Available qooxdoo log level: 
    //   LEVEL_OFF, LEVEL_ALL, 
    //   LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARN, LEVEL_ERROR, LEVEL_FATAL
    LOG_LEVEL_MAP.put( Level.OFF, "qx.log.Logger.LEVEL_OFF" );
    LOG_LEVEL_MAP.put( Level.ALL, "qx.log.Logger.LEVEL_ALL" );
    LOG_LEVEL_MAP.put( Level.WARNING, "qx.log.Logger.LEVEL_WARN" );
    LOG_LEVEL_MAP.put( Level.INFO, "qx.log.Logger.LEVEL_INFO" );
    LOG_LEVEL_MAP.put( Level.SEVERE, "qx.log.Logger.LEVEL_ERROR" );
    LOG_LEVEL_MAP.put( Level.FINE, "qx.log.Logger.LEVEL_DEBUG" );
    LOG_LEVEL_MAP.put( Level.FINER, "qx.log.Logger.LEVEL_DEBUG" );
    LOG_LEVEL_MAP.put( Level.FINEST, "qx.log.Logger.LEVEL_DEBUG" );
  }
  
  ////////////////////////////////////////////////////////
  // interface implementation of IDisplayLifeCycleAdapter
  
  public void preserveValues( final Display display ) {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_FOCUS_CONTROL, display.getFocusControl() );
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
      writeScrollBarStyle();
      writeJSLibraries();
      out.endElement( HTML.HEAD );
      out.startElement( HTML.BODY, null );
      out.startElement( HTML.SCRIPT, null );
      out.writeAttribute( HTML.TYPE, HTML.CONTENT_TEXT_JAVASCRIPT, null );
      StringBuffer initScript = new StringBuffer();
      initScript.append( jsConfigureLogger( getClientLogLevel() ) );
      initScript.append( jsAppInitialization() );
      initScript.append( jsThemeInitialization() );
      IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
      initScript.append( jsSetUIRoot( adapter.getId() ) );
      out.writeText( initScript.toString(), null );
      out.endElement( HTML.SCRIPT );    
      out.endElement( HTML.BODY );
      out.endElement( HTML.HTML );
    } else {
      response.setContentType( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
      
      out.write( "org.eclipse.rap.rwt.EventUtil.suspendEventHandling();" );
      disposeWidgets();
      RenderVisitor visitor = new RenderVisitor();
      Composite[] shells = display.getShells();
      for( int i = 0; i < shells.length; i++ ) {
        Composite shell = shells[ i ];
        WidgetTreeVisitor.accept( shell, visitor );
        visitor.reThrowProblem();
      }
      writeFocus( display );
      out.write( "qx.ui.core.Widget.flushGlobalQueues();" );
      out.write( "org.eclipse.rap.rwt.EventUtil.resumeEventHandling();" );
      markInitialized( display );
    }
  }

  private void writeJSLibraries() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    String[] libraries = out.getJSLibraries();
    IResourceManager manager = ResourceManager.getInstance();
    for( int i = 0; i < libraries.length; i++ ) {      
      String location = manager.getLocation( libraries[ i ] );
      writeScriptTag( out, location );
    }
    
    IResource[] resources = ResourceRegistry.get();
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ].isExternal() ) {
        writeScriptTag( out, resources[ i ].getLocation() );
      }
    }
  }
  
  public void readData( final Display display ) {
    Rectangle oldBounds = display.getBounds();
    readBounds( display );
    readFocusControl( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        IWidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
        adapter.readData( widget );
        return true;
      }
    };
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      Composite shell = shells[ i ];
      WidgetTreeVisitor.accept( shell, visitor );
    }
    
    // TODO: [fappel] since there is no possibility yet to determine whether
    //                a shell is maximized, we use this hack to adjust 
    //                the bounds of a maximized shell in case of a document
    //                resize event
    for( int i = 0; i < shells.length; i++ ) {
      if( shells[ i ].getBounds().equals( oldBounds ) ) {
        shells[ i ].setBounds( display.getBounds() );
      }
    }
  }
  
  public void processAction( final Display display ) {
    ProcessActionRunner.execute();
    RWTEvent.processScheduledEvents();
  }

  /////////////////////////////
  // Helping methods for render

  private static String jsConfigureLogger( final Level level ) {
    String jsLevel = ( String )LOG_LEVEL_MAP.get( level );
    String code = "qx.log.Logger.ROOT_LOGGER.setMinLevel( {0} );";
    return MessageFormat.format( code, new Object[] { jsLevel } );
  }

  private static String jsAppInitialization() {
    // TODO [rh] change org_eclipse_rap_rwt_requesthandler to something like
    //      request.set(ServerSide)Handler (see also request.js)
    String code =   "var org_eclipse_rap_rwt_requesthandler = \"{0}\";"
                  + "var app = org.eclipse.rap.rwt.Application;" 
                  + "qx.core.Init.getInstance().setApplication( app );";
    Object[] param = new Object[] { 
      ContextProvider.getRequest().getServletPath().substring( 1 )
    };
    return MessageFormat.format( code, param );
  }
  
  private static String jsThemeInitialization() {
    String code 
      = "var am = qx.manager.object.AppearanceManager.getInstance();"
      + "var theme = new org.eclipse.rap.rwt.DefaultAppearanceTheme();"  
      + "am.setAppearanceTheme( theme );";
    return code;
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

  private static void writeScrollBarStyle() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    out.startElement( HTML.STYLE, out );
    out.writeAttribute( HTML.TYPE, HTML.CONTENT_TEXT_CSS, null );
    StringBuffer css = new StringBuffer();
    css.append( "html, body, iframe { " );
    css.append( "scrollbar-base-color:#c0c0c0;" ); 
    css.append( "scrollbar-3d-light-color:#f8f8ff;" );
    css.append( "scrollbar-arrow-color:#0080c0;" );
    css.append( "scrollbar-darkshadow-color:#f0f0f8;" );
    css.append( "scrollbar-face-color:#f8f8ff;" );
    css.append( "scrollbar-highlight-color:white;" );
    css.append( "scrollbar-shadow-color:gray;" );
    css.append( "scrollbar-track-color:#f0f0f8;" );
    css.append( "}" ); 
    out.write( css.toString() );
    out.endElement( HTML.STYLE );
  }

  private static void writeFocus( final Display display ) throws IOException {
    IWidgetAdapter displayAdapter = DisplayUtil.getAdapter( display );
    Object oldValue = displayAdapter.getPreserved( PROP_FOCUS_CONTROL );
    if( isInitialRequest( display ) || oldValue != display.getFocusControl() ) {
      // TODO [rh] use JSWriter to output focus JavaScript 
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter out = stateInfo.getResponseWriter();
      String id = WidgetUtil.getId( display.getFocusControl() );
      out.write( "org.eclipse.rap.rwt.WidgetManager.getInstance()." ); 
      out.write( "focus( \"" ); 
      out.write( id ); 
      out.write( "\" );" );
    }
  }

  private static boolean isInitialRequest( final Display display ) {
    IWidgetAdapter displayAdapter = DisplayUtil.getAdapter( display );
    return !displayAdapter.isInitialized() && display.getFocusControl() != null;
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
    Rectangle oldBounds = display.getBounds();
    int width 
      = readIntPropertyValue( display, "bounds.width", oldBounds.width );
    int height
      = readIntPropertyValue( display, "bounds.height", oldBounds.height );
    Rectangle bounds = new Rectangle( 0, 0, width, height );
    getDisplayAdapter( display ).setBounds( bounds );
  }

  private static void readFocusControl( final Display display ) {
    // TODO [rh] revise this: traversing the widget tree once more only to find
    //      out which control is focus. Could that be optimized?
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer focusControlParam = new StringBuffer();
    focusControlParam.append( DisplayUtil.getId( display ) );
    focusControlParam.append( ".focusControl" );
    String id = request.getParameter( focusControlParam.toString() );
    Control focusControl = null;
    if( id != null ) {
      Shell[] shells = display.getShells();
      for( int i = 0; focusControl == null && i < shells.length; i++ ) {
        Widget widget = WidgetUtil.find( shells[ i ], id );
        if( widget instanceof Control ) {
          focusControl = ( Control )widget;
        }
      }
      getDisplayAdapter( display ).setFocusControl( focusControl );
    }
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

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }
}