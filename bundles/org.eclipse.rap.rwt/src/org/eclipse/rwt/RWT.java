/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServletLog;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.internal.widgets.BrowserHistory;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

/**
 * This class provides access to aspects of RWT which are not
 * part of the SWT API as RAP needs some additions regarding
 * the server and client communication. It is responsible for
 * providing access to the {@link ISessionStore} and the
 * {@link HttpServletRequest}.
 * 
 * @since 1.0
 * @see ILifeCycle
 * @see ISessionStore
 * @see IResourceManager
 * @see HttpServletRequest
 * @see HttpServletResponse
 */
public final class RWT {
  
  private static final String LOCALE = RWT.class.getName() + ".LOCALE";

  /**
   * <p>This utility class helps to provide a similar approach for compile safe
   * native language support than {@link org.eclipse.osgi.util.NLS NLS} does.
   * We can not use the original approach though, due to the nature of
   * server side environments, that have to deal with different locales
   * per user session or even requests.</p>
   *
   * <p>
   * Usage:
   * <pre>
   *  public class FooMessages {
   *    private static final String BUNDLE_NAME = "foo.bar.messages";
   *    
   *    public String MyMessage;
   *
   *    public static FooMessages get() {
   *      return ( FootMessages )RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, FooMessages.class );
   *    }
   *  }
   * </pre>
   * 
   * BUNDLE_NAME contains the name of a properties file (without file extension)
   * that follows the conventions of standard {@link ResourceBundle} property
   * files. For each field (in the example 'MyMessage') there has to be a
   * key entry in the localization property file. Use the
   * <code>FooMessages</code> like this in the application code:
   * 
   * <pre>
   *   Label label = ...;
   *   label.setText( FooMessages.get().MyMessage );
   * </pre>
   * </p>
   */
  public static final class NLS {
    
    private final static Map<ResourceBundle,Object> map = new HashMap<ResourceBundle, Object>();

    /**
     * Returns a NLS object for the given bundle and type. See
     * class description for usage information.
     * The resource bundles read by this method have to be ISO 8859-1 encoded.
     * This is according to the {@link java.util.Properties Properties} file
     * specification. 
     * 
     * @param bundleName the bundle to load.
     * @param clazz the class of the NLS object to load.
     */
    public static Object getISO8859_1Encoded( final String bundleName, final Class clazz ) {
      ClassLoader loader = clazz.getClassLoader();
      ResourceBundle bundle = ResourceBundle.getBundle( bundleName, getLocale(), loader );
      return internalGet( bundle, clazz );
    }

    /**
     * Returns a NLS object for the given bundle and type. See
     * class description for usage information.
     * The resource bundles read by this method have to be UTF-8 encoded. Note
     * that this is not according to the {@link java.util.Properties Properties} 
     * file specification and meant for a more convenient use. 
     * 
     * @param bundleName the bundle to load.
     * @param clazz the class of the NLS object to load.
     */
    public static Object getUTF8Encoded( final String bundleName, final Class clazz ) {
      ClassLoader loader = clazz.getClassLoader();
      ResourceBundle bundle = Utf8ResourceBundle.getBundle( bundleName, getLocale(), loader );
      return internalGet( bundle, clazz );
    }
    
    private static Object internalGet( final ResourceBundle bundle, final Class clazz ) {
      Object result;
      synchronized( map ) {
        result = map.get( bundle );
        if( result == null ) {
          result = ClassUtil.newInstance( clazz );
          Field[] fields = clazz.getDeclaredFields();
          for( int i = 0; i < fields.length; i++ ) {
            String fieldName = fields[ i ].getName();
            try {
              if(    String.class.isAssignableFrom( fields[ i ].getType() )
                  && Modifier.isPublic( fields[ i ].getModifiers() ) 
                  && !Modifier.isStatic( fields[ i ].getModifiers() ) )
              {
                try {
                  String value = bundle.getString( fieldName );
                  if( value != null ) {
                    fields[ i ].setAccessible( true );
                    fields[ i ].set( result, value );
                  }
                } catch( final MissingResourceException mre ) {
                  fields[ i ].setAccessible( true );
                  fields[ i ].set( result, "" );
                  throw mre;
                }
              }
            } catch( final Exception ex ) {
              String qualifiedName = clazz.getName() + "#" + fieldName;
              ServletLog.log( "Failed to load localized message for: " + qualifiedName, ex );
            }
          }
          map.put( bundle, result );
        }
      }
      return result;
    }
  }

