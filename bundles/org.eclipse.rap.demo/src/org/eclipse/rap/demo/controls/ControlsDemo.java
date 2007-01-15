/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class ControlsDemo implements IEntryPoint {

  private Text text;

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 800, 500 );
    shell.setLayout( new FillLayout() );
    SashForm sashForm = new SashForm( shell, RWT.VERTICAL );
    Composite compMain = new Composite( sashForm, RWT.NONE );
    Composite compFoot = new Composite( sashForm, RWT.NONE );
    sashForm.setWeights( new int[]{
      70, 30
    } );
    createMainPart( compMain );
    createFootPart( compFoot );
    shell.layout();
    return display;
  }

  // MAIN PART
  private void createMainPart( Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    new ButtonTab( topFolder );
//    new CBannerTab( topFolder );
    new CompositeTab( topFolder );
    new CoolBarTab( topFolder );
    new LabelTab( topFolder );
    new ListTab( topFolder );
    new SashTab( topFolder );
    new SashFormTab( topFolder );
    new ShellTab( topFolder );
    new TabFolderTab( topFolder );
    new TableTab( topFolder );
    new TextTab( topFolder );
    new ToolBarTab( topFolder );
    new TreeTab( topFolder );
    topFolder.setSelection( 0 );
  }

  // FOOT PART
  private void createFootPart( Composite parent ) {
    FillLayout footLayout = new FillLayout();
    parent.setLayout( footLayout );
    text = new Text( parent, RWT.MULTI );
    text.setText( "" );
  }

}
