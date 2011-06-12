/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine.configurables;

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.engine.*;


public class RWTConfigurationConfigurable implements Configurable {
  private final ServletContext servletContext;

  public RWTConfigurationConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    String realPath = servletContext.getRealPath( "/" );
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )context.getConfiguration();
    configuration.configure( realPath );
  }

  public void reset( ApplicationContext context ) {
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )context.getConfiguration();
    configuration.reset();
  }
}