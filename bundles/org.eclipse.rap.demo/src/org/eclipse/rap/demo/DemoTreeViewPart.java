/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo;

import java.util.ArrayList;
import org.eclipse.rap.jface.viewers.*;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.ui.IViewPart;
import org.eclipse.rap.ui.part.ViewPart;


public class DemoTreeViewPart extends ViewPart {

  private TreeViewer viewer;

  class TreeObject {

    private String name;
    private String location;
    private TreeParent parent;

    public TreeObject( String name ) {
      this.name = name;
    }

    public TreeObject( String name, String location ) {
        this.name = name;
        this.location = location;
      }

    public String getName() {
      return name;
    }

    public String getLocation() {
      return location;
    }
    
    public void setParent( TreeParent parent ) {
      this.parent = parent;
    }

    public TreeParent getParent() {
      return parent;
    }

    public String toString() {
      return getName();
    }
  }
  
  class TreeParent extends TreeObject {

    private ArrayList children;

    public TreeParent( String name ) {
      super( name );
      children = new ArrayList();
    }

    public void addChild( TreeObject child ) {
      children.add( child );
      child.setParent( this );
    }

    public void removeChild( TreeObject child ) {
      children.remove( child );
      child.setParent( null );
    }

    public TreeObject[] getChildren() {
      return ( TreeObject[] )children.toArray( new TreeObject[ children.size() ] );
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }
  }
  
  class TreeViewerContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {

    private TreeParent invisibleRoot;

    public void inputChanged( Viewer v, Object oldInput, Object newInput ) {
    }

    public void dispose() {
    }

    public Object[] getElements( Object parent ) {
      if( parent instanceof IViewPart ) {
        if( invisibleRoot == null )
          initialize();
        return getChildren( invisibleRoot );
      }
      return getChildren( parent );
    }

    public Object getParent( Object child ) {
      if( child instanceof TreeObject ) {
        return ( ( TreeObject )child ).getParent();
      }
      return null;
    }

    public Object[] getChildren( Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).getChildren();
      }
      return new Object[ 0 ];
    }

    public boolean hasChildren( Object parent ) {
      if( parent instanceof TreeParent )
        return ( ( TreeParent )parent ).hasChildren();
      return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real
     * code, you will connect to a real model and expose its hierarchy.
     */
    private void initialize() {
      TreeObject to1 = new TreeObject( "EclipseCon location", "http://maps.google.com/maps?q=5001%20Great%20America%20Pkwy%20Santa%20Clara%20CA%2095054" );
      TreeObject to2 = new TreeObject( "Eclipse Foundation", "http://maps.google.com/maps?q=Ottawa" );
      TreeObject to3 = new TreeObject( "Innoopract Inc", "http://maps.google.com/maps?q=Portland" );
      TreeParent p1 = new TreeParent( "Locate in browser view" );
      p1.addChild( to1 );
      p1.addChild( to2 );
      p1.addChild( to3 );
      TreeObject to4 = new TreeObject( "Leaf 4" );
      TreeParent p2 = new TreeParent( "Parent 2" );
      p2.addChild( to4 );
      TreeParent root = new TreeParent( "Root" );
      root.addChild( p1 );
      root.addChild( p2 );
      invisibleRoot = new TreeParent( "" );
      invisibleRoot.addChild( root );
    }
  }
  
  public void createPartControl( final Composite parent ) {
    viewer = new TreeViewer( parent );
    viewer.setContentProvider( new TreeViewerContentProvider() );
    viewer.setInput( this );
    getSite().setSelectionProvider( viewer );
  }
  
  public void setFocus() {
    viewer.getTree().setFocus();
  }
}
