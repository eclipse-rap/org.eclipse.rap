/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.service.IServiceHandler;


public abstract class AbstractServiceHandler implements IServiceHandler {

  static PrintWriter getOutputWriter() throws IOException {
    return getResponse().getWriter();
  }

  protected static HttpServletRequest getRequest() {
    return ContextProvider.getRequest();
  }

  protected static HttpServletResponse getResponse() {
    return ContextProvider.getResponse();
  }
}