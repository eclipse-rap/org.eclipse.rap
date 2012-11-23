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
package org.eclipse.rap.rwt.internal.protocol;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.remote.EventHandler;
import org.eclipse.rap.rwt.remote.MethodHandler;
import org.eclipse.rap.rwt.remote.PropertyHandler;
import org.eclipse.rap.rwt.remote.RemoteObjectDefinition;


public class RemoteObjectDefinitionImpl<T> implements RemoteObjectDefinition<T> {

  private final Class<T> type;
  private final Map<String, PropertyHandler<T>> properties;
  private final Map<String, EventHandler<T>> eventHandlers;
  private final Map<String, MethodHandler<T>> methods;

  public RemoteObjectDefinitionImpl( Class<T> type ) {
    ParamCheck.notNull( type, "RemoteObjectType" );
    this.type = type;
    this.properties = new HashMap<String, PropertyHandler<T>>();
    this.eventHandlers = new HashMap<String, EventHandler<T>>();
    this.methods = new HashMap<String, MethodHandler<T>>();
  }

  public Class<T> getType() {
    return type;
  }

  public void addProperty( String name, PropertyHandler<T> propertyHandler ) {
    ParamCheck.notNullOrEmpty( name, "Property Name" );
    ParamCheck.notNull( propertyHandler, "PropertyHandler" );
    properties.put( name, propertyHandler );
  }

  public void addEventHandler( String eventName, EventHandler<T> eventHandler ) {
    ParamCheck.notNullOrEmpty( eventName, "Event Name" );
    ParamCheck.notNull( eventHandler, "EventHandler" );
    eventHandlers.put( eventName, eventHandler );
  }

  public void addMethod( String name, MethodHandler<T> methodHandler ) {
    ParamCheck.notNullOrEmpty( name, "Mehtod Name" );
    ParamCheck.notNull( methodHandler, "MethodHandler" );
    methods.put( name, methodHandler );
  }

  public PropertyHandler<T> getProperty( String name ) {
    return properties.get( name );
  }

  public EventHandler<T> getEventHandler( String eventName ) {
    return eventHandlers.get( eventName );
  }

  public MethodHandler<T> getMethod( String name ) {
    return methods.get( name );
  }
}
