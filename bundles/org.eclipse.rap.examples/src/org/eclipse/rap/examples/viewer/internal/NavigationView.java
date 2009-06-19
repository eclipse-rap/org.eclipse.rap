/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.viewer.internal;

import java.util.Arrays;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.viewer.internal.model.ExamplesModel;
import org.eclipse.rap.examples.viewer.internal.model.ExampleCategory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;


public class NavigationView extends ViewPart {

  public static final String ID
    = "org.eclipse.rap.examples.viewer.navigationView";
  
  private ExpandBar expandBar;

  private Object selectedElement;

  private SelectionListener listSelectionListener = new ListSelectionListener();

  private SelectionProvider selectionProvider = new SelectionProvider();

  public void createPartControl( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    createExpandBar( parent );
    fillExpandBar( ExamplesModel.getInstance() );
    getSite().setSelectionProvider( selectionProvider );
    initSelection();
    getSite().getShell().getDisplay().asyncExec( new Runnable() {
      public void run() {
        getSite().getPage().activate( NavigationView.this );
      }
    } );
  }

  public void setFocus() {
    expandBar.setFocus();
  }

  private void createExpandBar( final Composite parent ) {
    expandBar = new ExpandBar( parent, SWT.V_SCROLL );
    expandBar.setSpacing( 2 );
    expandBar.addExpandListener( new ExpandListener() {
      
      public void itemCollapsed( final ExpandEvent e ) {
        ExpandItem item = ( ExpandItem )e.item;
        List list = ( List )item.getControl();
        list.deselectAll();
      }
      
      public void itemExpanded( final ExpandEvent e ) {
        ExpandItem[] items = expandBar.getItems();
        for( int i = 0; i < items.length; i++ ) {
          ExpandItem item = items[ i ];
          if( item != e.item ) {
            item.setExpanded( false );
            List list = ( List )item.getControl();
            list.deselectAll();
          }
        }
      }
    } );
    parent.addControlListener( new ControlAdapter() {

      public void controlResized( ControlEvent e ) {
        ExpandItem[] items = expandBar.getItems();
        for( int i = 0; i < items.length; i++ ) {
          ExpandItem item = items[ i ];
          List list = ( List )item.getControl();
          item.setHeight( list.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
        }
      }
    } );
  }

  private void fillExpandBar( final ExamplesModel model ) {
    ExampleCategory[] categories = model.getCategories();
    Arrays.sort( categories );
    for( int i = 0; i < categories.length; i++ ) {
      ExampleCategory category = categories[ i ];
      ExpandItem item = createCategoryItem( category );
      List list = ( List )item.getControl();
      list.setItems( model.getExamplesInCategory( category ) );
      item.setHeight( list.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    }
  }

  private ExpandItem createCategoryItem( final ExampleCategory category ) {
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    item.setText( category.getName() );
    List list = new List( expandBar, SWT.SINGLE );
    list.addSelectionListener( listSelectionListener );
    item.setControl( list );
    return item;
  }
  
  private void initSelection() {
    if( expandBar.getItemCount() > 0 ) {
      ExpandItem firstItem = expandBar.getItem( 0 );
      firstItem.setExpanded( true );
      List list = ( List )firstItem.getControl();
      list.setSelection( 0 );
      setSelection( list.getItem( 0 ) );
//      selectionProvider.fireSelectionChanged();
    }
  }

  private void setSelection( final Object newSelection ) {
    boolean changed = selectedElement == null
                      ? newSelection != null
                      : !selectedElement.equals( newSelection );
    if( changed ) {
      selectedElement = newSelection;
      selectionProvider.fireSelectionChanged();
    }
  }

  private final class ListSelectionListener extends SelectionAdapter {

    public void widgetSelected( final SelectionEvent e ) {
      List list = ( List )e.widget;
      int index = list.getSelectionIndex();
      setSelection( index == -1 ? null : list.getItem( index ) );
    }
  }

  private final class SelectionProvider implements ISelectionProvider {
  
    private ListenerList selectionChangedListeners = new ListenerList();
  
    public void addSelectionChangedListener( ISelectionChangedListener listener )
    {
      selectionChangedListeners.add( listener );
    }
    
    public void removeSelectionChangedListener( ISelectionChangedListener listener )
    {
      selectionChangedListeners.remove( listener );
    }

    public ISelection getSelection() {
      ISelection result;
      if( selectedElement == null ) {
        result = StructuredSelection.EMPTY;
      } else {
        result = new StructuredSelection( selectedElement );
      }
      return result;
    }
  
    public void setSelection( ISelection selection ) {
      throw new UnsupportedOperationException();
    }

    void fireSelectionChanged() {
      ISelection selection = getSelection();
      final SelectionChangedEvent event
        = new SelectionChangedEvent( this, selection );
      Object[] listeners = selectionChangedListeners.getListeners();
      for( int i = 0; i < listeners.length; ++i ) {
        final ISelectionChangedListener l
          = ( ISelectionChangedListener )listeners[ i ];
        SafeRunnable.run( new SafeRunnable() {

          public void run() {
            l.selectionChanged( event );
          }
        } );
      }
    }
  }
}
