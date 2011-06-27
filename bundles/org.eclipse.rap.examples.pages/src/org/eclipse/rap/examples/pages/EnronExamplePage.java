/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class EnronExamplePage implements IExamplePage {

  private static final File ROOT = new File( "/data/enron/maildir" );
  private TreeViewer viewer;
  private Text text;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    createTreeArea( parent );
    createTextArea( parent );
  }

  private void createTreeArea( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Enron Dataset (520.929 items)" );
    group.setLayoutData( ExampleUtil.createFillData() );
    FillLayout layout = new FillLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    group.setLayout( layout );
    createTreeViewer( group );
  }

  private void createTextArea( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( ExampleUtil.createFillData() );
    FillLayout layout = new FillLayout();
    layout.marginHeight = 5;
    group.setLayout( layout );
    createText( group );
  }

  private void createTreeViewer( Composite parent ) {
    viewer = new TreeViewer( parent, SWT.SINGLE | SWT.VIRTUAL | SWT.BORDER );
    viewer.setLabelProvider( new EnronLabelProvider( parent.getDisplay() ) );
    viewer.setContentProvider( new EnronLazyContentProvider( viewer ) );
    viewer.setInput( new EnronFolder( ROOT ) );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      
      public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection selection = ( IStructuredSelection )event.getSelection();
        Object firstElement = selection.getFirstElement();
        if( firstElement instanceof EnronNode ) {
          nodeSelected( ( EnronNode )firstElement );
        }
      }
    } );
  }

  private void createText( Composite parent ) {
    text = new Text( parent, SWT.MULTI | SWT.WRAP | SWT.BORDER );
  }

  private void nodeSelected( EnronNode selectedNode ) {
    if( selectedNode != null ) {
      File file = selectedNode.getFile();
      if( file.isFile() ) {
        try {
          text.setText( readFromFile( file ) );
        } catch( IOException e ) {
          throw new RuntimeException( "Failed to read from file " + file.getAbsolutePath() );
        }
      }
    }
  }

  private static String readFromFile( File inputFile ) throws IOException {
    StringBuffer resultBuffer = new StringBuffer();
    FileReader reader = new FileReader( inputFile );
    try {
      char[] charBuffer = new char[ 1024 ];
      int charsRead = 0;
      while( ( charsRead = reader.read( charBuffer  ) ) != -1 ) {
        resultBuffer.append( charBuffer, 0, charsRead );
      }
    } finally {
      reader.close();
    }
    return resultBuffer.toString();
  }

  private static final class EnronLabelProvider extends CellLabelProvider {

    private static final String ICON_FILE = "resources/file.png";
    private static final String ICON_FOLDER = "resources/folder.png";

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_OFFSET = 2;
    private static final int COLUMN_TIMEZONE = 1;

    private final Image fileImage;
    private final Image folderImage;

    EnronLabelProvider( final Device device ) {
      fileImage = createImage( device, ICON_FILE );
      folderImage = createImage( device, ICON_FOLDER );
    }

    public void update( final ViewerCell cell ) {
      Object element = cell.getElement();
      if( element instanceof EnronNode ) {
        EnronNode file = ( EnronNode )element;
        int columnIndex = cell.getColumnIndex();
        switch( columnIndex ) {
          case COLUMN_NAME:
            updateName( cell, file );
            break;
          case COLUMN_TIMEZONE:
            updateName( cell, file );
            break;
          case COLUMN_OFFSET:
            updateName( cell, file );
            break;
        }
      }
    }

    public String getToolTipText( final Object element ) {
      String result = "";
      if( element instanceof File ) {
        File file = ( File )element;
        result = file.getName();
      }
      return result;
    }

    private void updateName( ViewerCell cell, EnronNode node ) {
      cell.setText( node.getTitle() );
      cell.setImage( node instanceof EnronFolder ? folderImage : fileImage );
    }

    private static Image createImage( Device device, String name ) {
      ClassLoader classLoader = EnronLabelProvider.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream( name );
      Image result = null;
      if( inputStream != null ) {
        result = new Image( device, inputStream );
      }
      return result;
    }
  }

  private static class EnronLazyContentProvider implements ILazyTreeContentProvider {

    private final TreeViewer viewer;

    public EnronLazyContentProvider( TreeViewer viewer ) {
      this.viewer = viewer;
    }

    public Object getParent( Object element ) {
      Object result = null;
      if( element instanceof EnronNode ) {
        if( !ROOT.equals( element ) ) {
          result = ( ( EnronNode )element ).getParent();
        }
      }
      return result;
    }

    public void updateElement( Object parent, int index ) {
      if( parent instanceof EnronFolder ) {
        EnronFolder folder = ( EnronFolder )parent;
        EnronNode node = folder.getChild( index );
        if( node != null ) {
          viewer.replace( parent, index, node );
          viewer.setChildCount( node, node.getChildCount() );
        }
      }
    }

    public void updateChildCount( Object element, int currentChildCount ) {
      if( element instanceof EnronNode ) {
        EnronNode node = ( EnronNode )element;
        int childCount = node.getChildCount();
        if( childCount != currentChildCount ) {
            viewer.setChildCount( element, childCount );
        }
      }
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }

    public void dispose() {
    }
  }

  static class EnronNode {

    private final EnronFolder parent;
    private final String name;

    public EnronNode( EnronFolder parent, String name ) {
      this.parent = parent;
      this.name = name;
    }

    public boolean hasChildren() {
      return false;
    }

    public int getChildCount() {
      return 0;
    }

    public File getFile() {
      return new File( parent.file, name );
    }

    public String getTitle() {
      return name;
    }

    public EnronFolder getParent() {
      return parent;
    }
  }

  static class EnronFolder extends EnronNode {

    private final File file;
    private final int childCount;
    private EnronNode[] children;

    public EnronFolder( File file ) {
      super( null, null );
      this.file = file;
      readChildrenFromIndex();
      this.childCount = children.length;
    }

    public EnronFolder( EnronFolder parent, String name, int count ) {
      super( parent, name );
      this.file = new File( parent.file, name );
      this.childCount = count;
    }

    public boolean hasChildren() {
      return childCount > 0;
    }

    public int getChildCount() {
      return childCount;
    }

    public EnronNode getChild( int index ) {
      readChildrenFromIndex();
      return children[ index ];
    }

    public EnronNode[] getChildren() {
      readChildrenFromIndex();
      return children;
    }

    private void readChildrenFromIndex() {
      if( children == null ) {
        try {
          children = readIndex();
          if( childCount != 0 && children.length != childCount ) {
            throw new RuntimeException( "Children count in index ("
                                        + children.length
                                        + ") does not match default ("
                                        + childCount
                                        + "): "
                                        + file.getAbsolutePath()
                                        + " " );
          }
        } catch( IOException e ) {
          throw new RuntimeException( "Failed to read index for " + file.getAbsolutePath() );
        }
      }
    }

    private EnronNode[] readIndex() throws IOException {
      File indexFile = new File( file, ".index" );
      String indexString = readFromFile( indexFile );
      String[] lines = indexString.split( "\n" );
      List<EnronNode> nodes = new ArrayList<EnronNode>();
      for( int i = 0; i < lines.length; i++ ) {
        String line = lines[ i ];
        String[] parts = line.split( "\t" );
        if( parts.length == 3 ) {
          if( "d".equals( parts[ 0 ] ) ) {
            nodes.add( new EnronFolder( this, parts[ 1 ], Integer.parseInt( parts[ 2 ] ) ) );
          } else if( "f".equals( parts[ 0 ] ) ) {
            nodes.add( new EnronNode( this, parts[ 1 ] ) );
          }
        }
      }
      EnronNode[] result = new EnronNode[ nodes.size() ];
      nodes.toArray( result );
      return result;
    }
  }
}
