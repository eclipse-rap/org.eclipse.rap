/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
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
  private static final int MARGIN = 8;
  private static final int FOOLTER_HEIGHT = 53;

  private static final int EDITOR_ACTIVATE
    =   ColumnViewerEditor.TABBING_HORIZONTAL
      | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
      | ColumnViewerEditor.TABBING_VERTICAL
      | ColumnViewerEditor.KEYBOARD_ACTIVATION;

  private final static int MODERN_STYLE = 0;
  private final static int TABLE_STYLE = 1;

  private TreeViewer currentViewer;
  private TreeViewer modernTree;
  private TreeViewer tableTree;
  private int newItem;

  /////////////////
  // create widgets

  public void createControl( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    createTrees( parent );
    createFooter( parent );
    Tree tree = modernTree.getTree();
    tree.forceFocus();
    tree.select( tree.getItem( 0 ) );
  }

  private void createTrees( final Composite parent ) {
    Composite topLeft = createComposite( parent, SWT.TOP | SWT.LEFT  );
    Composite topRight = createComposite( parent, SWT.TOP | SWT.RIGHT );
    modernTree = createModernTree( topLeft );
    tableTree = createTableTree( topRight );
  }

  private Composite createComposite( final Composite parent,
                                     final int position )
  {
    Composite result = new Composite( parent, SWT.NONE );
    FormAttachment zero = new FormAttachment( 0 );
    FormAttachment hFull = new FormAttachment( 100 );
    FormAttachment vFull = new FormAttachment( 100, -FOOLTER_HEIGHT );
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
    formData.top = new FormAttachment( 100, -FOOLTER_HEIGHT );
    formData.left = new FormAttachment( 0, MARGIN );
    formData.right = new FormAttachment( 100, -MARGIN );
    formData.bottom = new FormAttachment( 100, -MARGIN );
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
    Tree tree = new Tree( composite, SWT.BORDER | SWT.MULTI );
    tree.setData( WidgetUtil.CUSTOM_VARIANT, "modern" );
    composite.setLayout( newFillLayout() );
    TreeViewer result = new TreeViewer( tree );
    result.setContentProvider( new TreeContentProvider() );
    TreeLabelProvider labelProvider
      = new TreeLabelProvider( composite.getDisplay(), MODERN_STYLE );
    result.setLabelProvider( labelProvider );
    result.setInput( createModel() );
    result.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler() );
    addDNDSupport( result );
    addCellEditor( result );
    addToolTipSupport( result );
    return result;
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
    CheckboxTreeViewer result = new CheckboxTreeViewer( tree );
    result.addCheckStateListener( new TreeCheckStateListener( result ) );
    result.setContentProvider( new TreeContentProvider() );
    TreeLabelProvider labelProvider
      = new TreeLabelProvider( composite.getDisplay(), TABLE_STYLE );
    result.setLabelProvider( labelProvider );
    result.setInput( createModel() );
    result.expandAll();
    tree.addFocusListener( new TreeFocusGainedHandler() );
    addDNDSupport( result );
    addCellEditor( result );
    addToolTipSupport( result );
    return result;
  }

  private static TreeColumn createColumn( final Tree parent,
                                          final String name,
                                          final int style,
                                          final int width )
  {
    TreeColumn result = new TreeColumn( parent, style );
    result.setText( name );
    result.setWidth( width );
    result.setMoveable( true );
    result.setResizable( true );
    return result;
  }

  private static FillLayout newFillLayout() {
    FillLayout result = new FillLayout();
    result.marginHeight = MARGIN;
    result.marginWidth = MARGIN;
    return result;
  }

  //////////////
  // Drag & Drop

  private void addDNDSupport( final TreeViewer viewer ) {
    Transfer[] types = new Transfer[] { TreeObjectTransfer.getInstance() };
    TreeDragListener dragListener = new TreeDragListener( viewer );
    viewer.addDragSupport( DND.DROP_MOVE, types, dragListener );
    TreeDropListener dropListener = new TreeDropListener();
    viewer.addDropSupport( DND.DROP_MOVE, types, dropListener );
  }

  //////////////
  // Cell-editor

  private static void addCellEditor( final TreeViewer viewer ) {
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

  private static void addToolTipSupport( final TreeViewer viewer ) {
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
    public void focusGained( final FocusEvent event ) {
      currentViewer = null;
      Tree currentTree = ( Tree )event.widget;
      if( modernTree.getTree() == currentTree ) {
        currentViewer = modernTree;
      } else if( tableTree.getTree() == currentTree ) {
        currentViewer = tableTree;
      }
    }
  }

  private static class TreeCheckStateListener implements ICheckStateListener {
    private final CheckboxTreeViewer viewer;

    TreeCheckStateListener( final CheckboxTreeViewer viewer ) {
      this.viewer = viewer;
    }

    public void checkStateChanged( final CheckStateChangedEvent event ) {
      TreeObject treeObject = ( TreeObject )event.getElement();
      boolean checked = event.getChecked();
      updateChildren( checked, treeObject );
      updateParent( treeObject );
    }

    private void updateParent( final TreeObject treeObject ) {
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

    private void updateChildren( final boolean checked,
                                 final TreeObject parent )
    {
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

    public void widgetSelected( final SelectionEvent event ) {
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
    public void widgetSelected( final SelectionEvent event ) {
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
    
    TreeDragListener( final TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public void dragStart( final DragSourceEvent event ) {
      dragData = getTreeObject( event.x, event.y );
    }
    
    public void dragSetData( DragSourceEvent event ) {
      event.data = dragData;
    }

    public void dragFinished( final DragSourceEvent event ) {
      viewer.refresh();
    }
    
    private TreeObject getTreeObject( final int x, final int y ) {
      TreeObject result = null;
      Point point = viewer.getTree().toControl( x, y );
      ViewerCell cell = viewer.getCell( point );
      if( cell != null ) {
        result = ( TreeObject )cell.getElement();
      }
      return result;
    }
  }

  private static class TreeDropListener extends DropTargetAdapter {
    public void dragEnter( final DropTargetEvent event ){
      event.feedback
        = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
    }

    public void drop( final DropTargetEvent event ) {
      if( event.data == null ) {
        event.detail = DND.DROP_NONE;
      } else {
        TreeObject draggedObject = ( TreeObject )event.data;
        TreeObject targetObject = ( TreeObject )event.item.getData();
        if( isValidDrop( draggedObject, targetObject ) ) {
          draggedObject.getParent().removeChild( draggedObject );
          targetObject.addChild( draggedObject );
        }
      }
    }

    private static boolean isValidDrop( final TreeObject draggedObject, 
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
      TransferData[] result = new TransferData[ types.length ];
      for( int i = 0; i < types.length; i++ ) {
        result[ i ] = new TransferData();
        result[ i ].type = types[ i ];
      }
      return result;
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

  private static final class CellModifier implements ICellModifier {
    private final TreeViewer viewer;

    CellModifier( final TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public boolean canModify( final Object element, final String property ) {
      return element instanceof City || property.equals( "Name" );
    }

    public Object getValue( final Object element, final String property ) {
      String result = "";
      TreeObject treeObject = ( TreeObject )element;
      if( property.equals( "Name" ) ) {
        result = treeObject.getName();
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

    public void modify( final Object element,
                        final String property,
                        final Object value )
    {
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

    CellEditorActivationStrategy( final ColumnViewer viewer ) {
      super( viewer );
    }

    protected boolean isEditorActivationEvent(
      final ColumnViewerEditorActivationEvent event )
    {
      boolean isTraversal
        = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL;
      boolean isDoubleClick
        = event.eventType
          == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
      boolean isProgrammatic
        = event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
      return isTraversal || isDoubleClick || isProgrammatic;
    }
  }

  ////////
  // Model

  private static class TreeObject {
    private final List children;
    private String name;
    private TreeObject parent;

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
    private String timezone;
    private int offset;

    public City( final String name, final String timezone, final int offset ) {
      super( name );
      setTimeZone( timezone );
      setOffset( offset );
    }

    private void setOffset( final int offset ) {
      this.offset = offset;
    }

    public void setTimeZone( final String tz ) {
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

    TreeLabelProvider( final Device device, final int style ) {
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

    public void update( final ViewerCell cell ) {
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

    public String getToolTipText( final Object element ) {
      String result = "";
      if( element instanceof City ) {
        City city = ( City )element;
        String name = city.getName();
        String timeZone = city.getTimeZone();
        String utcOffset = getUTCOffset( city );
        result = name + " (" + timeZone + ", " + utcOffset + ")";
      }
      return result;
    }

    private void updateName( final ViewerCell cell,
                             final TreeObject treeObject ) {
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

    private void updateTimeZone( final ViewerCell cell,
                                 final TreeObject treeObject )
    {
      if( treeObject instanceof City ) {
        City city = ( City )treeObject;
        cell.setText( city.getTimeZone() );
        if( style == TABLE_STYLE ) {
          cell.setForeground( timezoneTextColor );
        }
      }
    }

    private void updateOffset( final ViewerCell cell,
                               final TreeObject treeObject )
    {
      if( treeObject instanceof City ) {
        if( style == TABLE_STYLE ) {
          cell.setForeground( offsetTextColor );
        }
        City city = ( City )treeObject;
        cell.setText( getUTCOffset( city ) );
      }
    }

    private Font createFont( final String name,
                             final int size,
                             final int style )
    {
      FontData fontData = new FontData( name, size, style );
      return new Font( device, fontData );
    }

    private Image createImage( final String name ) {
      ClassLoader classLoader = getClass().getClassLoader();
      return Graphics.getImage( name, classLoader );
    }

    private String getUTCOffset( final City city ) {
      String sign = city.getOffset() >= 0 ? "-" : "";
      return "UTC " + sign + String.valueOf( city.getOffset() );
    }
  }

  private static class TreeContentProvider implements ITreeContentProvider {

    public Object[] getElements( final Object parent ) {
      return getChildren( parent );
    }

    public Object getParent( final Object child ) {
      Object result = null;
      if( child instanceof City ) {
        result = ( ( City )child ).getParent();
      }
      return result;
    }

    public Object[] getChildren( final Object parent ) {
      Object[] result = new Object[ 0 ];
      if( parent instanceof TreeObject ) {
        result = ( ( TreeObject )parent ).getChildren();
      }
      return result;
    }

    public boolean hasChildren( final Object parent ) {
      boolean result = false;
      if( parent instanceof TreeObject ) {
        result = ( ( TreeObject )parent ).hasChildren();
      }
      return result;
    }

    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
    }

    public void dispose() {
    }
  }
}
