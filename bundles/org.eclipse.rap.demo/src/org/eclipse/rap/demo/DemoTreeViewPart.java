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
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.rwt.widgets.Tree;
import org.eclipse.rap.ui.*;
import org.eclipse.rap.ui.part.ViewPart;


public class DemoTreeViewPart extends ViewPart {

  class TreeObject {

    private String name;
    private TreeParent parent;

    public TreeObject( String name ) {
      this.name = name;
    }

    public String getName() {
      return name;
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
      TreeObject to1 = new TreeObject( "Leaf 1" );
      TreeObject to2 = new TreeObject( "Leaf 2" );
      TreeObject to3 = new TreeObject( "Leaf 3" );
      TreeParent p1 = new TreeParent( "Parent 1" );
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
    parent.setLayout( new FormLayout() );
    TreeViewer viewer = new TreeViewer( parent );
    viewer.setContentProvider( new TreeViewerContentProvider() );
    viewer.setInput( this );
//    Tree tree = new Tree( left, RWT.NONE );
    Tree tree = viewer.getTree();
    FormData treeData = new FormData();
    tree.setLayoutData( treeData );
    treeData.top = new FormAttachment( 0, 2 );
    treeData.left = new FormAttachment( 0, 2 );
    treeData.right = new FormAttachment( 100, -2 );
    treeData.bottom = new FormAttachment( 100, -2 );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection selection
          = ( IStructuredSelection )event.getSelection();
        System.out.println(   "treeViewer selection: " 
                            + selection.getFirstElement() );
      }
    } );
    
    // selection service
    getSite().setSelectionProvider( viewer );
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    ISelectionService selectionService = window.getSelectionService();
    selectionService.addSelectionListener( new ISelectionListener() {
      public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        System.out.println( part.getTitle() );
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        System.out.println( sselection.getFirstElement() );
      } 
    } );
    IWorkbenchPage activePage = window.getActivePage();
    activePage.addPartListener( new IPartListener() {

      public void partActivated( IWorkbenchPart part ) {
        System.out.println( "activated: " + part.getTitle() );
      }

      public void partBroughtToTop( IWorkbenchPart part ) {
        System.out.println( "brought to top: " + part.getTitle() );
      }

      public void partClosed( IWorkbenchPart part ) {
        System.out.println( "closed: " + part.getTitle() );
      }

      public void partDeactivated( IWorkbenchPart part ) {
        System.out.println( "deactivated: " + part.getTitle() );
      }

      public void partOpened( IWorkbenchPart part ) {
        System.out.println( "opened: " + part.getTitle() );
      }
    } );
  }
}
