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

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import com.eclipsesource.oscilloscope.*;


public class OscilloscopeExample implements IExamplePage {

  private Oscilloscope[] oscopes;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Native Oscilloscope Widget" );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    createOscilloscopes( group );
    createStartStopButton( group );
  }

  private Oscilloscope[] createOscilloscopes( Composite parent ) {
    int count = 3;
    int width = 600;
    int height = 100;
    oscopes = new Oscilloscope[ count ];
    for( int i = 0; i < oscopes.length; i++ ) {
      Oscilloscope oscope = new Oscilloscope( parent, SWT.BORDER );
      // NOTE [tb] : resizing the widget is currently strongly discouraged
      oscope.setLayoutData( new GridData( width, height ) );
      oscope.setDataProvider( getDataProvider( i, height ) );
      Menu menu = createContextMenu( oscope );
      oscope.setMenu( menu );
      oscopes[ i ] = oscope;
    }
    return oscopes;
  }

  private Menu createContextMenu( final Oscilloscope oscope ) {
    Menu menu = new Menu( oscope );
    MenuItem colorItem = new MenuItem( menu, SWT.PUSH );
    colorItem.setText( "invert colors" );
    colorItem.addSelectionListener( new SelectionAdapter() {

      private boolean inverted;
      private Color white = oscope.getDisplay()
        .getSystemColor( SWT.COLOR_WHITE );
      private Color black = oscope.getDisplay()
        .getSystemColor( SWT.COLOR_BLACK );

      public void widgetSelected( SelectionEvent e ) {
        if( inverted ) {
          oscope.setBackground( null );
          oscope.setForeground( null );
        } else {
          oscope.setBackground( white );
          oscope.setForeground( black );
        }
        inverted = !inverted;
      }
    } );
    return menu;
  }

  private int getSpeed( int oscopeNr ) {
    return oscopeNr < 2 ? 25 : 50;
  }

  private DataProvider getDataProvider( int oscopeNr, int maxValue ) {
    DataProvider dataProvider;
    if( oscopeNr == 0 ) {
      dataProvider = new DummyDataProvider( maxValue, 0 );
    } else if( oscopeNr == 1 ) {
      dataProvider = new DummyDataProvider( maxValue, 0 );
    } else {
      dataProvider = new SecondaryDataProvider( maxValue );
    }
    return dataProvider;
  }

  private void createStartStopButton( Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setText( "start" );
    button.addSelectionListener( new SelectionAdapter() {

      boolean running = false;

      public void widgetSelected( SelectionEvent e ) {
        running = !running;
        for( int i = 0; i < oscopes.length; i++ ) {
          if( running ) {
            oscopes[ i ].start( getSpeed( i ) );
          } else {
            oscopes[ i ].stop();
          }
        }
        button.setText( running ? "stop" : "start" );
      }
    } );
  }

  private final class DummyDataProvider implements DataProvider {

    private final int maxValue;
    private final int noise;
    private double lastX;

    public DummyDataProvider( int maxValue, int noise ) {
      this.maxValue = maxValue;
      this.noise = noise;
    }

    public Data readData( int position ) {
      DataBuffer buffer = new DataBuffer( 200 );
      for( int i = 0; i < 200; i++ ) {
        double value = 0.5 + Math.sin( lastX ) * 0.5 * Math.cos( lastX / 10 );
        value = Math.round( value * maxValue );
        value += ( Math.random() - 0.5 ) * noise;
        buffer.append( i, ( int )value );
        lastX += 0.5;
      }
      return buffer.createData();
    }
  }

  private final class SecondaryDataProvider implements DataProvider {

    private int maxValue;

    public SecondaryDataProvider( int maxValue ) {
      this.maxValue = maxValue;
    }

    public Data readData( int position ) {
      DataBuffer buffer = new DataBuffer( 200 );
      for( int i = 0; i < 200; i++ ) {
        int absolutePos = position + i;
        int x = absolutePos % 50;
        int value = ( int )( x < 30 ? maxValue * 0.3 : maxValue * 0.7 );
        buffer.append( i, value );
      }
      return buffer.createData();
    }
  }
}
