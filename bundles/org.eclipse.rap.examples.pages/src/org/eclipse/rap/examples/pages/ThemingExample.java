/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class ThemingExample implements IExamplePage {

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    createButtonsArea( parent );
    createMenusArea( parent );
  }

  private void createButtonsArea( final Composite parent ) {
    Group group = createGroup( parent, "Push Buttons", 3 );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    GridLayout layout = new GridLayout( 3, false );
    group.setLayout( layout );
    createButton( group, "Simple", "simple" );
    createButton( group, "Pretty", "pretty" );
    createButton( group, "Fancy", "fancy" );
    createButton( group, "Simple animated", "simpleAnimated" );
    createButton( group, "Pretty animated", "prettyAnimated" );
    createButton( group, "Fancy animated", "fancyAnimated" );
  }

  private Button createButton( final Composite parent,
                               final String text,
                               final String variant )
  {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( text );
    button.setToolTipText( "Animated ToolTip" );
    button.setData( WidgetUtil.CUSTOM_VARIANT, variant );
    GridData layoutData = new GridData( SWT.FILL, SWT.TOP, true, true );
    layoutData.heightHint = 50;
    button.setLayoutData( layoutData );
    return button;
  }

  private void createMenusArea( final Composite parent ) {
    Group group = createGroup( parent, "Menus", 1 );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    GridData layoutData = new GridData( SWT.FILL, SWT.TOP, true, false );
    ToolBar bar = new ToolBar( group, SWT.BORDER );
    bar.setData( WidgetUtil.CUSTOM_VARIANT, "themingDemo" );
    bar.setLayoutData( layoutData );
    ToolItem item = new ToolItem( bar, SWT.DROP_DOWN );
    item.setData( WidgetUtil.CUSTOM_VARIANT, "themingDemo" );
    item.setText( "Simple" );
    addMenu( item, "simple" );
    item = new ToolItem( bar, SWT.DROP_DOWN );
    item.setData( WidgetUtil.CUSTOM_VARIANT, "themingDemo" );
    item.setText( "Pretty" );
    addMenu( item, "pretty" );
    item = new ToolItem( bar, SWT.DROP_DOWN );
    item.setData( WidgetUtil.CUSTOM_VARIANT, "themingDemo" );
    item.setText( "Fancy" );
    addMenu( item, "fancy" );
    bar.pack ();
  }

  private void addMenu( final ToolItem toolItem, final String variant ) {
    final ToolBar toolBar = toolItem.getParent();
    final Shell shell = toolBar.getShell();
    final Menu menu = new Menu( shell, SWT.POP_UP );
    menu.setData( WidgetUtil.CUSTOM_VARIANT, variant );
    for( int i = 0; i < 8; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText ( "Example-Item " + ( i + 1) );
      item.setData( WidgetUtil.CUSTOM_VARIANT, variant );
    }
    toolItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        Rectangle rect = toolItem.getBounds();
        Point point = new Point( rect.x, rect.y + rect.height );
        point = toolBar.toDisplay( point );
        menu.setLocation( point.x, point.y );
        menu.setVisible( true );
      }
    });
  }

  private Group createGroup( final Composite parent,
                             final String title,
                             final int columns )
  {
    Group group = new Group( parent, SWT.NONE );
    group.setText( title );
    GridLayout layout = new GridLayout( columns, true );
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    layout.verticalSpacing = 20;
    layout.horizontalSpacing = 20;
    group.setLayout( layout );
    return group;
  }
}
