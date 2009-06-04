/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.TextSizeDeterminationFacadeImpl;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA;
import org.eclipse.swt.widgets.*;

public class DisplayLCA implements IDisplayLifeCycleAdapter {

  private final static String PATTERN_APP_STARTUP
    =    "var req = org.eclipse.swt.Request.getInstance();"
       + "req.setUrl( \"{0}\", \"{1}\" );"
       + "req.setUIRootId( \"{2}\" );"
       + "var app = new org.eclipse.swt.Application();"
       + "qx.core.Init.getInstance().setApplication( app );";
  private final static String PATTERN_REQUEST_COUNTER
    =   "var req = org.eclipse.swt.Request.getInstance();"
      + "req.setRequestCounter( \"{0,number,#}\" );";
  private static final String DISPOSE_HANDLER_REGISTRY
    = "org.eclipse.rap.disposeHandlerRegistry";
  private static final JSVar DISPOSE_HANDLER_START
    = new JSVar( "function( " + JSWriter.WIDGET_REF + " ) {" );

  private static final String CLIENT_LOG_LEVEL
    = "org.eclipse.rwt.clientLogLevel";

  // Maps Java Level to the closest qooxdoo log level
  private static final Map LOG_LEVEL_MAP = new HashMap( 8 + 1, 1f );

