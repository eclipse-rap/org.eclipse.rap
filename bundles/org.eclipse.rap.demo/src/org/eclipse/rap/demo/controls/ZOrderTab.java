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

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ZOrderTab extends ExampleTab {

  public ZOrderTab( TabFolder folder ) {
    super( folder, "Z-Order" );
  }

  void createStyleControls( ) {
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout( RWT.VERTICAL ) );
    final Composite comp = new Composite( top, RWT.NONE );
    final Button b1 = new Button( comp, RWT.PUSH );
    b1.setText( "B1" );
    b1.setBounds( 20, 20, 100, 100 );
    final Button b2 = new Button( comp, RWT.PUSH );
    b2.setText( "B2" );
    b2.setBounds( 100, 50, 100, 100 );
    final Button b3 = new Button( comp, RWT.PUSH );
    b3.setText( "B3" );
    b3.setBounds( 180, 80, 100, 100 );
    Button above2 = new Button( comp, RWT.PUSH );
    above2.setText( "B2 above all" );
    above2.setBounds( 110, 220, 80, 25 );
    above2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveAbove( null );
        printChildren( comp );
      }
    } );
    Button below2 = new Button( comp, RWT.PUSH );
    below2.setText( "B2 below all" );
    below2.setBounds( 110, 250, 80, 25 );
    below2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveBelow( null );
        printChildren( comp );
      }
    } );
    Button above21 = new Button( comp, RWT.PUSH );
    above21.setText( "B2 above B1" );
    above21.setBounds( 25, 220, 80, 25 );
    above21.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveAbove( b1 );
        printChildren( comp );
      }
      
    } );
    Button below32 = new Button( comp, RWT.PUSH );
    below32.setText( "B2 below B1" );
    below32.setBounds( 25, 250, 80, 25 );
    below32.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveBelow( b1 );
        printChildren( comp );
      }
    } );
    Button above23 = new Button( comp, RWT.PUSH );
    above23.setText( "B2 above B3" );
    above23.setBounds( 195, 220, 80, 25 );
    above23.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveAbove( b3 );
        printChildren( comp );
      }
      
    } );
    Button below23 = new Button( comp, RWT.PUSH );
    below23.setText( "B2 below B3" );
    below23.setBounds( 195, 250, 80, 25 );
    below23.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        b2.moveBelow( b3 );
        printChildren( comp );
      }
    } );
  }

  private void printChildren( Composite comp ) {
    Control[] children = comp.getChildren();
    StringBuffer sb = new StringBuffer();
    for( int i = 0; i < children.length; i++ ) {
      sb.append( ((Button) children[ i ]).getText() + " " );
    }
  }
}
