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
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class ControlsDemo implements IEntryPoint {

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 850, 600 );
    shell.setLayout( new FillLayout() );
    createMainPart( shell );
    shell.layout();
    shell.setText( "RWT Controls Demo" );
    shell.open();
    return display;
  }

  // MAIN PART
  private void createMainPart( Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    new ButtonTab( topFolder );
//    new CBannerTab( topFolder );
    new ComboTab( topFolder );
    new CompositeTab( topFolder );
    new CoolBarTab( topFolder );
    new LabelTab( topFolder );
    new ListTab( topFolder );
//    new SashTab( topFolder );
    new SashFormTab( topFolder );
    new ShellTab( topFolder );
    new TabFolderTab( topFolder );
    new TableTab( topFolder );
    new TextTab( topFolder );
    new ToolBarTab( topFolder );
    new TreeTab( topFolder );
    new BrowserTab( topFolder );
    new GroupTab( topFolder );
    new ContainmentTab( topFolder );
    new ZOrderTab( topFolder );
    topFolder.setSelection( 0 );
  }

}
