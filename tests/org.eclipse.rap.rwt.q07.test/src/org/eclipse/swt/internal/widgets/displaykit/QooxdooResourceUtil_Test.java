/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;

import junit.framework.TestCase;


public class QooxdooResourceUtil_Test extends TestCase {

  public void testRegisterResources() throws Exception {
    QooxdooResourcesUtil.registerResources();
    IResourceManager resourceManager = ResourceManager.getInstance();
    assertTrue( resourceManager.isRegistered( "client.js" ) );
    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  public void testRegisterResourcesDebug() throws Exception {
    System.setProperty( "org.eclipse.rwt.clientLibraryVariant", "DEBUG" );
    QooxdooResourcesUtil.registerResources();
    IResourceManager resourceManager = ResourceManager.getInstance();
    assertFalse( resourceManager.isRegistered( "client.js" ) );
    assertTrue( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
