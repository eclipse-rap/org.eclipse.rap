/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.*;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


@SuppressWarnings("restriction")
public class ControlsDemo implements IEntryPoint, Serializable {

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE | SWT.MAX | SWT.RESIZE );
    shell.setBounds( 10, 10, 850, 600 );
    createContent( shell );
    shell.setText( "RWT\u2028 Controls\u2029 Demo" );
    Image image = Util.loadImage( display, "resources/shell.gif" );
    shell.setImage( image );
    shell.layout();
    shell.open();
    if( RWT.getLifeCycle() instanceof RWTLifeCycle ) {
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      display.dispose();
    }
    return 0;
  }

  private void createContent( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    int style = SWT.TOP | SWT.FLAT | SWT.BORDER;
    final CTabFolder topFolder = new CTabFolder( parent, style );
    topFolder.marginWidth = 5;
    topFolder.marginHeight = 5;

    final ExampleTab[] tabs = new ExampleTab[] {
      new ButtonTab( topFolder ),
//      new RequestTab( topFolder ),
      new CBannerTab( topFolder ),
      new CLabelTab( topFolder ),
      new ComboTab( topFolder ),
      new CompositeTab( topFolder ),
      new CanvasTab( topFolder ),
      new CoolBarTab( topFolder ),
      new DialogsTab( topFolder ),
      new DateTimeTab( topFolder ),
      new ExpandBarTab( topFolder ),
      new GroupTab( topFolder ),
      new LabelTab( topFolder ),
      new ListTab( topFolder ),
      new LinkTab( topFolder ),
      new SashTab( topFolder ),
      new SashFormTab( topFolder ),
      new ShellTab( topFolder ),
      new TabFolderTab( topFolder ),
      new CTabFolderTab( topFolder ),
      new TableTab( topFolder ),
      new TableViewerTab( topFolder ),
      new TextTab( topFolder ),
      new TextSizeTab( topFolder ),
      new SpinnerTab( topFolder ),
      new ToolBarTab( topFolder ),
      new TreeTab( topFolder ),
      new BrowserTab( topFolder ),
      new ScaleTab( topFolder ),
      new SliderTab( topFolder ),
      new ProgressBarTab( topFolder ),
      new ScrolledCompositeTab( topFolder ),
      new ToolTipTab( topFolder ),
      new DNDExampleTab( topFolder ),
      new ContainmentTab( topFolder ),
      new ZOrderTab( topFolder ),
      new FocusTab( topFolder ),
      new VariantsTab( topFolder ),
      new ControlDecoratorTab( topFolder ),
      new ErrorHandlingTab( topFolder ),
      new NLSTab( topFolder ),
    };
    tabs[ 0 ].createContents();
    topFolder.setSelection( 0 );
    topFolder.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent evt ) {
        int index = topFolder.getSelectionIndex();
        tabs[ index ].createContents();
      }
    } );
  }
}
