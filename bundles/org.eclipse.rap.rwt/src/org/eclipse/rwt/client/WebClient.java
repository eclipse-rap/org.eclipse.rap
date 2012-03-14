/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.client;

import java.util.Map;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


/**
 * The default RWT web client.
 *
 * @since 1.5
 */
public class WebClient {

  private static final String PREFIX = "org.eclipse.rap.rwt.webclient";

  /**
   * Property name for the id of the theme to be used with an entrypoint.
   * 
   * @see ApplicationConfiguration#addEntryPoint(String, Class, Map)
   * @see ApplicationConfiguration#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String THEME_ID = PREFIX + ".themeId";

}
