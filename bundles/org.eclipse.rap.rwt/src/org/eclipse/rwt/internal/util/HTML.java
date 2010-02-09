/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;


/**
 * <p>
 * 'Static' class which provides commonly used constants used to generate markup.
 * </p> 
 */
public final class HTML {

  public final static String BODY = "body";
  public final static String CHARSET = "charset";
  public final static String CONTENT = "content";
  public final static String CONTENT_TYPE = "Content-Type";
  public final static String HEAD = "head";
  public final static String HTML = "html";
  public final static String HTTP_EQUIV = "http-equiv";
  public final static String META = "meta";
  public final static String SCRIPT = "script";
  public final static String SRC = "src";
  public final static String STYLE = "style";
  public final static String TITLE = "title";
  public final static String TYPE = "type";

  public final static String CONTENT_TEXT_HTML_UTF_8 
    = "text/html; charset=UTF-8";
  public final static String CONTENT_TEXT_HTML = "text/html";
  public final static String CONTENT_TEXT_CSS = "text/css";
  public final static String CONTENT_TEXT_JAVASCRIPT = "text/javascript";
  public final static String CONTENT_TEXT_JAVASCRIPT_UTF_8 
    = "text/javascript; charset=UTF-8";

  public final static String CHARSET_NAME_UTF_8 = "UTF-8";
  public final static String CHARSET_NAME_ISO_8859_1 = "ISO-8859-1";
  
  public final static String CONTENT_ENCODING = "Content-Encoding";
  public final static String ENCODING_GZIP = "gzip";
  public final static String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String EXPIRES = "Expires";
  
  private HTML() {
    // prevent instantiation
  }
}