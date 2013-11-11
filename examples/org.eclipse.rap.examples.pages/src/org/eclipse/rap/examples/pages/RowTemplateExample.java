/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.Person;
import org.eclipse.rap.examples.pages.internal.Persons;
import org.eclipse.rap.examples.pages.internal.PersonsTemplate;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.widgets.DialogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class RowTemplateExample implements IExamplePage {

  private final class SelectionListener extends SelectionAdapter {

    private final Composite parent;

    private SelectionListener( Composite parent ) {
      this.parent = parent;
    }

    @Override
    public void widgetSelected( SelectionEvent e ) {
      if( "phone".equals( e.text ) ) {
        TableItem item = ( TableItem )e.item;
        alert( "Dialing...", "Calling " + item.getText( 2 ) + "!" );
      } else if( "mail".equals( e.text ) ) {
        String mail = ( ( TableItem )e.item ).getText( 3 );
        String firstName = ( ( TableItem )e.item ).getText( 0 );
        UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
        if( launcher != null ) {
          launcher.openURL( "mailto:" + mail + "?subject=RAP%20Rocks!&body=Hello%20" + firstName );
        } else {
          alert( "Now mailing to...", mail );
        }
      } else if( "arrow".equals( e.text ) ) {
        TableItem item = ( TableItem )e.item;
        String firstName = item.getText( 0 );
        alert( "Nothing here", "Lets edit " + firstName + "!" );
      }
    }

    private void alert( String title, String message ) {
      MessageBox messageBox = new MessageBox( parent.getShell(), SWT.ICON_INFORMATION );
      messageBox.setText( title );
      messageBox.setMessage( message );
      DialogUtil.open( messageBox, null );
    }
  }

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    parent.setLayout( ExampleUtil.createGridLayout( 1, true, true, true ) );
    parent.setLayoutData( ExampleUtil.createFillData() );
    createTable( parent );
  }

  private void createTable( Composite comp ) {
    TableViewer tableViewer = new TableViewer( comp, SWT.FULL_SELECTION | SWT.BORDER );
    tableViewer.setContentProvider( new ArrayContentProvider() );
    configColumnViewer( tableViewer );
    Table table = tableViewer.getTable();
    table.addSelectionListener( new SelectionListener( comp ) );
    table.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 56 ) );
    GridData tableLayoutData = ExampleUtil.createFillData();
    tableLayoutData.verticalIndent = 10;
    table.setLayoutData( tableLayoutData );
    table.setData( RWT.ROW_TEMPLATE, new PersonsTemplate() );
  }

  private void configColumnViewer( TableViewer viewer ) {
    addFirstNameColumn( viewer );
    addLastNameColumn( viewer );
    addPhoneColumn( viewer );
    addMailColumn( viewer );
    viewer.setInput( Persons.get( viewer.getControl().getDisplay() ) );
  }

  private void addFirstNameColumn( final ColumnViewer viewer ) {
    ViewerColumn firstNameColumn = createViewerColumn( viewer, "First Name", 200 );
    firstNameColumn.setLabelProvider( new ColumnLabelProvider() {
      @Override
      public String getText( Object element ) {
        Person p = ( Person )element;
        return p.getFirstName();
      }
      @Override
      public Image getImage( Object element ) {
        Person p = ( Person )element;
        return p.getImage();
      }
    } );
  }

  private void addLastNameColumn( ColumnViewer viewer ) {
    ViewerColumn lastNameColumn = createViewerColumn( viewer, "Last Name", 200 );
    lastNameColumn.setLabelProvider( new ColumnLabelProvider() {
      @Override
      public String getText( Object element ) {
        Person p = ( Person )element;
        return p.getLastName();
      }
    } );
  }

  private void addPhoneColumn( ColumnViewer viewer ) {
    ViewerColumn pohoneColumn = createViewerColumn( viewer, "Phone", 130 );
    pohoneColumn.setLabelProvider( new ColumnLabelProvider() {
      @Override
      public String getText( Object element ) {
        Person p = ( Person )element;
        return p.getPhone();
      }
    } );
  }

  private void addMailColumn( ColumnViewer viewer ) {
    ViewerColumn pohoneColumn = createViewerColumn( viewer, "E-Mail", 180 );
    pohoneColumn.setLabelProvider( new ColumnLabelProvider() {
      @Override
      public String getText( Object element ) {
        Person p = ( Person )element;
        return p.getMail();
      }
    } );
  }

  private ViewerColumn createViewerColumn( final ColumnViewer viewer, String name, int width ) {
    ViewerColumn viewerColumn = null;
    if( viewer instanceof TableViewer ) {
      TableViewer tableViewer = ( TableViewer )viewer;
      TableViewerColumn tableColumn = new TableViewerColumn( tableViewer, SWT.NONE );
      tableColumn.getColumn().setWidth( width );
      tableColumn.getColumn().setText( name );
      viewerColumn = tableColumn;
    } else if( viewer instanceof TreeViewer ) {
      TreeViewer treeViewer = ( TreeViewer )viewer;
      TreeViewerColumn treeColumn = new TreeViewerColumn( treeViewer, SWT.NONE );
      treeColumn.getColumn().setWidth( width );
      treeColumn.getColumn().setText( name );
      viewerColumn = treeColumn;
    }
    return viewerColumn;
  }

}
