/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;


public class TreeViewerExample implements IExamplePage {

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout() );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    final Tree tree = new Tree( composite, SWT.BORDER );
    GridData layoutData = new GridData( GridData.FILL_BOTH );
    layoutData.minimumHeight = 250;
    tree.setLayoutData( layoutData );
    TreeColumn column1 = new TreeColumn( tree, SWT.LEFT );
    column1.setText( "City" );
    column1.setWidth( 175 );
    TreeColumn column2 = new TreeColumn( tree, SWT.CENTER );
    column2.setText( "Timezone" );
    column2.setWidth( 75 );
    TreeColumn column3 = new TreeColumn( tree, SWT.CENTER );
    column3.setText( "Offset" );
    column3.setWidth( 75 );
    tree.setLinesVisible( true );
    tree.setHeaderVisible( true );
    TreeViewer viewer = new TreeViewer( tree );
    viewer.setContentProvider( new ExampleContentProvider() );
    viewer.setLabelProvider( new ExampleLabelProvider() );
    viewer.setInput( createDummyModel() );
    viewer.expandAll();
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
    northAmerica.addChild( new City( "Anchorage, Alaska", "AKST", -9 ) );
    return result;
  }

  static class TreeObject {
    private TreeObject parent;
    private final List children;
    public final String name;

    public TreeObject( final String name ) {
      this.name = name;
      children = new ArrayList();
    }
    
    public void setParent( final TreeObject parent ) {
      this.parent = parent;
    }
    
    public TreeObject getParent() {
      return parent;
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

  static class City extends TreeObject {
    public final String tz;
    public final int offset;

    public City( final String name, final String tz, final int offset ) {
      super( name );
      this.tz = tz;
      this.offset = offset;
    }
  }

  static final class ExampleLabelProvider extends CellLabelProvider {

    public void update( final ViewerCell cell ) {
      TreeObject object = ( TreeObject )cell.getElement();
      int columnIndex = cell.getColumnIndex();
      IWorkbench workbench = PlatformUI.getWorkbench();
      ISharedImages sharedImages = workbench.getSharedImages();
      switch( columnIndex ) {
        case 0:
          cell.setText( object.name );
          Image image;
          if( object.hasChildren() ) {
            image = sharedImages.getImage( ISharedImages.IMG_OBJ_FOLDER );
          } else {
            image = sharedImages.getImage( ISharedImages.IMG_OBJ_FILE );
          }
          cell.setImage( image );
          break;
        case 1:
          if( object instanceof City ) {
            City city = ( City )object;
            cell.setText( city.tz );
          }
          break;
        case 2:
          if( object instanceof City ) {
            City city = ( City )object;
            cell.setText( "UTC "
                          + ( city.offset >= 0 ? "-" : "" )
                          + String.valueOf( city.offset ) );
          }
          break;
      }
    }
  }

  static class ExampleContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {
  
    public void inputChanged( final Viewer v,
                              final Object oldInput,
                              final Object newInput )
    {
      // TODO
    }
  
    public void dispose() {
      // TODO
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
