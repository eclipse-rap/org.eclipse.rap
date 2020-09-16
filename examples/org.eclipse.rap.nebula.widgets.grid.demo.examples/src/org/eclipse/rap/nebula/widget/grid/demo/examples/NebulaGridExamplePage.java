/*******************************************************************************
 * Copyright (c) 2013, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.nebula.widget.grid.demo.examples;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


public class NebulaGridExamplePage implements IExamplePage {

  private static final int NO_DATA = Integer.MIN_VALUE;
  private static final String EURO = "€ ";
  private static final String[] YEARS = new String[] {
    "2016", "2017", "2018", "2019", "2020"
  };

  private List<CompanyData> data;

  @Override
  public void createControl( Composite parent ) {
    initData();
    parent.setLayout( ExampleUtil.createGridLayout( 1, true, true, true ) );
    ExampleUtil.createHeading( parent, "Profit and Loss Balance Sheet", 1 );
    parent.setLayoutData( ExampleUtil.createFillData() );
    Grid grid = createGrid( parent );
    GridColumn category = createColumn( grid, null, "", 235, SWT.CENTER );
    category.setFooterText( "Net income" );
    category.setResizeable( false );
    GridColumnGroup group
      = createGridColumnGroup( grid, "Period ( Year Ended December 31 )", SWT.CENTER );
    for( int i = 0; i < YEARS.length; i++ ) {
      createYearColumn( grid, group, YEARS[ i ] );
    }
    createTotalColumn( grid, group );
    createItems( grid );
    calcNetIncome( grid );
  }

  private void initData() {
    data = new ArrayList<CompanyData>();
    data.add( new CompanyData( "Consolidated Statements of<br/>Incoming Data:",
                               NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA ) );
    data.add( new CompanyData( "Revenues",
                               6138560, 10604917, 16593986, 21795550, 23650563 ) );
    data.add( new CompanyData( "Costs and expenses:",
                               NO_DATA, NO_DATA, NO_DATA, NO_DATA, NO_DATA ) );
    data.add( new CompanyData( "Costs of ravenues",
                               2577088, 4225027, 6649085, 8621506, 8844115 ) );
    data.add( new CompanyData( "Research and development",
                               599510, 1228589, 2119985, 2793192, 2843027 ) );
    data.add( new CompanyData( "Sales and marketing",
                               468152, 849518, 1461266, 1946244, 1983941 ) );
    data.add( new CompanyData( "General and administrative",
                               386532, 751787, 1279250, 1802639, 1667294 ) );
    data.add( new CompanyData( "Contribution to Google<br/>Foundation",
                               90000, 0, 0, 0, 0 ) );
    data.add( new CompanyData( "Total costs and expenses",
                               4121282, 7054921, 11509586, 15163581, 15338377 ) );
    data.add( new CompanyData( "Income from operations",
                               2017287, 3549996, 5084400, 6631969, 8312186 ) );
    data.add( new CompanyData( "Impairment of equity<br/>investments",
                               0, 0, 0, 1094757, 0 ) );
    data.add( new CompanyData( "Interest income and other, net",
                               124399, 461044, 589580, 316384, 69003 ) );
    data.add( new CompanyData( "Income before income taxes",
                               2141677, 4011040, 5673980, 5853596, 8381189 ) );
    data.add( new CompanyData( "Provision for income taxes",
                               676280, 933594, 1470260, 1626738, 1860741 ) );
  }

  private Grid createGrid( Composite parent ) {
    Grid grid = new Grid( parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI );
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    grid.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    GridData tableLayoutData = ExampleUtil.createFillData();
    tableLayoutData.verticalIndent = 10;
    grid.setLayoutData( tableLayoutData );
    grid.setHeaderVisible( true );
    grid.setFooterVisible( true );
    grid.setLinesVisible( true );
    grid.setCellSelectionEnabled( true );
    return grid;
  }

  private GridColumnGroup createGridColumnGroup( Grid grid, String name, int style ) {
    GridColumnGroup group = new GridColumnGroup( grid, style );
    group.setHeaderFont( new Font( group.getDisplay(), "Verdana", 14, SWT.BOLD ) );
    group.setText( name );
    return group;
  }

  private void createYearColumn( Grid grid, GridColumnGroup group, String text ) {
    GridColumn column = createColumn( grid, group, text, 148, SWT.RIGHT );
    column.setSummary( false );
  }

  private void createTotalColumn( Grid grid, GridColumnGroup columnGroup ) {
    GridColumn totalColumn
      = createColumn( grid, columnGroup, "Total (2005 - 2009)", 740, SWT.RIGHT );
    totalColumn.setDetail( false );
  }

  private static GridColumn createColumn( Grid grid,
                                          GridColumnGroup group,
                                          String name,
                                          int width,
                                          int style )
  {
    GridColumn column;
    if( group == null ) {
      column = new GridColumn( grid, style );
    } else {
      column = new GridColumn( group, style );
    }
    Font font = new Font( column.getDisplay(), "Verdana", 12, SWT.BOLD );
    column.setHeaderFont( font );
    column.setFooterFont( font );
    column.setText( name );
    column.setWidth( width );
    return column;
  }

  private void createItems( Grid grid ) {
    for( int i = 0; i < data.size(); i++ ) {
      CompanyData current = data.get( i );
      GridItem item;
      if( i > 2 && i < 8 ) {
        item = new GridItem( grid.getItem( 2 ), SWT.NONE );
      } else {
        item = new GridItem( grid, SWT.NONE );
      }
      if( current.name.indexOf( "<br/>" ) != -1 ) {
        item.setHeight( 44 );
      }
      item.setText( current.name );
      for( int j = 0; j < current.amount.length; j++ ) {
        String text = "";
        if( current.amount[ j ] != NO_DATA ) {
          text = format( current.amount[ j ] );
        }
        item.setText( j + 1, text );
      }
      item.setExpanded( true );
    }
  }

  private void calcNetIncome( Grid grid ) {
    for( int i = 0; i < YEARS.length + 1; i++ ) {
      CompanyData income = data.get( data.size() - 2 );
      CompanyData taxes = data.get( data.size() - 1 );
      int netIncome = income.amount[ i ] - taxes.amount[ i ];
      grid.getColumn( i + 1 ).setFooterText( format( netIncome ) );
    }
  }

  private String format( int amount ) {
    NumberFormat formatter = NumberFormat.getInstance( RWT.getLocale() );
    return amount == 0 ? "-" : EURO + formatter.format( amount );
  }

  private class CompanyData {

    public final String name;
    public final int[] amount = new int[ YEARS.length + 1 ];

    public CompanyData( String name, int... amount ) {
      this.name = name;
      int sum = 0;
      for( int i = 0; i < this.amount.length - 1; i++ ) {
        this.amount[ i ] = amount[ i ];
        sum += amount[ i ];
      }
      this.amount[ this.amount.length - 1 ] = sum;
    }

  }

}
