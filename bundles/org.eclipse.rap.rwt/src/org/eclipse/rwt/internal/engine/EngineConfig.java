/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.File;



public class EngineConfig implements IEngineConfig {

  private static final String PATH_PREFIX = "WEB-INF" + File.separator;
  private static final String CLASSES_PATH = PATH_PREFIX + "classes";
  private static final String LIB_PATH = PATH_PREFIX + "lib";

  private final File serverContextDir;

  public EngineConfig( final String appPath ) {
    serverContextDir = new File( appPath );
  }

  public File getServerContextDir() {
    return serverContextDir;
  }

  public File getLibDir() {
    return new File( serverContextDir, LIB_PATH );
  }

  public File getClassDir() {
    return new File( serverContextDir, CLASSES_PATH );
  }
}
