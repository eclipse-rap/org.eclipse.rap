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

import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this interface provide RWT specific operations on widgets. They are used to preserve
 * the state of a widget.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 * @deprecated This adapter interface was provided to facilitate the implementation of LCAs. New
 *             custom widgets should use the RemoteObject API instead of LCAs.
 * @see org.eclipse.rap.rwt.remote.RemoteObject
 */
@Deprecated
public interface WidgetAdapter extends org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter {

  /**
   * Returns the id that identifies the widget on the client.
   *
   * @return the widget id
   */
  String getId();

  /**
   * Returns the parent given to the widget constructor.
   *
   * @return the parent widget
   */
  Widget getParent();

  /**
   * Indicates whether this widget has been initialized already. A widget is considered initialized
   * when the response that creates and initializes the widget has been rendered.
   *
   * @return <code>true</code> if this widget has already been initialized, <code>false</code>
   *         otherwise
   */
  boolean isInitialized();

  /**
   * Preserves a specified value for a specified key. Used to preserve values in the LCA method
   * {@link WidgetLifeCycleAdapter#preserveValues(Widget) preserveValues}.
   *
   * @param propertyName the key to map the preserved value to
   * @param value the value to preserve
   */
  void preserve( String propertyName, Object value );

  /**
   * Returns the preserved value for a specified key.
   *
   * @param propertyName the key for the preserved value
   * @return the preserved value or <code>null</code> if there is no value preserved for this key
   */
  Object getPreserved( String propertyName );

  /**
   * Notifies the receiver that the given <code>widget</code> has been disposed of.
   *
   * @param widget the widget that has been disposed of
   */
  void markDisposed( Widget widget );

}
