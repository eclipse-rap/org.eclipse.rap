/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
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
package org.eclipse.rap.rwt.internal.client;

import org.eclipse.rap.rwt.client.service.ExitConfirmation;


public class ExitConfirmationImpl implements ExitConfirmation {

  private String message;

  @Override
  public void setMessage( String message ) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

}
