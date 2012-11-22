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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.remote.Call;
import org.eclipse.rap.rwt.remote.Event;
import org.eclipse.rap.rwt.remote.Property;
import org.eclipse.rap.rwt.remote.RemoteObjectDefinition;


public class RemoteObjectDefinitionImpl<T> implements RemoteObjectDefinition<T> {

  private final Class<T> type;
  private final List<Property<T>> properties;
  private final List<Event<T>> events;
  private final List<Call<T>> calls;

  public RemoteObjectDefinitionImpl( Class<T> type ) {
    checkArgument( type, "Type must not be null." );
    this.type = type;
    this.properties = new ArrayList<Property<T>>();
    this.events = new ArrayList<Event<T>>();
    this.calls = new ArrayList<Call<T>>();
  }

  public Class<T> getType() {
    return type;
  }

  public void addProperty( Property<T> property ) {
    checkArgument( property, "Property must not be null." );
    properties.add( property );
  }

  public void addEvent( Event<T> event ) {
    checkArgument( event, "Event must not be null." );
    events.add( event );
  }

  public void addCall( Call<T> call ) {
    checkArgument( call, "Call must not be null." );
    calls.add( call );
  }

  public List<Property<T>> getProperties() {
    return new ArrayList<Property<T>>( properties );
  }

  public List<Event<T>> getEvents() {
    return new ArrayList<Event<T>>( events );
  }

  public List<Call<T>> getCalls() {
    return new ArrayList<Call<T>>( calls );
  }

  private static void checkArgument( Object argument, String errorMessage ) {
    if( argument == null ) {
      throw new IllegalArgumentException( errorMessage );
    }
  }
}
