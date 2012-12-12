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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.URLLauncher;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ClientServicesTab extends ExampleTab {

  private static final String MINI_PDF = "clientservices/mini.pdf";


  public ClientServicesTab() {
    super( "Client Services" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    registerResources();
    createClientInfoExample( parent );
    createURLLauncherExample( parent );
    createJavaScriptExecuterExample( parent );
  }

  private void registerResources() {
    ResourceManager manager = RWT.getResourceManager();
    ClassLoader classLoader = this.getClass().getClassLoader();
    if( !manager.isRegistered( MINI_PDF ) ) {
      InputStream stream = classLoader.getResourceAsStream( "resources/mini.pdf" );
      manager.register( MINI_PDF, stream );
      try {
        stream.close();
      } catch( IOException e ) {
        throw new RuntimeException();
      }
    }
  }

  private void createClientInfoExample( Composite parent ) {
    Group group = createGroup( parent, "ClientInfo", 2 );
    ClientInfo info = RWT.getClient().getService( ClientInfo.class );
    final Label locale = new Label( group, SWT.NONE );
    locale.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    locale.setText( "Locale: " + info.getLocale() );
    final Label timezone = new Label( group, SWT.NONE );
    timezone.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    timezone.setText( "Timezone Offset: " + info.getTimezoneOffset() );
  }

  private void createURLLauncherExample( Composite parent ) {
    Group group = createGroup( parent, "URLLauncher", 2 );
    final Combo url = new Combo( group, SWT.NONE );
    url.add( "http://www.eclipse.org/" );
    url.add( RWT.getResourceManager().getLocation( MINI_PDF ) );
    url.add( "mailto:someone@nowhere.org" );
    url.add(    "mailto:otherone@nowhere.org?cc=third@nowhere.org"
             +  "&subject=Did%20you%20know%3F&body=RAP%20is%20awesome!" );
    url.add( "skype:echo123" );
    url.add( "tel:555-123456" );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    url.setLayoutData( layoutData );
    Button launch = new Button( group, SWT.PUSH );
    launch.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, false ) );
    launch.setText( "Launch" );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        URLLauncher launcher = RWT.getClient().getService( URLLauncher.class );
        launcher.openURL( url.getText() );
      }
    };
    url.addListener( SWT.DefaultSelection, executeListener );
    launch.addListener( SWT.Selection, executeListener );
  }

  private void createJavaScriptExecuterExample( Composite parent ) {
    Group group = createGroup( parent, "JavaScriptExecuter", 2 );
    final Text script = new Text( group, SWT.BORDER );
    script.setText( "alert( \"foo\" );" );
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


  private Group createGroup( Composite parent, String title, int cols ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    group.setLayout( new GridLayout( cols, false ) );
    group.setText( title );
    return group;
  }
}
