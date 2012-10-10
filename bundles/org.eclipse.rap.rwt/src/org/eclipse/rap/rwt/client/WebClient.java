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
package org.eclipse.rap.rwt.client;

import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rap.rwt.resources.IResource;


/**
 * The default RWT web client.
 *
 * @since 2.0
 */
public class WebClient implements Client {

  private static final String PREFIX = "org.eclipse.rap.rwt.webclient";

  /**
   * Entrypoint property name for a custom theme to be used with the entrypoint.
   * The value must be the id of a registered theme. If omitted, the default
   * theme will be used.
   *
   * @see RWT#DEFAULT_THEME_ID
   * @see Application#addEntryPoint(String, Class, Map)
   * @see Application#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String THEME_ID = PREFIX + ".themeId";

  /**
   * Entrypoint property name for additional HTML elements to be added to the
   * &lt;head&gt; section of the startup page. The value must contain a valid
   * HTML snippet that consists only of HTML elements that are permissible
   * sub-elements of <code>head</code> such as <code>meta</code> or
   * <code>link</code>.
   * <p>
   * <strong>Warning:</strong> the property value will not be validated by the
   * framework. Invalid HTML can break the application entirely or lead to
   * problems that are hard to identify. It's the responsibility of the
   * developer to ensure the correctness of the resulting page.
   * </p>
   *
   * @see Application#addEntryPoint(String, Class, Map)
   * @see Application#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String HEAD_HTML = PREFIX + ".additionalHeaders";

  /**
   * Entrypoint property name for custom HTML code to be placed inside the
   * <code>body</code> of the startup page. The value must be proper HTML 4.0 in
   * order not to break the surrounding page.
   * <p>
   * <strong>Warning:</strong> the property value will not be validated by the
   * framework. Invalid HTML can break the application entirely or lead to
   * problems that are hard to identify. It's the responsibility of the
   * developer to ensure the correctness of the resulting page.
   * </p>
   *
   * @see Application#addEntryPoint(String, Class, Map)
   * @see Application#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String BODY_HTML = PREFIX + ".bodyHtml";

  /**
   * Entrypoint property name for the title that will be displayed in the
   * browser window. The value must be the title string without any HTML markup.
   *
   * @see Application#addEntryPoint(String, Class, Map)
   * @see Application#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String PAGE_TITLE = PREFIX + ".pageTitle";

  /**
   * Entrypoint property name for the website icon (a.k.a favicon or shortcut
   * icon) that will be displayed by the web browser. The value must contain a
   * valid path where the image can be accessed on the server.
   * <p>
   * <strong>Note:</strong> if this property is provided, the image resource
   * must be registered to be available.
   * </p>
   *
   * @see Application#addResource(IResource)
   * @see Application#addEntryPoint(String, Class, Map)
   * @see Application#addEntryPoint(String, IEntryPointFactory,
   *      Map)
   */
  public static final String FAVICON = PREFIX + ".favicon";

  public <T> T getService( Class<T> type ) {
    T result = null;
    return result;
  }

}
