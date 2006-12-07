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

package org.eclipse.rap.rwt;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.engine.AdapterFactoryRegistry_Test;
import org.eclipse.rap.rwt.internal.engine.RWTServletContextListener_Test;
import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.internal.widgets.buttonkit.ButtonLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.controlkit.ControlLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.custom.ctabfolderkit.CTabFolderLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.displaykit.DisplayLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.labelkit.LabelLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.menukit.MenuLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.sashkit.SashLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.shellkit.ShellLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.tablekit.TableLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.textkit.TextLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.treeitemkit.TreeItemLCA_Test;
import org.eclipse.rap.rwt.internal.widgets.treekit.TreeLCA_Test;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.ResourceManager_Test;
import org.eclipse.rap.rwt.widgets.*;
import org.eclipse.rap.rwt.widgets.custom.CTabFolder_Test;
import org.eclipse.rap.rwt.widgets.custom.CTabItem_Test;


public class RWTTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.eclipse.rap.rwt" );
    suite.addTestSuite( EntryPointManager_Test.class );
    suite.addTestSuite( RWTServletContextListener_Test.class );
    suite.addTestSuite( AdapterFactoryRegistry_Test.class );
    suite.addTestSuite( RWTLifeCycle_Test.class );
    suite.addTestSuite( LifeCycleAdapter_Test.class );
    suite.addTestSuite( ResourceManager_Test.class );
    suite.addTestSuite( PreserveWidgetsPhaseListener_Test.class );
    suite.addTestSuite( RenderDispose_Test.class );
    suite.addTestSuite( WidgetAdapter_Test.class );
    suite.addTestSuite( JSWriter_Test.class );
    suite.addTestSuite( JSListenerType_Test.class );
    suite.addTestSuite( JSVar_Test.class );
    suite.addTestSuite( WidgetUtil_Test.class );
    suite.addTestSuite( ControlLCAUtil_Test.class );
    suite.addTestSuite( SelectionEvent_Test.class );
    suite.addTestSuite( ControlEvent_Test.class );
    suite.addTestSuite( DisposeEvent_Test.class );
    suite.addTestSuite( ShellEvent_Test.class );

    suite.addTestSuite( Display_Test.class );
    suite.addTestSuite( Shell_Test.class );
    suite.addTestSuite( Control_Test.class );
    suite.addTestSuite( Item_Test.class );
    suite.addTestSuite( ItemHolder_Test.class );
    suite.addTestSuite( ControlHolder_Test.class );
    suite.addTestSuite( Composition_Test.class );
    suite.addTestSuite( WidgetTreeVisitor_Test.class );
    suite.addTestSuite( Layout_Test.class );
    suite.addTestSuite( SlimList_Test.class );
    suite.addTestSuite( Tree_Test.class );
    suite.addTestSuite( TreeItem_Test.class );
    suite.addTestSuite( TabFolderAndItem_Test.class );
    suite.addTestSuite( Menu_Test.class );
    suite.addTestSuite( MenuItem_Test.class );
    suite.addTestSuite( MenuHolder_Test.class );
    suite.addTestSuite( Table_Test.class );
    suite.addTestSuite( Combo_Test.class );
    suite.addTestSuite( List_Test.class );
    suite.addTestSuite( ToolBar_Test.class );
    suite.addTestSuite( CTabFolder_Test.class );
    suite.addTestSuite( CTabItem_Test.class );
    
    suite.addTestSuite( Image_Test.class );
    suite.addTestSuite( Color_Test.class );
    suite.addTestSuite( RGB_Test.class );

    suite.addTestSuite( DisplayLCA_Test.class );
    suite.addTestSuite( ControlLCA_Test.class );
    suite.addTestSuite( LabelLCA_Test.class );
    suite.addTestSuite( TextLCA_Test.class );
    suite.addTestSuite( ButtonLCA_Test.class );
    suite.addTestSuite( SashLCA_Test.class );
    suite.addTestSuite( TreeLCA_Test.class );
    suite.addTestSuite( TreeItemLCA_Test.class );
    suite.addTestSuite( ShellLCA_Test.class );
    suite.addTestSuite( MenuLCA_Test.class );
    suite.addTestSuite( TableLCA_Test.class );
    suite.addTestSuite( CTabFolderLCA_Test.class );
    return suite;
  }
}