/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

/** <p>string constant definitions for reserved request params in the 
  * W4T framework.</p>
  */
public interface RequestParams {
 
  /** <p>the servlet parameter name for forcing a new session.</p> */  
  final static String STARTUP = "startup";
  /** <p>the servlet parameter name for the scripting support information 
    * of the browser.</p> */  
  final static String SCRIPT = "w4t_scriptEnabled";
  /** <p>the servlet parameter name for the AJaX support information 
   * of the browser.</p> */  
  final static String AJAX_ENABLED = "w4t_ajaxEnabled";
  /** used to ensure that the browser doesn't use a cached document that
   *  represents a WebForm of an invalidated session */
  final static String NO_CACHE = "nocache";
  /** startup request parameter that contains the initial width of
   *  the browsers client area. */
  final static String AVAILABLE_WIDTH = "w4t_width";
  /** startup request parameter that contains the initial height of
   *  the browsers client area. */
  final static String AVAILABLE_HEIGHT = "w4t_height";  
  /** <p>The servlet parameter name that contains the name of a resource. This
   * parameter is used as part of a resource request URL.</p> */
  final static String RESOURCE = "w4t_resource";
  /** <p>The request parameter name denoting the version number of a resource
   * which is requested within a resource request.</p> */
  final static String RESOURCE_VERSION = "w4t_res_version";
  /** <p>dummy parameter without any funtionality exept force the
   *  servlet engine to do a complete (session) encoding if necessary </p> */
  final static String ENCODING_DUMMY = "w4t_enc";

  /** <p>The servlet parameter name denothing the form's request counter.</p> */
  static final String REQUEST_COUNTER = "requestCounter";

  static final String UIROOT = "uiRoot";
}
