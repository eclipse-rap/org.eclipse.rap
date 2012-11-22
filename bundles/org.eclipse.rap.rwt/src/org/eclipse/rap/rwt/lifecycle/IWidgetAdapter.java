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

import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this interface provide RWT specific operations on widgets.
 * They are used to preserve the state of a widget.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 2.0
 */
public interface IWidgetAdapter extends RemoteObjectAdapter {

  /**
   * Returns the preserved value for a specified key.
   *
   * @param propertyName the key for the preserved value
   * @return the preserved value or <code>null</code> if there is no value
   *         preserved for this key
   */
  Object getPreserved( String propertyName );
  
  /**
   * Indicates whether this object has been initialized already. An object is
   * considered initialized when the create message has been rendered.
   *
   * @return <code>true</code> if this object has already been initialized,
   *         <code>false</code> otherwise
   */
  boolean isInitialized();

  /**
   * Notifies the receiver that the given <code>widget</code> has beend
   * disposed of.
   * @param widget the widget that has been disposed of
   * @since 1.2
   */
  void markDisposed( Widget widget );
  
  /**
   * Preserves a specified value for a specified key. Used to preserve values in
   * the LCA method
   *
   * @param propertyName the key to map the preserved value to
   * @param value the value to preserve
   */
  void preserve( String propertyName, Object value );
}
