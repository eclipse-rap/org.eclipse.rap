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
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.service.FileSettingStore_Test;
import org.eclipse.rap.rwt.service.SettingStoreFactory;
import org.eclipse.rap.rwt.testfixture.internal.service.MemorySettingStore;
import org.eclipse.rap.rwt.testfixture.internal.service.MemorySettingStoreFactory;
import org.junit.Test;


/**
 * Tests for the classes {@link MemorySettingStore}
 * and {@link MemorySettingStoreFactory}.
 */
public class MemorySettingStore_Test extends FileSettingStore_Test {

  private final SettingStoreFactory factory = new MemorySettingStoreFactory();

  @Override
  protected SettingStoreFactory getFactory() {
    return factory;
  }

  @Test
  public void testFactoryCreatesRightInstance() {
    String id = getClass().getName();
    assertTrue( factory.createSettingStore( id ) instanceof MemorySettingStore );
  }

}
