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

package org.eclipse.swt.internal.custom.ctabfolderkit;

import java.io.IOException;
import java.lang.reflect.Field;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;



public class CTabFolderLCA_Test extends TestCase {
  
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
            if( WidgetLCAUtil.hasChanged( widget, "visible", visible ) ) {
              markup.append( "visible=" + visible );
            }
          }
          public void renderDispose( final Widget widget ) throws IOException {
          }
          public void createResetHandlerCalls( final String typePoolId )
            throws IOException
          {
          }
          public String getTypePoolId( final Widget widget ) {
            return null;
          }
          public void renderInitialization( final Widget widget )
            throws IOException
          {
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testLCA() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    
    assertSame( CTabFolderLCA.class,
                folder.getAdapter( ILifeCycleAdapter.class ).getClass() );
    assertSame( CTabItemLCA.class,
                item.getAdapter( ILifeCycleAdapter.class ).getClass() );
  }
  
  public void testPreserveValues() {
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Label label = new Label( folder, SWT.NONE );
    folder.setTopRight( label, SWT.FILL );
    RWTFixture.markInitialized( display );
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
    Shell shell = new Shell( display , SWT.MULTI );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.setSize( 100, 100 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItemControl item1Control = new CTabItemControl( folder, SWT.NONE );
    item1.setControl( item1Control );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    CTabItemControl item2Control = new CTabItemControl( folder, SWT.NONE );
    item2.setControl( item2Control );
    shell.open();
    
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
    RWTFixture.fakeUIThread();
    folder.setSelection( item1 );
    RWTFixture.removeUIThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( folderId + ".selectedItemId", item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertSame( item2, folder.getSelection() );
    assertEquals( "visible=false", item1Control.markup.toString() );
    assertEquals( "visible=true", item2Control.markup.toString() );
    RWTFixture.removeUIThread();
  }
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.addSelectionListener( listener );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    
    // Select item1 and fake request that selects item2
    folder.setSelection( item1 );
    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    String name = folderId + "." + CTabFolderLCA.PARAM_SELECTED_ITEM_ID;
    Fixture.fakeRequestParam( name, item2Id );
    RWTFixture.fakeUIThread();
    RWTFixture.readDataAndProcessAction( folder );
    assertSame( item2, folder.getSelection() );
    assertEquals( "widgetSelected|", log.toString() );
    RWTFixture.removeUIThread();
  }
  
  public void testShowListEvent() {
    // Widgets for test
    Display display= new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 30, 130 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
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
    //       showList menu wasn't populated by previous showList requests
    folder.setSelection( item1 );
    folder.addCTabFolder2Listener( vetoListener );
    String folderId = WidgetUtil.getId( folder );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    RWTFixture.fakeUIThread();
    RWTFixture.readDataAndProcessAction( folder );
    RWTFixture.removeUIThread();
    assertEquals( "vetoShowList|", log.toString() );
    Menu menu = getShowListMenu( folder );
    assertEquals( null, menu );
    // clean up above test
    folder.removeCTabFolder2Listener( vetoListener );

    // Test showList event with listeners that does not veto showing
    log.setLength( 0 );
    folder.addCTabFolder2Listener( listener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    RWTFixture.fakeUIThread();
    RWTFixture.readDataAndProcessAction( folder );
    assertEquals( "showList|", log.toString() );
    menu = getShowListMenu( folder );
    assertEquals( 1, menu.getItemCount() );
    RWTFixture.removeUIThread();
  }
  
  private static Menu getShowListMenu( final CTabFolder folder ) {
    Menu result = null;
    try {
      Field field = CTabFolder.class.getDeclaredField( "showMenu" );
      field.setAccessible( true );
      result = ( Menu )field.get( folder );
    } catch( Exception e ) {
      e.printStackTrace();
    }
    return result;
  }
}
