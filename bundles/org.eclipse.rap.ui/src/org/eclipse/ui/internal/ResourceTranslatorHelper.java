/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import org.eclipse.core.internal.runtime.ResourceTranslator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Helper class that registers a runnable that handles NLS for externalized
 * labels in plugin.xml files regarding the setting of
 * <code>{@link RWT#getLocale()}</code>.
 * 
 * <p>Note that this functionality only takes effect if the
 * 'org.eclipse.rap.equinox.registry' fragment is available.</p>
 */
class ResourceTranslatorHelper {
  private static final Class[] PARAM_TYPE_ALGORITHM
    = new Class[] { Runnable.class };
  private static final String METHOD_SET_TRANSLATOR_ALGORITHM
    = "setTranslatorAlgorithm"; //$NON-NLS-1$
  private static final String METHOD_GET_BUNDLE_CONTEXT
    = "getBundleContext"; //$NON-NLS-1$
	private static final String FIELD_ALGORITHM_IO = "algorithmIO"; //$NON-NLS-1$
  private static Class CLASS_REGISTRY_FACTORY = RegistryFactory.class;
  
  
  /**
   * Plugable runnable that handles translation of externalized labels 
   * of extension declarations.
   */
  private static final class TranslatorAlgorithm implements Runnable {

    public void run() {
      String result = getKey();
      ResourceBundle bundle = null;
      try {
        if( ContextProvider.hasContext() ) {
          bundle = getResourceBundle();
        }
      } catch( final Exception shouldNotHappen ) {
        // TODO [fappel]: I use reflection to get the bundle context since this
        //                is not available in 3.2. We do not support the 
        //                NLS patch fragment in 3.2 but unfortunately our build
        //                process for 3.2 fails. As we will skip support for
        //                3.2 soon, this seems to be the easiest solution.
        //                Remove reflection usage after 3.2 support has been
        //                skipped.
        shouldNotHappen.printStackTrace();
      }
      if( bundle != null ) {
        result = ResourceTranslator.getResourceString( null, getKey(), bundle );
      }
      getIOHandle().set( result );
    }

    private ResourceBundle getResourceBundle()
      throws NoSuchMethodException,
             IllegalAccessException,
             InvocationTargetException
    {
      Bundle bundle = Platform.getBundle( getSymbolicName() );
      BundleContext bundleContext = getBundleContext( bundle );
      String id = BundleLocalization.class.getName();
      ServiceReference reference = bundleContext.getServiceReference( id );
      BundleLocalization localization
        = ( BundleLocalization )bundleContext.getService( reference );
      return localization.getLocalization( bundle, RWT.getLocale().toString() );
    }

    private BundleContext getBundleContext( final Bundle bundle )
      throws NoSuchMethodException,
             IllegalAccessException,
             InvocationTargetException
    {
      Method getBundleContext
        = Bundle.class.getDeclaredMethod( METHOD_GET_BUNDLE_CONTEXT, null );
      return ( BundleContext )getBundleContext.invoke( bundle, null );
    }

    private String getKey() {
      return ( ( String[] )getIOHandle().get() )[ 0 ];
    }

    private String getSymbolicName() {
      return ( ( String[] )getIOHandle().get() )[ 1 ];
    }

    private ThreadLocal getIOHandle() {
      ThreadLocal io = null;
      try {
        io = ( ThreadLocal )getAlgorithmIO().get( null );
      } catch( final Exception shouldNotHappen ) {
        // the exceptional condition were already checked before setting
        // this algorithm implementation
        shouldNotHappen.printStackTrace();
      }
      return io;
    }
  }

  
  static void registerAlgorithm() {
    if( isRWTLocalSpecificTranslationAllowed() ) {
      try {
        Object[] param = new Object[] {
          new TranslatorAlgorithm()
        };
        getAlgorithmSetter().invoke( null, param );
        
      } catch( final Exception shouldNotHappen ) {
        shouldNotHappen.printStackTrace();
      }
    }
  }

  
  //////////////////
  // helping methods
  
  private static boolean isRWTLocalSpecificTranslationAllowed() {
    boolean result = false;
    try {
      getAlgorithmIO();
      getAlgorithmSetter();
      result = true;
    } catch( final Exception ignore ) {
      // if an exception occures the patched version of RegistryFactory
      // isn't available
    }
    return result ;
  }

  private static Method getAlgorithmSetter() throws NoSuchMethodException {
    String name = METHOD_SET_TRANSLATOR_ALGORITHM;
    Class[] type = PARAM_TYPE_ALGORITHM;
    return CLASS_REGISTRY_FACTORY.getDeclaredMethod( name, type );
  }

  private static Field getAlgorithmIO() throws NoSuchFieldException {
    return CLASS_REGISTRY_FACTORY.getDeclaredField( FIELD_ALGORITHM_IO );
  }

  
  private ResourceTranslatorHelper() {
    // prevent instance creation
  }
}
