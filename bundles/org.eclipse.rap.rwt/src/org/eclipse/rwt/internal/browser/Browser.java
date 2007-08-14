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
package org.eclipse.rwt.internal.browser;

import java.util.Enumeration;
import java.util.Locale;

import org.eclipse.rwt.internal.service.ContextProvider;


/**
 * <p>Subclasses of Browser represent vendor-specific and version-specific
 * information about the web browser that is used on the client side to display
 * the pages from the current session.</p>
 * @see org.eclipse.rwt.W4TContext#getBrowser() W4TContext.getBrowser()
 */
public abstract class Browser {

  /**
   * <p>
   * Default value, if the detected browser vendor and version are unknown.
   * </p>
   */
  public final static String DEFAULT = "Default";
  
  /**
   * <p>
   * Netscape Navigator 6 or higher.
   * </p>
   */
  public final static String NAVIGATOR_6_UP = "Nav6up";
  /**
   * <p>
   * Microsoft Internet Explorer 5 or higher.
   * </p>
   */
  public final static String INTERNETEXPLORER_5_UP = "Ie5up";
  
  private static final String PACKAGE = "org.eclipse.rap.util.browser.";
  
  /** <p>Whether the browser represented by this Browser has javascript 
   * execution enabled.</p> */
  private final boolean scriptEnabled;
  /** <p>Whether the browser represented by this Browser has AJaX enabled.</p> 
   */
  private final boolean ajaxEnabled;
  
  /** <p>Whether AJaX mode is supported for this browser. Requires that the
   * browser can handle AJaX requests/responses and that there exist AJaX
   * rendering kits.</p> */
  protected boolean ajaxCapable = false;
  
  /**
   * The preferred Locale that the client will accept content in, based on the
   * Accept-Language header of the first request of the client session. If the
   * first client request doesn't provide an Accept-Language header, the the
   * default locale for the server is used.
   */
  private Locale locale;
  /**
   * an Enumeration of Locale objects indicating, in decreasing order starting
   * with the preferred locale, the locales that are acceptable to the client
   * based on the Accept-Language header of the first request of the client
   * session. If the first client request doesn't provide an Accept-Language
   * header, an Enumeration containing one Locale, the default locale for the
   * server is used.
   */
  private Enumeration locales;
  /**
   * the Internet Protocol (IP) address of the client that sent the requests of
   * the current session.
   */
  private String remoteAddr;
  /**
   * the fully qualified name of the client that sent the requests of the
   * current session. If the underlying engine cannot or chooses not to resolve
   * the hostname (to improve performance), this method returns the
   * dotted-string form of the IP address.
   */
  private String remoteHost;

  /**
   * <p>Constructs a new Browser instance.</p>
   * <p>This constructor actually calls {@link #Browser(boolean,boolean) 
   * Browser(scriptEnabled,false}}.</p>
   */
  public Browser( final boolean scriptEnabled ) {
    this( scriptEnabled, false );
  }
  
  /**
   * <p>Constructs a new Browser instance.</p>
   * <p>Note: you will probably not need to create Browser objects. Use
   * W4TContext.getBrowser() to retrieve the Browser object which represents
   * the browser that is used on the client side to display the pages from the
   * current session.</p>
   * @param scriptEnabled whether this browser supports execution of JavaScript.
   * @param ajaxEnabled whether this browser supports AJaX. If this argument
   * is set to <code>true</code>, the scriptEnabled argumentmust also be 
   * set to <code>true</code>. 
   */
  // TODO [rh] these two args should be replaced by enum (NoScript, Script, Ajax), must preserve api-compatibility?
  public Browser( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    this.scriptEnabled = scriptEnabled;
    this.ajaxEnabled = ajaxEnabled;
    locale = ContextProvider.getRequest().getLocale();
    locales = ContextProvider.getRequest().getLocales();
    remoteAddr = ContextProvider.getRequest().getRemoteAddr();
    remoteHost = ContextProvider.getRequest().getRemoteHost();
  }

