/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class ControlsDemo implements IEntryPoint {

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE | SWT.MAX | SWT.RESIZE );
    shell.setBounds( 10, 10, 850, 600 );
    createContent( shell );
    shell.setText( "SWT Controls Demo" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image = Graphics.getImage( "resources/shell.gif", classLoader );
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
    ensureMinTabHeight( topFolder );
    Display display = parent.getDisplay();
    Color selBg = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    Color selFg = display.getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT );
    topFolder.setSelectionBackground( selBg );
    topFolder.setSelectionForeground( selFg );

    final ExampleTab[] tabs = new ExampleTab[] {
      new ButtonTab( topFolder ),
//      new RequestTab( topFolder ),
      new CBannerTab( topFolder ),
      new CLabelTab( topFolder ),
      new ComboTab( topFolder ),
      new CompositeTab( topFolder ),
      new CoolBarTab( topFolder ),
      new DialogsTab( topFolder ),
      new GroupTab( topFolder ),
      new LabelTab( topFolder ),
      new ListTab( topFolder ),
      new LinkTab( topFolder ),
      new SashTab( topFolder ),
      new SashFormTab( topFolder ),
      new ShellTab( topFolder ),
      new TabFolderTab( topFolder ),
      // TODO [rh] bring back when layout problems are solved and demo tab is
      //      cleaned up
//      new CTabFolderTab( topFolder ),
      new TableTab( topFolder ),
      new TableViewerTab( topFolder ),
      new TextTab( topFolder ),
      new TextSizeTab( topFolder ),
      new SpinnerTab( topFolder ),
      new ToolBarTab( topFolder ),
      new TreeTab( topFolder ),
      new BrowserTab( topFolder ),
      new ContainmentTab( topFolder ),
      new ZOrderTab( topFolder ),
      new FocusTab( topFolder ),
      new ProgressBarTab( topFolder ),
      new ErrorHandlingTab( topFolder ),
      new NLSTab( topFolder )
    };
    tabs[ 0 ].createContents();
    topFolder.setSelection( 0 );
    topFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent evt ) {
        int index = topFolder.getSelectionIndex();
        tabs[ index ].createContents();
      }
    } );
  }

  private static void ensureMinTabHeight( final CTabFolder folder ) {
    int result = Graphics.getCharHeight( folder.getFont() );
    if( result < 18 ) {
      folder.setTabHeight( 18 );
    }
  }
}
