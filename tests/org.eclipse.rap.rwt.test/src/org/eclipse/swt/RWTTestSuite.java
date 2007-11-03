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

package org.eclipse.swt;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.browser.BrowserLoader_Test;
import org.eclipse.rwt.internal.engine.PhaseListenerRegistry_Test;
import org.eclipse.rwt.internal.engine.RWTServletContextListener_Test;
import org.eclipse.rwt.internal.events.EventAdapter_Test;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.HTMLUtil_Test;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ServiceHandler_Test;
import org.eclipse.rwt.service.SessionStore_Test;
import org.eclipse.rwt.widgets.ExternalBrowser_Test;
import org.eclipse.swt.browser.Browser_Test;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ctabfolderkit.CTabFolderLCA_Test;
import org.eclipse.swt.internal.custom.scrolledcompositekit.ScrolledCompositeLCA_Test;
import org.eclipse.swt.internal.events.ActivateEvent_Test;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA_Test;
import org.eclipse.swt.internal.widgets.combokit.ComboLCA_Test;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCA_Test;
import org.eclipse.swt.internal.widgets.coolbarkit.CoolBarLCA_Test;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCAFocus_Test;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA_Test;
import org.eclipse.swt.internal.widgets.labelkit.LabelLCA_Test;
import org.eclipse.swt.internal.widgets.linkkit.LinkLCA_Test;
import org.eclipse.swt.internal.widgets.listkit.ListLCA_Test;
import org.eclipse.swt.internal.widgets.menuitemkit.MenuItemLCA_Test;
import org.eclipse.swt.internal.widgets.menukit.MenuLCA_Test;
import org.eclipse.swt.internal.widgets.sashkit.SashLCA_Test;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA_Test;
import org.eclipse.swt.internal.widgets.spinnerkit.SpinnerLCA_Test;
import org.eclipse.swt.internal.widgets.tabfolderkit.TabFolderLCA_Test;
import org.eclipse.swt.internal.widgets.tablecolumnkit.TableColumnLCA_Test;
import org.eclipse.swt.internal.widgets.tableitemkit.TableItemLCA_Test;
import org.eclipse.swt.internal.widgets.tablekit.TableLCA_Test;
import org.eclipse.swt.internal.widgets.textkit.TextLCA_Test;
import org.eclipse.swt.internal.widgets.toolitemkit.ToolItemLCA_Test;
import org.eclipse.swt.internal.widgets.treecolumnkit.TreeColumnLCA_Test;
import org.eclipse.swt.internal.widgets.treeitemkit.TreeItemLCA_Test;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA_Test;
import org.eclipse.swt.widgets.*;



