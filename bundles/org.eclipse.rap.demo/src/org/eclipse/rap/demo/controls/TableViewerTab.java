/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class TableViewerTab extends ExampleTab {

  private static final int ADD_ITEMS = 300;

  private static final int FIRST_NAME = 0;
  private static final int LAST_NAME = 1;
  private static final int AGE = 2;
  private static final int EDITABLE = 3;

  private static final class Person {
    String firstName;
    String lastName;
    int age;
    boolean editable;

    public Person( final String firstName,
                   final String lastName,
                   final int age,
                   final boolean editable )
    {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
      this.editable = editable;
    }

    public String toString() {
      return firstName + " " + lastName + " " + age;
    }
  }

  private static final class PersonContentProvider
    implements IStructuredContentProvider
  {
    Object[] elements;
    public Object[] getElements( final Object inputElement ) {
      return elements;
    }
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
      if( newInput == null ) {
        elements = new Object[ 0 ];
      } else {
        java.util.List personList = ( java.util.List )newInput;
        elements = personList.toArray();
      }
    }
    public void dispose() {
      // do nothing
    }
  }

  private static final class LazyPersonContentProvider
    implements ILazyContentProvider
  {
    private TableViewer tableViewer;
    private List elements;
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput )
    {
      tableViewer = ( TableViewer )viewer;
      elements = ( List )newInput;
    }
    public void updateElement( final int index ) {
      tableViewer.replace( elements.get( index ), index );
    }
    public void dispose() {
      // do nothing
    }
  }

  private static final class PersonLabelProvider
    extends CellLabelProvider
  {
    public void update( final ViewerCell cell ) {
      Person person = ( Person )cell.getElement();
      int columnIndex = cell.getColumnIndex();
      switch( columnIndex ) {
        case FIRST_NAME:
          cell.setText( person.firstName );
          break;
        case LAST_NAME:
          cell.setText( person.lastName );
          break;
        case AGE:
          cell.setText( String.valueOf( person.age ) );
          break;
        case EDITABLE:
          cell.setText( person.editable ? "yes" : "no" );
          break;
      }
    }
  }

  private static final class PersonComparator
    extends ViewerComparator
    implements Comparator
  {
    private final boolean ascending;
    private final int property;
    public PersonComparator( final int property, final boolean ascending ) {
      this.property = property;
      this.ascending = ascending;
    }
    public int compare( final Viewer viewer,
                        final Object object1,
                        final Object object2 )
    {
      return compare( object1, object2 );
    }
    public boolean isSorterProperty( final Object elem, final String property )
    {
      return true;
    }
    public int compare( final Object object1, final Object object2 ) {
      Person person1 = ( Person )object1;
      Person person2 = ( Person )object2;
      int result = 0;
      if( property == FIRST_NAME ) {
        result = person1.firstName.compareTo( person2.firstName );
      } else if( property == LAST_NAME ) {
        result = person1.lastName.compareTo( person2.lastName );
      } else if( property == AGE ) {
        result = person1.age - person2.age;
      } else if( property == EDITABLE ) {
        if( person1.editable && !person2.editable ) {
          result = -1;
        } else if( !person1.editable && person2.editable ) {
          result = +1;
        }
      }
      if( !ascending ) {
        result = result * -1;
      }
      return result;
    }
  }

  private static final class PersonFilter extends ViewerFilter {
    private String text;
    public void setText( final String string ) {
      this.text = string;
    }
    public boolean select( final Viewer viewer,
                           final Object parentElement,
                           final Object element )
    {
      boolean result = true;
      Person person = ( Person )element;
      if( text != null && text.length() > 0 ) {
        String personText = person.toString().toLowerCase();
        result = personText.indexOf( text.toLowerCase() ) != -1;
      }
      return result;
    }
    public boolean isFilterProperty( final Object element, final String prop ) {
      return true;
    }
  }

  private static final class EditorActivationStrategy
    extends ColumnViewerEditorActivationStrategy
  {

    private EditorActivationStrategy( final ColumnViewer viewer ) {
      super( viewer );
      setEnableEditorActivationWithKeyboard( true );
    }

    protected boolean isEditorActivationEvent(
      final ColumnViewerEditorActivationEvent event )
    {
      boolean result;
      if( event.character == '\r' ) {
        result = true;
      } else {
        result = super.isEditorActivationEvent( event );
      }
      return result;
    }
  }

  private static final class NameEditingSupport extends EditingSupport {
    private final CellEditor editor;
    private final int property;
    public NameEditingSupport( final TableViewer viewer, final int property ) {
      super( viewer );
      this.property = property;
      editor = new TextCellEditor( viewer.getTable() );
    }

    protected boolean canEdit( final Object element ) {
      Person person = ( Person )element;
      return person.editable;
    }

    protected CellEditor getCellEditor( final Object element ) {
      return editor;
    }

    protected Object getValue( final Object element ) {
      Person person = ( Person )element;
      String result;
      if( FIRST_NAME == property ) {
        result = person.firstName;
      } else {
        result = person.lastName;
      }
      return result;
    }

    protected void setValue( final Object element, final Object value ) {
      Person person = ( Person )element;
      if( property == FIRST_NAME ) {
        person.firstName = ( String )value;
      } else {
        person.lastName = ( String )value;
      }
      getViewer().update( element, null );
    }
  }

  private static final class AgeEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public AgeEditingSupport( final TableViewer viewer ) {
      super( viewer );
      editor = new TextCellEditor( viewer.getTable() );
      editor.setValidator( new ICellEditorValidator() {
        public String isValid( final Object value ) {
          String result = null;
          try {
            Integer.parseInt( ( String )value );
          } catch( NumberFormatException e ) {
            String text = "''{0}'' is not a valid age.";
            result = MessageFormat.format( text, new Object[] { value } );
          }
          return result;
        }
      });
    }

    protected boolean canEdit( final Object element ) {
      Person person = ( Person )element;
      return person.editable;
    }

    protected CellEditor getCellEditor( final Object element ) {
      return editor;
    }

    protected Object getValue( final Object element ) {
      Person person = ( Person )element;
      return String.valueOf( person.age );
    }

    protected void setValue( final Object element, final Object value ) {
      if( value != null ) {
        Person person = ( Person )element;
        person.age = Integer.parseInt( ( String )value );
        getViewer().update( element, null );
      }
    }
  }

  private static final class EditableEditingSupport extends EditingSupport {
    private final CheckboxCellEditor editor;
    public EditableEditingSupport( final ColumnViewer viewer ) {
      super( viewer );
      editor = new CheckboxCellEditor();
    }

    protected boolean canEdit( final Object element ) {
      return true;
    }

    protected CellEditor getCellEditor( final Object element ) {
      return editor;
    }

    protected Object getValue( final Object element ) {
      Person person = ( Person )element;
      return Boolean.valueOf( person.editable );
    }

    protected void setValue( final Object element, final Object value ) {
      Person person = ( Person )element;
      person.editable = ( ( Boolean )value ).booleanValue();
      getViewer().update( element, null );
    }

  }

  private TableViewer viewer;
  private TableViewerColumn firstNameColumn;
  private TableViewerColumn lastNameColumn;
  private TableViewerColumn ageColumn;
  private TableViewerColumn editableColumn;
  private Label lblSelection;
  private Button btnCreateCellEditor;
  private final PersonFilter viewerFilter;
  private final PersonLabelProvider labelProvider;
  private final java.util.List persons = new ArrayList();

  public TableViewerTab( final CTabFolder topFolder ) {
    super( topFolder, "TableViewer" );
    viewerFilter = new PersonFilter();
    labelProvider = new PersonLabelProvider();
  }

  private void initPersons() {
    persons.clear();
    persons.add( new Person( "Rögn\"íy&", "Hövl&lt;_'><'&amp;", 1, false ) );
    persons.add( new Person( "Paul", "Panther", 1, false ) );
    persons.add( new Person( "Karl", "Marx", 2, false ) );
    persons.add( new Person( "Sofia", "Loren", 3, true ) );
    persons.add( new Person( "King", "Cool", 4, false ) );
    persons.add( new Person( "Albert", "Einstein", 5, true ) );
    persons.add( new Person( "Donald", "Duck", 6, false ) );
    persons.add( new Person( "Mickey", "Mouse", 7, true ) );
    persons.add( new Person( "Asterix", "", 8, false ) );
    persons.add( new Person( "Nero", "", 9, false ) );
    persons.add( new Person( "Elvis", "Presley", 10, true ) );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createAddItemsButton();
    createSelectYoungestPersonButton();
    createRemoveButton();
    createCellEditorButton();
    lblSelection = new Label( styleComp, SWT.NONE );
  }

  protected void createExampleControls( final Composite parent ) {
    if( btnCreateCellEditor != null && !btnCreateCellEditor.isDisposed() ) {
      btnCreateCellEditor.setEnabled( true );
    }
    parent.setLayout( new GridLayout( 2, false ) );
    GridDataFactory gridDataFactory;
    Label lblFilter = new Label( parent, SWT.NONE );
    lblFilter.setText( "Filter" );
    lblFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    Text txtFilter = new Text( parent, SWT.BORDER );
    txtFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    gridDataFactory = GridDataFactory.swtDefaults();
    gridDataFactory.grab( true, false );
    gridDataFactory.align( SWT.FILL, SWT.CENTER );
    gridDataFactory.applyTo( txtFilter );
    txtFilter.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        Text text = ( Text )event.widget;
        viewerFilter.setText( text.getText() );
        viewer.refresh();
      }
    } );
    if( viewer != null && !viewer.getControl().isDisposed() ) {
      viewer.getControl().dispose();
    }
    initPersons();
    viewer = new TableViewer( parent, getStyle() );
    viewer.setUseHashlookup( true );
    if( ( getStyle() & SWT.VIRTUAL ) == 0 ) {
      viewer.setContentProvider( new PersonContentProvider() );
    } else {
      viewer.setContentProvider( new LazyPersonContentProvider() );
    }
    viewer.setLabelProvider( new PersonLabelProvider() );
    firstNameColumn = createFirstNameColumn();
    lastNameColumn = createLastNameColumn();
    ageColumn = createAgeColumn();
    editableColumn = createEditableColumn();
    viewer.setInput( persons );
    viewer.setItemCount( persons.size() );
    viewer.addFilter( viewerFilter );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( final SelectionChangedEvent event ) {
        lblSelection.setText( "Selection: " + event.getSelection() );
        lblSelection.getParent().layout( new Control[] { lblSelection } );
      }
    } );
    viewer.getTable().setHeaderVisible( true );
    gridDataFactory = GridDataFactory.swtDefaults();
    gridDataFactory.grab( true, true );
    gridDataFactory.align( SWT.FILL, SWT.FILL );
    gridDataFactory.span( 2, SWT.DEFAULT );
    gridDataFactory.applyTo( viewer.getTable() );
    registerControl( viewer.getControl() );
  }

  private TableViewerColumn createFirstNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( labelProvider );
    TableColumn column = result.getColumn();
    column.setText( "First Name" );
    column.setWidth( 170 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, FIRST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createLastNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( labelProvider );
    TableColumn column = result.getColumn();
    column.setText( "Last Name" );
    column.setWidth( 100 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, LAST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createAgeColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( labelProvider );
    TableColumn column = result.getColumn();
    column.setText( "Age" );
    column.setWidth( 80 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, AGE, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createEditableColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( labelProvider );
    TableColumn column = result.getColumn();
    column.setText( "Editable" );
    column.setWidth( 50 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, EDITABLE, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private void addPerson() {
    int maxAge = 0;
    for( int i = 0; i < persons.size(); i++ ) {
      Person person = ( Person )persons.get( i );
      if( person.age > maxAge ) {
        maxAge = person.age;
      }
    }
    persons.add( new Person( "new", "person", maxAge + 1, false ) );
  }

  private void createAddItemsButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Add " + ADD_ITEMS + " Items" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          addPerson();
        }
        getViewer().setInput( persons );
        if( ( getStyle() & SWT.VIRTUAL ) != 0 ) {
          getViewer().setItemCount( persons.size() );
        }
      }
    } );
  }

  private void createSelectYoungestPersonButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Select youngest Person" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Person youngestPerson = null;
        int minAge = Integer.MAX_VALUE;
        for( int i = 0; i < persons.size(); i++ ) {
          Person person = ( Person )persons.get( i );
          if( person.age < minAge ) {
            minAge = person.age;
            youngestPerson = person;
          }
        }
        getViewer().setSelection( new StructuredSelection( youngestPerson ) );
        getViewer().reveal( youngestPerson );
      }
    } );
  }

  private void createRemoveButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Remove selected rows" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        IStructuredSelection selection
          = ( IStructuredSelection )getViewer().getSelection();
        Iterator iter = selection.iterator();
        while( iter.hasNext() ) {
          Person person = ( Person )iter.next();
          persons.remove( person );
        }
        getViewer().getTable().setTopIndex( 0 );
        if( ( getViewer().getTable().getStyle() & SWT.VIRTUAL ) != 0 ) {
          getViewer().setItemCount( persons.size() );
        }
        getViewer().setInput( persons );
      }
    } );
  }

  private void createCellEditorButton() {
    btnCreateCellEditor = new Button( styleComp, SWT.PUSH );
    btnCreateCellEditor.setText( "Create Cell Editor" );
    btnCreateCellEditor.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createCellEditor();
        btnCreateCellEditor.setEnabled( false );
      }
    } );
  }

  private void createCellEditor() {
    EditingSupport editingSupport;
    editingSupport = new NameEditingSupport( viewer, FIRST_NAME );
    firstNameColumn.setEditingSupport( editingSupport );
    editingSupport = new NameEditingSupport( viewer, LAST_NAME );
    lastNameColumn.setEditingSupport( editingSupport );
    editingSupport = new AgeEditingSupport( viewer );
    ageColumn.setEditingSupport( editingSupport );
    editingSupport = new EditableEditingSupport( viewer );
    editableColumn.setEditingSupport( editingSupport );
    ColumnViewerEditorActivationStrategy activationStrategy
      = new EditorActivationStrategy( viewer );
    FocusCellOwnerDrawHighlighter highlighter
      = new FocusCellOwnerDrawHighlighter( viewer );
    TableViewerFocusCellManager focusManager
      = new TableViewerFocusCellManager( viewer, highlighter );
    int feature
      = ColumnViewerEditor.TABBING_HORIZONTAL
      | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR;
    TableViewerEditor.create( viewer,
                              focusManager,
                              activationStrategy,
                              feature );
  }

  private static int updateSortDirection( final TableColumn column ) {
    Table table = column.getParent();
    if( column == table.getSortColumn() ) {
      if( table.getSortDirection() == SWT.UP ) {
        table.setSortDirection( SWT.DOWN );
      } else {
        table.setSortDirection( SWT.UP );
      }
    } else {
      table.setSortColumn( column );
      table.setSortDirection( SWT.DOWN );
    }
    return table.getSortDirection();
  }

  private static void sort( final TableViewer viewer,
                            final int property,
                            final boolean ascending )
  {
    if( ( viewer.getControl().getStyle() & SWT.VIRTUAL ) != 0 ) {
      List input = ( List )viewer.getInput();
      Collections.sort( input, new PersonComparator( property, ascending ) );
      viewer.refresh();
    } else {
      viewer.setComparator( new PersonComparator( property, ascending ) );
    }
  }

  private TableViewer getViewer() {
    return viewer;
  }
}
