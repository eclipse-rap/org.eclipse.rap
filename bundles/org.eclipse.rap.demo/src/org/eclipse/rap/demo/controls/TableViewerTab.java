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

import java.util.*;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class TableViewerTab extends ExampleTab {


  private static final int ADD_ITEMS = 300;

  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private static final String AGE = "age";

  private static final class Person {
    String firstName;
    String lastName;
    int age;

    public Person( final String firstName,
                   final String lastName,
                   final int age )
    {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
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
    extends LabelProvider
    implements ITableLabelProvider
  {
    public Image getColumnImage( final Object element, final int columnIndex ) {
      return null;
    }
    public String getColumnText( final Object element, final int columnIndex ) {
      Person person = ( Person )element;
      String result;
      switch( columnIndex ) {
        case 0:
          result = person.firstName;
        break;
        case 1:
          result = person.lastName;
        break;
        case 2:
          result = String.valueOf( person.age );
          break;
        default:
          result = "";
        break;
      }
      return result;
    }
  }

  private static final class PersonComparator
    extends ViewerComparator
    implements Comparator
  {
    private final boolean ascending;
    private final String property;
    PersonComparator( final String property, final boolean ascending ) {
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
      if( FIRST_NAME.equals( property ) ) {
        result = person1.firstName.compareTo( person2.firstName );
      } else if( LAST_NAME.equals( property ) ) {
        result = person1.lastName.compareTo( person2.lastName );
      } else if( AGE.equals( property ) ) {
        result = person1.age - person2.age;
      }
      if( !ascending ) {
        result = result * ( -1 );
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

  private final PersonFilter viewerFilter;
  private TableViewer viewer;
  private final java.util.List persons = new ArrayList();
  private Label lblSelection;

  public TableViewerTab( final CTabFolder topFolder ) {
    super( topFolder, "TableViewer" );
    viewerFilter = new PersonFilter();
  }

  private void initPersons() {
    persons.clear();
    persons.add( new Person( "Rögn\"íy&", "Hövl&lt;_'><', the char tester", 1 ) );
    persons.add( new Person( "Paul", "Panther", 1 ) );
    persons.add( new Person( "Karl", "Marx", 2 ) );
    persons.add( new Person( "Sofia", "Loren", 3 ) );
    persons.add( new Person( "King", "Cool", 4 ) );
    persons.add( new Person( "Albert", "Einstein", 5 ) );
    persons.add( new Person( "Donald", "Duck", 6 ) );
    persons.add( new Person( "Mickey", "Mouse", 7 ) );
    persons.add( new Person( "Asterix", "", 8 ) );
    persons.add( new Person( "Nero", "", 9 ) );
    persons.add( new Person( "Elvis", "Presley", 10 ) );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createAddItemsButton();
    createSelectYoungestPersonButton();
    createRemoveButton();
    lblSelection = new Label( styleComp, SWT.NONE );
  }

  protected void createExampleControls( final Composite parent ) {
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
    viewer.setColumnProperties( initColumnProperties( viewer ) );
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

  private static String[] initColumnProperties( final TableViewer viewer ) {
    Table table = viewer.getTable();
    TableColumn firstNameColumn = new TableColumn( table, SWT.NONE );
    firstNameColumn.setText( "First Name" );
    firstNameColumn.setWidth( 170 );
    firstNameColumn.setMoveable( true );
    firstNameColumn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, FIRST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    TableColumn lastNameColumn = new TableColumn( table, SWT.NONE );
    lastNameColumn.setText( "Last Name" );
    lastNameColumn.setWidth( 100 );
    lastNameColumn.setMoveable( true );
    lastNameColumn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, LAST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    TableColumn ageColumn = new TableColumn( table, SWT.NONE );
    ageColumn.setText( "Age" );
    ageColumn.setWidth( 80 );
    ageColumn.setMoveable( true );
    ageColumn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, AGE, sortDirection == SWT.DOWN );
      }
    } );
    return new String[] {
      FIRST_NAME, LAST_NAME, AGE
    };
  }

  private void addPerson() {
    int maxAge = 0;
    for( int i = 0; i < persons.size(); i++ ) {
      Person person = ( Person )persons.get( i );
      if( person.age > maxAge ) {
        maxAge = person.age;
      }
    }
    persons.add( new Person( "new", "person", maxAge + 1 ) );
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
                            final String property,
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
