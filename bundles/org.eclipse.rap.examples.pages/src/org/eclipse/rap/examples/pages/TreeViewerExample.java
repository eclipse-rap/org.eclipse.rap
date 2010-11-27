/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.*;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
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
  private final static int TABLE_STYLE = 2;
  
  private static final String ICON_GREENDOT = "resources/greendot.gif";
  private static final String ICON_WORLD = "resources/world.gif";
  private static final String ICON_EARTH = "resources/earth-icon.png";

  TreeViewer currentTree = null;
  TreeViewer[] allTrees = new TreeViewer[ 2 ];
  int newItem = 0;
  TreeObject draggedObject = null;
  final int footerHeight = 53;
  final int margin = 8;

  /////////////////
  // create widgets

  public void createControl( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    createTrees( parent );
    createFooter( parent );
    Tree first = allTrees[ 0 ].getTree();
    first.forceFocus();
    first.select( first.getItem( 0 ) );
  }
  
  private void createTrees( final Composite parent ) {
    Composite topLeft = createComposite( parent, SWT.TOP | SWT.LEFT  );
    Composite topRight = createComposite( parent, SWT.TOP | SWT.RIGHT );
    allTrees[ 0 ] = createModernTree( topLeft );
    allTrees[ 1 ] = createTableTree( topRight );
  }
  
  private Composite createComposite( final Composite parent, 
                                     final int position )
  {
    Composite result = new Composite( parent, SWT.NONE );
    FormAttachment zero = new FormAttachment( 0 );
    FormAttachment hFull = new FormAttachment( 100 );
    FormAttachment vFull = new FormAttachment( 100, -footerHeight );
    FormAttachment hFifty = new FormAttachment( 50 );
    boolean left = ( position & SWT.RIGHT ) == 0; 
    FormData formData = new FormData();
    result.setLayoutData( formData );
    formData.left = left ? zero : hFifty;
    formData.top = zero;
    formData.right = left ? hFifty : hFull;
    formData.bottom = vFull;
    return result;
  }
  
  private void createFooter( final Composite parent ) {
    Group bottom = new Group( parent, SWT.NONE );
    FormData formData = new FormData();
    bottom.setLayoutData( formData );
    formData.top = new FormAttachment( 100, -footerHeight );
    formData.left = new FormAttachment( 0, margin ); 
    formData.right = new FormAttachment( 100, -margin ); 
    formData.bottom = new FormAttachment( 100, -margin ); 
    createControlButtons( bottom );
  }  

  private void createControlButtons( final Composite parent ) {
    RowLayout layout = new RowLayout();
    layout.fill = true;
    layout.marginTop = 0;
    parent.setLayout( layout );
    Button newButton = new Button( parent, SWT.PUSH );
    newButton.setText( "New Item" );
    newButton.addSelectionListener( new NewButtonSelectionHandler() );
    Button removeButton = new Button( parent, SWT.PUSH );
    removeButton.setText( "Remove Item(s)" );
    removeButton.addSelectionListener( new RemoveButtonSelectionHandler() );    
    parent.layout();
  }
  
  private TreeViewer createModernTree( final Composite composite ) {
    final Tree tree = new Tree( composite, SWT.BORDER | SWT.MULTI);
    tree.setData( WidgetUtil.CUSTOM_VARIANT, "modern" );
    composite.setLayout( newFillLayout() );
    TreeViewer viewer = new TreeViewer( tree );
    viewer.setContentProvider( new ExampleContentProvider() );    
    viewer.setLabelProvider( new ExampleLabelProvider( composite.getDisplay(),
                                                       MODERN_STYLE ) );
    viewer.setInput( createDummyModel() );
    viewer.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler( viewer ) );
    addDNDSupport( viewer );
    addCellEditor( viewer );
    return viewer;
  }

  private TreeViewer createTableTree( final Composite composite ) {
    composite.setLayout( newFillLayout() );
    int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK;
    Tree tree = new Tree( composite, style );
    tree.setData( WidgetUtil.CUSTOM_VARIANT, "modern" );
    createColumn( tree, "City", SWT.LEFT, 155 );
    createColumn( tree, "Timezone", SWT.CENTER, 65 );
    createColumn( tree, "Offset", SWT.CENTER, 65 );
    tree.setLinesVisible( true );
    tree.setHeaderVisible( true );
    CheckboxTreeViewer viewer = new CheckboxTreeViewer( tree );
    viewer.addCheckStateListener( new TreeCheckStateListener() );
    viewer.setContentProvider( new ExampleContentProvider() );
    viewer.setLabelProvider( new ExampleLabelProvider( composite.getDisplay(), 
                                                       TABLE_STYLE ) );
    viewer.setInput( createDummyModel() );
    viewer.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler( viewer ) );
    addDNDSupport( viewer );
    addCellEditor( viewer );
    return viewer;
  }

  private TreeColumn createColumn( final Tree parent,
                                   final String name,
                                   final int style,
                                   final int width)
  {
    TreeColumn column = new TreeColumn( parent, style );
    column.setText( name );
    column.setWidth( width );
    column.setMoveable( true );
    column.setResizable( true );
    return column;
  }
  
  private FillLayout newFillLayout() {
    FillLayout result = new FillLayout();
    result.marginHeight = margin;
    result.marginWidth = margin;
    return result;
  }

  /////////////////////////////
  // Selection/Add/Remove items
  
  private final class TreeFocusGainedHandler extends FocusAdapter {

    private final TreeViewer viewer;

    private TreeFocusGainedHandler( final TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public void focusGained( final FocusEvent event ) {
      currentTree = viewer;
      for( int i = 0; i < allTrees.length; i++ ) {
        if( allTrees[ i ] != viewer ) {
          allTrees[ i ].getTree().deselectAll();
        }
      }
    }
  }

  private final class TreeCheckStateListener implements ICheckStateListener {

    public void checkStateChanged( final CheckStateChangedEvent event ) {
      TreeObject treeObject = ( TreeObject )event.getElement();
      boolean checked = event.getChecked();
      setChildrenChecked( checked, treeObject );
      handleParent( treeObject );
    }

    private void handleParent( final TreeObject treeObject ) {
      CheckboxTreeViewer viewer = ( CheckboxTreeViewer )currentTree;
      TreeObject parent = treeObject.getParent();
      boolean parentChecked = true;
      if( parent != null ) {
        TreeObject[] children = parent.getChildren();
        for( int i = 0; i < children.length; i++ ) {
          TreeObject child = children[ i ];
          if( !viewer.getChecked( child ) ) {
            parentChecked = false;
            break;
          }
        }
        viewer.setChecked( parent, parentChecked );
        handleParent( parent );
      }
    }

    private void setChildrenChecked( final boolean checked,
                                     final TreeObject parent )
    {
      CheckboxTreeViewer viewer = ( CheckboxTreeViewer )currentTree;
      TreeObject[] children = parent.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        TreeObject treeObject = children[ i ];
        viewer.setChecked( treeObject, checked );
        if( treeObject.hasChildren() ) {
          setChildrenChecked( checked, treeObject );
        }
      }
    }
  }
  
  private final class RemoveButtonSelectionHandler extends SelectionAdapter {

    public void widgetSelected( final SelectionEvent event ) {
      if( currentTree != null && !currentTree.getSelection().isEmpty() ) {
        ITreeSelection sel = ( ITreeSelection )currentTree.getSelection();
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
            currentTree.setSelection( new StructuredSelection( newSel ) );
          }
        }
        currentTree.refresh();
        currentTree.getTree().forceFocus();
      }
    }
  }

  private final class NewButtonSelectionHandler extends SelectionAdapter {

    public void widgetSelected( final SelectionEvent event ) {
      if( currentTree != null && !currentTree.getSelection().isEmpty() ) {
        ITreeSelection sel = ( ITreeSelection )currentTree.getSelection();
        TreeObject obj = ( TreeObject )sel.getFirstElement();
        newItem++;
        TreeObject newObject = new City( "New Item " + newItem , "", 0 );
        obj.addChild( newObject );
        currentTree.expandToLevel( obj, 1 );
        currentTree.refresh();
        currentTree.setSelection( new StructuredSelection( newObject ) );
        currentTree.getTree().forceFocus();
      }
    }
  }

  //////////////
  // Drag & Drop

  private void addDNDSupport( final TreeViewer viewer ) {
    int operations = DND.DROP_MOVE;
    Transfer[] types = new Transfer[] { TreeObjectTransfer.getInstance() };
    viewer.addDragSupport( operations, types, new TreeDragListener( viewer ) );
    viewer.addDropSupport( operations, types, new TreeDropListener( viewer ) );
  }
  
  private class TreeDragListener extends DragSourceAdapter {
    
    TreeViewer viewer;
    
    TreeDragListener( final TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public void dragStart( DragSourceEvent event ) {
      draggedObject = getTreeObject( viewer, event.x, event.y );
    }

    public void dragFinished( DragSourceEvent event ) {
      draggedObject = null; 
      viewer.refresh();
    }
    
  }
  
  private class TreeDropListener extends DropTargetAdapter {

    private TreeViewer viewer;

    TreeDropListener( final TreeViewer viewer ) {
      this.viewer = viewer;
    }
    
    public void dragEnter( final DropTargetEvent event ){
      event.feedback = DND.FEEDBACK_EXPAND 
                       | DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
    }


    public void drop( final DropTargetEvent event ) {
      TreeObject targetObject = getTreeObject( viewer, event.x, event.y );
      if( isValidDrop( draggedObject, targetObject ) ) {
        draggedObject.getParent().removeChild( draggedObject );
        targetObject.addChild( draggedObject );
      }
      viewer.refresh();
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

    protected int[] getTypeIds() {
      return new int[]{ TYPE_ID };
    }

    protected String[] getTypeNames() {
      return new String[]{ TYPE_NAME };
    }

    public TransferData[] getSupportedTypes() {
      int[] types = getTypeIds();
      TransferData[] data = new TransferData[ types.length ];
      for( int i = 0; i < types.length; i++ ) {
        data[ i ] = new TransferData();
        data[ i ].type = types[ i ];
      }
      return data;
    }

    public boolean isSupportedType( final TransferData transferData ) {
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

    public void javaToNative( final Object object, 
                              final TransferData transferData ) {
      transferData.data = object;
    }

    public Object nativeToJava( final TransferData transferData ) {
      return transferData.data;
    }
    
  }
  
  private boolean isValidDrop( final TreeObject draggedObject,
                               final TreeObject targetObject )
  {
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

  private static TreeObject getTreeObject( final TreeViewer viewer, 
                                           final int x, 
                                           final int y ) 
  {
    TreeObject result = null;
    Point point = viewer.getTree().toControl( x, y );
    TreeItem item = viewer.getTree().getItem( point );
    if( item != null ) {
      result = ( TreeObject )item.getData();
    }
    return result;
  }

  //////////////
  // Cell-editor

  private void addCellEditor( final TreeViewer viewer ) {
    CellEditor[] editors = new CellEditor[] { 
      new TextCellEditor( viewer.getTree() ),
      new TextCellEditor( viewer.getTree() ),
      new TextCellEditor( viewer.getTree() )
    };
    viewer.setCellEditors( editors );
    viewer.setCellModifier( new CellModifier( viewer ) );
    viewer.setColumnProperties( new String[] { "Name", 
      "Timezone", "Offset" } );
    ColumnViewerEditorActivationStrategy actSupport 
      = new CellEditorActivationStrategy( viewer );
    TreeViewerEditor.create( viewer, actSupport, EDITOR_ACTIVATE );
  }
  
  private final class CellModifier implements ICellModifier {

    private final TreeViewer viewer;

    private CellModifier( final TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public boolean canModify( final Object element, final String property ) {
      return element instanceof City || property.equals( "Name" );
    }

    public Object getValue( final Object element, final String property ) {
      String result = "";
      TreeObject obj = ( TreeObject )element;
      if( property.equals( "Name" ) ) {
        result = obj.getName();
      } else {
        City city = ( City )obj;
        if( property.equals( "Timezone" ) ) {
          result = city.getTimeZone();
        } else if( property.equals( "Offset" ) ) {
          result = String.valueOf( city.getOffset() );
        } else {
          System.err.println( "Unkown property " + property );
        }
      }
      return result; 
    }

    public void modify( final Object element, 
                        final String property, 
                        final Object value ) {
      TreeObject obj;
      if( element instanceof Item ) {
        obj = ( TreeObject )( ( Item ) element ).getData();
      } else {
        obj = ( TreeObject )element;
      }
      String str = ( String )value;
      if( property.equals( "Name" ) ) {
        obj.setName( str );
      } else {
        City city = ( City )obj;
        if( property.equals( "Timezone" ) ) {
          city.setTimeZone( str );
        } else if( property.equals( "Offset" ) ) {
          try{ 
            city.setOffset( Integer.parseInt( str ) );
          }catch ( NumberFormatException e) {
          }
        } else {
          System.err.println( "Unkown property " + property );
        }
      }
      viewer.update( obj, new String[]{ property } );
    }
  }
  
  private final class CellEditorActivationStrategy
    extends ColumnViewerEditorActivationStrategy
  {

    private CellEditorActivationStrategy( final ColumnViewer viewer ) {
      super( viewer );
    }
  
    protected boolean isEditorActivationEvent( 
                          final ColumnViewerEditorActivationEvent event )
    {
      return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
             || event.eventType 
               == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
             || event.eventType 
               == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
    }

  }

  ////////
  // Model

  private static class TreeObject {
    private TreeObject parent = null;
    private final List children;
    public String name;

    public TreeObject( final String name ) {
      setName( name );
      children = new ArrayList();
    }
    
    public void setParent( final TreeObject parent ) {
      this.parent = parent;
    }
    
    public TreeObject getParent() {
      return parent;
    }
  
    public void setName( final String name ) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
    
    public void addChild( final TreeObject child ) {
      children.add( child );
      child.setParent( this );
    }
  
    public void removeChild( final TreeObject child ) {
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

    public String toString() {
      return name;
    }
  }

  private static class City extends TreeObject {
    public String tz;
    public int offset;

    public City( final String name, final String tz, final int offset ) {
      super( name );
      setTimeZone( tz );
      setOffset( offset );
    }
    
    private void setOffset( int offset ) {
      this.offset = offset;
    }

    public void setTimeZone( final String tz ) {
      this.tz = tz;
    }
    
    private int getOffset() {
      return this.offset;
    }
    
    public String getTimeZone() {
      return this.tz;
    }
    
  }

  private static TreeObject createDummyModel() {
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
  
  ///////////
  // Provider

  private static final class ExampleLabelProvider extends CellLabelProvider {
    
    private Image image1 = null;
    private Image image2 = null;
    private final Font font1;
    private final Font font2;
    private final Font font3;
    private final Color color2;
    private final Color color3;
    private int style;
    
    ExampleLabelProvider( final Display device, final int style ) {
      FontData fontData1 = new FontData( "Times New Roman", 13, SWT.None );
      FontData fontData2 = new FontData( "Arial", 14, SWT.ITALIC );
      FontData fontData3 = new FontData( "Arial", 12, SWT.BOLD );
      font1 = new Font( device, fontData1 );
      font2 = new Font( device, fontData2 );
      font3 = new Font( device, fontData3 );
      color2 = Graphics.getColor( 239, 41, 41 );
      color3 = Graphics.getColor( 252, 175, 62 );
      ClassLoader classLoader = getClass().getClassLoader();
      if( style == MODERN_STYLE ) {
        image1 = Graphics.getImage( ICON_EARTH, classLoader );
        image2 = Graphics.getImage( ICON_GREENDOT, classLoader );
      } else {
        image1 = Graphics.getImage( ICON_WORLD, classLoader );
        image2 = Graphics.getImage( ICON_GREENDOT, classLoader );
      }
      this.style = style;
    }

    public void update( final ViewerCell cell ) {
      TreeObject object = ( TreeObject )cell.getElement();
      int columnIndex = cell.getColumnIndex();
      switch( columnIndex ) {
        case 0:
          cell.setText( object.name );
          if( style == MODERN_STYLE ) {
            if( object instanceof City ) {
              cell.setFont( font1 );
            } else {
              cell.setFont( font2 );
            }
          }
          cell.setImage( object instanceof City ? image2 : image1 );
        break;
        case 1:
          if( object instanceof City ) {
            City city = ( City )object;
            cell.setText( city.tz );
            if( style == TABLE_STYLE ) {
              cell.setForeground( color2 );
            }
          }
        break;
        case 2:
          if( object instanceof City ) {
            if( style == TABLE_STYLE ) {
              cell.setForeground( color3 );
              cell.setFont( font3 );
            }
            City city = ( City )object;
            cell.setText(   "UTC "
                          + ( city.offset >= 0 ? "-" : "" )
                          + String.valueOf( city.offset ) );
          }
        break;
      }
    }
  }

  private static class ExampleContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {
  
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
    }
  
    public void dispose() {
    }
  
    public Object[] getElements( final Object parent ) {
      return getChildren( parent );
    }
  
    public Object getParent( final Object child ) {
      if( child instanceof City ) {
        return ( ( City )child ).getParent();
      }
      return null;
    }
  
    public Object[] getChildren( final Object parent ) {
      if( parent instanceof TreeObject ) {
        return ( ( TreeObject )parent ).getChildren();
      }
      return new Object[ 0 ];
    }
  
    public boolean hasChildren( final Object parent ) {
      if( parent instanceof TreeObject ) {
        return ( ( TreeObject )parent ).hasChildren();
      }
      return false;
    }
  }
}
