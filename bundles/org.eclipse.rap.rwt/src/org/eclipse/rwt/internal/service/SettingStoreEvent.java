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

package org.eclipse.rwt.internal.service;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISettingStoreEvent;

/**
 * Default implementation of an {@link ISettingStoreEvent}. 
 */
public final class SettingStoreEvent implements ISettingStoreEvent {

  private String attribute;
  private String oldValue;
  private String newValue;

  public SettingStoreEvent( final String attribute, 
                            final String oldValue, 
                            final String newValue ) {
    ParamCheck.notNull( attribute, "attribute" );
    this.attribute = attribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
   
  ///////////////////////////////
  // interface ISettingStoreEvent

  public String getAttributeName() {
    return attribute;
  }

  public String getNewValue() {
    return newValue;
  }

  public String getOldValue() {
    return oldValue;
  }
}