  /**
   * The property to use in <code>Display.setData()</code> in order to activate global key events
   * for certain key sequences. The value for this property has to be an array of Strings, each
   * representing a key sequence. When this property is set on the display, the client will be
   * instructed to issue events for the given key sequences. These key events can be captured using
   * <code>Display.addFilter()</code>.
   * <p>
   * Valid strings for key sequences consist of one or more keys, separated by <code>+</code>. Keys
   * can be identified by their character, i.e. upper case letters, digits, or one of the special
   * characters <code>, . / \ [ ] ` '</code>. Moreover, the following keywords can be used to refer
   * to special keys: <code>BACKSPACE</code>, <code>TAB</code>, <code>RETURN</code>,
   * <code>ENTER</code>, <code>ESCAPE</code>, <code>SPACE</code>, <code>PAGE_UP</code>,
   * <code>PAGE_DOWN</code>, <code>END</code>, <code>HOME</code>, <code>ARROW_LEFT</code>,
   * <code>ARROW_UP</code>, <code>ARROW_RIGHT</code>, <code>ARROW_DOWN</code>, <code>INSERT</code>,
   * <code>DELETE</code>, <code>F1</code>, <code>F2</code>, <code>F3</code>, <code>F4</code>,
   * <code>F5</code>, <code>F6</code>, <code>F7</code>, <code>F8</code>, <code>F9</code>,
   * <code>F10</code>, <code>F11</code>, <code>F12</code>, Valid modifier keys are
   * <code>SHIFT</code>, <code>ALT</code>, and <code>CTRL</code>.
   * </p>
   * Examples: <code>&quot;A&quot;</code>, <code>&quot;F12&quot;</code>,
   * <code>&quot;CTRL+1&quot;</code>, <code>&quot;ALT+ARROW_DOWN&quot;</code>,
   * <code>&quot;ALT+SHIFT+X&quot;</code>.
   * <p>
   * </p>
   * <p>
   * Example code for implementing a key binding: <code><pre>
   * display.setData( RWT.ACTIVE_KEYS, new String[] { &quot;CTRL+1&quot;, &quot;CTRL+2&quot; } );
   * display.addFilter( SWT.KeyDown, new Listener() {
   *   public void handleEvent( Event event ) {
   *     boolean ctrlPressed = ( event.stateMask &amp; SWT.Ctrl ) != 0;
   *     if( ctrlPressed &amp;&amp; event.character == '1' ) {
   *       // handle Ctrl+1
   *     }
   *   }
   * } );
   * </pre></code>
   * </p>
   * 
   * @see Display#setData(String,Object)
   * @see Display#addFilter(int, Listener)
   * @since 1.4
   */
  public static final String ACTIVE_KEYS = "org.eclipse.rap.rwt.activeKeys";

  /**
   * Returns the instance of the life cycle which is currently processed.
   * 
   * @return instance of {@link ILifeCycle}
   */
  public static ILifeCycle getLifeCycle() {
    return RWTFactory.getLifeCycleFactory().getLifeCycle();
  }
  
  /**
   * Returns the instance of the currently available
   * {@link IResourceManager}
   * 
   * @return instance of {@link IResourceManager}
   */
  public static IResourceManager getResourceManager() {
    return RWTFactory.getResourceManager();
  }
  
  /**
   * Returns a manager to add and remove {@link IServiceHandler}s.
   * 
   * @return the {@link IServiceManager}
   */
  public static IServiceManager getServiceManager() {
    return RWTFactory.getServiceManager();
  }
  
