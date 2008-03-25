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

import org.eclipse.rwt.service.AbstractSettingStore_TestBase;
import org.eclipse.rwt.service.ISettingStoreFactory;

/**
 * Tests for the classes {@link MemorySettingStore} 
 * and {@link MemorySettingStoreFactory}.
 */
public class MemorySettingStore_Test extends AbstractSettingStore_TestBase {
  
  private ISettingStoreFactory factory = new MemorySettingStoreFactory();

  protected ISettingStoreFactory getFactory() {
    return factory;
  }
  
  public void testFactoryCreatesRightInstance() {
    String id = getClass().getName();
    assertTrue( factory.createSettingStore( id ) instanceof MemorySettingStore );
  }

}
