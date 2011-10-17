/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class TableViewerTab extends ExampleTab {

  private static final int ADD_ITEMS = 10000;

  private static final int COL_FIRST_NAME = 0;
  private static final int COL_LAST_NAME = 1;
  private static final int COL_AGE = 2;
  private static final int COL_MARRIED = 3;

  private TableViewer viewer;
  private TableViewerColumn firstNameColumn;
  private TableViewerColumn lastNameColumn;
  private TableViewerColumn ageColumn;
  private TableViewerColumn editableColumn;
  private Label lblSelection;
  private Button btnCreateCellEditor;
  private final PersonFilter viewerFilter;
  private final List<Person> persons = new ArrayList<Person>();

  public TableViewerTab( CTabFolder topFolder ) {
    super( topFolder, "TableViewer" );
    viewerFilter = new PersonFilter();
  }

  protected void createStyleControls( Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createAddItemsButton();
    createSelectYoungestPersonButton();
    createRemoveButton();
    createCellEditorButton();
    lblSelection = new Label( styleComp, SWT.WRAP );
  }

  protected void createExampleControls( Composite parent ) {
    if( btnCreateCellEditor != null && !btnCreateCellEditor.isDisposed() ) {
      btnCreateCellEditor.setEnabled( true );
    }
    parent.setLayout( new GridLayout( 2, false ) );
    Label lblFilter = new Label( parent, SWT.NONE );
    lblFilter.setText( "Filter" );
    lblFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    Text txtFilter = new Text( parent, SWT.BORDER );
    txtFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    txtFilter.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    txtFilter.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        viewerFilter.setText( text.getText() );
        viewer.refresh();
      }
    } );
    if( viewer != null && !viewer.getControl().isDisposed() ) {
      viewer.getControl().dispose();
    }
    initPersons();
    createViewer( parent );
    registerControl( viewer.getControl() );
  }

  private void initPersons() {
    persons.clear();
    persons.add( new Person( "Ada", "Lovelace", 1815, true ) );
    persons.add( new Person( "John", "von Neumann", 1903, true ) );
    persons.add( new Person( "Kurt", "Gödel", 1906, true ) );
    persons.add( new Person( "Grace Murray", "Hopper", 1906, true ) );
    persons.add( new Person( "Alan", "Turing", 1912, false ) );
    persons.add( new Person( "Claude", "Shannon", 1916, true ) );
    persons.add( new Person( "John", "Backus", 1924, true ) );
    persons.add( new Person( "Alan", "Kay", 1940, true ) );
    persons.add( new Person( "Dennis", "Ritchie", 1941, true ) );
    persons.add( new Person( "David", "Parnas", 1941, true ) );
    persons.add( new Person( "Larry", "Wall", 1954 , true ) );
    persons.add( new Person( "Linus", "Torvalds", 1969 , true ) );
  }

  private void createViewer( Composite parent ) {
    viewer = new TableViewer( parent, getStyle() );
    viewer.setContentProvider( createContentProvider() );
    firstNameColumn = createFirstNameColumn();
    lastNameColumn = createLastNameColumn();
    ageColumn = createAgeColumn();
    editableColumn = createEditableColumn();
    viewer.setInput( persons );
    viewer.setItemCount( persons.size() );
    viewer.addFilter( viewerFilter );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        lblSelection.setText( "Selection: " + event.getSelection() );
        lblSelection.getParent().layout( new Control[] { lblSelection } );
      }
    } );
    viewer.getTable().setHeaderVisible( true );
    ColumnViewerToolTipSupport.enableFor( viewer );
    GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true );
    tableData.horizontalSpan = 2;
    viewer.getTable().setLayoutData( tableData );
  }

  private IContentProvider createContentProvider() {
    IContentProvider result;
    if( ( getStyle() & SWT.VIRTUAL ) == 0 ) {
      result = new PersonContentProvider();
    } else {
      result = new LazyPersonContentProvider();
    }
    return result;
  }

  private TableViewerColumn createFirstNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_FIRST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "First Name" );
    column.setWidth( 170 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_FIRST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createLastNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_LAST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "Last Name" );
    column.setWidth( 120 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_LAST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createAgeColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_AGE ) );
    TableColumn column = result.getColumn();
    column.setText( "Born" );
    column.setWidth( 80 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_AGE, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createEditableColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_MARRIED ) );
    TableColumn column = result.getColumn();
    column.setText( "Married" );
    column.setWidth( 60 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_MARRIED, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private void createAddItemsButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Add " + ADD_ITEMS + " Items" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          persons.add( new Person( "new", "person", 11 + i, false ) );
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
      public void widgetSelected( SelectionEvent event ) {
        Person youngestPerson = null;
        int minAge = Integer.MAX_VALUE;
        for( int i = 0; i < persons.size(); i++ ) {
          Person person = persons.get( i );
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
      public void widgetSelected( SelectionEvent event ) {
        IStructuredSelection selection = ( IStructuredSelection )getViewer().getSelection();
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
      public void widgetSelected( SelectionEvent event ) {
        createCellEditor();
        btnCreateCellEditor.setEnabled( false );
      }
    } );
  }

  private void createCellEditor() {
    EditingSupport editingSupport;
    editingSupport = new FirstNameEditingSupport( viewer );
    firstNameColumn.setEditingSupport( editingSupport );
    editingSupport = new LastNameEditingSupport( viewer, getLastNames() );
    lastNameColumn.setEditingSupport( editingSupport );
    editingSupport = new AgeEditingSupport( viewer );
    ageColumn.setEditingSupport( editingSupport );
    editingSupport = new MarriedEditingSupport( viewer );
    editableColumn.setEditingSupport( editingSupport );
    ColumnViewerEditorActivationStrategy activationStrategy
      = new EditorActivationStrategy( viewer );
    FocusCellOwnerDrawHighlighter highlighter = new FocusCellOwnerDrawHighlighter( viewer );
    TableViewerFocusCellManager focusManager
      = new TableViewerFocusCellManager( viewer, highlighter );
    int feature = ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR;
    TableViewerEditor.create( viewer, focusManager, activationStrategy, feature );
  }

  private static int updateSortDirection( TableColumn column ) {
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

  @SuppressWarnings("unchecked")
  private static void sort( TableViewer viewer, int property, boolean ascending ) {
    if( ( viewer.getControl().getStyle() & SWT.VIRTUAL ) != 0 ) {
      List<Person> input = ( List<Person> )viewer.getInput();
      Collections.sort( input, new PersonComparator( property, ascending ) );
      viewer.refresh();
    } else {
      viewer.setComparator( new PersonComparator( property, ascending ) );
    }
  }

  private TableViewer getViewer() {
    return viewer;
  }

  private String[] getLastNames() {
    ArrayList<String> list = new ArrayList<String>();
    for( Person person : persons ) {
      if( person.lastName.length() > 0 ) {
        list.add( person.lastName );
      }
    }
    String[] result = new String[ list.size() ];
    list.toArray( result );
    return result;
  }

  private static final class Person {
    String firstName;
    String lastName;
    int age;
    boolean married;

    public Person( String firstName, String lastName, int age, boolean married ) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
      this.married = married;
    }

    public String toString() {
      return firstName + " " + lastName + " " + age + " " + married;
    }
  }

  private static final class PersonContentProvider implements IStructuredContentProvider {
    Object[] elements;
    public Object[] getElements( Object inputElement ) {
      return elements;
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
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

  private static final class LazyPersonContentProvider implements ILazyContentProvider {
    private TableViewer tableViewer;
    private List elements;

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
      tableViewer = ( TableViewer )viewer;
      elements = ( List )newInput;
    }
    public void updateElement( int index ) {
      tableViewer.replace( elements.get( index ), index );
    }
    public void dispose() {
      // do nothing
    }
  }

  private static final class PersonLabelProvider extends ColumnLabelProvider {
    private final int columnIndex;
    private final Color nameBackground;

    public PersonLabelProvider( int columnIndex ) {
      this.columnIndex = columnIndex;
      nameBackground = new Color( Display.getCurrent(), 248, 248, 248 );
    }

    public String getText( Object element ) {
      Person person = ( Person )element;
      String result = person.toString();
      switch( columnIndex ) {
        case COL_FIRST_NAME:
          result = person.firstName;
          break;
        case COL_LAST_NAME:
          result = person.lastName;
          break;
        case COL_AGE:
          result = String.valueOf( person.age );
          break;
        case COL_MARRIED:
          result = person.married ? "yes" : "no";
          break;
      }
      return result;
    }

    public Color getBackground( Object element ) {
      Color result = null;
      switch( columnIndex ) {
        case COL_FIRST_NAME:
        case COL_LAST_NAME:
          result = nameBackground;
          break;
      }
      return result;
    }

    public String getToolTipText( Object element ) {
      Person person = ( Person )element;
      String text = null;
      switch( columnIndex ) {
        case COL_FIRST_NAME:
          text = person.firstName;
          break;
        case COL_LAST_NAME:
          text = person.lastName;
          break;
        case COL_AGE:
          text = String.valueOf( person.age );
          break;
        case COL_MARRIED:
          text = person.married ? "yes" : "no";
          break;
      }
      return text;
    }
  }

  private static final class PersonComparator extends ViewerComparator
    implements Comparator<Person>
  {

    private final boolean ascending;
    private final int property;
    public PersonComparator( int property, boolean ascending ) {
      this.property = property;
      this.ascending = ascending;
    }

    public int compare( Viewer viewer, Object object1, Object object2 ) {
      return compare( ( Person )object1, ( Person )object2 );
    }

    public boolean isSorterProperty( Object elem, String property ) {
      return true;
    }

    public int compare( Person person1, Person person2 ) {
      int result = 0;
      if( property == COL_FIRST_NAME ) {
        result = person1.firstName.compareTo( person2.firstName );
      } else if( property == COL_LAST_NAME ) {
        result = person1.lastName.compareTo( person2.lastName );
      } else if( property == COL_AGE ) {
        result = person1.age - person2.age;
      } else if( property == COL_MARRIED ) {
        if( person1.married && !person2.married ) {
          result = -1;
        } else if( !person1.married && person2.married ) {
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
      text = string;
    }

    public boolean select( Viewer viewer, Object parentElement, Object element ) {
      boolean result = true;
      Person person = ( Person )element;
      if( text != null && text.length() > 0 ) {
        String personText = person.toString().toLowerCase();
        result = personText.indexOf( text.toLowerCase() ) != -1;
      }
      return result;
    }
    public boolean isFilterProperty( Object element, String prop ) {
      return true;
    }
  }

  private static final class EditorActivationStrategy extends ColumnViewerEditorActivationStrategy {

    private EditorActivationStrategy( ColumnViewer viewer ) {
      super( viewer );
      setEnableEditorActivationWithKeyboard( true );
    }

    protected boolean isEditorActivationEvent( ColumnViewerEditorActivationEvent event ) {
      boolean result;
      if( event.character == '\r' ) {
        result = true;
      } else {
        result = super.isEditorActivationEvent( event );
      }
      return result;
    }
  }

  private static final class FirstNameEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public FirstNameEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new TextCellEditor( viewer.getTable() );
    }

    protected boolean canEdit( Object element ) {
      return true;
    }

    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      String result;
      result = person.firstName;
      return result;
    }

    protected void setValue( Object element, Object value ) {
      Person person = ( Person )element;
      person.firstName = ( String )value;
      getViewer().update( element, null );
    }
  }

  private static final class LastNameEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public LastNameEditingSupport( TableViewer viewer, String[] suggestions ) {
      super( viewer );
      editor = new ComboBoxCellEditor( viewer.getTable(), suggestions, SWT.NONE );
    }

    protected boolean canEdit( Object element ) {
      return true;
    }

    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      CCombo ccombo = ( CCombo )editor.getControl();
      ccombo.setText( person.lastName );
      return new Integer( -2 );
    }

    protected void setValue( Object element, Object value ) {
      Person person = ( Person )element;
      CCombo ccombo = ( CCombo )editor.getControl();
      person.lastName = ccombo.getText();
      getViewer().update( element, null );
    }
  }

  private static final class AgeEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public AgeEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new TextCellEditor( viewer.getTable() );
      editor.setValidator( new ICellEditorValidator() {
        public String isValid( Object value ) {
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

    protected boolean canEdit( Object element ) {
      return true;
    }

    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      return String.valueOf( person.age );
    }

    protected void setValue( Object element, Object value ) {
      if( value != null ) {
        Person person = ( Person )element;
        person.age = Integer.parseInt( ( String )value );
        getViewer().update( element, null );
      }
    }
  }

  private static final class MarriedEditingSupport extends EditingSupport {
    private final RealCheckboxCellEditor editor;
    public MarriedEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new RealCheckboxCellEditor( viewer.getTable() );
    }

    protected boolean canEdit( Object element ) {
      return true;
    }

    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      return Boolean.valueOf( person.married );
    }

    protected void setValue( Object element, Object value ) {
      Person person = ( Person )element;
      person.married = ( ( Boolean )value ).booleanValue();
      getViewer().update( element, null );
    }

  }

  private static final class RealCheckboxCellEditor extends CellEditor {

    protected Button checkbox;

    public RealCheckboxCellEditor( Composite parent ) {
      super( parent, SWT.NONE );
    }

    @Override
    protected Control createControl( Composite parent ) {
      checkbox = new Button( parent, SWT.CHECK );
      return checkbox;
    }

    @Override
    protected Object doGetValue() {
      return checkbox.getSelection() ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    protected void doSetFocus() {
      checkbox.setFocus();
    }

    @Override
    protected void doSetValue( Object value ) {
      Assert.isTrue( value instanceof Boolean );
      if( checkbox != null ) {
        checkbox.setSelection( ( (Boolean ) value).booleanValue() );
      }
    }
  }

}
