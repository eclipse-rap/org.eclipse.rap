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

package org.eclipse.rwt.lifecycle;

import java.text.MessageFormat;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


/**
 * This is a helper class to obtain different aspects for a widget
 * related to the handling of widgets in RAP.
 * 
 * @since 1.0
 */
public final class WidgetUtil {
  
  private WidgetUtil() {
    // prevent instantiation
  }
  
  /**
   * Returns the according {@link IWidgetAdapter} for a specified
   * widget.
   * 
   * @param widget the widget
   * @return the {@link IWidgetAdapter} instance
   */
  public static IWidgetAdapter getAdapter( final Widget widget ) {
    IWidgetAdapter result;
    result = ( IWidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
    if( result == null ) {
      throwAdapterException( IWidgetAdapter.class );
    }
    return result;
  }
  
  /**
   * Returns the id of the widget that is used to identify the
   * widget on the client.
   * 
   * @param widget the widget
   * @return the id
   */
  public static String getId( final Widget widget ) {
    return getAdapter( widget ).getId();
  }

  /**
   * Returns the {@link AbstractWidgetLCA} instance for this
   * widget.
   * 
   * @param widget the widget
   * @return the lca
   */
  // TODO [bm] why do we return AbstractWidgetLCA instead of pulling the interesting
  // methods up to IWidgetLifeCycleAdapter and using this to talk to the outside
  // world
  public static AbstractWidgetLCA getLCA( final Widget widget ) {
    Class clazz = ILifeCycleAdapter.class;
    AbstractWidgetLCA result = ( AbstractWidgetLCA )widget.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }
  
  /**
   * This is used to find a widget with a specified id within a widget tree.
   * <!-- does anybody have a better word for tree? -->
   * @param root the root widget where to start the search
   * @param id the id of the widget
   * @return the widget or <code>null</code> if there was no widget found with that id within
   * the tree
   * <!-- does anybody have a better word for tree? -->
   */
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