/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.util.EventObject;

import org.eclipse.rwt.internal.util.ParamCheck;

/**
 * A SettingStoreEvent holds information regarding the change
 * of a single attribute inside the setting store. The change can be
 * one of the following:
 * <ul>
 * <li>attribute added - {@link #getOldValue()} returns <code>null</code>,
 *                       {@link #getNewValue()} returns a String other 
 *                       than <code>null</code></li>
 * <li>attribute modified - {@link #getOldValue()} and {@link #getNewValue()}
 *                       return a String other than <code>null</code></li>
 * <li>attribute removed - {@link #getOldValue()} returns a String other
 *                       than <code>null</code>, {@link #getNewValue()} returns
 *                       <code>null</code>
 * </ul>
 * <strong>Note:</strong> Clients are responsible for using the 
 * {@link #getAttributeName()} method, to check if the
 * changed attribute is of interest to them.
 * <p>
 * @since 1.1
 */
public final class SettingStoreEvent extends EventObject {

  private static final long serialVersionUID = 1L;
  
  private String attribute;
  private String oldValue;
  private String newValue;

  public SettingStoreEvent( final ISettingStore source,
                            final String attribute, 
                            final String oldValue, 
                            final String newValue )
  {
    super( source );
    ParamCheck.notNull( attribute, "attribute" );
    this.attribute = attribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
   
  ///////////////////////////////
  // Interface ISettingStoreEvent

  /**
   * Returns the name of the changed attribute.
   * @return a String; never <code>null</code>
   */
  public String getAttributeName() {
    return attribute;
  }

  /**
   * Return the new value of the attribute
   * @return a String; may be <code>null</code> if the attribute has been
   *         removed from the store
   */
  public String getNewValue() {
    return newValue;
  }

  /**
   * Returns the previous value of the attribute.
   * @return a String; may be <code>null</code> if the attribute did not
   *         exist previously
   */
  public String getOldValue() {
    return oldValue;
  }
}
