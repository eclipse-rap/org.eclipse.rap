/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rwt.lifecycle.IWidgetLifeCycleAdapter;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCAFacade;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public final class LifeCycleAdapterFactory implements AdapterFactory {

  private static final Class[] ADAPTER_LIST = new Class[] {
    ILifeCycleAdapter.class
  };

  private final Object displayAdapterLock;
  private final Object widgetAdaptersLock;
  // Holds the single display life cycle adapter. MUST be created lazily because its constructor 
  // needs a resource manager to be in place
  private IDisplayLifeCycleAdapter displayAdapter;
  // Maps widget classes to their respective life cycle adapters
  private final Map<Class,ILifeCycleAdapter> widgetAdapters;

  
  public LifeCycleAdapterFactory() {
    displayAdapterLock = new Object();
    widgetAdaptersLock = new Object();
    widgetAdapters = new HashMap<Class,ILifeCycleAdapter>();
  }
  
  public Object getAdapter( Object adaptable, Class adapter ) {
    Object result = null;
    if( isDisplayLCA( adaptable, adapter ) ) {
      result = getDisplayLCA();
    } else if( isWidgetLCA( adaptable, adapter ) ) {
      result = getWidgetLCA( adaptable.getClass() );
    }
    return result;
  }

  public Class[] getAdapterList() {
    return ADAPTER_LIST;
  }

  ///////////////////////////////////////////////////////////
  // Helping methods to obtain life cycle adapter for display

  private static boolean isDisplayLCA( Object adaptable, Class adapter ) {
    return adaptable instanceof Display && adapter == ILifeCycleAdapter.class;
  }

  private synchronized ILifeCycleAdapter getDisplayLCA() {
    synchronized( displayAdapterLock ) {
      if( displayAdapter == null ) {
        displayAdapter = DisplayLCAFacade.getDisplayLCA();
      }
      return displayAdapter;
    }
  }

  ////////////////////////////////////////////////////////////
  // Helping methods to obtain life cycle adapters for widgets

  private static boolean isWidgetLCA( Object adaptable, Class adapter ) {
    return adaptable instanceof Widget && adapter == ILifeCycleAdapter.class;
  }

  private synchronized ILifeCycleAdapter getWidgetLCA( Class clazz ) {
    // [fappel] This code is performance critical, don't change without checking against a profiler
    ILifeCycleAdapter result;
    synchronized( widgetAdaptersLock ) {
      result = widgetAdapters.get( clazz );
      if( result == null ) {
        ILifeCycleAdapter adapter = null;
        Class superClass = clazz;
        while( !Object.class.equals( superClass ) && adapter == null ) {
          adapter = loadWidgetLCA( superClass );
          if( adapter == null ) {
            superClass = superClass.getSuperclass();
          }
        }
        widgetAdapters.put( clazz, adapter );
        result = adapter;
      }
    }
    if( result == null ) {
      String msg = "Failed to obtain life cycle adapter for: " + clazz.getName();
      throw new LifeCycleAdapterException( msg );
    }
    return result;
  }

  private static IWidgetLifeCycleAdapter loadWidgetLCA( Class clazz ) {
    IWidgetLifeCycleAdapter result = null;
    String className = LifeCycleAdapterUtil.getSimpleClassName( clazz );
    String[] variants = LifeCycleAdapterUtil.getKitPackageVariants( clazz );
    for( int i = 0; result == null && i < variants.length; i++ ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( variants[ i ] );
      buffer.append( "." );
      buffer.append( className );
      buffer.append( "LCA" );
      String classToLoad = buffer.toString();
      ClassLoader loader = clazz.getClassLoader();
      try {
        result = ( IWidgetLifeCycleAdapter )ClassUtil.newInstance( loader, classToLoad );
      } catch( ClassInstantiationException thr ) {
        // ignore and try to load next package name variant
      }
    }
    return result;
  }
}
