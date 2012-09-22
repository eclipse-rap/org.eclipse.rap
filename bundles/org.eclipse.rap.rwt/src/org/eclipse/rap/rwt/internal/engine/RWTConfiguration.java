/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.engine;

import java.io.File;

import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class RWTConfiguration {

  private File contextDirectory;

  public void configure( String contextDirectory ) {
    ParamCheck.notNull( contextDirectory, "contextDirectory" );
    this.contextDirectory = new File( contextDirectory );
  }

  public void reset() {
    contextDirectory = null;
  }

  public File getContextDirectory() {
    checkConfigured();
    return contextDirectory;
  }

  public boolean isConfigured() {
    return contextDirectory != null;
  }

  private void checkConfigured() {
    if( !isConfigured() ) {
      throw new IllegalStateException( "RWTConfigurationImpl has not been configured." );
    }
  }

}