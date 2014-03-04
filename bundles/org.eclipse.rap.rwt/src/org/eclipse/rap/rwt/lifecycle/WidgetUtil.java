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
package org.eclipse.rap.rwt.lifecycle;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
   */
  public static final String CUSTOM_WIDGET_ID
    = org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.CUSTOM_WIDGET_ID;

  /**
   * @see #CUSTOM_WIDGET_ID
   * @deprecated The system property has been moved to the internal class
   *             {@link RWTProperties}. Please look up the name in the
   *             documentation of {@link #CUSTOM_WIDGET_ID}.
   */
  @Deprecated
  public static final String ENABLE_UI_TESTS = RWTProperties.ENABLE_UI_TESTS;

  private WidgetUtil() {
    // prevent instantiation
  }

  /**
   * Returns the according {@link WidgetAdapter} for a specified
   * widget.
   *
   * @param widget the widget
   * @return the {@link WidgetAdapter} instance
   */
  public static WidgetAdapter getAdapter( Widget widget ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getAdapter( widget );
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
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId( widget );
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
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getVariant( widget );
  }

  /**
   * Returns the {@link AbstractWidgetLCA} instance for this widget.
   *
   * @param widget the widget to obtain the life cycle adapter from
   * @return the life cycle adapter for the given widget
   * @deprecated New custom widgets should use the RemoteObject API instead of LCAs.
   */
  @Deprecated
  public static AbstractWidgetLCA getLCA( Widget widget ) {
    final org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA lca
      = org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getLCA( widget );
    if( lca instanceof AbstractWidgetLCA ) {
      return ( AbstractWidgetLCA )lca;
    }
    return new AbstractWidgetLCA() {

      @Override
      public void readData( Widget widget ) {
        lca.readData( widget );
      }

      @Override
      public void preserveValues( Widget widget ) {
        lca.preserveValues( widget );
      }

      @Override
      public void renderInitialization( Widget widget ) throws IOException {
        lca.renderInitialization( widget );
      }

      @Override
      public void renderChanges( Widget widget ) throws IOException {
        lca.renderChanges( widget );
      }

      @Override
      public void renderDispose( Widget widget ) throws IOException {
        lca.renderDispose( widget );
      }

      @Override
      public void doRedrawFake( Control control ) {
        lca.doRedrawFake( control );
      }
    };
  }

  /**
   * This method searches for a widget with the given <code>id</code> within
   * the widget hierarchy starting at <code>root</code>.
   *
   * @param root the root widget where to start the search
   * @param id the id of the widget to search for
   * @return the widget or <code>null</code> if there was no widget found with
   * the given <code>id</code> within the widget hierarchy
   */
  public static Widget find( Composite root, final String id ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.find( root, id );
  }

  /**
   * Adds keys to the list of keys of widget data that are synchronized with the client. It is save
   * to add the same key twice, there are no side-effects. The method has to be called from the UI
   * thread and affects the entire UI-session. The data is only transferred from server to client,
   * not back.
   * <p>
   * <strong>Note:</strong> This method is considered <strong>provisional</strong> and may change
   * again until the final release.
   * </p>
   *
   * @see org.eclipse.swt.widgets.Widget#setData(String, Object)
   * @param keys The keys to add to the list.
   * @since 2.2
   */
  public static void registerDataKeys( String... keys ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys( keys );
  }

}
