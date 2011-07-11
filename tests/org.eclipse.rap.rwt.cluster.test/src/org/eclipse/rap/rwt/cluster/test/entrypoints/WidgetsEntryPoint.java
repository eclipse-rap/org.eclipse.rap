/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;


@SuppressWarnings("unused")
public class WidgetsEntryPoint implements IEntryPoint {

  private Shell shell;

  public int createUI() {
    Display display = new Display();
    shell = new Shell( display );
    shell.setMaximized( true );
    shell.setLayout( new RowLayout() );
    createButton();
    createBrowser();
    createCBanner();
    createCTabFolder();
    createCLabel();
    createCombo();
    createCCombo();
    createComposite();
    createCoolBar();
    createDateTime();
    createGroup();
    createLabel();
    createLink();
    createList();
    createProgressBar();
    createSashForm();
    createScale();
    createSlider();
    createSpinner();
    createTabFolder();
    createTable();
    createText();
    createToolBar();
    createTree();
    obtainAccessibles();
    shell.open();
    return 0;
  }

  @SuppressWarnings("restriction")
  private void obtainAccessibles() {
    AllWidgetTreeVisitor.accept( shell, new WidgetTreeVisitor() {
      public boolean visit( Widget widget ) {
        if( widget instanceof Control ) {
          Control control = ( Control )widget;
          control.getAccessible();
        }
        return true;
      }
    } );
  }

  private void createProgressBar() {
    new ProgressBar( shell, SWT.NONE );
  }

  private void createList() {
    new List( shell, SWT.NONE );
  }

  private void createLink() {
    new Link( shell, SWT.NONE );
  }

  private void createTree() {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
  }

  private void createToolBar() {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    new ToolItem( toolBar, SWT.PUSH );
  }

  private void createText() {
    new Text( shell, SWT.SINGLE );
    new Text( shell, SWT.MULTI );
    new Text( shell, SWT.PASSWORD );
  }

  private void createTable() {
    Table table = new Table( shell, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
  }

  private void createTabFolder() {
    TabFolder tabFolder = new TabFolder( shell, SWT.NONE );
    new TabItem( tabFolder, SWT.NONE );
  }

  private void createSpinner() {
    new Spinner( shell, SWT.NONE );
  }

  private void createSlider() {
    new Slider( shell, SWT.NONE );
  }

  private void createScale() {
    new Scale( shell, SWT.NONE );
  }

  private void createSashForm() {
    SashForm sashForm = new SashForm( shell, SWT.NONE );
    new Label( sashForm, SWT.NONE );
    new Label( sashForm, SWT.NONE );
    sashForm.setWeights( new int[]{ 30, 70 } );
  }

  private void createLabel() {
    new Label( shell, SWT.NONE );
  }

  private void createGroup() {
    new Group( shell, SWT.NONE );
  }

  private void createButton() {
    new Button( shell, SWT.PUSH );
    new Button( shell, SWT.RADIO );
    new Button( shell, SWT.CHECK );
    new Button( shell, SWT.TOGGLE );
  }

  private void createDateTime() {
    new DateTime( shell, SWT.DATE );
    new DateTime( shell, SWT.TIME );
    new DateTime( shell, SWT.CALENDAR );
  }

  private void createCoolBar() {
    CoolBar coolBar = new CoolBar( shell, SWT.NONE );
    new CoolItem( coolBar, SWT.NONE );
  }

  private void createComposite() {
    new Composite( shell, SWT.NONE );
  }

  private void createCombo() {
    new Combo( shell, SWT.NONE );
  }

  private void createCCombo() {
    new CCombo( shell, SWT.NONE );
  }
  
  private void createCLabel() {
    new CLabel( shell, SWT.NONE );
  }

  private void createCTabFolder() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );
    new CTabItem( tabFolder, SWT.NONE );
  }

  private void createCBanner() {
    new CBanner( shell, SWT.NONE );
  }

  private void createBrowser() {
    new Browser( shell, SWT.NONE );
  }
}
