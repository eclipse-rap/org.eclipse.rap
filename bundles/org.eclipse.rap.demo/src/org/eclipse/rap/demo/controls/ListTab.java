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
import org.eclipse.rap.jface.viewers.*;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ListTab extends ExampleTab {

  private static final java.util.List ELEMENTS;
  static {
    ELEMENTS = new ArrayList();
    String text 
      = "A very long item that demonstrates horizontal scrolling in a List";
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

  public ListTab( final TabFolder folder ) {
    super( folder, "List" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "H_SCROLL" );
    createStyleButton( "V_SCROLL" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    int style = getStyle();
    list = new List( parent, style );
    list.setLayoutData( new RowData( 200, 200 ) );
    Menu menu = new Menu( list );
    MenuItem menuItem = new MenuItem( menu, RWT.PUSH );
    menuItem.setText( "Context menu item" );
    list.setMenu( menu );
    ListViewer viewer = new ListViewer( list );
    viewer.setContentProvider( new ListContentProvider() );
    viewer.setLabelProvider( new LabelProvider() );
    viewer.setInput( ELEMENTS );
    int separatorStyle = RWT.SEPARATOR | RWT.HORIZONTAL | RWT.SHADOW_OUT;
    Label separator = new Label( parent, separatorStyle );
    separator.setLayoutData( new RowData( 440, 5 ) );
    Label codeLabel = new Label( parent, RWT.WRAP );
    codeLabel.setLayoutData( new RowData( 450, 35 ) );
    String codeLabelText 
      = "Please note that the content of the above List is provided by a " 
      + "ListViewer with JFace API. The source code looks like this:";
    codeLabel.setText( codeLabelText );
    Browser browser = new Browser( parent, RWT.MULTI );
    browser.setLayoutData( new RowData( 450, 210 ) );
    String note
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
    browser.setText( note );
    registerControl( list );
  }
}
