/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;


public class ProcessActionRunner {
  
  private static final String ATTR_RUNNABLE_LIST 
    = ProcessActionRunner.class.getName();

  @SuppressWarnings("unchecked")
  public static void add( final Runnable runnable ) {
    if( CurrentPhase.get() != null ) {
      if(    PhaseId.PREPARE_UI_ROOT.equals( CurrentPhase.get() ) 
          || PhaseId.PROCESS_ACTION.equals( CurrentPhase.get() ) ) 
      {
        runnable.run();
      } else {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        List<Runnable> list = ( List<Runnable> )stateInfo.getAttribute( ATTR_RUNNABLE_LIST );
        if( list == null ) {
          list = new ArrayList<Runnable>();
          stateInfo.setAttribute( ATTR_RUNNABLE_LIST, list );
        }
        if( !list.contains( runnable ) ) {
          list.add( runnable );
        }
      }
    }
  }
  
  public static boolean executeNext() {
    boolean result = false;
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List list = ( List )stateInfo.getAttribute( ATTR_RUNNABLE_LIST );
    if( list != null && list.size() > 0 ) {
      Runnable runnable = ( Runnable )list.remove( 0 );
      runnable.run();
      result = true;
    }    
    return result;
  }
  
  @SuppressWarnings("unchecked")
  public static void execute() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List<Runnable> list = ( List<Runnable> )stateInfo.getAttribute( ATTR_RUNNABLE_LIST );
    if( list != null ) {
      Runnable[] runables = list.toArray( new Runnable[ list.size() ] );
      for( int i = 0; i < runables.length; i++ ) {
        // TODO: [fappel] think about exception handling.
        runables[ i ].run();
      }
    }    
  }
}
