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

package org.eclipse.rap.rwt.internal.custom.ctabfolderkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.custom.*;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.custom.ICTabFolderAdapter;
import org.eclipse.rap.rwt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.rap.rwt.internal.engine.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class CTabFolderLCA_Test extends TestCase {
  
  public void testLCA() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    CTabItem item = new CTabItem( folder, RWT.NONE );
    
    assertSame( CTabFolderLCA.class,
                folder.getAdapter( ILifeCycleAdapter.class ).getClass() );
    assertSame( CTabItemLCA.class,
                item.getAdapter( ILifeCycleAdapter.class ).getClass() );
  }
  
  public void testPreserveValues() {
    SelectionListener selectionListener = new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    Label label = new Label( folder, RWT.NONE );
    folder.setTopRight( label, RWT.FILL );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( folder );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Object selectionIndex 
      = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_INDEX );
    assertEquals( new Integer( folder.getSelectionIndex() ), selectionIndex );
    Object width = adapter.getPreserved( "width" );
    assertEquals( new Integer( folder.getBounds().width ), width );
    Object minVisible 
      = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMinimizeVisible() ), minVisible );
    Object maxVisible 
      = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMaximizeVisible() ), maxVisible );
    Object tabHeight = adapter.getPreserved( CTabFolderLCA.PROP_TAB_HEIGHT );
    assertEquals( new Integer( folder.getTabHeight() ), tabHeight );
    Object topRight = adapter.getPreserved( CTabFolderLCA.PROP_TOP_RIGHT );
    assertSame( topRight, folder.getTopRight() );
    Object minimized = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZED );
    assertEquals( Boolean.valueOf( folder.getMinimized() ), minimized );
    Object maximized = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZED );
    assertEquals( Boolean.valueOf( folder.getMaximized() ), maximized );
    RWTFixture.clearPreserved();
    folder.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
  }
  
  public void testChangeSelection() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.MULTI );
    CTabFolder folder = new CTabFolder( shell, RWT.MULTI );
    folder.setSize( 100, 100 );
    CTabItem item1 = new CTabItem( folder, RWT.NONE );
    CTabItemControl item1Control = new CTabItemControl( folder, RWT.NONE );
    item1.setControl( item1Control );
    CTabItem item2 = new CTabItem( folder, RWT.NONE );
    CTabItemControl item2Control = new CTabItemControl( folder, RWT.NONE );
    item2.setControl( item2Control );
    
    String displayId = DisplayUtil.getId( display );
    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );

    // Let pass one startup request to init the 'system'
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    
    // The actual test request: item1 is selected, the request selects item2
    folder.setSelection( item1 );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( folderId + ".selectedItemId", item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    lifeCycle.execute();
    assertSame( item2, folder.getSelection() );
    assertEquals( "visible=false", item1Control.markup.toString() );
    assertEquals( "visible=true", item2Control.markup.toString() );
  }
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.MULTI );
    folder.addSelectionListener( listener );
    CTabItem item1 = new CTabItem( folder, RWT.NONE );
    CTabItem item2 = new CTabItem( folder, RWT.NONE );
    
    // Select item1 and fake request that selects item2
    folder.setSelection( item1 );
    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    String name = folderId + "." + CTabFolderLCA.PARAM_SELECTED_ITEM_ID;
    Fixture.fakeRequestParam( name, item2Id );
    RWTFixture.readDataAndProcessAction( folder );
    assertSame( item2, folder.getSelection() );
    assertEquals( "widgetSelected|", log.toString() );
  }
  
  public void testShowListEvent() {
    // Widgets for test
    Display display= new Display();
    Shell shell = new Shell( display, RWT.NONE );
    final CTabFolder folder = new CTabFolder( shell, RWT.SINGLE );
    folder.setSize( 30, 130 );
    CTabItem item1 = new CTabItem( folder, RWT.NONE );
    new CTabItem( folder, RWT.NONE );
    // 
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    final ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    final StringBuffer log = new StringBuffer();
    CTabFolder2Listener listener = new CTabFolder2Adapter() {
      public void showList( final CTabFolderEvent event ) {
        assertEquals( true, event.doit );
        log.append( "showList|" );
      }
    };
    CTabFolder2Listener vetoListener = new CTabFolder2Adapter() {
      public void showList( final CTabFolderEvent event ) {
        Rectangle chevronRect = folderAdapter.getChevronRect();
        Rectangle eventRet 
          = new Rectangle( event.x, event.y, event.width, event.height);
        assertEquals( eventRet, chevronRect );
        assertEquals( true, event.doit );
        assertEquals( folder, event.getSource() );
        log.append( "vetoShowList|" );
        event.doit = false;
      }
    };
    
    // Test showList event with listeners that prevents menu form showing
    // Note: this test must run first since it relies on the fact that the 
    //       showList menu wan't populated by previous showList requests
    folder.setSelection( item1 );
    folder.addCTabFolder2Listener( vetoListener );
    String folderId = WidgetUtil.getId( folder );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    RWTFixture.readDataAndProcessAction( folder );
    assertEquals( "vetoShowList|", log.toString() );
    Menu menu = folderAdapter.getShowListMenu();
    assertEquals( 0, menu.getItemCount() );
    // clean up above test
    folder.removeCTabFolder2Listener( vetoListener );

    // Test showList event with listeners that does not veto showing
    log.setLength( 0 );
    folder.addCTabFolder2Listener( listener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    RWTFixture.readDataAndProcessAction( folder );
    assertEquals( "showList|", log.toString() );
    menu = folderAdapter.getShowListMenu();
    assertEquals( 1, menu.getItemCount() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private static final class CTabItemControl extends Composite {

    public final StringBuffer markup = new StringBuffer();
    
    public CTabItemControl( final Composite parent, final int style ) {
      super( parent, style );
    }
    
    public Object getAdapter( final Class adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
        result = new AbstractWidgetLCA() {
          public void preserveValues( final Widget widget ) {
            Control control = ( Control )widget;
            IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
            Boolean visible = Boolean.valueOf( control.isVisible() );
            adapter.preserve( "visible", visible );
          }
          public void renderChanges( final Widget widget ) throws IOException {
            markup.setLength( 0 );
            Control control = ( Control )widget;
            Boolean visible = Boolean.valueOf( control.isVisible() );
            if( WidgetUtil.hasChanged( widget, "visible", visible ) ) {
              markup.append( "visible=" + visible );
            }
          }
          public void renderDispose( final Widget widget ) throws IOException {
          }
          public void renderInitialization( final Widget widget ) throws IOException {
          }
          public void readData( final Widget widget ) {
          }
        };
      } else {
        result = super.getAdapter( adapter );
      }
      return result;
    }
  }
}
