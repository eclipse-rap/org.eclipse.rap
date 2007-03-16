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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class ControlsDemo implements IEntryPoint {

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 850, 600 );
    createContent( shell );
    shell.setText( "RWT Controls Demo" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image = Image.find( "resources/shell.gif", classLoader );
    shell.setImage( image  );
    shell.layout();
    shell.open();
    return display;
  }

  private void createContent( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    // TODO [rh] replace with CTabFolder
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    ExampleTab tab = new ButtonTab( topFolder );
    tab.createContents();
//    tab = new RequestTab( topFolder );
//    tab.createContents();
//    tab = new CBannerTab( topFolder );
//    tab.createContents();
    tab = new ComboTab( topFolder );
    tab.createContents();
    tab = new CompositeTab( topFolder );
    tab.createContents();
    tab = new CoolBarTab( topFolder );
    tab.createContents();
    tab = new DialogsTab( topFolder );
    tab.createContents();
    tab = new LabelTab( topFolder );
    tab.createContents();
    tab = new ListTab( topFolder );
    tab.createContents();
//    tab = new SashTab( topFolder );
//    tab.createContents();
    tab = new SashFormTab( topFolder );
    tab.createContents();
    tab = new ShellTab( topFolder );
    tab.createContents();
    tab = new TabFolderTab( topFolder );
    tab.createContents();
    tab = new TableTab( topFolder );
    tab.createContents();
    tab = new TextTab( topFolder );
    tab.createContents();
//    tab = new SpinnerTab( topFolder );
//    tab.createContents();
    tab = new ToolBarTab( topFolder );
    tab.createContents();
    tab = new TreeTab( topFolder );
    tab.createContents();
    tab = new BrowserTab( topFolder );
    tab.createContents();
    tab = new ContainmentTab( topFolder );
    tab.createContents();
    tab = new ZOrderTab( topFolder );
    tab.createContents();
//    tab = new FocusTab( topFolder );
//    tab.createContents();
    topFolder.setSelection( 0 );
  }
}