  /**
   * Returns the setting store instance for this session.
   * @return a {@link ISettingStore}; never <code>null</code>
   * @since 1.1
   */
  public static ISettingStore getSettingStore() {
    return RWTFactory.getSettingStoreManager().getStore();
  }
  
  /**
   * Returns the {@link IServiceStore} that is mapped
   * to the currently processed request.
   * 
   * @return {@link IServiceStore}
   */
  public static IServiceStore getServiceStore() {
    return ContextProvider.getStateInfo();
  }
  
  /**
   * Returns the <code>ISessionStore</code> of the <code>HttpSession</code>
   * to which the currently processed request belongs.
   * 
   * @return instance of {@link ISessionStore}
   */
  public static ISessionStore getSessionStore() {
    return ContextProvider.getSession();
  }
  
  /**
   * Returns the <code>IApplicationStore</code> instance that represents the web context's
   * global data storage area.
   * 
   * @return instance of {@link IApplicationStore}
   * @since 1.4
   */
  public static IApplicationStore getApplicationStore() {
    return RWTFactory.getApplicationStore();
  }
  
  /**
   * Returns the <code>HttpServletRequest</code> that is currently
   * processed.
   * 
   * @return instance of {@link HttpServletRequest}
   */
  public static HttpServletRequest getRequest() {
    return ContextProvider.getRequest();
  }
  
  /**
   * Returns the <code>HttpServletResponse</code> that is mapped
   * to the currently processed request.
   * 
   * @return instance of {@link HttpServletResponse}
   */
  public static HttpServletResponse getResponse() {
    return ContextProvider.getResponse();
  }

  /**
   * Returns the preferred <code>Locale</code> that the client will accept
   * content in. This is either the <code>Locale</code> that was set in 
   * session-scope using the {@link #setLocale(Locale)} method or the locale
   * based on the <code>Accept-Language</code> HTTP header of the current 
   * request. If neither the <code>Locale</code> was set programmatically, nor 
   * the client request provides an <code>Accept-Language</code> header, this 
   * method returns the default locale for the server.
   * 
   * @return the preferred <code>Locale</code> for the client.
   * 
   * @see #setLocale(Locale)
   */
  public static Locale getLocale() {
    ISessionStore session = ContextProvider.getSession();
    Locale result = ( Locale )session.getAttribute( LOCALE );
    if( result == null ) {
      result = ContextProvider.getRequest().getLocale();
    }
    if( result == null ) {
      result = Locale.getDefault();
    }
    return result;
  }
  
  /**
   * Sets the preferred <code>Locale</code> that the client will accept
   * content in to current session. The value set can be retrieved with
   * the {@link #getLocale()} method.
   * 
   * @see #getLocale()
   */
  public static void setLocale( final Locale locale ) {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( LOCALE, locale );
  }

  /**
   * Returns an instance if <code>IBrowserHistory</code> that provides support 
   * for the browser's history.
   * 
   * @return the browser history support implementation
   * @see IBrowserHistory
   * @since 1.3
   */
  public static IBrowserHistory getBrowserHistory() {
    Object instance = SessionSingletonBase.getInstance( BrowserHistory.class );
    return ( IBrowserHistory )instance;
  }
  
  /**
   * Executes the run method of the given <code>runnable</code> on the 
   * request thread. This method may only be called from the UI thread.
   * <p>
   * <strong>NOTE:</strong> This API is provisional and may change without
   * further notice.
   * </p>
   * @param runnable the code to be executed on the request thread
   * @throws SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the UI thread</li>
   * </ul>
   * @since 1.3
   */
  public static void requestThreadExec( final Runnable runnable ) {
    ParamCheck.notNull( runnable, "runnable" );
    Display display = LifeCycleUtil.getSessionDisplay();
    if( display == null || display.getThread() != Thread.currentThread() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    LifeCycle lifeCycle = ( LifeCycle )getLifeCycle();
    lifeCycle.requestThreadExec( runnable );
  }

  private RWT() {
    // prevent instantiation
  }
}
