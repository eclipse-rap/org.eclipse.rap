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

package org.eclipse.swt.internal.lifecycle;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.lifecycle.ILifeCycleAdapter;
import org.eclipse.swt.lifecycle.IWidgetLifeCycleAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import com.w4t.AdapterFactory;


/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public final class LifeCycleAdapterFactory implements AdapterFactory {

  private static final Class[] ADAPTER_LIST = new Class[] {
    ILifeCycleAdapter.class,
  };
  
  // Holds the single display life cycle adapter. MUST be created lazily
  // because its constructor needs a resource manager to be in place
  private static IDisplayLifeCycleAdapter displayAdapter;
  // Maps widget classes to their respective life cycle adapters
  // Key: Class<Widget>, value: IWidgetLifeCycleAdapter
  private static final Map widgetAdapters = new HashMap();

  public Object getAdapter( final Object adaptable, final Class adapter ) {
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
  
  private boolean isDisplayLCA( final Object adaptable, final Class adapter ) {
    return adaptable instanceof Display && adapter == ILifeCycleAdapter.class;
  }

  private static synchronized ILifeCycleAdapter getDisplayLCA() {
    if( displayAdapter == null ) {
      displayAdapter = new DisplayLCA();
    }
    return displayAdapter;
  }
  
  ////////////////////////////////////////////////////////////
  // Helping methods to obtain life cycle adapters for widgets
  
  private boolean isWidgetLCA( final Object adaptable, final Class adapter ) {
    return adaptable instanceof Widget && adapter == ILifeCycleAdapter.class;
  }
  
  private static String getSimpleClassName( final Class clazz ) {
    String className = clazz.getName();
    int idx = className.lastIndexOf( '.' );
    return className.substring( idx + 1 );
  }
  
  private static synchronized ILifeCycleAdapter getWidgetLCA( 
    final Class clazz ) 
  {
    // Note [fappel]: Since this code is performance critical, don't change
    //                anything without checking it against a profiler.
    ILifeCycleAdapter result = ( ILifeCycleAdapter )widgetAdapters.get( clazz );
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
    if( result == null ) {
      String text = "Failed to obtain life cycle adapter for class ''{0}\''.";
      Object[] params = new Object[]{ clazz.getName() };
      String msg = MessageFormat.format( text, params );
      throw new LifeCycleAdapterException( msg );      
    }
    return result;
  }
  
  private static IWidgetLifeCycleAdapter loadWidgetLCA( final Class clazz ) 
  {
    IWidgetLifeCycleAdapter result = null;
    String[] variants = getPackageVariants( clazz.getPackage().getName() );
    for( int i = 0; result == null && i < variants.length; i++ ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( variants[ i ] );
      buffer.append( "." );
      buffer.append( getSimpleClassName( clazz ).toLowerCase() );
      buffer.append( "kit." );
      buffer.append( getSimpleClassName( clazz ) );
      buffer.append( "LCA" );
      String classToLoad = buffer.toString();
      ClassLoader loader = clazz.getClassLoader();
      try {
        Class adapterClass = loader.loadClass( classToLoad );
        result = ( IWidgetLifeCycleAdapter )adapterClass.newInstance();
      } catch( final Throwable thr ) {
        // ignore and try to load next package name variant
      }
    }
    return result;
  }
  
  static String[] getPackageVariants( final String packageName ) {
    String[] result;
    if( packageName == null || "".equals( packageName ) ) {
      result = new String[] { "internal" };
    } else {
      String[] segments = packageName.split( "\\." );
      result = new String[ segments.length + 1 ];
      for( int i = 0; i < result.length; i++ ) {
        StringBuffer buffer = new StringBuffer();
        for( int j = 0; j < segments.length; j++ ) {
          if( j == i ) {
            buffer.append( "internal." );
          }
          buffer.append( segments[ j ] );
          if( j < segments.length - 1 ) {
            buffer.append( "." );
          }
        }
        if( i == segments.length ) {
          buffer.append( ".internal" );
        }
        result[ i ] = buffer.toString();
      }
    }
    return result;
  }
}
