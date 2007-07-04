/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.lifecycle.UICallBackUtil;
import org.eclipse.swt.widgets.*;

public class ProgressBarTab extends ExampleTab {

  private static final int COUNT = 20;
//  private static final int COUNT = 2;

  public ProgressBarTab( final CTabFolder parent ) {
    this( parent, "ProgressBar" );
  }

  public ProgressBarTab( final CTabFolder parent, final String title ) {
    super( parent, title );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle() == 0 ? SWT.HORIZONTAL : getStyle();
    
    final ProgressBar progressBar = new ProgressBar( parent, style );
    progressBar.setLocation( 10, 10 );
    progressBar.setSize( 200, 22 );
    progressBar.setMaximum( COUNT );
    
    final Button button = new Button( parent, SWT.PUSH );
    button.setText( "Start Background Process" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent evt ) {
        button.setEnabled( false );
        // activate UI-callback mechanism
        UICallBackUtil.activateUICallBack( ProgressBarTab.class.getName() );
        // create and start background thread that updates the progress bar
        Thread thread = new Thread( createRunnable( progressBar, button ) );
        thread.setDaemon( true );
        thread.start();
        
      }
    } );
    button.pack();
    Rectangle bounds = progressBar.getBounds();
    int top = bounds.y + bounds.height + 10;
    button.setLocation( bounds.x, top );
    
//    Button button2 = new Button( parent, SWT.PUSH );
//    button2.setText( "create content" );
//    final int[] count = new int[ 1 ];
//    final Composite[] panel = new Composite[ 1 ];
//    button2.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( final SelectionEvent evt ) {
//        createContent( progressBar, panel, count[ 0 ]++ );
//      }
//    } );
//    button2.pack();
//    button2.setLocation( bounds.x + button.getBounds().width + 5, top );
  }

  private Runnable createRunnable( final ProgressBar progressBar,
                                   final Button button )
  {
    final int maximum = progressBar.getMaximum();
    final Display display = progressBar.getDisplay();
    Runnable result = new Runnable() {
      public void run() {
        final Composite panel[] = new Composite[ 1 ];
        for( int i = 0; i <= maximum; i++ ) {
          final int selection = i;
          try {
            // simulate some work
            Thread.sleep( 250 );
          } catch( final Throwable shouldNotHappen ) {
            shouldNotHappen.printStackTrace();
          }
          // perform process bar update
          display.syncExec( new Runnable() {
            public void run() {
              if( !progressBar.isDisposed() ) {
//                createContent( progressBar, panel, selection );
                progressBar.setSelection( selection );
                if( selection == maximum ) {
                  button.setEnabled( true );
                  // deactivate UI-callback mechanism
                  String id = ProgressBarTab.class.getName();
                  UICallBackUtil.deactivateUICallBack( id );
                  if( panel[ 0 ] != null ) {
                    panel[ 0 ].dispose();
                  }
                }
              }
            }
          } );
        }
      }
    };
    return result;
  }

  protected void createStyleControls( Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL, true );
    createStyleButton( "VERTICAL", SWT.VERTICAL, false );
    createStyleButton( "INDETERMINATE", SWT.INDETERMINATE, false );
  }
