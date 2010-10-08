/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterUtil;
import org.eclipse.swt.widgets.Widget;

public final class ThemeAdapterUtil {

  private static final Map themeAdapters = new HashMap();

  public static IThemeAdapter getThemeAdapter( final Widget widget ) {
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
    String packageName = clazz.getPackage().getName();
    String[] variants = LifeCycleAdapterUtil.getPackageVariants( packageName );
    for( int i = 0; result == null && i < variants.length; i++ ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( variants[ i ] );
      buffer.append( "." );
      String simpleClassName = LifeCycleAdapterUtil.getSimpleClassName( clazz );
      buffer.append( simpleClassName.toLowerCase( Locale.ENGLISH ) );
      buffer.append( "kit." );
      buffer.append( simpleClassName );
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
      Class adapterClass = classLoader.loadClass( className );
      result = ( IThemeAdapter )adapterClass.newInstance();
    } catch( final ClassNotFoundException e ) {
      // ignore, try to load from next package name variant
    } catch( final InstantiationException e ) {
      String message =   "Failed to instantiate theme adapter class "
                       + className;
      throw new ThemeManagerException( message, e );
    } catch( final IllegalAccessException e ) {
      String message =   "Failed to instantiate theme adapter class "
                       + className;
      throw new ThemeManagerException( message, e );
    }
    return result;
  }
}
