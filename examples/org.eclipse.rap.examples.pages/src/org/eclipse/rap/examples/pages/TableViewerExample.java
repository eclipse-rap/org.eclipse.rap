/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.Elements.Element;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class TableViewerExample implements IExamplePage {

  private static final int NUMBER = 0;
  private static final int SYMBOL = 1;
  private static final int NAME = 2;
  private static final int SERIES = 3;
  private static final int GROUP = 4;
  private static final int PERIOD = 5;

  private TableViewer viewer;
  private Label lblSelection;
  private final ElementsFilter viewerFilter;
  private final ElementsLabelProvider labelProvider;
  private List elements;
  private Text txtFilter;
  private Label lblHelp;
  private TableViewerColumn nrColumn;
  private TableViewerColumn symColumn;
  private TableViewerColumn nameColumn;
  private TableViewerColumn seriesColumn;
  private TableViewerColumn groupColumn;
  private TableViewerColumn periodColumn;

  private static Color[] seriesColors;

  public TableViewerExample() {
    viewerFilter = new ElementsFilter();
    labelProvider = new ElementsLabelProvider();
  }

  public void createControl( Composite parent ) {
    initColors( parent.getDisplay() );
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    parent.setLayout( ExampleUtil.createGridLayout( 1, true, true, true ) );
    parent.setLayoutData( ExampleUtil.createFillData() );
    createTextFilter( parent );
    createViewer( parent );
    createLabelSelection( parent );
    createLabelHelp( parent );
    viewer.getTable().forceFocus();
    handleSelection( true );
  }

  private void initColors( Display display ) {
    seriesColors = new Color[] {
      null,
      new Color( display, 239, 41, 41 ),
      new Color( display, 233, 185, 110 ),
      new Color( display, 252, 233, 79 ),
      new Color( display, 114, 159, 207 ),
      new Color( display, 173, 127, 168 ),
      new Color( display, 173, 127, 168 ),
      new Color( display, 252, 175, 62 ),
      new Color( display, 238, 238, 236 ),
      new Color( display, 156, 159, 153 ),
      new Color( display, 138, 226, 52 ),
    };
  }

  private void createTextFilter( Composite parent ) {
    txtFilter = new Text( parent, SWT.BORDER );
    GridData gridData = ExampleUtil.createHorzFillData();
    txtFilter.setLayoutData( gridData );
    txtFilter.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        viewerFilter.setText( text.getText() );
        viewer.refresh();
        updateLabel();
      }
    } );
    txtFilter.addKeyListener( new KeyAdapter() {
      @Override
      public void keyPressed( KeyEvent e ) {
        if( e.keyCode == 13 || e.keyCode == SWT.ESC || e.keyCode == SWT.ARROW_DOWN ) {
          handleSelection( e.keyCode == SWT.ARROW_DOWN );
          viewer.getTable().forceFocus();
        }
      }
    } );
    txtFilter.setData( RWT.ACTIVE_KEYS, new String[]{ "ENTER", "ESCAPE", "ARROW_DOWN" } );
  }

  private void createViewer( Composite parent ) {
    viewer = new TableViewer( parent, SWT.BORDER );
    GridData tableLayoutData = ExampleUtil.createFillData();
    viewer.getTable().setLayoutData( tableLayoutData );
    elements = Elements.getElements();
    ColumnViewerToolTipSupport.enableFor( viewer );
    viewer.setUseHashlookup( true );
    viewer.setContentProvider( new ElementsContentProvider() );
    viewer.setLabelProvider( new ElementsLabelProvider() );
    viewer.getTable().setHeaderVisible( true );
    viewer.getTable().setLinesVisible( true );
    nrColumn = createColumn( "Nr.", 50, NUMBER );
    symColumn = createColumn( "Sym.", 50, SYMBOL );
    nameColumn = createColumn( "Name", 140, NAME );
    seriesColumn = createColumn( "Series", 180, SERIES );
    groupColumn = createColumn( "Group", 50, GROUP );
    periodColumn = createColumn( "Period", 50, PERIOD );
    viewer.setInput( elements );
    viewer.addFilter( viewerFilter );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        updateLabel();
      }
    } );
    addViewerKeyboardControl();
  }

  private void createLabelSelection( Composite parent ) {
    Composite selBorder = new Composite( parent, SWT.BORDER );
    selBorder.setLayout( ExampleUtil.createFillLayout(true) );
    GridData gridData = ExampleUtil.createFillData();
    gridData.minimumHeight = 25;
    selBorder.setLayoutData( gridData );
    lblSelection = new Label( selBorder, SWT.NONE );
  }

  private void createLabelHelp( Composite parent ) {
    lblHelp = new Label( parent, SWT.WRAP );
    GridData labelHelpLayoutData = ExampleUtil.createHorzFillData();
    lblHelp.setLayoutData( labelHelpLayoutData );
    String helpContent = "Shortcuts: [CTRL+F] - Filter | ";
    helpContent += "Sort by: [CTRL+R] - Number, [CTRL+Y] - Symbol, [CTRL+N] - Name, ";
    helpContent += "[CTRL+S] - Series, [CTRL+G] - Group, [CTRL+E] - Period";
    lblHelp.setText( helpContent );
    lblHelp.setForeground( new Color( lblHelp.getDisplay(), 150, 150, 150 ) );
    FontData[] font = lblHelp.getFont().getFontData();
    for( int i = 0; i < font.length; i++ ) {
      font[ i ].setHeight( font[ i ].getHeight() - 4 );
    }
    lblHelp.setFont( new Font( lblHelp.getDisplay(), font ) );
  }

  private void addViewerKeyboardControl() {
    viewer.getTable().addKeyListener( new KeyAdapter() {
      @Override
      public void keyPressed( KeyEvent e ) {
        if( e.stateMask == SWT.CTRL ) {
          switch( e.character ) {
            case 'f':
              txtFilter.forceFocus();
            break;
            case 'r':
              sortByColumn( nrColumn.getColumn(), NUMBER, true );
            break;
            case 'n':
              sortByColumn( nameColumn.getColumn(), NAME, true );
            break;
            case 'y':
              sortByColumn( symColumn.getColumn(), SYMBOL, true );
            break;
            case 's':
              sortByColumn( seriesColumn.getColumn(), SERIES, true );
            break;
            case 'g':
              sortByColumn( groupColumn.getColumn(), GROUP, true );
              break;
            case 'e':
              sortByColumn( periodColumn.getColumn(), PERIOD, true );
            break;
          }
        }
      }
    } );
    String[] shortcuts = new String[]{
      "CTRL+F", "CTRL+N", "CTRL+R", "CTRL+Y", "CTRL+S", "CTRL+G", "CTRL+E"
    };
    viewer.getTable().setData( RWT.ACTIVE_KEYS, shortcuts );
    viewer.getTable().setData( RWT.CANCEL_KEYS, shortcuts );
  }

  private void handleSelection( boolean reset ) {
    if( viewer.getTable().getItemCount() > 0 ) {
      if( reset || viewer.getSelection().isEmpty() ) {
        viewer.getTable().select( 0 );
      }
      int index = viewer.getTable().getSelectionIndex();
      // NOTE : setSelection needed as it also sets focus index and scrolls
      viewer.getTable().setSelection( index );
    }
    updateLabel();
  }

  private void updateLabel() {
    StructuredSelection sel = ( StructuredSelection )viewer.getSelection();
    Element firstElement = ( Element )sel.getFirstElement();
    if( firstElement != null ) {
      lblSelection.setText(   firstElement.name
                            + " ("
                            + firstElement.symbol
                            + ")" );
    } else {
      lblSelection.setText( "" );
    }
  }

  private TableViewerColumn createColumn( String text, int width, final int sortProperty ) {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( labelProvider );
    TableColumn column = result.getColumn();
    column.setText( text );
    column.setWidth( width );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        sortByColumn( ( TableColumn )event.widget, sortProperty, false );
      }
    } );
    return result;
  }

  private void sortByColumn( TableColumn column, int sortProperty, boolean reset ) {
    int sortDirection = updateSortDirection( column );
    sort( viewer, sortProperty, sortDirection == SWT.DOWN );
    handleSelection( false );
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
      List input = ( List )viewer.getInput();
      Collections.sort( input, new ElementComparator( property, ascending ) );
      viewer.refresh();
    } else {
      viewer.setComparator( new ElementComparator( property, ascending ) );
    }
  }

  private static final class ElementsContentProvider implements IStructuredContentProvider {
    Object[] elements;
    public Object[] getElements( Object inputElement ) {
      return elements;
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
      if( newInput == null ) {
        elements = new Object[ 0 ];
      } else {
        List elementsList = ( List )newInput;
        elements = elementsList.toArray();
      }
    }

    public void dispose() {
      // do nothing
    }
  }

  private static final class ElementsLabelProvider extends CellLabelProvider {
    @Override
    public void update( ViewerCell cell ) {
      Element element = ( Element )cell.getElement();
      int columnIndex = cell.getColumnIndex();
      switch( columnIndex ) {
        case NUMBER:
          cell.setText( String.valueOf( element.number ) );
          break;
        case GROUP:
          cell.setText( String.valueOf( element.group ) );
          break;
        case PERIOD:
          cell.setText( String.valueOf( element.period ) );
          break;
        case NAME:
          cell.setText( element.name );
          break;
        case SYMBOL:
          cell.setText( element.symbol );
          break;
        case SERIES:
          cell.setText( element.getSeriesName() );
          cell.setBackground( seriesColors[ element.series ] );
          break;
      }
    }

    @Override
    public String getToolTipText( Object object ) {
      Element element = ( Element )object;
      return
          element.symbol
        + ": "
        + element.name
        + ", "
        + element.getSeriesName();
    }
  }

  private static final class ElementComparator extends ViewerComparator implements Comparator {
    private final boolean ascending;
    private final int property;

    public ElementComparator( int property, boolean ascending ) {
      this.property = property;
      this.ascending = ascending;
    }

    @Override
    public int compare( Viewer viewer, Object object1, Object object2 ) {
      return compare( object1, object2 );
    }

    @Override
    public boolean isSorterProperty( Object elem, String property ) {
      return true;
    }

    public int compare( Object object1, Object object2 ) {
      Element element1 = ( Element )object1;
      Element element2 = ( Element )object2;
      int result = 0;
      if( property == NAME ) {
        result = element1.name.compareTo( element2.name );
      } else if( property == NUMBER ) {
        result = compare( element1.number, element2.number );
      } else if( property == GROUP ) {
        result = compare( element1.group, element2.group );
      } else if( property == PERIOD ) {
        result = compare( element1.period, element2.period );
      } else if( property == SYMBOL ) {
        result = element1.symbol.compareTo( element2.symbol );
      } else if( property == SERIES ) {
        result = compare( element1.series, element2.series );
      }
      if( !ascending ) {
        result = result * -1;
      }
      return result;
    }

    private static int compare( int number1, int number2 ) {
      int result;
      if( number1 < number2 ) {
        result = -1;
      } else if( number1 > number2 ) {
        result = +1;
      } else {
        result = 0;
      }
      return result;
    }
  }

  private static final class ElementsFilter extends ViewerFilter {
    private String text;

    public void setText( String string ) {
      this.text = string;
    }

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {
      boolean result = true;
      Element chemElement = ( Element )element;
      if( text != null && text.length() > 0 ) {
        String lowerCaseText = text.toLowerCase();
        String elementName = chemElement.name.toLowerCase();
        result = elementName.indexOf( lowerCaseText ) != -1;
        if( !result ) {
          String elementSymbol = chemElement.symbol.toLowerCase();
          result = elementSymbol.indexOf( lowerCaseText ) != -1;
        }
        if( !result ) {
          String elementSeries = chemElement.getSeriesName().toLowerCase();
          result = elementSeries.indexOf( lowerCaseText ) != -1;
        }
      }
      return result;
    }

    @Override
    public boolean isFilterProperty( Object element, String prop ) {
      return true;
    }
  }
}
