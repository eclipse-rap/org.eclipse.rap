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
package org.eclipse.rwt.internal.service;

/** <p>string constant definitions for reserved request params in the 
  * W4T framework.</p>
  */
// TODO [rh] prefix all request parameters with 'w4t'
// TODO [w4t] extract constants that are common to rwt and w4t, then move back
public interface RequestParams {
 
  /** <p>Request parameter <em>value</em> used to mark event parameters as 
   * 'not occured'.</p> */
  public static final String NOT_OCCURED = "not_occured";
  /** <p>the servlet parameter name for the w4t administration pages.</p> */
  final static String ADMIN = "w4t_admin";
  /** <p>the servlet parameter name for forcing a new session.</p> */  
  final static String STARTUP = "startup";
  /** <p>the servlet parameter name for the w4t browser survey.</p> */  
  final static String SURVEY = "w4t_survey";
  /** <p>This constant is for documentation purpose only. It is part of the 
   * timestamp-trigger request-parameters but never evaluated by W4T.</p> 
   * <p>See /resources/js/windowmanager.js (function 
   * <code>triggerTimeStamp_DOM</code>)</p> */
  final static String RANDOM = "w4tRandom";
  /** <p>the servlet parameter name for the scripting support information 
    * of the browser.</p> */  
  final static String SCRIPT = "w4t_scriptEnabled";
  /** <p>the servlet parameter name for the AJaX support information 
   * of the browser.</p> */  
  final static String AJAX_ENABLED = "w4t_ajaxEnabled";
  /** <p>Form parameter which denotes whether the current request was issued
    * by an XMLHttpRequest-Object (<code>true</code>) or by a form submit
    * (<code>false</code>).</p> */
  final static String IS_AJAX_REQUEST = "w4t_isAjaxRequest";
  /** <p>indicates that this request doesn't contain any form data
   *  of databound components. Request value is null in standard
   *  form submits, 'true' in javaScript request URLs.</p> */
  final static String PARAMLESS_GET = "w4t_paramlessGET";
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
  /** <p>parameter which indicates that the current request purpose
   *  is to trigger a WebForms timeout.</p> */
  final static String REQUEST_TIMESTAMP_NAME = "w4tTriggerTimeStamp";
  /** <p>dummy parameter without any funtionality exept force the
   *  servlet engine to do a complete (session) encoding if necessary </p> */
  final static String ENCODING_DUMMY = "w4t_enc";
  /** <p>This constant is for documentation purpose only. It denotes a hidden
   * input field that is created on demand  and may receive focus to trick 
   * some browsers.</p>
   * <p>Currently used in treeview_ie_gecko.js.</p> */
  final static String HIDDEN_FOCUS = "w4t_hidden_focus";

  /** <p>The servlet parameter name denothing the form's request counter.</p> */
  static final String REQUEST_COUNTER = "requestCounter";
  /** <p>The element which is focused</p> */
  static final String FOCUS_ELEMENT = "focusElement";

  /** <p>The request parameter name denothing the height of the browser window.
   * </p> */
  static final String AVAIL_HEIGHT = "availHeight";
  /** <p>The request parameter name denoting the width of the browser window.
   * </p> */
  static final String AVAIL_WIDTH = "availWidth";
  /** <p>The current vertical position of the browser window's scroll bar.
   * </p> */
  static final String SCROLL_Y = "scrollY";
  /** <p>The current horizontal position of the browser window's scroll bar.
   * </p> */
  static final String SCROLL_X = "scrollX";

  static final String UIROOT = "uiRoot";
}
