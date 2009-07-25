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

  private static String[] SERIES_NAMES = new String[] {
    "Unknown",
    "Alkali metal",
    "Alkaline earth metal",
    "Halogen",
    "Noble gas",
    "Lanthanide",
    "Actinide",
    "Transition metal",
    "Poor metal",
    "Metalloid",
    "Nonmetal",
  };

  private static String[] SERIES_IDS = new String[] {
    "Unknown",
    "AM",
    "AEM",
    "H",
    "NG",
    "L",
    "A",
    "TM",
    "M",
    "MO",
    "NM",
  };
  
  private static List elements = new ArrayList();

  static {
    ClassLoader classLoader = Elements.class.getClassLoader();
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
          int group = Integer.parseInt( parts[ 1 ] );
          int period = Integer.parseInt( parts[ 2 ] );
          String symbol = parts[ 3 ];
          String name = parts[ 4 ];
          int series = readCategory( parts[ 5 ] );
          Element element
            = new Element( number, period, group, series, symbol, name );
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

  public static int readCategory( final String string ) {
    int result = 0;
    for( int i = 1; i < SERIES_IDS.length && result == 0; i++ ) {
      if( SERIES_IDS[ i ].equals( string ) ) {
        result = i;
      }
    }
    return result;
  }

  public static List getElements() {
    return new ArrayList( elements );
  }

  public static class Element {
    public final int number;
    public final int period;
    public final int group;
    public final int series;
    public final String symbol;
    public final String name;

    Element( final int number,
             final int period,
             final int group,
             final int series,
             final String symbol,
             final String name )
    {
      this.number = number;
      this.period = period;
      this.group = group;
      this.series = series;
      this.symbol = symbol;
      this.name = name;
    }

    public String getSeriesName() {
      return SERIES_NAMES[ series ];
    }
  }
}
