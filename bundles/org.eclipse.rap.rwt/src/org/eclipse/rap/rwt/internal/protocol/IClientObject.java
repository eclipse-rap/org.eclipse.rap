/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;


public interface IClientObject {

  void create( String type );

  void set( String name, int value );

  void set( String name, double value );

  void set( String name, boolean value );

  void set( String name, String value );

  void set( String name, JsonValue value );

  void listen( String eventType, boolean listen );

  void call( String method, JsonObject parameters );

  void destroy();

}
