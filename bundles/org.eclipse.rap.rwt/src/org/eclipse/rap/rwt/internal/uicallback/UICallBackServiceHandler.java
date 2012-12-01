/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.uicallback;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;


public class UICallBackServiceHandler implements ServiceHandler {

  public final static String HANDLER_ID = "org.eclipse.rap.uicallback";

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    UICallBackManager.getInstance().processRequest( response );
  }

}