  /**
   * <p>Constructs a new Browser instance.</p>
   * <p>Note: you will probably not need to create Browser objects. Use
   * W4TContext.getBrowser() to retrieve the Browser object which represents
   * the browser that is used on the client side to display the pages from the
   * current session.</p>
   * <p>This constructor may be used to create e.g. the predecessor of the
   * currently set browser without loosing its session depending attributes in
   * the newly created Browser object.</p>
   */
  public Browser( final Browser browser ) {
    this.scriptEnabled = browser.scriptEnabled;
    this.ajaxEnabled = browser.ajaxEnabled;
    // keep the ajaxCapable flag from the given browser; the RendererCache needs
    // this to work 
    this.ajaxCapable = browser.ajaxCapable;
    locale = browser.getLocale();
    locales = browser.getLocales();
    remoteAddr = browser.getRemoteAddr();
    remoteHost = browser.getRemoteHost();
  }

  /**
   * <p>
   * returns whether this browser is either the same or a later version than
   * the browser represented by the passed browser identifier.
   * </p>
   * 
   * <p>
   * <b>Example: </b> use the following code to find out whether the browser of
   * the current session is Netscape Navigator 4 or higher:
   * 
   * <pre>
   *   Browser browser = W4TContext.getBrowser();
   *   boolean isNetscape4up = browser.isCompatible( Browser.NAVIGATOR_4_UP );
   * </pre>
   * 
   * <p>
   * Note that a later browser is not necessarily compatible to an older
   * version. This method tells you only whether this Browser is the same or a
   * later version, nothing about actual compatibility.
   * </p>
   * 
   * @param anotherBrowser
   *          denotes a browser version as specified in the public field
   *          definitions in {@link org.eclipse.rwt.internal.browser.Browser Browser}
   */
  // TODO [rh] method never used, remove it? 
  public boolean isCompatible( final String anotherBrowser ) {
    boolean result = false;
    String thisBrowser = toString();
    if( thisBrowser.equals( anotherBrowser ) ) {
      result = true;
    }
    if( !result ) {
      try {
        Class anotherClass = Class.forName( PACKAGE + anotherBrowser );
        if( this.getClass().isAssignableFrom( anotherClass ) ) {
          result = true;
        }
        if( !result && !thisBrowser.endsWith( "up" ) ) {
          String name = this.getClass().getName() + "up";
          if( Class.forName( name ).isAssignableFrom( anotherClass ) ) {
            result = true;
          }
        }
      } catch( ClassNotFoundException cnfe ) {
        // this means nothing more than that there is no compatibility at all
      }
    }
    return result;
  }

  // attribute setters and getters
  ////////////////////////////////
  
  /**
   * <p>Whether the browser represented by this Browser has JavasSript 
   * execution enabled.</p>
   */
  public boolean isScriptEnabled() {
    return scriptEnabled;
  }
  
  /**
   * <p>Returns whether the browser represented by this Browser object has
   * <em>AJaX</em> enabled.</p>
   */
  public boolean isAjaxEnabled() {
    return ajaxCapable && ajaxEnabled;
  }

  public String toString() {
    String result = this.getClass().getName();
    int index = result.lastIndexOf( '.' ) + 1;
    return result.substring( index );
  }

  /**
   * <p>Returns the preferred Locale that the client will accept content in, 
   * based on the Accept-Language header of the first request of the client 
   * session. If the first client request doesn't provide an Accept-Language 
   * header, the the default locale for the server is returned.</p>
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * <p>Returns an Enumeration of Locale objects indicating, in decreasing order
   * starting with the preferred locale, the locales that are acceptable to the
   * client based on the Accept-Language header of the first request of the
   * client session. If the first client request doesn't provide an
   * Accept-Language header, an Enumeration containing one Locale, the default
   * locale for the server is returned.</p>
   */
  public Enumeration getLocales() {
    return locales;
  }

  /**
   * <p>Returns the Internet Protocol (IP) address of the client that sends the
   * requests of the current session.</p>
   */
  public String getRemoteAddr() {
    return remoteAddr;
  }

  /**
   * <p>Returns the fully qualified name of the client that sents the requests 
   * of the current session. If the underlying engine cannot or chooses not to
   * resolve the hostname (to improve performance), this method returns the
   * dotted-string form of the IP address.</p>
   */
  public String getRemoteHost() {
    return remoteHost;
  }

  /**
   * <p>Returns whether the browser represented by this Browser object 
   * understands XHTML.</p>
   */
  public boolean isXHTMLCapable() {
    return false;
  }
}
