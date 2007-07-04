/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.lifecycle;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.internal.lifecycle.CurrentPhase;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


public class ProcessActionRunner {
  
  private static final String ATTR_RUNNABLE_LIST 
    = ProcessActionRunner.class.getName();

  public static void add( final Runnable runnable ) {
    if(    PhaseId.PREPARE_UI_ROOT.equals( CurrentPhase.get() ) 
        || PhaseId.PROCESS_ACTION.equals( CurrentPhase.get() ) ) 
    {
      runnable.run();
    } else {
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      List list = ( List )stateInfo.getAttribute( ATTR_RUNNABLE_LIST );
      if( list == null ) {
        list = new ArrayList();
        stateInfo.setAttribute( ATTR_RUNNABLE_LIST, list );
      }
      if( !list.contains( runnable ) ) {
        list.add(  runnable );
      }
    }
  }
  
  public static void execute() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List list = ( List )stateInfo.getAttribute( ATTR_RUNNABLE_LIST );
    if( list != null ) {
      Runnable[] runables = new Runnable[ list.size() ];
      list.toArray( runables );
      for( int i = 0; i < runables.length; i++ ) {
        // TODO: [fappel] think about exception handling.
        runables[ i ].run();
      }
    }    
  }
}
