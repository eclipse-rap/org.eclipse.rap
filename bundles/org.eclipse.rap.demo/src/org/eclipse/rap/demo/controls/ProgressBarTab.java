/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.lifecycle.UICallBackUtil;
import org.eclipse.swt.widgets.*;

public class ProgressBarTab extends ExampleTab {

  public ProgressBarTab( final CTabFolder parent ) {
    this( parent, "ProgressBar" );
  }

  public ProgressBarTab( final CTabFolder parent, final String title ) {
    super( parent, title );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle() == 0 ? SWT.HORIZONTAL : getStyle();
    final ProgressBar progressBar = new ProgressBar( parent, style );
    final Button button = new Button( parent, SWT.PUSH );
    
    progressBar.setLocation( 10, 10 );
    progressBar.setSize( 200, 22 );
    progressBar.setMaximum( 20 );
    
    button.setText( "Start Background Process" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent evt ) {
        button.setEnabled( false );
        UICallBackUtil.activateUICallBack( ProgressBarTab.class.getName() );
        Runnable runnable = createRunnable( progressBar,
                                            button,
                                            progressBar.getMaximum(),
                                            progressBar.getDisplay() );
        Thread thread = new Thread( runnable );
        thread.setDaemon( true );
        thread.start();
        
      }
    } );
    button.pack();
    Rectangle bounds = progressBar.getBounds();
    button.setLocation( bounds.x, bounds.y + bounds.height + 10 );
  }

  private Runnable createRunnable( final ProgressBar progressBar,
                                   final Button button,
                                   final int maximum,
                                   final Display display )
  {
    Runnable result = new Runnable() {
      public void run() {
        for( int i = 0; i <= maximum; i++ ) {
          final int selection = i;
          try {
            Thread.sleep( 250 );
          } catch( Throwable th ) {
          }
          display.syncExec( new Runnable() {
            public void run() {
              if( !progressBar.isDisposed() ) {
                progressBar.setSelection( selection );
                if( selection == maximum ) {
                  button.setEnabled( true );
                  String id = ProgressBarTab.class.getName();
                  UICallBackUtil.deactivateUICallBack( id );
                }
              }
            }
          } );
        }
      }
    };
    return result;
  }

  protected void createStyleControls() {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL, true );
    createStyleButton( "VERTICAL" );
    createStyleButton( "INDETERMINATE", SWT.INDETERMINATE, false );
  }
}
