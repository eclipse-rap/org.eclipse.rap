/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


/** unit tests for EngineConfig. */
public class EngineConfig_Test extends TestCase {

  public EngineConfig_Test( String name ) {
    super( name );
  }
  
  protected void tearDown() throws Exception {
    Fixture.removeContext();
  }
  
  public void testConfig() throws Exception {
    File appRootDir = Fixture.getWebAppBase();
    
    EngineConfig config = new EngineConfig( appRootDir.toString() );
    assertTrue( config.getSourceDir() == null );
    
    assertTrue( config.getClassDir().exists() );    
    assertTrue( config.getLibDir().exists() );
    assertTrue( config.getServerContextDir().exists() );
    assertTrue( config.getConfigFile().exists() );
  }
}