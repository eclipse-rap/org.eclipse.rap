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

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Elements {

  private static Elements INSTANCE;

  private List elements = new ArrayList();

  private Elements() {
    ClassLoader classLoader = getClass().getClassLoader();
    String resourceFileName = "resources/elements.csv";
    InputStream stream = classLoader.getResourceAsStream( resourceFileName );
    BufferedInputStream bufferedStream = new BufferedInputStream( stream );
    BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
    try {
      String line;
      while( ( line = reader.readLine() ) != null ) {
        String[] parts = line.split( "\t" );
        if( parts.length > 0 ) {
          int number = Integer.parseInt( parts[ 0 ] );
          String name = parts[ 1 ];
          String symbol = parts[ 2 ];
          Element element = new Element( number, name, symbol );
          elements.add( element );
        }
      }
    } catch( IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        bufferedStream.close();
      } catch( IOException e ) {
        e.printStackTrace();
      }
    }
  }

  public static Elements getInstance() {
    if( INSTANCE == null ) {
      INSTANCE = new Elements();
    }
    return INSTANCE;
  }

  public List getElements() {
    return new ArrayList( elements );
  }

  public static class Element {
    public final int number;
    public final String name;
    public final String symbol;

    public Element( final int number,
                    final String name,
                    final String symbol )
    {
      this.number = number;
      this.name = name;
      this.symbol = symbol;
    }
  }
}
