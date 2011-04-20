/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ctabfolderkit;

import java.io.IOException;
import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
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
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
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
    Composite shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Label label = new Label( folder, SWT.NONE );
    folder.setTopRight( label, SWT.FILL );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( folder );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Object selectionIndex
     = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_INDEX );
    assertEquals( new Integer( folder.getSelectionIndex() ), selectionIndex );
    Object width = adapter.getPreserved( "width" );
    assertEquals( new Integer( folder.getBounds().width ), width );
    Object minVisible = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMinimizeVisible() ), minVisible );
    Object maxVisible = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMaximizeVisible() ), maxVisible );
    Object tabHeight = adapter.getPreserved( CTabFolderLCA.PROP_TAB_HEIGHT );
    assertEquals( new Integer( folder.getTabHeight() ), tabHeight );
    Object minimized = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZED );
    assertEquals( Boolean.valueOf( folder.getMinimized() ), minimized );
    Object maximized = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZED );
    assertEquals( Boolean.valueOf( folder.getMaximized() ), maximized );
    hasListeners
     = ( Boolean )adapter.getPreserved( CTabFolderLCA.PROP_FOLDER_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    ICTabFolderAdapter folderAdapter
     = ( ICTabFolderAdapter )folder.getAdapter( ICTabFolderAdapter.class );
    Object minimizerect = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZE_RECT );
    assertEquals( folderAdapter.getMinimizeRect(), minimizerect );
    Object maximizerect = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZE_RECT );
    assertEquals( folderAdapter.getMaximizeRect(), maximizerect );
    Object tabPosition = adapter.getPreserved( CTabFolderLCA.PROP_TAB_POSITION );
    assertEquals( new Integer( folder.getTabPosition() ), tabPosition );
    Object selectionBg = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_BG );
    assertEquals( folderAdapter.getUserSelectionBackground(), selectionBg );
    Object selectionFg = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_FG );
    assertEquals( folderAdapter.getUserSelectionForeground(), selectionFg );
    Object chevronVisible
     = adapter.getPreserved( CTabFolderLCA.PROP_CHEVRON_VISIBLE );
    assertEquals( Boolean.valueOf( folderAdapter.getChevronVisible() ),
                  chevronVisible );
    Object chevronRect = adapter.getPreserved( CTabFolderLCA.PROP_CHEVRON_RECT );
    assertEquals( folderAdapter.getChevronRect(), chevronRect );
    Fixture.clearPreserved();
    folder.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    folder.addCTabFolder2Listener( new CTabFolder2Listener() {

      public void close( final CTabFolderEvent event ) {
      }

      public void maximize( final CTabFolderEvent event ) {
      }

      public void minimize( final CTabFolderEvent event ) {
      }

      public void restore( final CTabFolderEvent event ) {
      }

      public void showList( final CTabFolderEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners
     = ( Boolean )adapter.getPreserved( CTabFolderLCA.PROP_FOLDER_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    CTabItem item1 = new CTabItem( folder, SWT.NULL );
    item1.setText( "item1" );
    CTabItem item2 = new CTabItem( folder, SWT.NULL );
    item2.setText( "item2" );
    CTabItem item3 = new CTabItem( folder, SWT.NULL );
    item3.setText( "item3" );
    folder.setSelection( 2 );
    folder.setBounds( 20, 30, 40, 30 );
    folder.setMinimizeVisible( true );
    folder.setMaximizeVisible( true );
    folder.setMaximized( true );
    folder.setMinimized( true );
    folder.setTabHeight( 40 );
    folder.setTabPosition( 1024 );
    Color background = Graphics.getColor( 122, 233, 188 );
    folder.setSelectionBackground( background );
    Color foreground = Graphics.getColor( 233, 122, 199 );
    folder.setSelectionForeground( foreground );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    selectionIndex = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_INDEX );
    assertEquals( new Integer( 2 ), selectionIndex );
    width = adapter.getPreserved( "width" );
    assertEquals( new Integer( 40 ), width );
    minVisible = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZE_VISIBLE );
    assertEquals( Boolean.TRUE, minVisible );
    maxVisible = adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZE_VISIBLE );
    assertEquals( Boolean.TRUE, maxVisible );
    minimized = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZED );
    assertEquals( Boolean.TRUE, minimized );
    maximized = adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZED );
    assertEquals( Boolean.TRUE, maximized );
    tabHeight = adapter.getPreserved( CTabFolderLCA.PROP_TAB_HEIGHT );
    assertEquals( new Integer( 40 ), tabHeight );
    tabPosition = adapter.getPreserved( CTabFolderLCA.PROP_TAB_POSITION );
    assertEquals( new Integer( 1024 ), tabPosition );
    selectionBg = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_BG );
    assertEquals( background, selectionBg );
    selectionFg = adapter.getPreserved( CTabFolderLCA.PROP_SELECTION_FG );
    assertEquals( foreground, selectionFg );
    assertNotNull( adapter.getPreserved( CTabFolderLCA.PROP_MINIMIZE_RECT ) );
    assertNotNull( adapter.getPreserved( CTabFolderLCA.PROP_MAXIMIZE_RECT ) );
    assertNotNull( adapter.getPreserved( CTabFolderLCA.PROP_CHEVRON_RECT ) );
    assertNotNull( adapter.getPreserved( CTabFolderLCA.PROP_CHEVRON_VISIBLE ) );
    chevronVisible = adapter.getPreserved( CTabFolderLCA.PROP_CHEVRON_VISIBLE );
    assertTrue( chevronVisible instanceof Boolean );
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    folder.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( folder );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    folder.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    folder.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    folder.setEnabled( true );
    // control_listeners
    folder.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // foreground background font
    Color controlBackground = Graphics.getColor( 122, 33, 203 );
    folder.setBackground( controlBackground );
    Color controlForeground = Graphics.getColor( 211, 178, 211 );
    folder.setForeground( controlForeground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    folder.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( controlBackground, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( controlForeground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( null, folder.getToolTipText() );
    Fixture.clearPreserved();
    folder.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    assertEquals( "some text", folder.getToolTipText() );
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    folder.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( folder, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testChangeSelection() {
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

    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );

    // Let pass one startup request to init the 'system'
    Fixture.fakeNewRequest( display );
    RWTFactory.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    Fixture.executeLifeCycleFromServerThread( );


    // The actual test request: item1 is selected, the request selects item2
    folder.setSelection( item1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( folderId + ".selectedItemId", item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.executeLifeCycleFromServerThread( );
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
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.addSelectionListener( listener );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );

    // Select item1 and fake request that selects item2
    folder.setSelection( item1 );
    String folderId = WidgetUtil.getId( folder );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    String name = folderId + "." + CTabFolderLCA.PARAM_SELECTED_ITEM_ID;
    Fixture.fakeRequestParam( name, item2Id );
    Fixture.readDataAndProcessAction( folder );
    assertSame( item2, folder.getSelection() );
    assertEquals( "widgetSelected|", log.toString() );
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    Fixture.readDataAndProcessAction( folder );
    assertEquals( "vetoShowList|", log.toString() );
    Menu menu = getShowListMenu( folder );
    assertEquals( null, menu );
    // clean up above test
    folder.removeCTabFolder2Listener( vetoListener );

    // Test showList event with listeners that does not veto showing
    log.setLength( 0 );
    folder.addCTabFolder2Listener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( CTabFolderLCA.EVENT_SHOW_LIST, folderId );
    Fixture.readDataAndProcessAction( folder );
    assertEquals( "showList|", log.toString() );
    menu = getShowListMenu( folder );
    assertEquals( 1, menu.getItemCount() );
  }

  public void testWriteSelectionBackgroundGradient_Vertical() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );

    Fixture.fakeResponseWriter();
    CTabFolderLCA lca = new CTabFolderLCA();
    lca.preserveValues( folder );
    Fixture.markInitialized( folder );
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 100 };
    folder.setSelectionBackground( gradientColors, percents, true );
    lca.renderChanges( folder );
    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setSelectionBackground( \"#0000ff\" );"
      + "w.setSelectionBackgroundGradient( [\"#00ff00\",\"#0000ff\" ], "
      + "[0,100 ], true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteSelectionBackgroundGradient_Horizontal()
    throws IOException
  {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );

    Fixture.fakeResponseWriter();
    CTabFolderLCA lca = new CTabFolderLCA();
    lca.preserveValues( folder );
    Fixture.markInitialized( folder );
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 100 };
    folder.setSelectionBackground( gradientColors, percents );
    lca.renderChanges( folder );
    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setSelectionBackground( \"#0000ff\" );"
      + "w.setSelectionBackgroundGradient( [\"#00ff00\",\"#0000ff\" ], "
      + "[0,100 ], false );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteSelectionBackgroundImage() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE );
    Fixture.fakeResponseWriter();
    CTabFolderLCA lca = new CTabFolderLCA();
    lca.preserveValues( folder );
    Image image = Graphics.getImage( Fixture.IMAGE_50x100 );
    folder.setSelectionBackground( image );
    lca.renderChanges( folder );
    String imagePath = ImageFactory.getImagePath( image );
    String expected = "w.setSelectionBackgroundImage( [ \"" + imagePath + "\",50,100 ] );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
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
