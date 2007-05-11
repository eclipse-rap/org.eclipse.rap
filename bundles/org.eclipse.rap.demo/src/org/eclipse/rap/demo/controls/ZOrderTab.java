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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class ZOrderTab extends ExampleTab {

  private Label label;

  public ZOrderTab( final TabFolder folder ) {
    super( folder, "Z-Order" );
  }

  protected void createStyleControls() {
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout( SWT.VERTICAL ) );
    final Composite comp = new Composite( top, SWT.NONE );
    final Composite bcomp = new Composite( comp, SWT.NONE );
    bcomp.setBounds( 0, 0, 300, 200 );
    final Label labelA = new Label( bcomp, SWT.BORDER | SWT.CENTER );
    labelA.setText( "A" );
    labelA.setBounds( 20, 20, 100, 100 );
    labelA.setBackground( BG_COLOR_BLUE );
    final Label labelB = new Label( bcomp, SWT.BORDER | SWT.CENTER );
    labelB.setText( "B" );
    labelB.setBounds( 100, 50, 100, 100 );
    labelB.setBackground( BG_COLOR_GREEN );
    final Label labelC = new Label( bcomp, SWT.BORDER | SWT.CENTER );
    labelC.setText( "C" );
    labelC.setBounds( 180, 80, 100, 100 );
    labelC.setBackground( BG_COLOR_BROWN );
    label = new Label( comp, SWT.CENTER );
    label.setBounds( 25, 190, 250, 20 );
    printChildren( bcomp );
    Button aboveA = new Button( comp, SWT.PUSH );
    aboveA.setText( "B above A" );
    aboveA.setBounds( 25, 220, 80, 25 );
    aboveA.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveAbove( labelA );
        printChildren( bcomp );
      }
    } );
    Button belowA = new Button( comp, SWT.PUSH );
    belowA.setText( "B below A" );
    belowA.setBounds( 25, 250, 80, 25 );
    belowA.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveBelow( labelA );
        printChildren( bcomp );
      }
    } );
    Button aboveAll = new Button( comp, SWT.PUSH );
    aboveAll.setText( "B above all" );
    aboveAll.setBounds( 110, 220, 80, 25 );
    aboveAll.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveAbove( null );
        printChildren( bcomp );
      }
    } );
    Button belowAll = new Button( comp, SWT.PUSH );
    belowAll.setText( "B below all" );
    belowAll.setBounds( 110, 250, 80, 25 );
    belowAll.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveBelow( null );
        printChildren( bcomp );
      }
    } );
    Button aboveC = new Button( comp, SWT.PUSH );
    aboveC.setText( "B above C" );
    aboveC.setBounds( 195, 220, 80, 25 );
    aboveC.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveAbove( labelC );
        printChildren( bcomp );
      }
      
    } );
    Button belowC = new Button( comp, SWT.PUSH );
    belowC.setText( "B below C" );
    belowC.setBounds( 195, 250, 80, 25 );
    belowC.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelB.moveBelow( labelC );
        printChildren( bcomp );
      }
    } );
    comp.setTabList( new Control[]{
      aboveA, aboveAll, aboveC, belowA, belowAll, belowC
    } );
  }

  private void printChildren( final Composite comp ) {
    Control[] children = comp.getChildren();
    StringBuffer sb = new StringBuffer( "Z-Order: " );
    for( int i = 0; i < children.length; i++ ) {
      sb.append( ((Label) children[ i ]).getText() + " " );
    }
    label.setText( sb.toString() );
  }
}
