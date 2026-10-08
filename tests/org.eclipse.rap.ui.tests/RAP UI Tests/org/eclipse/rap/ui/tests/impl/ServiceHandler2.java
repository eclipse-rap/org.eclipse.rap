/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.IServiceHandler;
import org.eclipse.rap.ui.tests.ServiceHandlerExtensionTest;


public class ServiceHandler2 implements IServiceHandler {

  public void service( HttpServletRequest request, HttpServletResponse response ) {
    ServiceHandlerExtensionTest.log = this.getClass().getName();
  }
}
