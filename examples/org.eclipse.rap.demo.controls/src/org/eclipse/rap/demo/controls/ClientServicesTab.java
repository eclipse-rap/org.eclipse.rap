/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ClientServicesTab extends ExampleTab {

  public ClientServicesTab() {
    super( "Client Services" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    createJavaScriptExecuterExample( parent );
  }

  private void createJavaScriptExecuterExample( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    group.setLayout( new GridLayout( 2, false ) );
    group.setText( "JavaScriptExecuter" );
    final Text script = new Text( group, SWT.BORDER );
    script.setText( "alert( \"foo\" ); ");
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    script.setLayoutData( layoutData );
    Button execute = new Button( group, SWT.PUSH );
    execute.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, false ) );
    execute.setText( "Execute" );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        JavaScriptExecutor jse = RWT.getClient().getService( JavaScriptExecutor.class );
        jse.execute( script.getText() );
      }
    };
    script.addListener( SWT.DefaultSelection, executeListener );
    execute.addListener( SWT.Selection, executeListener );
  }
}
