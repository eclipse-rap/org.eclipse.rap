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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.*;

public class ControlsDemo implements IEntryPoint {

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE | SWT.MAX | SWT.RESIZE );
    shell.setBounds( 10, 10, 850, 600 );
    createContent( shell );
    shell.setText( "SWT Controls Demo" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image = Image.find( "resources/shell.gif", classLoader );
    shell.setImage( image );
    shell.layout();
    shell.open();
    return display;
  }

  private void createContent( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    final CTabFolder topFolder = new CTabFolder( parent, SWT.TOP );
    topFolder.marginWidth = 5;
    topFolder.marginHeight = 5;
    Display display = parent.getDisplay();
    Color selBg = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    Color selFg = display.getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT );
    topFolder.setSelectionBackground( selBg );
    topFolder.setSelectionForeground( selFg );
    ExampleTab tab = new ProgressBarTab( topFolder );
    tab.createContents();
    tab = new ButtonTab( topFolder );
    tab.createContents();
//    tab = new RequestTab( topFolder );
//    tab.createContents();
    tab = new CBannerTab( topFolder );
    tab.createContents();
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
    tab = new SashTab( topFolder );
    tab.createContents();
    tab = new SashFormTab( topFolder );
    tab.createContents();
    tab = new ShellTab( topFolder );
    tab.createContents();
    tab = new TabFolderTab( topFolder );
    tab.createContents();
    tab = new TableTab( topFolder );
    tab.createContents();
    tab = new TableViewerTab( topFolder );
    tab.createContents();
    tab = new TextTab( topFolder );
    tab.createContents();
    tab = new TextSizeTab( topFolder );
    tab.createContents();
    tab = new SpinnerTab( topFolder );
    tab.createContents();
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
    tab = new FocusTab( topFolder );
    tab.createContents();
    topFolder.setSelection( 0 );
  }
}
