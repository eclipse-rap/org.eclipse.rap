/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import java.text.MessageFormat;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


/**
 * This is a helper class to obtain different aspects for a widget
 * related to the handling of widgets in RAP.
 *
 * @since 2.0
 */
public final class WidgetUtil {

  /**
   * @deprecated Use {@link RWT#CUSTOM_VARIANT} instead
   */
  @Deprecated
  public static final String CUSTOM_VARIANT = RWT.CUSTOM_VARIANT;

  /**
   * <p>
   * <strong>Note:</strong> This constant is provisional and subject to change
   * without further notice.
   * </p>
   * <p>
   * This constant can be used to apply a custom widget id to a widget. By
   * default, the framework applies a unique widget id to every widget. This id
   * is used to identify the widget in the client/server protocol. For UI tests,
   * it can be helpful to replace these generated ids with custom ids in order
   * to make the ids more human-readable and more stable against changes in the
   * UI. The following snippet applies a custom widget id to a widget:
   * </p>
   *
   * <pre>
   * widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, &quot;myCustomId&quot; )
   * </pre>
   * <p>
   * The support for custom widget ids must be explicitly enabled by setting the
   * system property <code>org.eclipse.rap.rwt.enableUITests</code> to
   * <code>true</code>. If activated, the default web client will also set the
   * HTML id attribute for every widget that are rendered to the client to the
   * widget id.
   * </p>
   * <p>
   * A custom widget id must be unique within the user session. It is the
   * clients' responsibility to choose a unique id. Assigning an id that is used
   * by another widget will lead to indeterministic behavior.
   * <p>
   * </p>
   * A custom widget id must only contain characters that are valid according to
   * the <a href="http://www.w3.org/TR/html401/types.html#type-cdata">W3C
   * recommendation for id and name attributes</a>. </p>
   *
   * @see Widget#setData(String,Object)
   * @see #getId(Widget)
   * @since 1.1
   */
  public static final String CUSTOM_WIDGET_ID = "org.eclipse.rap.rwt.customWidgetId";

  /**
   * @see #CUSTOM_WIDGET_ID
   * @deprecated The system property has been moved to the internal class
   *             {@link RWTProperties}. Please look up the name in the
   *             documentation of {@link #CUSTOM_WIDGET_ID}.
   * @since 1.1
   */
  @Deprecated
  public static final String ENABLE_UI_TESTS = RWTProperties.ENABLE_UI_TESTS;

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
  public static IWidgetAdapter getAdapter( Widget widget ) {
    IWidgetAdapter result;
    result = widget.getAdapter( IWidgetAdapter.class );
    if( result == null ) {
      throwAdapterException( IWidgetAdapter.class );
    }
    return result;
  }

  /**
   * Returns the id of the given <code>widget</code> that is used to identify
   * the widget on the client.
   *
   * @param widget the widget to obtain the id for, must not be
   *          <code>null</code>
   * @return the id for the given <code>widget</code>
   */
  public static String getId( Widget widget ) {
    return getAdapter( widget ).getId();
  }

  /**
   * Returns the widget variant defined for the given widget using
   * <code>Widget.setData()</code>.
   *
   * @param widget the widget whose variant is requested
   * @return the variant or <code>null</code> if no variant has been specified
   *         for the given widget
   */
  public static String getVariant( Widget widget ) {
    String result = null;
    WidgetAdapter widgetAdapter = ( WidgetAdapter )getAdapter( widget );
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

  /**
   * Returns the {@link AbstractWidgetLCA} instance for this widget.
   *
   * @param widget the widget to obtain the life cycle adapter from
   * @return the life cycle adapter for the given widget
   */
  // TODO [bm] why do we return AbstractWidgetLCA instead of pulling the interesting
  // methods up to WidgetLifeCycleAdapter and using this to talk to the outside
  // world
  public static AbstractWidgetLCA getLCA( Widget widget ) {
    AbstractWidgetLCA lca = ( AbstractWidgetLCA )widget.getAdapter( WidgetLifeCycleAdapter.class );
    if( lca == null ) {
      throwAdapterException( AbstractWidgetLCA.class );
    }
    return lca;
  }

  /**
   * This method searches for a widget with the given <code>id</code> within
   * the widget hierachy starting at <code>root</code>.
   *
   * @param root the root widget where to start the search
   * @param id the id of the widget to search for
   * @return the widget or <code>null</code> if there was no widget found with
   * the given <code>id</code> within the widget hierarchy
   */
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

  private static void throwAdapterException( Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
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
