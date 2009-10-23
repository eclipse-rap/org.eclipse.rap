/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;

/**
 * A utility class providing an interface to add Javascript to the response.
 * Currently, the Javascript is only added once at the current request, but it
 * can be extended to add the Javascript at each response.
 * @since 1.3
 */
public class JSExecutor {

  private static final String JS_EXECUTOR = JSExecutor.class.getName()
                                            + "#instance";
  private final Display display;
  private final StringBuffer code;

  private JSExecutor() {
    this( Display.getCurrent() );
  }

  private JSExecutor( final Display display ) {
    this.display = display;
    this.code = new StringBuffer();
    LifeCycleFactory.getLifeCycle().addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;
      private final StringBuffer code = JSExecutor.this.code;
      private final Display display = JSExecutor.this.display;

      public void beforePhase( final PhaseEvent event ) {
        // do nothing
      }

      public void afterPhase( final PhaseEvent event ) {
        if( display == RWTLifeCycle.getSessionDisplay() ) {
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          HtmlResponseWriter writer = stateInfo.getResponseWriter();
          try {
            writer.write( code.toString(), 0, code.length() );
          } catch( IOException e ) {
            // TODO [rh] proper exception handling - think about adding throws
            // IOException to after/beforePhase as there are various places
            // like this
            throw new RuntimeException( e );
          } finally {
            LifeCycleFactory.getLifeCycle().removePhaseListener( this );
          }
        }
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }

  private void append( final String command ) {
    code.append( command );
  }

  /**
   * Adds some Javascript to the response stream.
   */
  public static void executeJS( final String code ) {
    final IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JSExecutor jsExecutor = ( JSExecutor )stateInfo.getAttribute( JS_EXECUTOR );
    if( null == jsExecutor ) {
      jsExecutor = new JSExecutor();
      stateInfo.setAttribute( JS_EXECUTOR, jsExecutor );
    }
    jsExecutor.append( code );
  }
}
