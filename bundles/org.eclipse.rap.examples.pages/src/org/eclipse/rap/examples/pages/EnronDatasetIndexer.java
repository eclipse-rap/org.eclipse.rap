/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
import java.util.*;


public class EnronDatasetIndexer {

  private static final File ROOT = new File( "/data/enron/maildir" );
  private int fileCount;
  private int dirCount;

  public static void main( String[] args ) {
    try {
      EnronDatasetIndexer indexer = new EnronDatasetIndexer();
      indexer.index( ROOT );
      System.out.println( "Directory Count: " + indexer.dirCount );
      System.out.println( "File Count: " + indexer.fileCount );
      System.out.println( "Total: " + ( indexer.dirCount + indexer.fileCount ) );
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  public EnronDatasetIndexer() {
    fileCount = 0;
    dirCount = 0;
  }

  private int index( File file ) throws IOException {
    File[] children = file.listFiles();
    if( children == null ) {
      throw new RuntimeException( "no child count available for " + file.getAbsolutePath() );
    }
    List<FileEntry> list = new ArrayList<FileEntry>();
    int count = 0;
    for( int i = 0; i < children.length; i++ ) {
      File child = children[ i ];
      if( !".index".equals( child.getName() ) ) {
        count++;
        count( child );
        if( child.isDirectory() ) {
          int childCount = index( child );
          list.add( new FileEntry( child.getName(), 'd', childCount ) );
        } else {
          list.add( new FileEntry( child.getName(), 'f', 0 ) );
        }
      }
    }
    createIndexFile( file, list );
    return count;
  }

  private void count( File file ) {
    if( file.isDirectory() ) {
      dirCount++;
    } else {
      fileCount++;
    }
  }

  private static void createIndexFile( File file, List<FileEntry> list ) throws IOException {
    File indexFile = new File( file, ".index" );
    sortFileList( list );
    String string = createString( list );
    writeToFile( indexFile, string );
  }

  private static void sortFileList( List<FileEntry> list ) {
    Collections.sort( list, new Comparator<FileEntry>() {

      public int compare( FileEntry file1, FileEntry file2 ) {
        if( file1.type < file2.type ) {
          return -1;
        }
        if( file1.type > file2.type ) {
          return 1;
        }
        return file1.name.compareTo( file2.name );
      }
    } );
  }

  private static String createString( List list ) {
    StringBuffer buffer = new StringBuffer();
    for( Iterator iterator = list.iterator(); iterator.hasNext(); ) {
      FileEntry file = ( FileEntry )iterator.next();
      buffer.append( file.type );
      buffer.append( "\t" );
      buffer.append( file.name );
      buffer.append( "\t" );
      buffer.append( file.count );
      buffer.append( "\n" );
    }
    return buffer.toString();
  }

  private static void writeToFile( File file, String string ) throws IOException {
    BufferedWriter writer = new BufferedWriter( new FileWriter( file ) );
    try {
      writer.write( string );
    } finally {
      writer.close();
    }
  }

  private static class FileEntry {
    final String name;
    final int count;
    final char type;
    
    FileEntry( String name, char type, int count ) {
      this.name = name;
      this.count = count;
      this.type = type;
    }
  }
}
