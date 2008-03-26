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

package org.eclipse.rwt.service;

final class FTSettingStoreListener 
  implements ISettingStoreListener
{

  private int count = 0;
  private ISettingStoreEvent lastEvent;
  
  public void settingChanged( final ISettingStoreEvent event ) {
      count++;
      lastEvent = event;
  }
  
  int getCount() {
    return count;
  }
  
  ISettingStoreEvent getEvent() {
    return lastEvent;
  }
}