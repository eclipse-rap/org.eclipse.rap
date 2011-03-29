/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.util;


/**
 * 'Static' class which provides commonly used constants used to generate markup.
 */
public final class HTML {

  public final static String CONTENT_TYPE = "Content-Type";
  public final static String HTTP_EQUIV = "http-equiv";

  public final static String CONTENT_TEXT_HTML = "text/html";
  public final static String CONTENT_TEXT_JAVASCRIPT = "text/javascript";
  public final static String CONTENT_TEXT_JAVASCRIPT_UTF_8 = "text/javascript; charset=UTF-8";

  public final static String CHARSET_NAME_UTF_8 = "UTF-8";
  public final static String CHARSET_NAME_ISO_8859_1 = "ISO-8859-1";

  private HTML() {
    // prevent instantiation
  }
}
