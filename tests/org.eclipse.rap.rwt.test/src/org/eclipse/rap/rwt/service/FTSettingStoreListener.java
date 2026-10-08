/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;


final class FTSettingStoreListener
  implements SettingStoreListener
{

  private int count = 0;
  private SettingStoreEvent lastEvent;

  public void settingChanged( SettingStoreEvent event ) {
      count++;
      lastEvent = event;
  }

  int getCount() {
    return count;
  }

  SettingStoreEvent getEvent() {
    return lastEvent;
  }
}