//
//  private void createContent( final ProgressBar progressBar,
//                              final Composite[] panel,
//                              final int selection )
//  {
//    if( panel[ 0 ] != null ) {
//      panel[ 0 ].dispose();
//    }
//    Composite parent = progressBar.getParent();
//    panel[ 0 ] = new Composite( parent, SWT.None );
//    Composite composite = panel[ 0 ];
//    GridLayout gridLayout = new GridLayout( 9, true );
//    composite.setLayout( gridLayout );
//    composite.setBounds( 10, 80, 700, 500 );
//    
//    for( int j = 0; j < 39; j++ ) {
//      boolean useDifferentAttributes = j > 19;
//      Label label = new Label( composite, SWT.None );
//      setFont( label, j );
//      createPopUpMenu( label );
//      ActivateEvent.addListener( label, new ActivateListener() {
//        public void activated( ActivateEvent event ) {
//        }
//        public void deactivated( ActivateEvent event ) {
//        }
//      } );
//      label.setText( "L" + selection + ":" + j );
//      if( j > 20 ) {
//        ClassLoader loader = ProgressBarTab.class.getClassLoader();
//        label.setImage( Image.find( "resources/button-image.gif", loader ) );
//        label.setToolTipText( "tooltip" );
//      }
//      
//      int textStyle = useDifferentAttributes ? SWT.NONE : SWT.BORDER;
//      textStyle |= j > 10 ? ( j > 30 ? SWT.PASSWORD : SWT.MULTI ) : SWT.NONE;
//      Text text = new Text( composite, textStyle );
//      text.addFocusListener( new FocusAdapter() {
//        public void focusGained( final FocusEvent event ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "text focus gained", 
//                                         null );
//        }
//      } );
//      setFont( text, j );
//      createPopUpMenu( text );
//      text.setText(  "T" + selection + ":" +  j );
//      text.addModifyListener( new ModifyListener() {
//        public void modifyText( final ModifyEvent event ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "Text modified.",
//                                         null );
//        }
//      } );
//      text.addSelectionListener( new SelectionAdapter() {
//        public void widgetSelected( final SelectionEvent e ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "Text selected.",
//                                         null );
//
//        }
//        public void widgetDefaultSelected( final SelectionEvent e ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "Text default selected.",
//                                         null );
//        }
//      } );
//      
//      
//      int buttonStyle = useDifferentAttributes ? SWT.NONE : SWT.BORDER;
//      buttonStyle |= j > 10 ? ( j > 30 ? SWT.RADIO : SWT.CHECK ) : SWT.PUSH;
//      Button button = new Button( composite, buttonStyle );
//      button.addFocusListener( new FocusAdapter() {
//        public void focusGained( final FocusEvent event ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "button focus gained", 
//                                         null );
//        }
//      } );
//      button.setText( "B" + selection + ":" + j );
//      if( j < 5 ) {
//        ClassLoader loader = ProgressBarTab.class.getClassLoader();
//        button.setImage( Image.find( "resources/button-image.gif", loader ) );
//      }
//      setFont( button, j );
//      createPopUpMenu( button );
//      button.addSelectionListener( new SelectionAdapter() {
//        public void widgetSelected( final SelectionEvent e ) {
//          MessageDialog.openInformation( progressBar.getShell(), 
//                                         "Info", 
//                                         "Button selected.",
//                                         null );
//        }
//      } );
//      
//      if( useDifferentAttributes ) {
//        Color bgColor = Color.getColor( 240, 240, 12 * ( j - 19 ) );
//        Color foreColor = Color.getColor( 240 - ( 12 * ( j - 19 ) ), 0, 0 );
//        label.setBackground( bgColor );
//        label.setForeground( foreColor );
//        text.setBackground( bgColor );
//        text.setForeground( foreColor );
//        button.setBackground( bgColor );
//        button.setForeground( foreColor );
//      }
//    }
//    composite.layout();
//  }
//
//  private void setFont( final Control control, final int index ) {
//    FontData current = control.getFont().getFontData()[ 0 ];
//    Font newFont = Font.getFont( current.getName(),
//                                 current.getHeight() + index, 
//                                 current.getStyle() );
//    control.setFont( newFont );
//  }

//  private void createPopUpMenu( final Control control ) {
//    Menu menu = new Menu(  control );
//    control.setMenu( menu );
//    MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
//    menuItem.setText( "push me" );
//    menuItem.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( SelectionEvent e ) {
//        MessageDialog.openWarning( control.getShell(), 
//                                   "Warning", 
//                                   "Dondodisagän",
//                                   null );
//      }
//    } );
//  }
}
