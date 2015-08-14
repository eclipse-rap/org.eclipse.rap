/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterUtil.getKitPackageVariants;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rap.rwt.internal.util.ClassUtil;
import org.eclipse.swt.widgets.Widget;


public final class LifeCycleAdapterFactory {

  private final Object widgetAdaptersLock;
  private final Map<Class<?>, WidgetLCA<?>> widgetAdapters;

  public LifeCycleAdapterFactory() {
    widgetAdaptersLock = new Object();
    widgetAdapters = new HashMap<>();
  }

  public WidgetLCA<?> getWidgetLCA( Widget widget ) {
    Class<?> clazz = widget.getClass();
    // [fappel] This code is performance critical, don't change without checking against a profiler
    WidgetLCA<?> result;
    synchronized( widgetAdaptersLock ) {
      result = widgetAdapters.get( clazz );
      if( result == null ) {
        WidgetLCA<?> adapter = null;
        Class<?> superClass = clazz;
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
    return result;
  }

  private static WidgetLCA<?> loadWidgetLCA( Class<?> clazz ) {
    String className = clazz.getSimpleName();
    ClassLoader loader = clazz.getClassLoader();
    for( String variant : getKitPackageVariants( clazz ) ) {
      String classToLoad = new StringBuilder()
        .append( variant )
        .append( '.' )
        .append( className )
        .append( "LCA" ).toString();
      try {
        return ( WidgetLCA<?> )ClassUtil.newInstance( loader, classToLoad );
      } catch( @SuppressWarnings( "unused" ) ClassInstantiationException ignore ) {
        // ignore and try to load next package name variant
      }
    }
    return null;
  }

}
