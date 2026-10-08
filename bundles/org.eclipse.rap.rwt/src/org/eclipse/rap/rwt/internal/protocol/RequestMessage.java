/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
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
package org.eclipse.rap.rwt.internal.protocol;

import java.util.List;

import org.eclipse.rap.json.JsonObject;


public class RequestMessage extends Message {

  protected RequestMessage( JsonObject json ) {
    super( json );
  }

  protected RequestMessage( JsonObject head, List<Operation> operations ) {
    super( head, operations );
  }

}
