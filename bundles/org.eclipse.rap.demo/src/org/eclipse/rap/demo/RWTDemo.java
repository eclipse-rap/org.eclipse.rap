/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import java.util.ArrayList;
import org.eclipse.jface.viewers.*;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

// TODO: Do we still need this demo ?
public class RWTDemo implements IEntryPoint {

  private static final int ROWS = 40;
  private static final int COLUMNS = 10;
  private Text txtGroupNameSash;
  private Text txtGroupDescriptionSash;
  private Text txtGroupNameTab1;
  private int previousTabSelected = 0;
  private int tabSelected = 0;
  
  class TreeObject {

    private String name;
    private TreeParent parent;

    public TreeObject( String name ) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setParent( TreeParent parent ) {
      this.parent = parent;
    }

    public TreeParent getParent() {
      return parent;
    }

    public String toString() {
      return getName();
    }
  }
  
  class TreeParent extends TreeObject {

    private ArrayList children;

    public TreeParent( String name ) {
      super( name );
      children = new ArrayList();
    }

    public void addChild( TreeObject child ) {
      children.add( child );
      child.setParent( this );
    }

    public void removeChild( TreeObject child ) {
      children.remove( child );
      child.setParent( null );
    }

    public TreeObject[] getChildren() {
      return ( TreeObject[] )children.toArray( new TreeObject[ children.size() ] );
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }
  }
  
  class TreeViewerContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {

    private TreeParent invisibleRoot;

    public void inputChanged( Viewer v, Object oldInput, Object newInput ) {
    }

    public void dispose() {
    }

    public Object[] getElements( Object parent ) {
      if( parent instanceof IEntryPoint ) {
        if( invisibleRoot == null )
          initialize();
        return getChildren( invisibleRoot );
      }
      return getChildren( parent );
    }

    public Object getParent( Object child ) {
      if( child instanceof TreeObject ) {
        return ( ( TreeObject )child ).getParent();
      }
      return null;
    }

    public Object[] getChildren( Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).getChildren();
      }
      return new Object[ 0 ];
    }

    public boolean hasChildren( Object parent ) {
      if( parent instanceof TreeParent )
        return ( ( TreeParent )parent ).hasChildren();
      return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real
     * code, you will connect to a real model and expose its hierarchy.
     */
    private void initialize() {
      TreeObject to1 = new TreeObject( "Leaf 1" );
      TreeObject to2 = new TreeObject( "Leaf 2" );
      TreeObject to3 = new TreeObject( "Leaf 3" );
      TreeParent p1 = new TreeParent( "Parent 1" );
      p1.addChild( to1 );
      p1.addChild( to2 );
      p1.addChild( to3 );
      TreeObject to4 = new TreeObject( "Leaf 4" );
      TreeParent p2 = new TreeParent( "Parent 2" );
      p2.addChild( to4 );
      TreeParent root = new TreeParent( "Root" );
      root.addChild( p1 );
      root.addChild( p2 );
      invisibleRoot = new TreeParent( "" );
      invisibleRoot.addChild( root );
    }
  }


  public Display createUI() {
    Display display = new Display();
    final Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 800, 600 );
    
    createMenu( shell );

    final ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    item1.setText( "new" );

    ToolItem item2 = new ToolItem( toolBar, SWT.PUSH );
    item2.setText( "open" );
    item2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createShell2( shell.getDisplay() );
      }
    } );
    
    ToolItem item3 = new ToolItem( toolBar, SWT.PUSH );
    item3.setText( "save as" );

    ToolItem item4 = new ToolItem( toolBar, SWT.PUSH );
    item4.setText( "print" );
    
    ClassLoader loader = getClass().getClassLoader();
    item1.setImage( Image.find( "resources/newfile_wiz.gif", loader ) );
    item2.setImage( Image.find( "resources/newfolder_wiz.gif", loader ) );
    item3.setImage( Image.find( "resources/newprj_wiz.gif", loader ) );
    item4.setImage( Image.find( "resources/search_src.gif", loader ) );
    
    final Composite content = new Composite( shell, SWT.NONE );
    content.setLayout( new FillLayout() );
    layoutShell( shell, toolBar, content );
    
    
    SashForm sashForm = new SashForm( content, SWT.HORIZONTAL );
    Composite left = new Composite( sashForm, SWT.NONE );
    Composite right = new Composite( sashForm, SWT.NONE );
    sashForm.setWeights( new int[]{
      25, 
      75
    } );
    left.setLayout( new FormLayout() );
    TreeViewer viewer = new TreeViewer( left );
    viewer.setContentProvider( new TreeViewerContentProvider() );
    viewer.setInput( this );
