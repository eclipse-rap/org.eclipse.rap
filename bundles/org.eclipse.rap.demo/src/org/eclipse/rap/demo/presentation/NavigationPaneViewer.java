/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.presentation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.PageBook;

public class NavigationPaneViewer
  extends Composite
  implements ISelectionProvider, ISelectionChangedListener
{
  private static final Color COLOR_WHITE = Graphics.getColor( 255, 255, 255 );
  private final static int BUTTON_HEIGHT = 30;
  private Label title;
  private PageBook pageBook;
  private NavigationPaneContent[] content = {}; 
  private Composite selectorArea;
  private Set selectionListener = new HashSet();
  private ISelection selection = StructuredSelection.EMPTY;


  private final class Selector extends SelectionAdapter {
    private final NavigationPaneContent page;
    private Selector( final NavigationPaneContent page ) {
      this.page = page;
    }
    public void widgetSelected( SelectionEvent e ) {
      for( int i = 0; i < content.length; i++ ) {
        if( content[ i ].isSelectionProvider() ) {
          ISelectionProvider provider = content[ i ].getSelectionProvider();
          provider.removeSelectionChangedListener( NavigationPaneViewer.this );
        }
        Object selector = content[ i ].getSelector();
        ( ( Button )selector ).setSelection( content[ i ] == page );
      }
      pageBook.showPage( page.getControl() );
      title.setText( page.getLabel() );
      page.getControl().setFocus();
      if( page.isSelectionProvider() ) {
        ISelectionProvider provider = page.getSelectionProvider();
        provider.addSelectionChangedListener( NavigationPaneViewer.this );
        setSelection( provider.getSelection() );
      } else {
        setSelection( StructuredSelection.EMPTY );
      }
    }
  }


  public NavigationPaneViewer( final Composite parent,
                               final int style,
                               final NavigationPaneContent[] content )
  {
    super( parent, style );
    if( content != null ) {
      this.content = content;
    }
    createControl();
  }
  

  private void createControl() {
    this.setLayout( new FormLayout() );    
    createTitleArea( this );
    createContentArea( this );
    createSelectorArea( this );
  }
  
  private void createTitleArea( final Composite parent ) {
    title = new Label( parent, SWT.NONE );
    title.setBackground( COLOR_WHITE );
    FontData fontData = title.getFont().getFontData()[ 0 ];
    Font titleFont = Graphics.getFont( fontData.getName(),
                                       18,
                                       fontData.getStyle() | SWT.BOLD );
    title.setFont( titleFont );
    FormData fd = new FormData();
    title.setLayoutData( fd );
    fd.top = new FormAttachment( 0, 0 );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    fd.bottom = new FormAttachment( 0, BUTTON_HEIGHT );
    title.setText( "Trallala" );
  }

  private void createContentArea( final Composite parent ) {
    pageBook = new PageBook( parent, SWT.NONE );
    FormData fd = new FormData();
    pageBook.setLayoutData( fd );
    fd.top = new FormAttachment( 0, BUTTON_HEIGHT + 1 );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    int bottom = -BUTTON_HEIGHT * ( content.length + 1 ) - 1;
    fd.bottom = new FormAttachment( 100, bottom );
    pageBook.setBackground( COLOR_WHITE );
    
    for( int i = 0; i < content.length; i++ ) {
      createPage( i );
    }    
  }

  private Control createPage( final int pageIndex ) {
    Composite result = new Composite( pageBook, SWT.NONE );
    result.setBackground( COLOR_WHITE );
    result.setLayout( new FillLayout() );
    content[ pageIndex ].setControl( result );
    content[ pageIndex ].createControl( result );
    return result;
  }
  
  private void createSelectorArea( final Composite parent ) {
    selectorArea = new Composite( parent, SWT.NONE );
    FormData fd = new FormData();
    selectorArea.setLayoutData( fd );
    fd.top = new FormAttachment( 100, -BUTTON_HEIGHT * ( content.length + 1 ) );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    fd.bottom = new FormAttachment( 100, 0 );
    FillLayout fillLayout = new FillLayout( SWT.VERTICAL );
    fillLayout.spacing = - 1;
    selectorArea.setLayout( fillLayout );
    
    for( int i = 0; i < content.length; i++ ) {
      createSelector( i );
    }
    
    Label label = new Label( selectorArea, SWT.NONE );
    
    if( content.length > 0 ) {
      new Selector( content[ 0 ] ).widgetSelected( null );
    }
    
  }


  private void createSelector( int i ) {
    Button button = new Button( selectorArea, SWT.TOGGLE | SWT.FLAT );
    content[ i ].setSelector( button );
    button.setText( content[ i ].getLabel() );
    button.addSelectionListener( new Selector( content[ i ] ) );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "mybutton" );
    button.setSelection( false );
  }

  
  ////////////////////////////////
  // interface ISelectionProvider

  public void addSelectionChangedListener(
    final ISelectionChangedListener listener )
  {
    selectionListener.add( listener );
  }


  public void removeSelectionChangedListener(
    final ISelectionChangedListener listener )
  {
    selectionListener.remove( listener );
  }
  
  public ISelection getSelection() {
    return selection;
  }
  
  public void setSelection( final ISelection newSelection ) {
    ISelection oldSelection = selection;
    if( newSelection == null ) {
      selection = StructuredSelection.EMPTY;
    } else {
      selection = newSelection;
    }
    if( !oldSelection.equals( selection ) ) {
      SelectionChangedEvent evt = new SelectionChangedEvent( this, selection );
      Object[] listeners = selectionListener.toArray();
      for( int i = 0; i < listeners.length; i++ ) {
        try {
          ISelectionChangedListener lsnr
            = ( ISelectionChangedListener )listeners[ i ];
          lsnr.selectionChanged( evt );
        } catch( final RuntimeException re ) {
          // TODO Auto-generated catch block
          re.printStackTrace();
        }
      }
    }
  }
  
  
  ///////////////////////////////
  // interface ISelectionListener
  
  public void selectionChanged( final SelectionChangedEvent event ) {
    setSelection( event.getSelection() );
  }
}