/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public final class ScrolledCompositeTab extends ExampleTab {

  private ScrolledComposite composite;
  private Composite content;
  private Button showFocusedControl;
  private Button alwaysShowScrollBars;

  public ScrolledCompositeTab( final CTabFolder topFolder ) {
    super( topFolder, "Scrolled Composite" );
    setDefaultStyle( SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER, true );
    createStyleButton( "H_SCROLL", SWT.H_SCROLL, true );
    createStyleButton( "V_SCROLL", SWT.V_SCROLL, true );
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    alwaysShowScrollBars = createAlwaysShowScrollBarsButton();
    showFocusedControl = createShowFocusedControlButton();
    createShowControlButton();
    createFocusControlButton();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    composite = new ScrolledComposite( parent, getStyle() );
    composite.setLayoutData( new GridData( GridData.FILL,
                                           GridData.FILL,
                                           true,
                                           true ) );
    content = new Composite( composite, SWT.NONE );
    content.setLayout( new GridLayout( 5, true ) );
    for( int i = 0; i < 100; i++ ) {
      Button b = new Button( content, SWT.PUSH );
      b.setText( "Button " + i );
      GridData data = new GridData();
      data.widthHint = 120;
      b.setLayoutData( data );
    }
    composite.setContent( content );
    composite.setExpandHorizontal( true );
    composite.setExpandVertical( true );
    if( alwaysShowScrollBars != null ) {
      composite.setAlwaysShowScrollBars( alwaysShowScrollBars.getSelection() );
    }
    if( showFocusedControl != null ) {
      composite.setShowFocusedControl( showFocusedControl.getSelection() );
    }
    composite.addControlListener( new ControlAdapter() {

      public void controlResized( ControlEvent e ) {
        Point size = content.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
        composite.setMinSize( size );
      }
    } );
    registerControl( composite );
  }

  private void createShowControlButton() {
    Button btnA = new Button( styleComp, SWT.PUSH );
    btnA.setText( "Show Button 89" );
    btnA.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        composite.showControl( content.getChildren()[ 89 ] );
      }
    } );
  }
  
  private void createFocusControlButton() {
    Button btnA = new Button( styleComp, SWT.PUSH );
    btnA.setText( "Focus Button 89" );
    btnA.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        content.getChildren()[ 89 ].setFocus();
      }
    } );
  }
  
  private Button createAlwaysShowScrollBarsButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Always Show ScrollBars" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        composite.setAlwaysShowScrollBars( button.getSelection() );
      }
    } );
    return button;
  }
  
  private Button createShowFocusedControlButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Show Focused Control" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        composite.setShowFocusedControl( button.getSelection() );
      }
    } );
    return button;
  }
}
