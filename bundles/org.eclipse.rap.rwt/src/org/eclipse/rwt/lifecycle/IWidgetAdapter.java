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


/**
 * Instances of this interface provide RWT specific operations on widgets.
 * They are used to preserve the state of a widget.
 * 
 * @since 1.0
 */
public interface IWidgetAdapter {
  
  /**
   * Returns the id that identifies the widget on the client.
   * 
   * @return the widget id
   */
  String getId();

  /**
   * Indicates whether this widget has been initialized already. A widget is
   * considered initialized when the response that creates and initializes the
   * widget has been rendered.
   * 
   * @return <code>true</code> if this widget has already been initialized,
   *         <code>false</code> otherwise
   */
  boolean isInitialized();
  
  /**
   * Preserves a specified value for a specified key. Used to preserve values in
   * the LCA method
   * {@link IWidgetLifeCycleAdapter#preserveValues(org.eclipse.swt.widgets.Widget)
   * preserveValues}.
   * 
   * @param propertyName the key to map the preserved value to
   * @param value the value to preserve
   */
  void preserve( String propertyName, Object value );
  
  /**
   * Returns the preserved value for a specified key.
   * 
   * @param propertyName the key for the preserved value
   * @return the preserved value or <code>null</code> if there is no value
   *         preserved for this key
   */
  Object getPreserved( String propertyName );
}
