/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.Arrays;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.*;
import org.eclipse.rap.examples.internal.model.ExampleCategory;
import org.eclipse.rap.examples.internal.model.ExamplesModel;
import org.eclipse.rwt.*;
import org.eclipse.rwt.events.BrowserHistoryEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;


public class NavigationView extends ViewPart {

  public static final String ID
    = "org.eclipse.rap.examples.navigationView";

  private ExpandBar expandBar;
  private String selectedElement;
  private final SelectionListener listSelectionListener;
  private final SelectionProvider selectionProvider;

  public NavigationView() {
    listSelectionListener = new ListSelectionListener();
    selectionProvider = new SelectionProvider();
  }

  public void createPartControl( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    createExpandBar( parent );
    fillExpandBar( ExamplesModel.getInstance() );
    getSite().setSelectionProvider( selectionProvider );
    initBrowserHistorySupport();
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
// TODO [rst] Auto-collapse mechanism, enable when there are more items
//    expandBar.addExpandListener( new ExpandListener() {
//
//      public void itemCollapsed( final ExpandEvent e ) {
//        ExpandItem item = ( ExpandItem )e.item;
//        List list = ( List )item.getControl();
//        list.deselectAll();
//      }
//
//      public void itemExpanded( final ExpandEvent e ) {
//        ExpandItem[] items = expandBar.getItems();
//        for( int i = 0; i < items.length; i++ ) {
//          ExpandItem item = items[ i ];
//          if( item != e.item ) {
//            item.setExpanded( false );
//            List list = ( List )item.getControl();
//            list.deselectAll();
//          }
//        }
//      }
//    } );
    // workaround to apply TextSizeDetermination results
    parent.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
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
    // TODO [rst] Remove this block when auto-collapse is activated again
    // ----
    item.setExpanded( true );
    list.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        ExpandItem[] items = expandBar.getItems();
        for( int i = 0; i < items.length; i++ ) {
          ExpandItem item = items[ i ];
          List list = ( List )item.getControl();
          if( list != e.widget ) {
            list.deselectAll();
          }
        }
      }
    } );
    // ----
    return item;
  }

  private void initBrowserHistorySupport() {
    IBrowserHistory history = RWT.getBrowserHistory();
    history.addBrowserHistoryListener( new BrowserHistoryListener() {
      public void navigated( final BrowserHistoryEvent event ) {
        setSelection( event.entryId, true );
      }
    } );
  }

  private void initSelection() {
    if( expandBar.getItemCount() > 0 ) {
      ExpandItem firstItem = expandBar.getItem( 0 );
      firstItem.setExpanded( true );
      List list = ( List )firstItem.getControl();
      list.setSelection( 0 );
      setSelection( list.getItem( 0 ), false );
    }
  }

  private void setSelection( final String newSelection, 
                             final boolean updateControl )
  {
    boolean changed;
    if( selectedElement == null ) {
      changed = newSelection != null;
    } else {
      changed = !selectedElement.equals( newSelection );
    }
    if( changed ) {
      selectedElement = newSelection;
      // keep in sync with branding
      String text = "RAP Examples - " + selectedElement;
      RWT.getBrowserHistory().createEntry( selectedElement, text );
      selectionProvider.fireSelectionChanged();
    }
    if( updateControl ) {
      ExpandItem[] expandItems = expandBar.getItems();
      boolean done = false;
      for( int i = 0; !done && i < expandItems.length; i++ ) {
        ExpandItem expandItem = expandItems[ i ];
        List list = ( List )expandItem.getControl();
        String[] listItems = list.getItems();
        int index = indexOf( listItems, newSelection );
        if( index != -1 ) {
          done = true;
          list.setSelection( index );
          expandItems[ i ].setExpanded( true );
        }
      }
    }
  }

  private static int indexOf( final String[] strings, final String string ) {
    int result = -1;
    for( int i = 0; result == -1 && i < strings.length; i++ ) {
      if( string.equals( strings[ i ] ) ) {
        result = i;
      }
    }
    return result;
  }

  private final class ListSelectionListener extends SelectionAdapter {

    public void widgetSelected( final SelectionEvent event ) {
      List list = ( List )event.widget;
      int index = list.getSelectionIndex();
      setSelection( index == -1 ? null : list.getItem( index ), false );
    }
  }

  private final class SelectionProvider implements ISelectionProvider {

    private final ListenerList selectionChangedListeners = new ListenerList();

    public void addSelectionChangedListener(
      final ISelectionChangedListener lsnr )
    {
      selectionChangedListeners.add( lsnr );
    }

    public void removeSelectionChangedListener(
      final ISelectionChangedListener lsnr )
    {
      selectionChangedListeners.remove( lsnr );
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

    public void setSelection( final ISelection selection ) {
      throw new UnsupportedOperationException();
    }

    void fireSelectionChanged() {
      ISelection selection = getSelection();
      final SelectionChangedEvent event
        = new SelectionChangedEvent( this, selection );
      Object[] listeners = selectionChangedListeners.getListeners();
      for( int i = 0; i < listeners.length; ++i ) {
        final ISelectionChangedListener listener
          = ( ISelectionChangedListener )listeners[ i ];
        SafeRunnable.run( new SafeRunnable() {
          public void run() {
            listener.selectionChanged( event );
          }
        } );
      }
    }
  }
}
