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
package org.eclipse.rwt.internal.engine;

import java.io.File;

import org.eclipse.rwt.internal.IEngineConfig;


/** 
 * <p>containns configuration seetings for the W4 Toolkit engine.</p>
 */
public class EngineConfig implements IEngineConfig {

  private static final String WEB_INF =   File.separator 
                                        + "WEB-INF" 
                                        + File.separator;

  private static final String CONF    = WEB_INF + "conf"; 
  private static final String CLASSES = WEB_INF + "classes";
  private static final String LIB     = WEB_INF + "lib";    

  private File serverContextDir;
  

  public EngineConfig( final String appPath ) {
    serverContextDir = new File( appPath );
  }


  // interface methods
  ////////////////////
  
  public File getConfigFile() {
    File result = new File(   getServerContextDir().toString() 
                            + CONF 
                            + File.separator
                            + "W4T.xml" );
    return result;
  }

  public File getLibDir() {
    File result = new File(   getServerContextDir().toString() 
                            + LIB );
    return result;    
  }

  public File getServerContextDir() {
    return serverContextDir;
  }

  public File getClassDir() {
    File result = new File(   getServerContextDir().toString() 
                            + CLASSES );
    return result;    
  }

  public File getSourceDir() {
    return null;
  }
}