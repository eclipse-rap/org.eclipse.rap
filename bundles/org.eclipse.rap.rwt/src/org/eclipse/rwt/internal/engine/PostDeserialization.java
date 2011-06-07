/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.service.ISessionStore;


public class PostDeserialization {
  private static final String PROCESSORS = PostDeserialization.class.getName() + "#processors";
  private static final Runnable[] EMPTY_PROCESSORS = new Runnable[ 0 ];

  public static void runProcessors( ISessionStore sessionStore ) {
    Runnable[] processors = getProcessors( sessionStore );
    clearProcessors( sessionStore );
    for( Runnable processor : processors ) {
      processor.run();
    }
  }
  
  public static void addProcessor( ISessionStore sessionStore, Runnable processor ) {
    List<Runnable> processorsList = getProcessorsList( sessionStore );
    processorsList.add( processor );
  }

  @SuppressWarnings("unchecked")
  private static Runnable[] getProcessors( ISessionStore sessionStore ) {
    Runnable[] result = EMPTY_PROCESSORS;
    List<Runnable> list = ( List<Runnable> )sessionStore.getAttribute( PROCESSORS );
    if( list != null ) {
      result = list.toArray( new Runnable[ list.size() ] );
    }
    return result;
  }
  
  
  @SuppressWarnings("unchecked")
  private static List<Runnable> getProcessorsList( ISessionStore sessionStore ) {
    List<Runnable> result = ( List<Runnable> )sessionStore.getAttribute( PROCESSORS );
    if( result == null ) {
      result = new LinkedList<Runnable>();
      sessionStore.setAttribute( PROCESSORS, result );
    }
    return result;
  }

  private static void clearProcessors( ISessionStore sessionStore ) {
    sessionStore.removeAttribute( PROCESSORS );
  }

  private PostDeserialization() {
    // prevent instantiation
  }
}
