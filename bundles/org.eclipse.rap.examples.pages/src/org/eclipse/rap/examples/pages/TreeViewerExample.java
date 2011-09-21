/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.*;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.*;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class TreeViewerExample implements IExamplePage {

  private static final int EDITOR_ACTIVATE
    =   ColumnViewerEditor.TABBING_HORIZONTAL
      | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
      | ColumnViewerEditor.TABBING_VERTICAL
      | ColumnViewerEditor.KEYBOARD_ACTIVATION;

  private final static int MODERN_STYLE = 0;
  private final static int TABLE_STYLE = 1;

  private TreeViewer currentViewer;
  private TreeViewer simpleTree;
  private TreeViewer complexTree;
  private int newItem;

  /////////////////
  // create widgets

  public void createControl( Composite parent ) {
    GridLayout mainLayout = ExampleUtil.createMainLayout( 2 );
    mainLayout.makeColumnsEqualWidth = false;
    parent.setLayout( mainLayout );
    createTopLeft( parent );
    createTopRight( parent );
    createFooter( parent );
    setFocus();
  }

  private void createTopLeft( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Simple Tree" );
    group.setLayoutData( ExampleUtil.createFillData() );
    FillLayout layout = new FillLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    group.setLayout( layout );
    simpleTree = createSimpleTree( group );
  }

  private void createTopRight( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Complex Tree" );
    group.setLayoutData( ExampleUtil.createFillData() );
    FillLayout layout = new FillLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    group.setLayout( layout );
    complexTree = createComplexTree( group );
  }

  private void createFooter( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData footerData = ExampleUtil.createHorzFillData();
    footerData.horizontalSpan = 2;
    group.setLayoutData( footerData );
    createControlButtons( group );
  }

  private void setFocus() {
    Tree tree = simpleTree.getTree();
    tree.forceFocus();
    tree.select( tree.getItem( 0 ) );
  }

  private void createControlButtons( Composite parent ) {
    RowLayout layout = new RowLayout();
    layout.fill = true;
    layout.marginHeight = 5;
    layout.marginTop = 0;
    parent.setLayout( layout );
    Button newButton = new Button( parent, SWT.PUSH );
    newButton.setText( "New Item" );
    newButton.addSelectionListener( new NewButtonSelectionHandler() );
    Button removeButton = new Button( parent, SWT.PUSH );
    removeButton.setText( "Remove Item(s)" );
    removeButton.addSelectionListener( new RemoveButtonSelectionHandler() );
  }

  private TreeViewer createSimpleTree( Composite parent ) {
    Tree tree = new Tree( parent, SWT.BORDER | SWT.MULTI );
    TreeViewer result = new TreeViewer( tree );
    result.setContentProvider( new TreeContentProvider() );
    TreeLabelProvider labelProvider = new TreeLabelProvider( parent.getDisplay(), MODERN_STYLE );
    result.setLabelProvider( labelProvider );
    result.setInput( createModel() );
    result.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler() );
    addDNDSupport( result );
    addCellEditor( result );
    addToolTipSupport( result );
    return result;
  }

  private TreeViewer createComplexTree( Composite parent ) {
    int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK;
    Tree tree = new Tree( parent, style );
    createColumn( tree, "City", SWT.LEFT, 155 );
    createColumn( tree, "Timezone", SWT.CENTER, 65 );
    createColumn( tree, "Offset", SWT.CENTER, 65 );
    tree.setLinesVisible( true );
    tree.setHeaderVisible( true );
    CheckboxTreeViewer result = new CheckboxTreeViewer( tree );
    result.addCheckStateListener( new TreeCheckStateListener( result ) );
    result.setContentProvider( new TreeContentProvider() );
    TreeLabelProvider labelProvider
      = new TreeLabelProvider( parent.getDisplay(), TABLE_STYLE );
    result.setLabelProvider( labelProvider );
    result.setInput( createModel() );
    result.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler() );
    addDNDSupport( result );
    addCellEditor( result );
    addToolTipSupport( result );
    return result;
  }

  private static TreeColumn createColumn( Tree parent, String name, int style, int width ) {
    TreeColumn result = new TreeColumn( parent, style );
    result.setText( name );
    result.setWidth( width );
    result.setMoveable( true );
    result.setResizable( true );
    return result;
  }

  //////////////
  // Drag & Drop

  private static void addDNDSupport( TreeViewer viewer ) {
    Transfer[] types = new Transfer[] { TreeObjectTransfer.getInstance() };
    TreeDragListener dragListener = new TreeDragListener( viewer );
    viewer.addDragSupport( DND.DROP_MOVE, types, dragListener );
    TreeDropListener dropListener = new TreeDropListener( viewer );
    viewer.addDropSupport( DND.DROP_MOVE, types, dropListener );
  }

  //////////////
  // Cell-editor

  private static void addCellEditor( TreeViewer viewer ) {
    CellEditor[] editors = new CellEditor[] {
      new TextCellEditor( viewer.getTree() ),
      new TextCellEditor( viewer.getTree() ),
      new TextCellEditor( viewer.getTree() )
    };
    viewer.setCellEditors( editors );
    viewer.setCellModifier( new CellModifier( viewer ) );
    String[] columnProperties = new String[] { "Name", "Timezone", "Offset" };
    viewer.setColumnProperties( columnProperties );
    ColumnViewerEditorActivationStrategy activationStrategy
      = new CellEditorActivationStrategy( viewer );
    TreeViewerEditor.create( viewer, activationStrategy, EDITOR_ACTIVATE );
  }

  private static void addToolTipSupport( TreeViewer viewer ) {
    ColumnViewerToolTipSupport.enableFor( viewer );
  }

  private static TreeObject createModel() {
    TreeObject result = new TreeObject( "" );
    TreeObject asia = new TreeObject( "Asia" );
    result.addChild( asia );
    asia.addChild( new City( "Hong Kong", "HKT", +8 ) );
    asia.addChild( new City( "Tokyo", "JST", +9 ) );
    TreeObject europe = new TreeObject( "Europe" );
    result.addChild( europe );
    europe.addChild( new City( "Lisbon", "WET", 0 ) );
    europe.addChild( new City( "Berlin", "CET", +1 ) );
    europe.addChild( new City( "Sofia", "EET", +2 ) );
    europe.addChild( new City( "Moscow", "MT", +3 ) );
    TreeObject northAmerica = new TreeObject( "North America" );
    result.addChild( northAmerica );
    northAmerica.addChild( new City( "New York", "EST", -5 ) );
    northAmerica.addChild( new City( "Chicago", "CST", -6 ) );
    northAmerica.addChild( new City( "Los Angeles", "PST", -8 ) );
    northAmerica.addChild( new City( "Anchorage", "AKST", -9 ) );
    return result;
  }

  private final class TreeFocusGainedHandler extends FocusAdapter {
    @Override
    public void focusGained( FocusEvent event ) {
      currentViewer = null;
      Tree currentTree = ( Tree )event.widget;
      if( simpleTree.getTree() == currentTree ) {
        currentViewer = simpleTree;
      } else if( complexTree.getTree() == currentTree ) {
        currentViewer = complexTree;
      }
    }
  }

  private static class TreeCheckStateListener implements ICheckStateListener {
    private final CheckboxTreeViewer viewer;

    TreeCheckStateListener( CheckboxTreeViewer viewer ) {
      this.viewer = viewer;
    }

    public void checkStateChanged( CheckStateChangedEvent event ) {
      TreeObject treeObject = ( TreeObject )event.getElement();
      boolean checked = event.getChecked();
      updateChildren( checked, treeObject );
      updateParent( treeObject );
    }

    private void updateParent( TreeObject treeObject ) {
      TreeObject parent = treeObject.getParent();
      if( parent != null ) {
        boolean parentChecked = true;
        TreeObject[] children = parent.getChildren();
        for( int i = 0; parentChecked && i < children.length; i++ ) {
          TreeObject child = children[ i ];
          if( !viewer.getChecked( child ) ) {
            parentChecked = false;
          }
        }
        viewer.setChecked( parent, parentChecked );
        updateParent( parent );
      }
    }

    private void updateChildren( boolean checked, TreeObject parent ) {
      TreeObject[] children = parent.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        TreeObject treeObject = children[ i ];
        viewer.setChecked( treeObject, checked );
        if( treeObject.hasChildren() ) {
          updateChildren( checked, treeObject );
        }
      }
    }
  }

  private final class RemoveButtonSelectionHandler extends SelectionAdapter {

    @Override
    public void widgetSelected( SelectionEvent event ) {
      if( currentViewer != null && !currentViewer.getSelection().isEmpty() ) {
        ITreeSelection sel = ( ITreeSelection )currentViewer.getSelection();
        Iterator iterator = sel.iterator();
        TreeObject parent = null;
        while( iterator.hasNext() ) {
          TreeObject obj = ( TreeObject )iterator.next();
          parent = obj.getParent();
          if( parent != null ) {
            parent.removeChild( obj );
          }
        }
        if( parent != null ) {
          TreeObject newSel = null;
          if( parent.getParent() == null ) {
            TreeObject[] children = parent.getChildren();
            if( children.length > 0 ) {
              newSel = children[ 0 ];
            }
          } else {
            newSel = parent;
          }
          if( newSel != null ) {
            currentViewer.setSelection( new StructuredSelection( newSel ) );
          }
        }
        currentViewer.refresh();
        currentViewer.getTree().forceFocus();
      }
    }
  }

  private final class NewButtonSelectionHandler extends SelectionAdapter {
    @Override
    public void widgetSelected( SelectionEvent event ) {
      if( currentViewer != null && !currentViewer.getSelection().isEmpty() ) {
        ITreeSelection sel = ( ITreeSelection )currentViewer.getSelection();
        TreeObject obj = ( TreeObject )sel.getFirstElement();
        newItem++;
        TreeObject newObject = new City( "New Item " + newItem , "", 0 );
        obj.addChild( newObject );
        currentViewer.expandToLevel( obj, 1 );
        currentViewer.refresh();
        currentViewer.setSelection( new StructuredSelection( newObject ) );
        currentViewer.getTree().forceFocus();
      }
    }
  }

  //////////////
  // Drag & Drop

  private static class TreeDragListener extends DragSourceAdapter {
    private final TreeViewer viewer;
    private Object dragData;

    TreeDragListener( TreeViewer viewer ) {
      this.viewer = viewer;
    }

    @Override
    public void dragStart( DragSourceEvent event ) {
      dragData = getTreeObject( event.x, event.y );
    }

    @Override
    public void dragSetData( DragSourceEvent event ) {
      event.data = dragData;
    }

    @Override
    public void dragFinished( DragSourceEvent event ) {
      viewer.refresh();
    }

    private TreeObject getTreeObject( int x, int y ) {
      TreeObject result = null;
      ViewerCell cell = viewer.getCell( new Point( x, y ) );
      if( cell != null ) {
        result = ( TreeObject )cell.getElement();
      }
      return result;
    }
  }

  private static class TreeDropListener extends DropTargetAdapter {
    private final TreeViewer viewer;

    public TreeDropListener( TreeViewer viewer ) {
      this.viewer = viewer;
    }

    @Override
    public void dragEnter( DropTargetEvent event ){
      event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
    }

    @Override
    public void drop( DropTargetEvent event ) {
      if( event.data == null ) {
        event.detail = DND.DROP_NONE;
      } else {
        TreeObject draggedObject = ( TreeObject )event.data;
        TreeObject targetObject = ( TreeObject )event.item.getData();
        if( isValidDrop( draggedObject, targetObject ) ) {
          draggedObject.getParent().removeChild( draggedObject );
          targetObject.addChild( draggedObject );
          viewer.refresh();
        }
      }
    }

    private static boolean isValidDrop( TreeObject draggedObject, TreeObject targetObject ) {
      boolean result = false;
      if( draggedObject != null && targetObject != null ) {
        result = true;
        TreeObject current = targetObject;
        while( current != null && result ) {
          result = current != draggedObject;
          current = current.getParent();
        }
      }
      return result;
    }
  }

  private static class TreeObjectTransfer extends Transfer {
    private static final String TYPE_NAME = "treeObject";
    private static final int TYPE_ID = registerType( TYPE_NAME );

    private TreeObjectTransfer() {
    }

    public static TreeObjectTransfer getInstance() {
      Object instance = SessionSingletonBase.getInstance(
        TreeObjectTransfer.class
      );
      return ( TreeObjectTransfer )instance;
    }

    @Override
    protected int[] getTypeIds() {
      return new int[]{ TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
      return new String[]{ TYPE_NAME };
    }

    @Override
    public TransferData[] getSupportedTypes() {
      int[] types = getTypeIds();
      TransferData[] result = new TransferData[ types.length ];
      for( int i = 0; i < types.length; i++ ) {
        result[ i ] = new TransferData();
        result[ i ].type = types[ i ];
      }
      return result;
    }

    @Override
    public boolean isSupportedType( TransferData transferData ) {
      boolean result = false;
      if( transferData != null ) {
        int[] types = getTypeIds();
        for( int i = 0; !result && i < types.length; i++ ) {
          if( transferData.type == types[ i ] )
            result = true;
        }
      }
      return result;
    }

    @Override
    public void javaToNative( Object object, TransferData transferData ) {
      transferData.data = object;
    }

    @Override
    public Object nativeToJava( TransferData transferData ) {
      return transferData.data;
    }
  }

  private static final class CellModifier implements ICellModifier {
    private final TreeViewer viewer;

    CellModifier( TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public boolean canModify( Object element, String property ) {
      return element instanceof City || property.equals( "Name" );
    }

    public Object getValue( Object element, String property ) {
      String result = "";
      TreeObject treeObject = ( TreeObject )element;
      if( property.equals( "Name" ) ) {
        result = treeObject.getTitle();
      } else if( property.equals( "Timezone" ) ) {
        City city = ( City )treeObject;
        result = city.getTimeZone();
      } else if( property.equals( "Offset" ) ) {
        City city = ( City )treeObject;
        result = String.valueOf( city.getOffset() );
      } else {
        throw new IllegalArgumentException( "Unkown property " + property );
      }
      return result;
    }

    public void modify( Object element, String property, Object value ) {
      TreeObject treeObject;
      if( element instanceof Item ) {
        treeObject = ( TreeObject )( ( Item ) element ).getData();
      } else {
        treeObject = ( TreeObject )element;
      }
      String string = ( String )value;
      if( property.equals( "Name" ) ) {
        treeObject.setName( string );
      } else if( property.equals( "Timezone" ) ) {
        City city = ( City )treeObject;
        city.setTimeZone( string );
      } else if( property.equals( "Offset" ) ) {
        City city = ( City )treeObject;
        try {
          city.setOffset( Integer.parseInt( string ) );
        } catch( NumberFormatException e ) {
        }
      } else {
        throw new IllegalArgumentException( "Unkown property " + property );
      }
      viewer.update( treeObject, new String[]{ property } );
    }
  }

  private static final class CellEditorActivationStrategy
    extends ColumnViewerEditorActivationStrategy
  {

    CellEditorActivationStrategy( ColumnViewer viewer ) {
      super( viewer );
    }

    @Override
    protected boolean isEditorActivationEvent( ColumnViewerEditorActivationEvent event ) {
      boolean isTraversal = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL;
      boolean isDoubleClick
        = event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
      boolean isProgrammatic = event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
      return isTraversal || isDoubleClick || isProgrammatic;
    }
  }

  ////////
  // Model

  private static class TreeObject {
    private final List<TreeObject> children;
    private String name;
    private TreeObject parent;

    public TreeObject( String name ) {
      setName( name );
      children = new ArrayList<TreeObject>();
    }

    public void setParent( TreeObject parent ) {
      this.parent = parent;
    }

    public TreeObject getParent() {
      return parent;
    }

    public void setName( String name ) {
      this.name = name;
    }

    public String getTitle() {
      return name;
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
      TreeObject[] result = new TreeObject[ children.size() ];
      children.toArray( result );
      return result;
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private static class City extends TreeObject {
    private String timezone;
    private int offset;

    public City( String name, String timezone, int offset ) {
      super( name );
      setTimeZone( timezone );
      setOffset( offset );
    }

    private void setOffset( int offset ) {
      this.offset = offset;
    }

    public void setTimeZone( String tz ) {
      this.timezone = tz;
    }

    private int getOffset() {
      return this.offset;
    }

    public String getTimeZone() {
      return this.timezone;
    }
  }

  private static final class TreeLabelProvider extends CellLabelProvider {
    private static final String ICON_GREENDOT = "resources/greendot.gif";
    private static final String ICON_WORLD = "resources/world.gif";
    private static final String ICON_EARTH = "resources/earth-icon.png";

    private static final int COLUMN_TEXT = 0;
    private static final int COLUMN_OFFSET = 2;
    private static final int COLUMN_TIMEZONE = 1;

    private final Device device;
    private final Image continentImage;
    private final Image cityImage;
    private final Font cityFont;
    private final Font continentFont;
    private final Color timezoneTextColor;
    private final Color offsetTextColor;
    private int style;

    TreeLabelProvider( Device device, int style ) {
      this.device = device;
      this.style = style;
      this.cityFont = createFont( "Times New Roman", 13, SWT.NONE );
      this.continentFont = createFont( "Arial", 14, SWT.ITALIC );
      this.timezoneTextColor = Graphics.getColor( 239, 41, 41 );
      this.offsetTextColor = Graphics.getColor( 252, 175, 62 );
      if( style == MODERN_STYLE ) {
        this.continentImage = createImage( ICON_EARTH );
        this.cityImage = createImage( ICON_GREENDOT );
      } else {
        this.continentImage = createImage( ICON_WORLD );
        this.cityImage = createImage( ICON_GREENDOT );
      }
    }

    @Override
    public void update( ViewerCell cell ) {
      TreeObject treeObject = ( TreeObject )cell.getElement();
      int columnIndex = cell.getColumnIndex();
      switch( columnIndex ) {
        case COLUMN_TEXT:
          updateName( cell, treeObject );
        break;
        case COLUMN_TIMEZONE:
          updateTimeZone( cell, treeObject );
        break;
        case COLUMN_OFFSET:
          updateOffset( cell, treeObject );
        break;
      }
    }

    @Override
    public String getToolTipText( Object element ) {
      String result = "";
      if( element instanceof City ) {
        City city = ( City )element;
        String name = city.getTitle();
        String timeZone = city.getTimeZone();
        String utcOffset = getUTCOffset( city );
        result = name + " (" + timeZone + ", " + utcOffset + ")";
      }
      return result;
    }

    private void updateName( ViewerCell cell, TreeObject treeObject ) {
      cell.setText( treeObject.name );
      if( style == MODERN_STYLE ) {
        if( treeObject instanceof City ) {
          cell.setFont( cityFont );
        } else {
          cell.setFont( continentFont );
        }
      }
      cell.setImage( treeObject instanceof City ? cityImage : continentImage );
    }

    private void updateTimeZone( ViewerCell cell, TreeObject treeObject ) {
      if( treeObject instanceof City ) {
        City city = ( City )treeObject;
        cell.setText( city.getTimeZone() );
        if( style == TABLE_STYLE ) {
          cell.setForeground( timezoneTextColor );
        }
      }
    }

    private void updateOffset( ViewerCell cell, TreeObject treeObject ) {
      if( treeObject instanceof City ) {
        if( style == TABLE_STYLE ) {
          cell.setForeground( offsetTextColor );
        }
        City city = ( City )treeObject;
        cell.setText( getUTCOffset( city ) );
      }
    }

    private Font createFont( String name, int size, int style ) {
      FontData fontData = new FontData( name, size, style );
      return new Font( device, fontData );
    }

    private Image createImage( String name ) {
      ClassLoader classLoader = getClass().getClassLoader();
      return Graphics.getImage( name, classLoader );
    }

    private static String getUTCOffset( City city ) {
      String sign = city.getOffset() >= 0 ? "-" : "";
      return "UTC " + sign + String.valueOf( city.getOffset() );
    }
  }

  private static class TreeContentProvider implements ITreeContentProvider {

    public Object[] getElements( Object parent ) {
      return getChildren( parent );
    }

    public Object getParent( Object child ) {
      Object result = null;
      if( child instanceof City ) {
        result = ( ( City )child ).getParent();
      }
      return result;
    }

    public Object[] getChildren( Object parent ) {
      Object[] result = new Object[ 0 ];
      if( parent instanceof TreeObject ) {
        result = ( ( TreeObject )parent ).getChildren();
      }
      return result;
    }

    public boolean hasChildren( Object parent ) {
      boolean result = false;
      if( parent instanceof TreeObject ) {
        result = ( ( TreeObject )parent ).hasChildren();
      }
      return result;
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }

    public void dispose() {
    }
  }
}
