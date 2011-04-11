/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - removed singletons and static fields (Bug 227787)
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterUtil;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.swt.widgets.Widget;


public final class ThemeAdapterUtil {

  private final Map themeAdapters;

  public static IThemeAdapter getThemeAdapter( final Widget widget ) {
    return getInstance().doGetThemeAdapter( widget );
  }

  private IThemeAdapter doGetThemeAdapter( final Widget widget ) {
    Class widgetClass = widget.getClass();
    IThemeAdapter result;
    synchronized( themeAdapters ) {
      result = ( IThemeAdapter )themeAdapters.get( widgetClass );
      if( result == null ) {
        IThemeAdapter adapter = null;
        Class superClass = widgetClass;
        while( !Object.class.equals( superClass ) && adapter == null ) {
          adapter = loadThemeAdapter( superClass );
          if( adapter == null ) {
            superClass = superClass.getSuperclass();
          }
        }
        themeAdapters.put( widgetClass, adapter );
        result = adapter;
      }
    }
    if( result == null ) {
      String text = "Failed to obtain theme adapter for class ''{0}\''.";
      Object[] params = new Object[]{ widgetClass.getName() };
      String msg = MessageFormat.format( text, params );
      throw new ThemeManagerException( msg );
    }
    return result;
  }

  private static IThemeAdapter loadThemeAdapter( final Class clazz ) {
    IThemeAdapter result = null;
    String className = LifeCycleAdapterUtil.getSimpleClassName( clazz );
    String[] variants = LifeCycleAdapterUtil.getKitPackageVariants( clazz );
    for( int i = 0; result == null && i < variants.length; i++ ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( variants[ i ] );
      buffer.append( "." );
      buffer.append( className );
      buffer.append( "ThemeAdapter" );
      String classToLoad = buffer.toString();
      ClassLoader loader = clazz.getClassLoader();
      result = loadThemeAdapter( classToLoad, loader );
    }
    return result;
  }

  private static IThemeAdapter loadThemeAdapter( final String className,
                                                 final ClassLoader classLoader )
  {
    IThemeAdapter result = null;
    try {
      result = ( IThemeAdapter )ClassUtil.newInstance( classLoader, className );
    } catch( ClassInstantiationException cie ) {
      // ignore, try to load from next package name variant
    }
    return result;
  }

  private static ThemeAdapterUtil getInstance() {
    return ( ThemeAdapterUtil )ApplicationContext.getSingleton( ThemeAdapterUtil.class );
  }

  private ThemeAdapterUtil() {
    themeAdapters = new HashMap();
  }
}
