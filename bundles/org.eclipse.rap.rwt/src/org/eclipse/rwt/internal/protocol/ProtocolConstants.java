/******************************************************************************* 
* Copyright (c) 2011 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rwt.internal.protocol;


public final class ProtocolConstants {

  public static final String MESSAGE_META = "meta";
  public static final String MESSAGE_OPERATIONS = "operations";
  
  public static final String META_REQUEST_COUNTER = "requestCounter";

  public static final String OPERATION_TARGET = "target";
  public static final String OPERATION_ACTION = "action";
  public static final String OPERATION_PROPERTIES = "properties";
  
  public static final String TYPE_SET = "set";
  public static final String TYPE_LISTEN = "listen";
  public static final String TYPE_CREATE = "create";
  public static final String TYPE_DESTROY = "destroy";    
  public static final String TYPE_CALL = "call";
  public static final String TYPE_EXECUTE_SCRIPT = "executeScript";
  
  public static final String CALL_METHOD_NAME = "name";
  public static final String CREATE_PARENT = "parent";
  public static final String CREATE_TYPE = "type";
  public static final String CREATE_STYLE = "style";
  public static final String EXECUTE_SCRIPT_TYPE = "scriptType";
  public static final String EXECUTE_SCRIPT_CONTENT = "content";
  
  private ProtocolConstants() {
    // prevent instantiation
  }
}