//    Tree tree = new Tree( left, SWT.NONE );
    Tree tree = viewer.getTree();
    FormData treeData = new FormData();
    tree.setLayoutData( treeData );
    treeData.top = new FormAttachment( 0, 2 );
    treeData.left = new FormAttachment( 0, 2 );
    treeData.right = new FormAttachment( 100, -2 );
    treeData.bottom = new FormAttachment( 100, -2 );
    tree.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        txtGroupNameTab1.setText( "treeItem selected: " + event.item.getText() );
      }
    } );
//    TreeItem item1 = new TreeItem( tree, SWT.NONE );
//    item1.setText( "item 1" );
//    TreeItem item2 = new TreeItem( tree, SWT.NONE );
//    item2.setText( "item 2" );
//    TreeItem item2sub1 = new TreeItem( item2, SWT.NONE );
//    item2sub1.setText( "subItem1" );
    right.setLayout( new FillLayout() );
    SashForm sashFormVertical = new SashForm( right, SWT.VERTICAL );
    Composite top = new Composite( sashFormVertical, SWT.NONE );
    Composite down = new Composite( sashFormVertical, SWT.NONE );
    sashFormVertical.setWeights( new int[]{
      60, 40
    } );
    top.setLayout( new FillLayout() );
    down.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( top, SWT.NONE );
    TabFolder downFolder = new TabFolder( down, SWT.NONE );
    createTab1( topFolder );
    createTab2( topFolder );
    createTab3( topFolder );
    createTab4( downFolder );
    topFolder.setSelection( 0 );
    downFolder.setSelection( 0 );
    topFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        previousTabSelected = tabSelected;
        tabSelected = ( ( TabFolder )event.getSource() ).getSelectionIndex();
        TabItem item = topFolder.getItem( previousTabSelected );
        txtGroupNameSash.setText( "The previous tab you visited was: "
                                  + item.getText() );
      }
    } );
    content.layout();
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        layoutShell( shell, toolBar, content );
      }
    } );
    shell.setText( "SWT Demo" );
    shell.open();
    return display;
  }

  private void layoutShell( final Shell shell,
                            final ToolBar toolBar,
                            final Composite content )
  {
    Rectangle clientArea = shell.getClientArea();
    int toolBarHeight = 30;
    toolBar.setBounds( clientArea.x,
                       clientArea.y,
                       clientArea.width, 
                       toolBarHeight );
    content.setBounds( clientArea.x, 
                       clientArea.y + toolBarHeight + 1, 
                       clientArea.width, 
                       clientArea.height - toolBarHeight - 1 );
    content.layout();
  }

  private void createMenu( final Shell shell ) {
    final Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileMenuItem = new MenuItem( menuBar, SWT.CASCADE );
    fileMenuItem.setText( "File" );
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileMenuItem.setMenu( fileMenu );
    MenuItem openItem = new MenuItem( fileMenu, SWT.PUSH );
    openItem.setText( "Open" );
    openItem.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        createShell2( shell.getDisplay() );
      }
    } );
    MenuItem openRecentItem = new MenuItem( fileMenu, SWT.CASCADE );
    openRecentItem.setText( "Open recently used" );
    Menu recentMenu = new Menu( openRecentItem );
    MenuItem recentItem1 = new MenuItem( recentMenu, SWT.PUSH );
    recentItem1.setText( "myfile.txt" );
    openRecentItem.setMenu( recentMenu );
    new MenuItem( fileMenu, SWT.SEPARATOR );
    MenuItem exitItem = new MenuItem( fileMenu, SWT.PUSH );
    exitItem.setText( "Exit" );
  }

  private void createTab1( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "FormLayoutTab" );
    Composite composite = new Composite( folder, SWT.NONE );
    item.setControl( composite );
    composite.setLayout( new FormLayout() );
    Label lblName = new Label( composite, SWT.NONE );
    lblName.setText( "Name" );
    FormData lblNameData = new FormData();
    lblName.setLayoutData( lblNameData );
    lblNameData.top = new FormAttachment( 0, 5 );
    lblNameData.left = new FormAttachment( 0, 5 );
    lblNameData.right = new FormAttachment( 20, 0 );
    lblNameData.bottom = new FormAttachment( 0, 25 );
    txtGroupNameTab1 = new Text( composite, SWT.MULTI | SWT.BORDER );
    txtGroupNameTab1.setText( "" );
    FormData txtGroupNameData = new FormData();
    txtGroupNameTab1.setLayoutData( txtGroupNameData );
    txtGroupNameData.top = new FormAttachment( 0, 5 );
    txtGroupNameData.left = new FormAttachment( lblName, 5 );
    txtGroupNameData.right = new FormAttachment( 100, -5 );
    txtGroupNameData.bottom = new FormAttachment( 0, 25 );
    Label lblDescription = new Label( composite, SWT.NONE );
    lblDescription.setText( "Description" );
    FormData lblDescriptionData = new FormData();
    lblDescription.setLayoutData( lblDescriptionData );
    lblDescriptionData.top = new FormAttachment( lblName, 10 );
    lblDescriptionData.left = new FormAttachment( 0, 5 );
    lblDescriptionData.right = new FormAttachment( 20, 0 );
    Text txtGroupDescription = new Text( composite, SWT.MULTI | SWT.BORDER );
    txtGroupDescription.setText( "Select a TreeItem" );
    FormData txtGroupDescriptionData = new FormData();
    txtGroupDescription.setLayoutData( txtGroupDescriptionData );
    txtGroupDescriptionData.top = new FormAttachment( lblName, 10 );
    txtGroupDescriptionData.left = new FormAttachment( lblDescription, 5 );
    txtGroupDescriptionData.bottom = new FormAttachment( 100, -5 );
    txtGroupDescriptionData.right = new FormAttachment( 100, -5 );
  }

  private void createShell2( final Display display ) {
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setLayout( new FillLayout() );
    Composite composite = new Composite( shell, SWT.SHELL_TRIM );
    composite.setLayout( new FillLayout() );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "FillLayout" );
    final Text text = new Text( composite, SWT.MULTI | SWT.BORDER );
    text.setText( "None ASCII characters: äöüß@, select the Sash or press the button" );
    Sash sash = new Sash( composite, SWT.NONE );
    sash.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent event ) {
        text.setText( "widgetSelected: "
                      + event.getSource()
                      + " bounds: ["
                      + event.x
                      + ", "
                      + event.y
                      + ", "
                      + event.width
                      + ", "
                      + event.height
                      + "]" );
      }
    } );
    Button button = new Button( composite, SWT.NONE );
    button.setText( "Push Me" );
    button.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        text.setText( "widgetSelected: "
                      + event.getSource()
                      + " bounds: ["
                      + event.x
                      + ", "
                      + event.y
                      + ", "
                      + event.width
                      + ", "
                      + event.height
                      + "]" );
      }
    } );
    shell.setBounds( 250, 20, 400, 200 );
    shell.layout();
    shell.open();
  }

  private void createTab2( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "SashTab" );
    Composite composite = new Composite( folder, SWT.NONE );
    item.setControl( composite );
    composite.setLayout( new FillLayout() );
    SashForm sashForm = new SashForm( composite, SWT.NONE );
    Composite composite1 = new Composite( sashForm, SWT.NONE );
    Composite composite2 = new Composite( sashForm, SWT.NONE );
    sashForm.setWeights( new int[]{
      25, 75
    } );
    composite1.setLayout( new FormLayout() );
    Text txtLeft = new Text( composite1, SWT.BORDER );
    txtLeft.setText( "This text is on a SashForm" );
    FormData txtLeftData = new FormData();
    txtLeft.setLayoutData( txtLeftData );
    txtLeftData.top = new FormAttachment( 0, 5 );
    txtLeftData.left = new FormAttachment( 0, 5 );
    txtLeftData.right = new FormAttachment( 100, -5 );
    txtLeftData.bottom = new FormAttachment( 100, -5 );
    composite2.setLayout( new FormLayout() );
    Label lblName = new Label( composite2, SWT.NONE );
    lblName.setText( "Name" );
    FormData lblNameData = new FormData();
    lblName.setLayoutData( lblNameData );
    lblNameData.top = new FormAttachment( 0, 5 );
    lblNameData.left = new FormAttachment( 0, 5 );
    lblNameData.right = new FormAttachment( 20, 0 );
    lblNameData.bottom = new FormAttachment( 0, 25 );
    txtGroupNameSash = new Text( composite2, SWT.BORDER );
    txtGroupNameSash.setText( "The previous tab you visited was: " );
    FormData txtGroupNameData = new FormData();
    txtGroupNameSash.setLayoutData( txtGroupNameData );
    txtGroupNameData.top = new FormAttachment( 0, 5 );
    txtGroupNameData.left = new FormAttachment( lblName, 5 );
    txtGroupNameData.right = new FormAttachment( 100, -5 );
    txtGroupNameData.bottom = new FormAttachment( 0, 25 );
    Label lblDescription = new Label( composite2, SWT.NONE );
    lblDescription.setText( "Description" );
    FormData lblDescriptionData = new FormData();
    lblDescription.setLayoutData( lblDescriptionData );
    lblDescriptionData.top = new FormAttachment( lblName, 10 );
    lblDescriptionData.left = new FormAttachment( 0, 5 );
    lblDescriptionData.right = new FormAttachment( 20, 0 );
    txtGroupDescriptionSash = new Text( composite2, SWT.BORDER );
    txtGroupDescriptionSash.setText( "try selecting single or multiple table rows" );
    FormData txtGroupDescriptionData = new FormData();
    txtGroupDescriptionSash.setLayoutData( txtGroupDescriptionData );
    txtGroupDescriptionData.top = new FormAttachment( lblName, 10 );
    txtGroupDescriptionData.left = new FormAttachment( lblDescription, 5 );
    txtGroupDescriptionData.bottom = new FormAttachment( 100, -5 );
    txtGroupDescriptionData.right = new FormAttachment( 100, -5 );
  }

  private void createTab3( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "MoreWidgetsTab" );
    Composite panel = new Composite( folder, SWT.NONE );
    panel.setLayout( new FormLayout() );
    item.setControl( panel );
    Button button = new Button( panel, SWT.NONE );
    button.setText( "Click Me" );
    FormData buttonData = new FormData();
    button.setLayoutData( buttonData );
    buttonData.top = new FormAttachment( 0, 5 );
    buttonData.left = new FormAttachment( 0, 5 );
    buttonData.right = new FormAttachment( 30, 0 );
    buttonData.height = 24;
    // buttonData.bottom = new FormAttachment( 15, 0 );
    Composite radioComp = new Composite( panel, SWT.NONE );
    radioComp.setLayout( new FillLayout() );
    Button radio1 = new Button( radioComp, SWT.RADIO );
    radio1.setText( "choice one" );
    Button radio2 = new Button( radioComp, SWT.RADIO );
    radio2.setText( "choice two" );
    FormData radioCompData = new FormData();
    radioComp.setLayoutData( radioCompData );
    radioCompData.top = new FormAttachment( 0, 5 );
    radioCompData.left = new FormAttachment( button, 15 );
    radioCompData.right = new FormAttachment( 70, -15 );
    radioCompData.height = 24;
    // radioCompData.bottom = new FormAttachment( 15, 0 );
    Button checkButton = new Button( panel, SWT.CHECK );
    checkButton.setText( "check me." );
    FormData checkButtonData = new FormData();
    checkButton.setLayoutData( checkButtonData );
    checkButtonData.top = new FormAttachment( 0, 5 );
    checkButtonData.left = new FormAttachment( radioComp, 5 );
    checkButtonData.right = new FormAttachment( 100, -5 );
    checkButtonData.height = 24;
    Combo combo = new Combo( panel, SWT.NONE );
    combo.add( "selectItem1" );
    combo.add( "selectItem2" );
    combo.add( "selectItem3" );
    FormData comboData = new FormData();
    combo.setLayoutData( comboData );
    comboData.top = new FormAttachment( button, 15 );
    comboData.left = new FormAttachment( 0, 5 );
    comboData.right = new FormAttachment( 30, 0 );
    comboData.height = 24;
    List list = new List( panel, SWT.MULTI | SWT.BORDER );
    list.add( "Selection1" );
    list.add( "Selection2" );
    list.add( "Selection3" );
    list.add( "Selection4" );
    list.add( "Selection5" );
    list.add( "Selection6" );
    list.setToolTipText( "I'm a list" );
    list.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        List list = ( List )e.getSource();
        String selected = "";
        int[] selectionIndices = list.getSelectionIndices();
        for( int i = 0; i < selectionIndices.length; i++ ) {
          selected += selectionIndices[ i ] + " ";
        }
        System.out.println( selected );
      }
    } );
    FormData listData = new FormData();
    list.setLayoutData( listData );
    listData.top = new FormAttachment( combo, 15 );
    listData.left = new FormAttachment( 0, 5 );
    listData.right = new FormAttachment( 30, 0 );
    listData.height = 96;
  }

  private void createTab4( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "TableTab" );
    Composite composite = new Composite( folder, SWT.NONE );
    item.setControl( composite );
    composite.setLayout( new FormLayout() );
    final Table table = new Table( composite, SWT.NONE );
    FormData formData = new FormData();
    table.setLayoutData( formData );
    formData.top = new FormAttachment( 0, 5 );
    formData.left = new FormAttachment( 0, 5 );
    formData.right = new FormAttachment( 100, -5 );
    formData.bottom = new FormAttachment( 100, -5 );
    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, SWT.NONE );
      tableColumn.setText( "Column" + i );
      if( i == 2 ) {
        tableColumn.setWidth( 190 );
      } else {
        tableColumn.setWidth( 70 );
      }
    }
    for( int i = 0; i < ROWS; i++ ) {
      TableItem tableItem = new TableItem( table, SWT.NONE );
      for( int j = 0; j < COLUMNS; j++ ) {
        tableItem.setText( j, "Item" + i + "-" + j );
      }
    }
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        String selected = "";
        int[] selectionIndices = table.getSelectionIndices();
        for( int i = 0; i < selectionIndices.length; i++ ) {
          selected += selectionIndices[ i ] + " ";
        }
        System.out.println( selected );
        txtGroupDescriptionSash.setText( "Indices of selected tableRows are: "
                                         + selected );
      }
    } );
  }
}
