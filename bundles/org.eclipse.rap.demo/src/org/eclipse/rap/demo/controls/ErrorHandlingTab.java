/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import javax.servlet.http.HttpSession;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ErrorHandlingTab extends ExampleTab {

  private static final int DELAY = 2000;

  public ErrorHandlingTab( final CTabFolder topFolder ) {
    super( topFolder, "Error Handling" );
  }

  protected void createStyleControls( final Composite parent ) {
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Label lblInfo = new Label( parent, SWT.WRAP );
    lblInfo.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    String info 
      = "Simulate a server-side session timeout.\n"
      + "Click the 'Invalidate Session' button that will invalidate the " 
      + "session after a short delay.\n"
      + "Thereafter, try to proceed using the application. With the next " 
      + "request, a new session is created. You are informed about that and " 
      + "can start working with the new session";
    lblInfo.setText( info );
    Button btnInvalidateSession = new Button( parent, SWT.PUSH );
    String msg = "Invalidate Session";
    btnInvalidateSession.setText( msg );
    final Label lblFeedback = new Label( parent, SWT.NONE );
    btnInvalidateSession.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        lblFeedback.setText( "The session will be invalidated shortly." );
        lblFeedback.getParent().layout();
        final HttpSession session = RWT.getSessionStore().getHttpSession();
        Thread thread = new Thread( new Runnable() {
          public void run() {
            try {
              Thread.sleep( DELAY );
              session.invalidate();
            } catch( InterruptedException e ) {
              // ignore, invalidate won't be executed
            }
          }
        } );
        thread.start();
      }
    } );
    Button btnErrorResponse = new Button( parent, SWT.PUSH );
    btnErrorResponse.setText( "Deliver response with JavaScript error" );
    btnErrorResponse.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        RWT.getLifeCycle().addPhaseListener( new PhaseListener() {
          private static final long serialVersionUID = 1L;
          public void beforePhase( final PhaseEvent event ) {
          }
          public void afterPhase( final PhaseEvent event ) {
            IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
            HtmlResponseWriter writer = stateInfo.getResponseWriter();
            writer.append( "this is no valid JavaScript!" );
            RWT.getLifeCycle().removePhaseListener( this );
          }
          public PhaseId getPhaseId() {
            return PhaseId.RENDER;
          }
        } );
      }
    } );
    Button btnServerException = new Button( parent, SWT.PUSH );
    btnServerException.setText( "Throw uncaught server-side exeption" );
    btnServerException.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        throw new RuntimeException( "Shit happens, rama rama ding ding" );
      }
    } );
    Button btnServerError = new Button( parent, SWT.PUSH );
    btnServerError.setText( "Throw uncaught server-side error" );
    btnServerError.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        throw new SWTError( "Some error occured" );
      }
    } );
  }

}
