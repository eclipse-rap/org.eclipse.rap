/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RWTConfigurationImpl_Test extends TestCase {

  private RWTConfigurationImpl configuration;

  public void testConfigure() {
    configure();

    assertTrue( configuration.getContextDirectory().exists() );
  }

  public void testConfigureParamPathNotNull() {
    try {
      configuration.configure( null );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnconfigured() {
    try {
      configuration.getContextDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  protected void setUp() {
    Fixture.createWebContextDirectory();
    configuration = new RWTConfigurationImpl();
  }

  protected void tearDown() {
    Fixture.deleteWebContextDirectory();
  }

  private void configure() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    configuration.configure( path );
  }
}