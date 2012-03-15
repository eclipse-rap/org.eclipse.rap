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

import org.eclipse.rwt.RWT;
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
   * Entrypoint property name for a custom theme to be used with the entrypoint.
   * The value must be the id of a registered theme. If omitted, the default
   * theme will be used.
   *
   * @see RWT#DEFAULT_THEME_ID
   * @see ApplicationConfiguration#addEntryPoint(String, Class, Map)
   * @see ApplicationConfiguration#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String THEME_ID = PREFIX + ".themeId";

  /**
   * Entrypoint property name for custom HTML code to be placed inside the
   * <code>&lt;body&gt;</code> tag of the main page. The value must be proper
   * HTML 4.0 in order not to break the surrounding page.
   *
   * @see ApplicationConfiguration#addEntryPoint(String, Class, Map)
   * @see ApplicationConfiguration#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String BODY_HTML = PREFIX + ".bodyHtml";

}
