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

package org.eclipse.rap.rwt.lifecycle;

import java.text.MessageFormat;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.rwt.widgets.Widget;


/**
 * TODO [rh] JavaDoc
 */
// TODO [rh] should we split up methods into WidgetUtil and WidgetLCAUtil?
public final class WidgetUtil {
  
  private WidgetUtil() {
    // prevent instantiation
  }
  
  public static IWidgetAdapter getAdapter( final Widget widget ) {
    IWidgetAdapter result;
    result = ( IWidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
    if( result == null ) {
      throwAdapterException( IWidgetAdapter.class );
    }
    return result;   
  }
  
  public static String getId( final Widget widget ) {
    return getAdapter( widget ).getId();
  }

  public static AbstractWidgetLCA getLCA( final Widget widget ) {
    Class clazz = ILifeCycleAdapter.class;
    AbstractWidgetLCA result = ( AbstractWidgetLCA )widget.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }
  
  public static Widget find( final Composite root, final String id ) {
    final Widget[] result = { null };
    if( id != null ) {
      WidgetTreeVisitor.accept( root, new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
          if( getId( widget ).equals( id ) ) {
            result[ 0 ] = widget;
          }
          return result[ 0 ] == null;
        }
      } );
    }
    return result[ 0 ];
  }
  
  private static void throwAdapterException( final Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
  }
}