/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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

import java.text.MessageFormat;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


public final class WidgetUtil {

  private WidgetUtil() {
    // prevent instantiation
  }

  public static WidgetAdapter getAdapter( Widget widget ) {
    WidgetAdapter adapter = widget.getAdapter( WidgetAdapter.class );
    if( adapter == null ) {
      throw new IllegalStateException( "Could not retrieve an instance of WidgetAdapter." );
    }
    return adapter;
  }

  public static String getId( Widget widget ) {
    return getAdapter( widget ).getId();
  }

  public static String getVariant( Widget widget ) {
    String result = null;
    WidgetAdapterImpl widgetAdapter = ( WidgetAdapterImpl )getAdapter( widget );
    Object data = widget.getData( RWT.CUSTOM_VARIANT );
    if( data instanceof String ) {
      result = ( String )data;
      if( !result.equals( widgetAdapter.getCachedVariant() ) ) {
        if( validateVariantString( result ) ) {
          widgetAdapter.setCachedVariant( result );
        } else {
          String pattern = "Illegal character in widget variant ''{0}''";
          Object[] arguments = new Object[] { result };
          String message = MessageFormat.format( pattern, arguments );
          throw new IllegalArgumentException( message );
        }
      }
    }
    return result;
  }

  public static AbstractWidgetLCA getLCA( Widget widget ) {
    AbstractWidgetLCA lca = ( AbstractWidgetLCA )widget.getAdapter( WidgetLifeCycleAdapter.class );
    if( lca == null ) {
      throw new IllegalStateException( "Could not retrieve an instance of WidgetLifeCycleAdapter." );
    }
    return lca;
  }

  public static Widget find( Composite root, final String id ) {
    final Widget[] result = { null };
    if( id != null ) {
      WidgetTreeVisitor.accept( root, new AllWidgetTreeVisitor() {
        @Override
        public boolean doVisit( Widget widget ) {
          if( getId( widget ).equals( id ) ) {
            result[ 0 ] = widget;
          }
          return result[ 0 ] == null;
        }
      } );
    }
    return result[ 0 ];
  }

  public static void registerDataKeys( String... keys ) {
    ParamCheck.notNull( keys, "keys" );
    WidgetDataUtil.registerDataKeys( keys );
  }

  private static boolean validateVariantString( String variant ) {
    boolean result = false;
    String name = variant;
    if( name.startsWith( "-" ) ) {
      name = name.substring( 1 );
    }
    int length = name.length();
    if( length > 0 ) {
      result = isValidStart( name.charAt( 0 ) );
      for( int i = 1; i < length && result; i++ ) {
        result &= isValidPart( name.charAt( i ) );
      }
    }
    return result;
  }

  private static boolean isValidStart( char ch ) {
    return ch == '_'
      || ( ch >= 'a' && ch <= 'z' )
      || ( ch >= 'A' && ch <= 'Z' )
      || ( ch >= 128 && ch <= 255 );
  }

  private static boolean isValidPart( char ch ) {
    return isValidStart( ch )
      || ( ch >= '0' && ch <= '9' )
      || ch == '-';
  }

}
