/*******************************************************************************
* Copyright (c) 2011, 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;


final class ProtocolConstants {

  // Message properties
  public static final String HEAD = "head";
  public static final String OPERATIONS = "operations";

  // Operation properties
  public static final String OPERATION_TARGET = "target";
  public static final String OPERATION_ACTION = "action";
  public static final String OPERATION_PROPERTIES = "properties";

  // Action types
  public static final String ACTION_CREATE = "create";
  public static final String ACTION_SET = "set";
  public static final String ACTION_LISTEN = "listen";
  public static final String ACTION_CALL = "call";
  public static final String ACTION_DESTROY = "destroy";

  // Common properties
  public static final String REQUEST_COUNTER = "requestCounter";
  public static final String CALL_METHOD_NAME = "method";
  public static final String CREATE_PARENT = "parent";
  public static final String CREATE_TYPE = "type";
  public static final String CREATE_STYLE = "style";

  private ProtocolConstants() {
    // prevent instantiation
  }
}
