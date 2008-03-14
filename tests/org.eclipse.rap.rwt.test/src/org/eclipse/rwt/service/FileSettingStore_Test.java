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


/**
 * Tests for the classes {@link FileSettingStore} and 
 * {@link RWTFileSettingStoreFactory}.
 */
public class FileSettingStore_Test extends AbstractSettingStore_Test {
  
  private ISettingStoreFactory factory = new RWTFileSettingStoreFactory();

  protected ISettingStoreFactory getFactory() {
    return factory;
  }
  
  public void testFactoryCreatesRightInstance() {
    String id = getClass().getName();
    assertTrue( factory.createSettingStore( id ) instanceof FileSettingStore );
  }


}
