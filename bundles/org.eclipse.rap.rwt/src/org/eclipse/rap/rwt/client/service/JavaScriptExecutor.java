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
package org.eclipse.rap.rwt.client.service;


/**
 * @since 2.0
 */
public interface JavaScriptExecutor extends ClientService {

  /**
   * Evaluate the JavaScript code on the client
   *
   * If the code throws an error, it will crash the web client.
   * Accessing internals of the web client is strongly discouraged.
   *
   * @param code the JavaScript code to evaluate
   */
  public void execute( String code );

}
