/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class to open and close an external browser window.
 * 
 * @since 1.0
 */
public final class ExternalBrowser {

  private static final class JSExecutor implements PhaseListener {

    private static final long serialVersionUID = 1L;

    private final Display display;
    private String code;

    private JSExecutor( final Display display ) {
      this.display = display;
      this.code = "";
    }
    
    private void append( final String command ) {
      code += command;
    }

    public void beforePhase( final PhaseEvent event ) {
      // do nothing
    }

    public void afterPhase( final PhaseEvent event ) {
      if( display == RWTLifeCycle.getSessionDisplay() ) {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        HtmlResponseWriter writer = stateInfo.getResponseWriter();
        try {
          writer.write( code, 0, code.length() );
        } catch( IOException e ) {
          // TODO [rh] proper exception handling - think about adding throws
          //      IOException to after/beforePhase as there are various places
          //      like this
          throw new RuntimeException( e );
        } finally {
          LifeCycleFactory.getLifeCycle().removePhaseListener( this );
        }
      }
    }

    public PhaseId getPhaseId() {
      return PhaseId.RENDER;
    }
  }

  /**
   * Style parameter (value 1&lt;&lt;1) indicating that the address combo and
   * 'Go' button will be created for the browser. 
   * <p>Note: This style parameter is a hint and might be ignored by some 
   * browsers.</p>
   */
  public static final int LOCATION_BAR = 1 << 1;

  /**
   * Style parameter (value 1&lt;&lt;2) indicating that the navigation bar for
   * navigating web pages will be created for the web browser.
   * <p>Note: This style parameter is a hint and might be ignored by some 
   * browsers.</p>
   */
  public static final int NAVIGATION_BAR = 1 << 2;

  /**
   * Style constant (value 1&lt;&lt;3) indicating that status will be tracked
   * and shown for the browser (page loading progress, text messages etc.).
   * <p>Note: This style parameter is a hint and might be ignored by some 
   * browsers.</p>
   */
  public static final int STATUS = 1 << 3;

  private static final String JS_EXECUTOR 
    = ExternalBrowser.class.getName() + "#jsExecutor";

  private static final String OPEN 
    = "org.eclipse.rwt.widgets.ExternalBrowser." 
    + "open( \"{0}\", \"{1}\", \"{2}\" );";
  private static final String CLOSE
    = "org.eclipse.rwt.widgets.ExternalBrowser.close( \"{0}\" );";

  /**
   * Opens the given <code>url</code> in an external browser.
   * 
   * <p>The method will reuse an existing browser window if the same
   * <code>id</code> value is passed to it.</p>
   * 
   * @param id if an instance of a browser with the same id is already
   *   opened, it will be reused instead of opening a new one. The id
   *   must neither be <code>null</code> nor empty.
   * @param url the URL to display, must not be <code>null</code>
   * @param style the style display constants. Style constants should be
   *   bitwise-ORed together.
   *   
   * @throws SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the <code>id</code> or <code>url</code> 
   *      is <code>null</code></li>
   *    <li>ERROR_INVALID_ARGUMENT - if the <code>id</code> is empty</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that 
   *      created the receiver</li>
   * </ul>
   */
  public static void open( final String id, final String url, final int style ) 
  {
    checkWidget();
    if( id == null || url == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    executeJS( getOpenJS( id, url, style ) );
  }
  
  /**
   * Closes the browser window denoted by the given <code>id</code>. The
   * method does nothing if there is no browser window with the given id.
   * 
   * @param id if an instance of a browser with the same id is opened, 
   *   it will be close. The id must neither be <code>null</code> nor empty.
   *   
   * @throws SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the <code>id</code> is 
   *      <code>null</code></li>
   *    <li>ERROR_INVALID_ARGUMENT - if the <code>id</code> is empty</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that 
   *      created the receiver</li>
   * </ul>
   */
  public static void close( final String id ) {
    checkWidget();
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    executeJS( getCloseJS( id ) );
  }

  ///////////////////////////////
  // JavaScript code 'generation'
  
  private static String getOpenJS( final String id, 
                                   final String url, 
                                   final int style ) 
  {
    String[] args = new String[] { escapeId( id ), url, getFeatures( style ) };
    return MessageFormat.format( OPEN, args );
  }
  
  private static String getCloseJS( final String id ) {
    return MessageFormat.format( CLOSE, new String[] { escapeId( id ) } );
  }
  
  static String escapeId( final String id ) {
    String result = id;
    result = result.replaceAll( "\\_", "\\_0" );
    result = result.replaceAll( "\\.", "\\_" );
    // IE does not accept blanks in popup-window names
    result = result.replaceAll( " ", "\\__" );
    return result;
  }
  
  private static String getFeatures( final int style ) {
    StringBuffer result = new StringBuffer();
    appendFeature( result, "dependent", true );
    appendFeature( result, "scrollbars", true );
    appendFeature( result, "resizable", true );
    appendFeature( result, "status", ( style & STATUS ) != 0 );
    appendFeature( result, "location", ( style & LOCATION_BAR ) != 0 );
    boolean navigation = ( style & NAVIGATION_BAR ) != 0;
    appendFeature( result, "toolbar", navigation );
    appendFeature( result, "menubar", navigation );
    return result.toString();
  }
  
  private static void appendFeature( final StringBuffer features, 
                                     final String feature, 
                                     final boolean enable ) 
  {
    if( features.length() > 0 ) {
      features.append( "," );
    }
    features.append( feature );
    features.append( "=" );
    features.append( enable ? 1 : 0 );
  }

  private static void executeJS( final String code ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JSExecutor jsExecutor = ( JSExecutor )stateInfo.getAttribute( JS_EXECUTOR );
    if( jsExecutor == null ) {
      jsExecutor = new JSExecutor( Display.getCurrent() );
      LifeCycleFactory.getLifeCycle().addPhaseListener( jsExecutor );
      stateInfo.setAttribute( JS_EXECUTOR, jsExecutor );
    }
    jsExecutor.append( code );
  }

  //////////////////
  // Helping methods
  
  private static void checkWidget() {
    if( Display.getCurrent().getThread() != Thread.currentThread() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
  }

  private ExternalBrowser() {
    // prevent instantiation
  }
}
