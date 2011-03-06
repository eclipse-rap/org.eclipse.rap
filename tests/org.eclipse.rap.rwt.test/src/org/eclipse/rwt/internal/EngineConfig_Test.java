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
package org.eclipse.rwt.internal;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class EngineConfig_Test extends TestCase {

  public void testConfig() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    EngineConfig config = new EngineConfig( path );

    assertTrue( config.getServerContextDir().exists() );
    assertTrue( config.getClassDir().exists() );
    assertTrue( config.getLibDir().exists() );
  }
  
  protected void setUp() {
    Fixture.createWebContextDirectories();
  }
  
  protected void tearDown() throws Exception {
  }
}
