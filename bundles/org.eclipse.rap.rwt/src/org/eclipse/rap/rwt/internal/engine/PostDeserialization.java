/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.engine;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.service.UISession;


public class PostDeserialization {
  private static final String PROCESSORS = PostDeserialization.class.getName() + "#processors";
  private static final Runnable[] EMPTY_PROCESSORS = new Runnable[ 0 ];

  public static void runProcessors( UISession uiSession ) {
    Runnable[] processors = getProcessors( uiSession );
    clearProcessors( uiSession );
    for( Runnable processor : processors ) {
      processor.run();
    }
  }

  public static void addProcessor( UISession uiSession, Runnable processor ) {
    List<Runnable> processorsList = getProcessorsList( uiSession );
    processorsList.add( processor );
  }

  @SuppressWarnings("unchecked")
  private static Runnable[] getProcessors( UISession uiSession ) {
    Runnable[] result = EMPTY_PROCESSORS;
    List<Runnable> list = ( List<Runnable> )uiSession.getAttribute( PROCESSORS );
    if( list != null ) {
      result = list.toArray( new Runnable[ list.size() ] );
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static List<Runnable> getProcessorsList( UISession uiSession ) {
    List<Runnable> result = ( List<Runnable> )uiSession.getAttribute( PROCESSORS );
    if( result == null ) {
      result = new LinkedList<Runnable>();
      uiSession.setAttribute( PROCESSORS, result );
    }
    return result;
  }

  private static void clearProcessors( UISession uiSession ) {
    uiSession.removeAttribute( PROCESSORS );
  }

  private PostDeserialization() {
    // prevent instantiation
  }
}
