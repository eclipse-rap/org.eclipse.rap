/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt;

import java.lang.reflect.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.*;


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
   * server side environments, that have to deal with different languages
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
    
    private final static Map map = new HashMap();

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
    public static Object getISO8859_1Encoded( final String bundleName, 
                                              final Class clazz )
    {
      ClassLoader loader = clazz.getClassLoader();
      ResourceBundle bundle
        = ResourceBundle.getBundle( bundleName, getLocale(), loader );
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
    public static Object getUTF8Encoded( final String bundleName, 
                                         final Class clazz )
    {
      ClassLoader loader = clazz.getClassLoader();
      ResourceBundle bundle
        = Utf8ResourceBundle.getBundle( bundleName, getLocale(), loader );
      return internalGet( bundle, clazz );
    }
    
    private static Object internalGet( final ResourceBundle bundle, 
                                       final Class clazz )
    {
      Object result;
      synchronized( map ) {
        result = map.get( bundle );
        if( result == null ) {
          try {
            Constructor constructor = clazz.getDeclaredConstructor( null );
            constructor.setAccessible( true );
            result = constructor.newInstance( null );
          } catch( final Exception ex ) {
            throw new IllegalStateException( ex.getMessage() );
          }
          final Field[] fieldArray = clazz.getDeclaredFields();
          for( int i = 0; i < fieldArray.length; i++ ) {
            try {
              int modifiers = fieldArray[ i ].getModifiers();
              if(    String.class.isAssignableFrom( fieldArray[ i ].getType() )
                  && Modifier.isPublic( modifiers ) 
                  && !Modifier.isStatic( modifiers ) )
              {
                try {
                  String value = bundle.getString( fieldArray[ i ].getName() );
                  if( value != null ) {
                    fieldArray[ i ].setAccessible( true );
                    fieldArray[ i ].set( result, value );
                  }
                } catch( final MissingResourceException mre ) {
                  fieldArray[ i ].setAccessible( true );
                  fieldArray[ i ].set( result, "" );
                  mre.printStackTrace();
                }
              }
            } catch( final Exception ex ) {
              // TODO Auto-generated catch block
              ex.printStackTrace();
            }
          }
          map.put( bundle, result );
        }
      }
      return result;
    }
  }
  
  private static final IServiceManager serviceManager = new IServiceManager() {
    public void registerServiceHandler( final String id, 
                                        final IServiceHandler serviceHandler ) 
    {
      ServiceManager.registerServiceHandler( id, serviceHandler );
    }
    public void unregisterServiceHandler( final String id ) 
    {
      ServiceManager.unregisterServiceHandler( id );
    }
  };
  
  /**
   * Returns the instance of the current life cycle
   * which is currently processed.
   * 
   * @return instance of {@link ILifeCycle}
   */
  public static ILifeCycle getLifeCycle() {
    return LifeCycleFactory.getLifeCycle();
  }
  
  /**
   * Returns the instance of the currently available
   * {@link IResourceManager}
   * 
   * @return instance of {@link IResourceManager}
   */
  public static IResourceManager getResourceManager() {
    return ResourceManager.getInstance();
  }
  
  /**
   * Returns a manager to add and remove {@link IServiceHandler}s.
   * 
   * @return the {@link IServiceManager}
   */
  public static IServiceManager getServiceManager() {
    return serviceManager;
  }
  
  /**
   * Returns the setting store instance for this session.
   * @return a {@link ISettingStore}; never <code>null</code>
   * @since 1.1
   */
  public static ISettingStore getSettingStore() {
    return SettingStoreManager.getStore();
  }
  
  /**
   * Returns the {@link IServiceStateInfo} that is mapped
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
   * content in. This is eihter the <code>Locale</code> that was set in 
   * session-scope using the {@link #setLocale(Locale)} method or the locale
   * based on the Accept-Language header of the current request. If neighter
   * the <code>Locale</code> was set programmatically, nor the client request
   * provides an Accept-Language header, this method returns the default
   * locale for the server.
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
  
  private RWT() {
    // prevent instantiation
  }
}
