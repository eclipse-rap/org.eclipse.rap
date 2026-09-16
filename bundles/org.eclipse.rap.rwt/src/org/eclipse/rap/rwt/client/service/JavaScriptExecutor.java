/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Kyle Smith - Add evalute method
 ******************************************************************************/
package org.eclipse.rap.rwt.client.service;

import java.util.concurrent.CompletableFuture;


/**
 * The JavaScriptExecuter service allows executing JavaScript code on the client.
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface JavaScriptExecutor extends ClientService {

  /**
   * Executes the JavaScript code on the client.
   * <p>
   * If the code throws an error, it will crash the web client. Accessing internals of the web
   * client is strongly discouraged.
   *
   * @param code the JavaScript code to evaluate
   */
  public void execute( String code );

  /**
   * Evaluates the JavaScript code on the client. Unlike {@link #execute(String)}, this method
   * returns a value (asynchronously) if the specified JavaScript code returns a value. The returned
   * {@link CompletableFuture} is completed when the client object sends the return value, if any,
   * in the next remote call, which may never happen. If the JavaScript code does not return a
   * value, the future is completed with {@code null}.
   * <p>
   * <b>Important:</b> Do not wait for the future to complete in the UI thread or it will cause a
   * deadlock. The code to execute is only sent to the client once the UI thread is available, which
   * will not happen if the future is blocking it. Instead, use another thread or
   * {@link java.util.concurrent.ExecutorService ExecutorService}.
   * <p>
   * If the code throws an error, it will crash the web client. Accessing internals of the web
   * client is strongly discouraged.
   *
   * @param code the JavaScript code to evaluate
   * @return a future that will be completed with the result of the JavaScript code, or a future
   *         that will be completed with {@code null} if the given JavaScript code does not return a
   *         value (i.e., returns {@code undefined})
   * @since 4.3
   */
  public CompletableFuture<String> evaluate( String code );
}
