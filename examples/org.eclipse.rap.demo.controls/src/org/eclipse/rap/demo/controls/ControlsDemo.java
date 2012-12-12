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

import java.io.Serializable;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


@SuppressWarnings("restriction")
public class ControlsDemo implements EntryPoint, Serializable {

  private Tree tree;
  private Composite exampleParent;

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE );
    shell.setBackground( display.getSystemColor( SWT.COLOR_GRAY ) );
    createContent( shell );
    // These special characters must be ignored
    shell.setText( "RWT\u2028 Controls\u2029 Demo" );
    Image image = Util.loadImage( display, "resources/shell.gif" );
    shell.setImage( image );
    shell.layout();
    shell.setMaximized( true );
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

  private void createContent( Composite parent ) {
    parent.setLayout( new FormLayout() );
    tree = new Tree( parent, SWT.FULL_SELECTION );
    tree.setLayoutData( createLayoutDataForTree() );
    exampleParent = new Composite( parent, SWT.NONE );
    exampleParent.setLayout( new FillLayout() );
    exampleParent.setLayoutData( createLayoutDataForExampleParent() );
    fillTree( parent );
  }

  private void fillTree( Composite parent ) {
    for( ExampleTab tab : createExampleTabs() ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( tab.getName() );
      item.setData( tab );
    }
    tree.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        Object data = event.item.getData();
        selectTab( ( ExampleTab )data );
      }
    } );
    selectTab( ( ExampleTab )tree.getItem( 0 ).getData() );
  }

  private void selectTab( ExampleTab exampleTab ) {
    Control[] children = exampleParent.getChildren();
    for( Control control : children ) {
      control.dispose();
    }
    if( exampleTab != null ) {
      exampleTab.createContents( exampleParent );
    }
    exampleParent.layout();
  }

  private FormData createLayoutDataForTree() {
    FormData layoutData = new FormData();
    layoutData.left = new FormAttachment( 0, 0 );
    layoutData.top = new FormAttachment( 0, 0 );
    layoutData.bottom = new FormAttachment( 100, 0 );
    layoutData.width = 190;
    return layoutData;
  }

  private FormData createLayoutDataForExampleParent() {
    FormData layoutData = new FormData();
    layoutData.left = new FormAttachment( tree, 10 );
    layoutData.top = new FormAttachment( 0, 0 );
    layoutData.right = new FormAttachment( 100, 0 );
    layoutData.bottom = new FormAttachment( 100, 0 );
    return layoutData;
  }

  private static ExampleTab[] createExampleTabs() {
    return new ExampleTab[] {
      new ButtonTab(),
      new BrowserTab(),
      new CanvasTab(),
      new CBannerTab(),
      new CLabelTab(),
      new ComboTab(),
      new CompositeTab(),
      new CoolBarTab(),
      new CTabFolderTab(),
      new DateTimeTab(),
      new DialogsTab(),
      new ExpandBarTab(),
      new FocusTab(),
      new GroupTab(),
      new LabelTab(),
      new ListTab(),
      new LinkTab(),
      new ProgressBarTab(),
//      new RequestTab(),
      new SashTab(),
      new SashFormTab(),
      new ScaleTab(),
      new ScrolledCompositeTab(),
      new ShellTab(),
      new SliderTab(),
      new SpinnerTab(),
      new TabFolderTab(),
      new TableTab(),
      new TableViewerTab(),
      new TextTab(),
      new TextSizeTab(),
      new ToolBarTab(),
      new ToolTipTab(),
      new TreeTab(),
      new DNDExampleTab(),
      new ContainmentTab(),
      new ZOrderTab(),
      new VariantsTab(),
      new ControlDecorationTab(),
      new ErrorHandlingTab(),
      new ClientServicesTab(),
      new NLSTab(),
    };
  }

}
