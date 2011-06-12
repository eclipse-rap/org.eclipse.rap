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
package org.eclipse.rap.rwt.osgi;

import org.eclipse.rwt.engine.Configurator;
import org.osgi.service.http.HttpService;


public interface RWTService {

  public static final String PROPERTY_CONTEXT_NAME = "contextName";

  RWTContext start( Configurator configurator,
                    HttpService httpService,
                    String contextName );

  RWTContext start( Configurator configurator,
                    HttpService httpService,
                    String contextName,
                    String contextDirectory );
  boolean isAlive();
}