  static final String PROP_FOCUS_CONTROL = "focusControl";
  static final String PROP_CURR_THEME = "currTheme";
  static final String PROP_EXIT_CONFIRMATION = "exitConfirmation";
  static final String PROP_TIMEOUT_PAGE = "timeoutPage";

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
    adapter.preserve( PROP_CURR_THEME, ThemeUtil.getCurrentThemeId() );
    adapter.preserve( PROP_TIMEOUT_PAGE, getTimeoutPage() );
    adapter.preserve( PROP_EXIT_CONFIRMATION, getExitConfirmation() );
  }

  public void render( final Display display ) throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    HttpServletResponse response = ContextProvider.getResponse();
    HttpServletRequest request = ContextProvider.getRequest();
    // TODO [rh] should be replaced by requestCounter == 0
    if( request.getParameter( RequestParams.UIROOT ) == null ) {
      writeClientDocument( display );
    } else {
      response.setContentType( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
      out.write( "org.eclipse.swt.EventUtil.suspendEventHandling();" );
      out.write( getRequestCounter() );
      disposeWidgets();
      writeTheme( display );
      writeErrorPages( display );
      writeExitConfirmation( display );
      RenderVisitor visitor = new RenderVisitor();
      Composite[] shells = display.getShells();
      for( int i = 0; i < shells.length; i++ ) {
        Composite shell = shells[ i ];
        WidgetTreeVisitor.accept( shell, visitor );
        visitor.reThrowProblem();
      }
      writeActiveControls( display );
      writeFocus( display );
      out.write( "qx.ui.core.Widget.flushGlobalQueues();" );
      out.write( "org.eclipse.swt.EventUtil.resumeEventHandling();" );
      markInitialized( display );
    }
  }

  private String getRequestCounter() {
    Object[] param = new Object[] { RWTRequestVersionControl.nextRequestId() };
    return MessageFormat.format( PATTERN_REQUEST_COUNTER, param );
  }

  private static void writeTheme( final Display display ) throws IOException {
    String currThemeId = ThemeUtil.getCurrentThemeId();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldThemeId = adapter.getPreserved( PROP_CURR_THEME );
    if( !currThemeId.equals( oldThemeId ) ) {
      Theme theme = ThemeManager.getInstance().getTheme( currThemeId );
      StringBuffer buffer = new StringBuffer();
      buffer.append( "qx.theme.manager.Meta.getInstance().setTheme( " );
      buffer.append( theme.getJsId() );
      buffer.append( " );" );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter out = stateInfo.getResponseWriter();
      out.write( buffer.toString() );
    }
  }

  private static void writeErrorPages( final Display display )
    throws IOException
  {
    String timeoutPage = getTimeoutPage();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldTimeoutPage = adapter.getPreserved( PROP_TIMEOUT_PAGE );
    if( !timeoutPage.equals( oldTimeoutPage ) ) {
      String pattern
        = "org.eclipse.swt.Request.getInstance().setTimeoutPage( \"{0}\" );";
      Object[] param = new Object[] { timeoutPage };
      String jsCode = MessageFormat.format( pattern, param );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter out = stateInfo.getResponseWriter();
      out.write( jsCode );
    }
  }

  private static String getTimeoutPage() {
    String timeoutTitle
      = RWTMessages.getMessage( "RWT_SessionTimeoutPageTitle" );
    String timeoutHeadline
      = RWTMessages.getMessage( "RWT_SessionTimeoutPageHeadline" );
    String pattern = RWTMessages.getMessage( "RWT_SessionTimeoutPageMessage" );
    Object[] arguments = new Object[]{ "<a {HREF_URL}>", "</a>" };
    String timeoutMessage = MessageFormat.format( pattern, arguments );
    // TODO Escape umlauts etc
    String timeoutPage = "<html><head><title>"
                         + timeoutTitle
                         + "</title></head><body><p>"
                         + timeoutHeadline
                         + "</p><p>"
                         + timeoutMessage
                         + "</p></body></html>";
    return timeoutPage;
  }

  private static void writeExitConfirmation( final Display display )
    throws IOException
  {
    String exitConfirmation = getExitConfirmation();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldExitConfirmation = adapter.getPreserved( PROP_EXIT_CONFIRMATION );
    boolean hasChanged = exitConfirmation == null
                         ? oldExitConfirmation != null
                         : !exitConfirmation.equals( oldExitConfirmation );
    if( hasChanged ) {
      String exitConfirmationStr = exitConfirmation == null
                                   ? "null"
                                   : "\"" + exitConfirmation + "\"";
      String code = "qx.core.Init.getInstance().getApplication()"
                    + ".setExitConfirmation( "
                    + exitConfirmationStr
                    + " );";
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter out = stateInfo.getResponseWriter();
      out.write( code );
    }
  }

  private static String getExitConfirmation() {
    AbstractBranding branding = BrandingUtil.findBranding();
    String result = null; // does not display exit dialog
    if( branding.showExitConfirmation() ) {
      result = branding.getExitConfirmationText();
      if( result == null ) {
        result = ""; // displays an exit dialog with empty message
      }
    }
    return result;
  }

  private static void writeClientDocument( final Display display )
    throws IOException
  {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTML.CONTENT_TEXT_HTML_UTF_8 );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    out.startElement( HTML.HTML, null );
    out.startElement( HTML.HEAD, null );
    out.startElement( HTML.META, null );
    out.writeAttribute( HTML.HTTP_EQUIV, HTML.CONTENT_TYPE, null );
    out.writeAttribute( HTML.CONTENT, HTML.CONTENT_TEXT_HTML_UTF_8, null );
    out.startElement( HTML.TITLE, null );
    out.endElement( HTML.TITLE );

    writeLibraries();

    out.endElement( HTML.HEAD );
    out.startElement( HTML.BODY, null );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    String id = adapter.getId();
    out.startElement( HTML.SCRIPT, null );
    out.writeAttribute( HTML.TYPE, HTML.CONTENT_TEXT_JAVASCRIPT, null );

    writeAppScript( id );
    writeErrorPages( display );
    writeExitConfirmation( display );

    out.endElement( HTML.SCRIPT );
    out.endElement( HTML.BODY );
    out.endElement( HTML.HTML );
  }

  public static void writeAppScript( final String id ) throws IOException {
    StringBuffer initScript = new StringBuffer();
    initScript.append( jsConfigureLogger( getClientLogLevel() ) );
    initScript.append( jsAppInitialization( id ) );
    HtmlResponseWriter out = ContextProvider.getStateInfo().getResponseWriter();
    out.writeText( initScript.toString(), null );
  }

  public static void writeLibraries() throws IOException {
    QooxdooResourcesUtil.registerResources();
    ThemeManager.getInstance().registerResources();
    writeScrollBarStyle();
    writeJSLibraries();
  }

  private static void writeJSLibraries() throws IOException {
    HtmlResponseWriter out = ContextProvider.getStateInfo().getResponseWriter();
    IResource[] resources = ResourceRegistry.get();
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ].isExternal() && resources[ i ].isJSLibrary() ) {
        writeScriptTag( out, resources[ i ].getLocation() );
      }
    }
    writeScriptTag( out, JSLibraryServiceHandler.getRequestURL() );
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

  public void processAction( final Device display ) {
    ProcessActionRunner.execute();
    TypedEvent.processScheduledEvents();
  }

  /////////////////////////////
  // Helping methods for render

  private static String jsConfigureLogger( final Level level ) {
    String jsLevel = ( String )LOG_LEVEL_MAP.get( level );
    String code = "qx.log.Logger.ROOT_LOGGER.setMinLevel( {0} );";
    return MessageFormat.format( code, new Object[] { jsLevel } );
  }

  private static String jsAppInitialization( final String displayId ) {
    StringBuffer code = new StringBuffer();
    // font size measurment
    code.append( TextSizeDeterminationFacadeImpl.getStartupProbeCode() );
    // application
    HttpServletRequest request = ContextProvider.getRequest();
    String url = request.getServletPath().substring( 1 );
    Object[] param = new Object[] {
      url,
      ContextProvider.getResponse().encodeURL( url ),
      displayId
    };
    code.append( MessageFormat.format( PATTERN_APP_STARTUP, param ) );
    return code.toString();
  }

  private static void disposeWidgets() throws IOException {
    Widget[] disposedWidgets = DisposedWidgets.getAll();
    // [fappel]: client side disposal order is crucial for the widget
    //           caching mechanism - we need to dispose of children first. This
    //           is reverse to the server side mechanism (which is analog to
    //           SWT).
    for( int i = disposedWidgets.length - 1; i >= 0; i-- ) {
      Widget toDispose = disposedWidgets[ i ];
      AbstractWidgetLCA lca = WidgetUtil.getLCA( toDispose );

      Set disposeHandler = getAlreadyRegisteredHandlers();
      String key = lca.getTypePoolId( toDispose );
      if( key != null && !disposeHandler.contains( key ) ) {
        JSWriter writer = JSWriter.getWriterFor( toDispose );
        Object[] params = new Object[] {
          new Integer( key.hashCode() ),
          DISPOSE_HANDLER_START
        };
        writer.startCall( JSWriter.WIDGET_MANAGER_REF,
                          "registerResetHandler",
                          params );
        try {
          lca.createResetHandlerCalls( key );
        } finally {
          writer.endCall( new Object[] { new JSVar( "}" ) } );
          disposeHandler.add( key );
        }
      }

      lca.renderDispose( toDispose );
    }
  }

  private static Set getAlreadyRegisteredHandlers() {
    ISessionStore session = ContextProvider.getSession();
    Set result = ( Set )session.getAttribute( DISPOSE_HANDLER_REGISTRY );
    if( result == null ) {
      result = new HashSet();
      session.setAttribute( DISPOSE_HANDLER_REGISTRY, result );
    }
    return result;
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
    // TODO [rh] the browser does not seem to be detected when this
    //      code gets executed. Once this is fixed, do only render this when
    //      browser is IE
	  // TODO [bm] this could be part of ralfs themeing or?
    HtmlResponseWriter out = ContextProvider.getStateInfo().getResponseWriter();
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
    IDisplayAdapter displayAdapter = getDisplayAdapter( display );
    IWidgetAdapter widgetAdapter = DisplayUtil.getAdapter( display );
    Object oldValue = widgetAdapter.getPreserved( PROP_FOCUS_CONTROL );
    if(    !widgetAdapter.isInitialized()
        || oldValue != display.getFocusControl()
        || displayAdapter.isFocusInvalidated() )
    {
      // TODO [rst] Added null check as a NPE occurred in some rare cases
      Control focusControl = display.getFocusControl();
      if( focusControl != null ) {
        // TODO [rh] use JSWriter to output focus JavaScript
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        HtmlResponseWriter out = stateInfo.getResponseWriter();
        String id = WidgetUtil.getId( display.getFocusControl() );
        out.write( "org.eclipse.swt.WidgetManager.getInstance()." );
        out.write( "focus( \"" );
        out.write( id );
        out.write( "\" );" );
      }
    }
  }

  // TODO [rh] writing activeControl should be handled by the ShellLCA itself
  //      The reason why this is currently done here is, that the control to
  //      activate might not yet be created client-side, when ShellLCA writes
  //      the statement to set the active control.
  private static void writeActiveControls( final Display display )
    throws IOException
  {
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      ShellLCA.writeActiveControl( shells[ i ] );
    }
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

  static void readBounds( final Display display ) {
    Rectangle oldBounds = display.getBounds();
    int width
      = readIntPropertyValue( display, "bounds.width", oldBounds.width );
    int height
      = readIntPropertyValue( display, "bounds.height", oldBounds.height );
    Rectangle bounds = new Rectangle( 0, 0, width, height );
    getDisplayAdapter( display ).setBounds( bounds );
  }

  static void readFocusControl( final Display display ) {
    // TODO [rh] revise this: traversing the widget tree once more only to find
    //      out which control is focused. Could that be optimized?
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer focusControlParam = new StringBuffer();
    focusControlParam.append( DisplayUtil.getId( display ) );
    focusControlParam.append( ".focusControl" );
    String id = request.getParameter( focusControlParam.toString() );
    if( id != null ) {
      Control focusControl = null;
      // Even though the loop below would anyway result in focusControl == null
      // the client may send 'null' to indicate that no control on the active
      // shell currently has the input focus.
      if( !"null".equals(  id ) ) {
        Shell[] shells = display.getShells();
        for( int i = 0; focusControl == null && i < shells.length; i++ ) {
          Widget widget = WidgetUtil.find( shells[ i ], id );
          if( widget instanceof Control ) {
            focusControl = ( Control )widget;
          }
        }
      }
      if( focusControl != null && EventUtil.isAccessible( focusControl ) ) {
        getDisplayAdapter( display ).setFocusControl( focusControl );
      }
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