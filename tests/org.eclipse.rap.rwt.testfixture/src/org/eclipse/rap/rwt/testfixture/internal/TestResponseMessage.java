/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
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
package org.eclipse.rap.rwt.testfixture.internal;

import java.util.ArrayList;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;


/**
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 */
public class TestResponseMessage extends ResponseMessage {

  public TestResponseMessage() {
    super( new JsonObject(), new ArrayList<Operation>() );
  }

  public TestResponseMessage( JsonObject json ) {
    super( json );
  }

}
