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


public interface IWidgetAdapter {
  
  String getId();

  boolean isInitialized();
  
  /**
   * Used to preserve values in the {@link IWidgetLifeCycleAdapter}
   * 
   * @param propertyName the key for the preserved value
   * @param value the value itself
   */
  void preserve( String propertyName, Object value );
  
  /**
   * Returns the preserved value for a specified key.
   * 
   * @param propertyName the key for the preserved value
   * 
   * @return the preserved value or <code>null</code> if
   * there is no value preserved
   */
  Object getPreserved( String propertyName );
}
