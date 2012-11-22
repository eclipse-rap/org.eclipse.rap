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
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.util.ClassUtil;
import org.eclipse.rap.rwt.remote.RemoteObjectSpecifier;


public class RemoteObjectSynchronizerRegistry {

  private final List<RemoteObjectSynchronizer<?>> synchronizers;

  public static RemoteObjectSynchronizerRegistry getInstance() {
    Object instance = RWT.getApplicationStore().getAttribute( RemoteObjectSynchronizerRegistry.class.getName() );
    if( instance == null ) {
      instance = new RemoteObjectSynchronizerRegistry();
      RWT.getApplicationStore().setAttribute( RemoteObjectSynchronizerRegistry.class.getName(), instance );
    }
    return ( RemoteObjectSynchronizerRegistry )instance;
  }

  private RemoteObjectSynchronizerRegistry() {
    synchronizers = Collections.synchronizedList( new ArrayList<RemoteObjectSynchronizer<?>>() );
  }

  public <T> void register( Class<T> type, Class<? extends RemoteObjectSpecifier<T>> specifierType )
    throws IllegalStateException 
  {
    if( getSynchronizerForType( type ) == null ) {
      createDefinition( type, createSpecifier( specifierType ) );
    } 
  }
  
  private <T> RemoteObjectSpecifier<T> createSpecifier( Class<? extends RemoteObjectSpecifier<T>> specifierType ) {
    return ClassUtil.newInstance( specifierType );
  }

  private <T> void createDefinition( Class<T> type, RemoteObjectSpecifier<T> specifier ) {
    RemoteObjectDefinitionImpl<T> definition = new RemoteObjectDefinitionImpl<T>( type );
    specifier.define( definition );
    synchronizers.add( new RemoteObjectSynchronizer<T>( definition, specifier.getType() ) );
  }
  
  @SuppressWarnings( "unchecked" )
  public <T> RemoteObjectSynchronizer<T> getSynchronizerForType( Class<T> type ) {
    for( RemoteObjectSynchronizer<?> synchronizer : synchronizers ) {
      if( synchronizer.getType() == type ) {
        return ( RemoteObjectSynchronizer<T> )synchronizer;
      }
    }
    return null;
  }
  
}
