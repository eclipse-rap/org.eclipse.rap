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
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;


class ConfigurationReaderConfigurable implements Configurable {
  private final ServletContext servletContext;

  ConfigurationReaderConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    String realPath = servletContext.getRealPath( "/" );
    EngineConfig engineConfig = new EngineConfig( realPath );
    context.getConfigurationReader().setEngineConfig( engineConfig );
  }

  public void reset( ApplicationContext context ) {
    context.getConfigurationReader().setEngineConfig( null );
  }
}
