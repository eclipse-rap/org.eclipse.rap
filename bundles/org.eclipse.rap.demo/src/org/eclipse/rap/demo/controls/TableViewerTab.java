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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class TableViewerTab extends ExampleTab {

  private static final int ADD_ITEMS = 10000;

  private static final int FIRST_NAME = 0;
  private static final int LAST_NAME = 1;
  private static final int AGE = 2;
  private static final int MARRIED = 3;
  private static final String[] LAST_NAMES = {
    "Hövl&lt;_'><'&amp;",
    "Panther",
    "Marx",
    "Loren",
    "Cool",
    "Einstein",
    "Duck",
    "Mouse",
    "",
    "Presley"
  };

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
    private int columnIndex;

    public PersonLabelProvider( int columnIndex ) {
      this.columnIndex = columnIndex;
    }

    public String getText( Object element ) {
      Person person = ( Person )element;
      String result = person.toString();
      switch( columnIndex ) {
        case FIRST_NAME:
          result = person.firstName;
          break;
        case LAST_NAME:
          result = person.lastName;
          break;
        case AGE:
          result = String.valueOf( person.age );
          break;
        case MARRIED:
          result = person.married ? "yes" : "no";
          break;
      }
      return result;
    }
    
    public Color getBackground( Object element ) {
      Color result = null;
//      switch( columnIndex ) {
//        case FIRST_NAME:
//          result = Graphics.getColor( 225, 225, 225 );
//          break;
//        case LAST_NAME:
//          result = Graphics.getColor( 230, 230, 230 );
//          break;
//        case AGE:
//          result = Graphics.getColor( 235, 235, 235 );
//          break;
//        case MARRIED:
//          result = Graphics.getColor( 240, 240, 240 );
//          break;
//      }
      return result;
    }

    public String getToolTipText( Object element ) {
      Person person = ( Person )element;
      String text = null;
      switch( columnIndex ) {
        case FIRST_NAME:
          text = person.firstName;
          break;
        case LAST_NAME:
          text = person.lastName;
          break;
        case AGE:
          text = String.valueOf( person.age );
          break;
        case MARRIED:
          text = person.married ? "yes" : "no";
          break;
      }
      return text;
    }
  }

  private static final class PersonComparator extends ViewerComparator implements Comparator {
    private final boolean ascending;
    private final int property;
    public PersonComparator( int property, boolean ascending ) {
      this.property = property;
      this.ascending = ascending;
    }

    public int compare( Viewer viewer, Object object1, Object object2 ) {
      return compare( object1, object2 );
    }

    public boolean isSorterProperty( Object elem, String property ) {
      return true;
    }
    public int compare( Object object1, Object object2 ) {
      Person person1 = ( Person )object1;
      Person person2 = ( Person )object2;
      int result = 0;
      if( property == FIRST_NAME ) {
        result = person1.firstName.compareTo( person2.firstName );
      } else if( property == LAST_NAME ) {
        result = person1.lastName.compareTo( person2.lastName );
      } else if( property == AGE ) {
        result = person1.age - person2.age;
      } else if( property == MARRIED ) {
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
      this.text = string;
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
    public LastNameEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new ComboBoxCellEditor( viewer.getTable(), LAST_NAMES, SWT.NONE );
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

  private TableViewer viewer;
  private TableViewerColumn firstNameColumn;
  private TableViewerColumn lastNameColumn;
  private TableViewerColumn ageColumn;
  private TableViewerColumn editableColumn;
  private Label lblSelection;
  private Button btnCreateCellEditor;
  private final PersonFilter viewerFilter;
  private final java.util.List<Person> persons = new ArrayList<Person>();

  public TableViewerTab( CTabFolder topFolder ) {
    super( topFolder, "TableViewer" );
    viewerFilter = new PersonFilter();
  }

  private void initPersons() {
    persons.clear();
    persons.add( new Person( "Rögn\"íy&", LAST_NAMES[ 0 ], 1, false ) );
    persons.add( new Person( "Paul", LAST_NAMES[ 1 ], 1, false ) );
    persons.add( new Person( "Karl", LAST_NAMES[ 2 ], 2, false ) );
    persons.add( new Person( "Sofia", LAST_NAMES[ 3 ], 3, true ) );
    persons.add( new Person( "King", LAST_NAMES[ 4 ], 4, false ) );
    persons.add( new Person( "Albert", LAST_NAMES[ 5 ], 5, true ) );
    persons.add( new Person( "Donald", LAST_NAMES[ 6 ], 6, false ) );
    persons.add( new Person( "Mickey", LAST_NAMES[ 7 ], 7, true ) );
    persons.add( new Person( "Asterix", LAST_NAMES[ 8 ], 8, false ) );
    persons.add( new Person( "Nero", LAST_NAMES[ 8 ], 9, false ) );
    persons.add( new Person( "Elvis", LAST_NAMES[ 9 ], 10, true ) );
  }

  protected void createStyleControls( Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createAddItemsButton();
    createSelectYoungestPersonButton();
    createRemoveButton();
    createCellEditorButton();
    lblSelection = new Label( styleComp, SWT.NONE );
  }

  protected void createExampleControls( Composite parent ) {
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
    viewer = new TableViewer( parent, getStyle() );
    viewer.setUseHashlookup( true );
    if( ( getStyle() & SWT.VIRTUAL ) == 0 ) {
      viewer.setContentProvider( new PersonContentProvider() );
    } else {
      viewer.setContentProvider( new LazyPersonContentProvider() );
    }
    firstNameColumn = createFirstNameColumn();
    lastNameColumn = createLastNameColumn();
    ageColumn = createAgeColumn();
    editableColumn = createEditableColumn();
    viewer.setInput( persons );
    viewer.setItemCount( persons.size() );
    viewer.addFilter( viewerFilter );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      int[] selection = new int[ 0 ];
      public void selectionChanged( SelectionChangedEvent event ) {
        lblSelection.setText( "Selection: " + event.getSelection() );
        lblSelection.getParent().layout( new Control[] { lblSelection } );
        Table table = viewer.getTable();
        if( Boolean.TRUE.equals( table.getData( Table.ALWAYS_HIDE_SELECTION ) ) ) {
          for( int i = 0; i < selection.length; i++ ) {
            table.getItem( selection[ i ] ).setBackground( null );
          }
          selection = table.getSelectionIndices();
          for( int i = 0; i < selection.length; i++ ) {
            table.getItem( selection[ i ] ).setBackground( Graphics.getColor( 160, 205, 230 ) );
          }
        }
      }
    } );
    viewer.getTable().setHeaderVisible( true );
    ColumnViewerToolTipSupport.enableFor( viewer );
    gridDataFactory = GridDataFactory.swtDefaults();
    gridDataFactory.grab( true, true );
    gridDataFactory.align( SWT.FILL, SWT.FILL );
    gridDataFactory.span( 2, SWT.DEFAULT );
    gridDataFactory.applyTo( viewer.getTable() );
    registerControl( viewer.getControl() );
  }

  private TableViewerColumn createFirstNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( FIRST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "First Name" );
    column.setWidth( 170 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, FIRST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createLastNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( LAST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "Last Name" );
    column.setWidth( 120 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, LAST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createAgeColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( AGE ) );
    TableColumn column = result.getColumn();
    column.setText( "Age" );
    column.setWidth( 80 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, AGE, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createEditableColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( MARRIED ) );
    TableColumn column = result.getColumn();
    column.setText( "Married" );
    column.setWidth( 60 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, MARRIED, sortDirection == SWT.DOWN );
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
    editingSupport = new LastNameEditingSupport( viewer );
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
    int feature
      = ColumnViewerEditor.TABBING_HORIZONTAL
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

  private static void sort( TableViewer viewer, int property, boolean ascending ) {
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
