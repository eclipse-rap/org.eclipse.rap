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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.resources.IResourceManager;


public class QooxdooResourceUtil_Test extends TestCase {

  private IResourceManager resourceManager;

  public void testRegisterResources() throws Exception {
    QooxdooResourcesUtil.registerResources();
    assertTrue( resourceManager.isRegistered( "client.js" ) );
    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  public void testRegisterResourcesDebug() throws Exception {
    System.setProperty( "org.eclipse.rwt.clientLibraryVariant", "DEBUG" );
    QooxdooResourcesUtil.registerResources();
    assertFalse( resourceManager.isRegistered( "client.js" ) );
    assertTrue( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    resourceManager = RWT.getResourceManager();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
