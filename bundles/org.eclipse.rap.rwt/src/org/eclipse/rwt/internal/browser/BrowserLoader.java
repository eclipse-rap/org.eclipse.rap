/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.browser;

import java.lang.reflect.Constructor;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;



/** <p>Loads the proper subclass of Browser which represents vendor-specific 
 * and version-specific information about the web browser that is used 
 * on the client side to display the pages from the current session.</p>
 */
public final class BrowserLoader {
  
  public static final String USER_AGENT = "User-Agent";

  /**
   * <p>Loads the browser object matching the browser, script- and
   * AJaX-settings extracted from the current context, or a default browser
   * with disabled script- and AJaX-settings if no matching browser could 
   * be created.</p> 
   * @return the Browser object matching the detected browser
   */
  public static Browser load() {
    String browserClassName = detectBrowserClassName();
    Browser result;
    if( browserClassName.equals( Default.class.getName() ) ) {
      result = new Default( false );
    } else {
      result = loadClassForName( browserClassName, 
                                 isScriptEnabled(),
                                 isAjaxEnabled() );
    }
    return result;
  }
  
  /**
   * <p>Loads a browser object for a given browser class name and given
   * settings for scriptEnabled and ajaxEnabled, or a default browser if 
   * matching browser class name can not be found </p>
   * 
   * @param browserClassName the classname
   * @param scriptEnabled boolean true if script should be enabled
   * @param ajaxEnabled boolean true if ajax should be enabled
   * @return the matching browser object
   */
  public static Browser loadClassForName( final String browserClassName, 
                                          final boolean scriptEnabled,
                                          final boolean ajaxEnabled )
  {
    Browser result;
    try {
      Class clazz = Class.forName( browserClassName );
      Class[] types = new Class[]{ Boolean.TYPE, Boolean.TYPE };
      Constructor constructor = clazz.getConstructor( types );
      Object[] params = new Object[]{
        Boolean.valueOf( scriptEnabled ), Boolean.valueOf( ajaxEnabled )
      };
      result = ( Browser )constructor.newInstance( params );
    } catch( final Exception shouldNotHappen ) {
      System.out.println( createMessage( browserClassName ) );
      result = new Default( scriptEnabled, ajaxEnabled );
    }
    return result;
  }
  
  /**
   * <p>Loads a browser object for a given browser class name and given
   * browser template, or a default browser if matching 
   * browser class name can not be found </p>
   * 
   * @param browserClassName the class to be instantiated
   * @param browser
   */
  public static Browser loadClassForName( final String browserClassName,
                                          final Browser browser ) 
  {
    Browser result = null;
    try {
      Class clazz = Class.forName( browserClassName );
      Class[] types = new Class[]{ Browser.class };
      Constructor constructor = clazz.getConstructor( types );
      Object[] params = new Object[]{ browser };
      result = ( Browser )constructor.newInstance( params );
      
    } catch( final Exception ex ) {
      System.out.println( createMessage( browserClassName ) );                   
      result = loadDefaultBrowser( browser );
    }
    return result;
  }

  private static String createMessage( final String browserClassName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String userAgent = request.getHeader( USER_AGENT );
    StringBuffer result = new StringBuffer();
    result.append( new Date() );
    result.append( " BROWSERLOADER: Browser instantiation fault.\n" );
    result.append( "Could not load a valid org.eclipse.rap.Browser " );
    result.append( "subclass for the received name '" ); 
    result.append( browserClassName ); 
    result.append( "'. Switch to default Browser.\n" );
    result.append( "User-Agent: " );
    result.append( userAgent );
    return result.toString();
  }  
  
   // Helper methods
   /////////////////////////////
  
  /**
   * <p>Determines the classname of the browser matching the userAgent-String
   * used in the current context </p>
   * @return className of the Browser
   */
  private static String detectBrowserClassName() {
    String userAgent = ContextProvider.getRequest().getHeader( USER_AGENT );
    BrowserDetector detector = BrowserDetector.getInstance();
    return detector.getBrowserClassName( userAgent );
  }
  
  /**
   * <p>Determines if script is enabled in the current context.</p>
   * @return true if script is enabled
   */
  private static boolean isScriptEnabled() {
    HttpServletRequest request = ContextProvider.getRequest();
    return "true".equals( request.getParameter( RequestParams.SCRIPT ) );
  }
  
  /**
   * <p>Determines if AJaX is enabled in the current context.</p>
   * @return true if AJaX is enabled
   */
  private static boolean isAjaxEnabled() {
    HttpServletRequest request = ContextProvider.getRequest();
    return "true".equals( request.getParameter( RequestParams.AJAX_ENABLED ) );
  }
  
  private static Browser loadDefaultBrowser( final Browser browser ) {
    return new Default( browser.isScriptEnabled(), browser.isAjaxEnabled() );
  }
  
  private BrowserLoader() {
    // prevent instantiation
  }
}