public class RWTTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.eclipse.rap.rwt" );
    // Former W4T Tests
    suite.addTestSuite( NLS_Test.class );
    suite.addTestSuite( AdapterManager_Test.class );
    suite.addTestSuite( HtmlResponseWriter_Test.class );
    suite.addTestSuite( HtmlResponseWriterUtil_Test.class );
    suite.addTestSuite( SessionSingleton_Test.class );
    suite.addTestSuite( LifeCycleFactory_Test.class );
    suite.addTestSuite( PhaseId_Test.class );
    suite.addTestSuite( Scope_Test.class );
    suite.addTestSuite( Logger_Test.class );
    suite.addTestSuite( ServiceHandler_Test.class );
    suite.addTestSuite( SessionStore_Test.class );
    suite.addTestSuite( EngineConfig_Test.class );
    suite.addTestSuite( ResourceManagerImpl_Test.class );
    suite.addTestSuite( ResourceUtil_Test.class );
    suite.addTestSuite( EventAdapter_Test.class );
    suite.addTestSuite( BrowserLoader_Test.class );
    suite.addTestSuite( WrappedRequest_Test.class );
    suite.addTestSuite( ConfigurationReader_Test.class );
    suite.addTestSuite( HTMLUtil_Test.class );
    suite.addTestSuite( ContextProvider_Test.class );
    
    // RWT Tests
    suite.addTestSuite( EntryPointManager_Test.class );
    suite.addTestSuite( RWTServletContextListener_Test.class );
    suite.addTestSuite( AdapterFactoryRegistry_Test.class );
    suite.addTestSuite( PhaseListenerRegistry_Test.class );
    suite.addTestSuite( ResourceRegistry_Test.class );
    suite.addTestSuite( RWTLifeCycle_Test.class );
    suite.addTestSuite( RWTRequestVersionControl_Test.class );
    suite.addTestSuite( LifeCycleAdapter_Test.class );
    suite.addTestSuite( ResourceManager_Test.class );
    suite.addTestSuite( PreserveWidgetsPhaseListener_Test.class );
    suite.addTestSuite( RenderDispose_Test.class );
    suite.addTestSuite( WidgetAdapter_Test.class );
    suite.addTestSuite( JSWriter_Test.class );
    suite.addTestSuite( JSListenerType_Test.class );
    suite.addTestSuite( JSVar_Test.class );
    suite.addTestSuite( WidgetUtil_Test.class );
    suite.addTestSuite( WidgetLCAUtil_Test.class );
    suite.addTestSuite( ControlLCAUtil_Test.class );
    suite.addTestSuite( SelectionEvent_Test.class );
    suite.addTestSuite( ControlEvent_Test.class );
    suite.addTestSuite( DisposeEvent_Test.class );
    suite.addTestSuite( ShellEvent_Test.class );
    suite.addTestSuite( TreeEvent_Test.class );
    suite.addTestSuite( ActivateEvent_Test.class );
    suite.addTestSuite( FocusEvent_Test.class );
    suite.addTestSuite( TypedEvent_Test.class );
    suite.addTestSuite( UntypedEvents_Test.class );
    suite.addTestSuite( UntypedEventAdapter_Test.class );
    suite.addTestSuite( UICallBackManager_Test.class );
    suite.addTestSuite( UICallBackServiceHandler_Test.class );
    suite.addTestSuite( DuplicateRequest_Test.class );

    suite.addTestSuite( Display_Test.class );
    suite.addTestSuite( Shell_Test.class );
    suite.addTestSuite( Widget_Test.class );
    suite.addTestSuite( Control_Test.class );
    suite.addTestSuite( Composite_Test.class );
    suite.addTestSuite( Item_Test.class );
    suite.addTestSuite( ItemHolder_Test.class );
    suite.addTestSuite( ControlHolder_Test.class );
    suite.addTestSuite( Composition_Test.class );
    suite.addTestSuite( WidgetTreeVisitor_Test.class );
    suite.addTestSuite( Layout_Test.class );
    suite.addTestSuite( SlimList_Test.class );
    suite.addTestSuite( Button_Test.class );
    suite.addTestSuite( Tree_Test.class );
    suite.addTestSuite( TreeItem_Test.class );
    suite.addTestSuite( TreeColumn_Test.class );
    suite.addTestSuite( TabFolderAndItem_Test.class );
    suite.addTestSuite( Menu_Test.class );
    suite.addTestSuite( MenuItem_Test.class );
    suite.addTestSuite( MenuHolder_Test.class );
    suite.addTestSuite( Table_Test.class );
    suite.addTestSuite( TableColumn_Test.class );
    suite.addTestSuite( TableItem_Test.class );
    suite.addTestSuite( Combo_Test.class );
    suite.addTestSuite( List_Test.class );
    suite.addTestSuite( ToolBar_Test.class );
    suite.addTestSuite( CTabFolder_Test.class );
    suite.addTestSuite( CTabItem_Test.class );
    suite.addTestSuite( CLabel_Test.class );
    suite.addTestSuite( CoolBar_Test.class );
    suite.addTestSuite( CoolItem_Test.class );
    suite.addTestSuite( Label_Test.class );
    suite.addTestSuite( Link_Test.class );
    suite.addTestSuite( Browser_Test.class );
    suite.addTestSuite( Group_Test.class );
    suite.addTestSuite( Text_Test.class );
    suite.addTestSuite( Spinner_Test.class );
    suite.addTestSuite( ExternalBrowser_Test.class );
    suite.addTestSuite( ProgressBar_Test.class );

    suite.addTestSuite( Image_Test.class );
    suite.addTestSuite( Color_Test.class );
    suite.addTestSuite( RGB_Test.class );
    suite.addTestSuite( Font_Test.class );
    suite.addTestSuite( FontData_Test.class );
    suite.addTestSuite( Graphics_Test.class );
    suite.addTestSuite( TextSizeEstimation_Test.class );
    suite.addTestSuite( TextSizeDetermination_Test.class );
    suite.addTestSuite( ResourceFactory_Test.class );

    // == LCAs ==
    suite.addTestSuite( DisplayLCA_Test.class );
    suite.addTestSuite( DisplayLCAFocus_Test.class );
    suite.addTestSuite( ControlLCA_Test.class );
    suite.addTestSuite( LabelLCA_Test.class );
    suite.addTestSuite( TextLCA_Test.class );
    suite.addTestSuite( ButtonLCA_Test.class );
    suite.addTestSuite( SashLCA_Test.class );
    suite.addTestSuite( TreeLCA_Test.class );
    suite.addTestSuite( TreeItemLCA_Test.class );
    suite.addTestSuite( TreeColumnLCA_Test.class );
    suite.addTestSuite( ShellLCA_Test.class );
    suite.addTestSuite( MenuLCA_Test.class );
    suite.addTestSuite( MenuItemLCA_Test.class );
    suite.addTestSuite( TableLCA_Test.class );
    suite.addTestSuite( TableColumnLCA_Test.class );
    suite.addTestSuite( TableItemLCA_Test.class );
    suite.addTestSuite( CTabFolderLCA_Test.class );
    suite.addTestSuite( CoolBarLCA_Test.class );
    suite.addTestSuite( LinkLCA_Test.class );
    suite.addTestSuite( ListLCA_Test.class );
    suite.addTestSuite( SpinnerLCA_Test.class );
    suite.addTestSuite( ComboLCA_Test.class );
    suite.addTestSuite( ToolItemLCA_Test.class );
    suite.addTestSuite( TabFolderLCA_Test.class );
    suite.addTestSuite( ScrolledCompositeLCA_Test.class );

    // == Theming ==
    suite.addTestSuite( Theme_Test.class );
    suite.addTestSuite( ThemeManager_Test.class );
    suite.addTestSuite( ThemeUtil_Test.class );
    suite.addTestSuite( ThemeWriter_Test.class );
    suite.addTestSuite( ThemeDefinitionReader_Test.class );
    suite.addTestSuite( QxColor_Test.class );
    suite.addTestSuite( QxBorder_Test.class );
    suite.addTestSuite( QxBoolean_Test.class );
    suite.addTestSuite( QxDimension_Test.class );
    suite.addTestSuite( QxBoxDimensions_Test.class );
    suite.addTestSuite( QxFont_Test.class );

    return suite;
  }
}