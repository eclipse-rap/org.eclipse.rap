/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ListTab extends ExampleTab {

  private static final java.util.List ELEMENTS;

  static {
    ELEMENTS = new ArrayList();
    String text
      = "A very long item that demonstrates horizontal scrolling in a List";
    ELEMENTS.add( text );
    text = "An item with a linebreak\n(converted to a whitespace)";
    ELEMENTS.add( text );
    text = "...and other control chars: \u0003 \t \u0004 \u000F";
    ELEMENTS.add( text );
    for( int i = 1; i <= 25; i++ ) {
      ELEMENTS.add( "Item " + i );
    }
  }

  private static final class ListContentProvider
    implements IStructuredContentProvider
  {
    public Object[] getElements( final Object inputElement ) {
      return ( ( java.util.List )inputElement ).toArray();
    }
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
      // do nothing
    }
    public void dispose() {
      // do nothing
    }
  }

  private List list;

  public ListTab( final CTabFolder topFolder ) {
    super( topFolder, "List" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SINGLE", SWT.SINGLE );
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "H_SCROLL", SWT.H_SCROLL );
    createStyleButton( "V_SCROLL", SWT.V_SCROLL );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 2, true ) );
    int style = getStyle();
    
    list = new List( parent, style );
    list.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    Menu menu = new Menu( list );
    MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
    menuItem.setText( "Context menu item" );
    list.setMenu( menu );
    ListViewer viewer = new ListViewer( list );
    viewer.setContentProvider( new ListContentProvider() );
    viewer.setLabelProvider( new LabelProvider() );
    viewer.setInput( ELEMENTS );
    list.addSelectionListener( new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent event ) {
        String item = list.getItem( list.getSelectionIndex() );
        String message = "Selected Item: " + item;
        MessageDialog.openInformation( getShell(), "Selection", message );
      }
    } );
    registerControl( list );
    
    // List 2
    List list2 = new List( parent, style );
    list2.add( "Item 1" );
    list2.add( "Item 2" );
    list2.add( "Item 3" );
    list2.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    registerControl( list2 );
    createPopupMenu( parent.getShell(), list2 );
    
    // Code
    int separatorStyle = SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT;
    Label separator = new Label( parent, separatorStyle );
    separator.setLayoutData( createGridDataWithSpan() );
    Label codeLabel = new Label( parent, SWT.WRAP );
    String codeLabelText
      = "Please note that the content of the left List is provided by a "
      + "ListViewer with JFace API.";
    codeLabel.setText( codeLabelText );
    codeLabel.setLayoutData( createGridDataWithSpan() );
    Link link = new Link( parent, SWT.NONE );
    link.setText( "See <a>example code</a>" );
    link.setLayoutData( createGridDataWithSpan() );
    link.addSelectionListener( new SelectionAdapter() {
      private String code = getExampleCode();
      public void widgetSelected( final SelectionEvent event ) {
        String title = "ListViewer Example Code";
        HtmlDialog dialog = new HtmlDialog( parent.getShell(), title, code );
        dialog.setSize( 550, 400 );
        dialog.open();
      }
    } );
  }

  private GridData createGridDataWithSpan() {
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalSpan = 2;
    return gridData;
  }

  private void createPopupMenu( final Shell parent, final List list ) {
    final Menu menu = new Menu( parent, SWT.POP_UP );
    String[] listItems = list.getItems();
    for( int i = 0; i < listItems.length; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( listItems[ i ] );
    }
    menu.addMenuListener( new MenuAdapter() {
      public void menuShown( MenuEvent e ) {
        MenuItem[] items = menu.getItems();
        for( int i = 0; i < items.length; i++ ) {
          MenuItem item = items[ i ];
          item.setEnabled( list.isSelected( i ) );
        }
      }
    } );
    list.setMenu( menu );
  }

  private String getExampleCode() {
    String result
      = "<html><head></head></body>"
      + "<pre>"
      + "class ListContentProvider implements IStructuredContentProvider {\n"
      + "  public Object[] getElements( final Object inputElement ) {\n"
      + "    return ( ( java.util.List )inputElement ).toArray();\n"
      + "  }\n"
      + "}\n"
      + "...\n"
      + "java.util.List elements = ...\n"
      + "...\n"
      + "ListViewer viewer = new ListViewer( parent );\n"
      + "viewer.setContentProvider( new ListContentProvider() );\n"
      + "viewer.setLabelProvider( new LabelProvider() );\n"
      + "java.util.List input = new ArrayList();\n"
      + "... // populate list\n"
      + "viewer.setInput( input );\n"
      + "</pre>"
      + "</body>";
    return result;
  }
}
