/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.StartupPage_Test;
import org.eclipse.rwt.internal.textsize.TextSizeDeterminationFacadeImpl_Test;
import org.eclipse.rwt.internal.textsize.MeasurementHandler_Test;
import org.eclipse.rwt.internal.widgets.fileuploadkit.FileUploadLCA_Test;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.browser.browserkit.BrowserLCA_Test;
import org.eclipse.swt.internal.custom.ccombokit.CComboLCA_Test;
import org.eclipse.swt.internal.custom.clabelkit.CLabelLCA_Test;
import org.eclipse.swt.internal.custom.ctabfolderkit.CTabFolderLCA_Test;
import org.eclipse.swt.internal.custom.scrolledcompositekit.ScrolledCompositeLCA_Test;
import org.eclipse.swt.internal.dnd.dragsourcekit.DragSourceLCA_Test;
import org.eclipse.swt.internal.events.ActivateEvent_Test;
import org.eclipse.swt.internal.events.EventLCAUtil_Test;
import org.eclipse.swt.internal.internal.widgets.controldecoratorkit.ControlDecoratorLCA_Test;
import org.eclipse.swt.internal.widgets.WidgetAdapter_Test;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA_Test;
import org.eclipse.swt.internal.widgets.canvaskit.CanvasLCA_Test;
import org.eclipse.swt.internal.widgets.canvaskit.GCOperationWriter_Test;
import org.eclipse.swt.internal.widgets.combokit.ComboLCA_Test;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCA_Test;
import org.eclipse.swt.internal.widgets.coolbarkit.CoolBarLCA_Test;
import org.eclipse.swt.internal.widgets.coolitemkit.CoolItemLCA_Test;
import org.eclipse.swt.internal.widgets.datetimekit.DateTimeLCA_Test;
import org.eclipse.swt.internal.widgets.displaykit.*;
import org.eclipse.swt.internal.widgets.expandbarkit.ExpandBarLCA_Test;
import org.eclipse.swt.internal.widgets.expanditemkit.ExpandItemLCA_Test;
import org.eclipse.swt.internal.widgets.labelkit.LabelLCA_Test;
import org.eclipse.swt.internal.widgets.linkkit.LinkLCA_Test;
import org.eclipse.swt.internal.widgets.listkit.ListLCA_Test;
import org.eclipse.swt.internal.widgets.menuitemkit.MenuItemLCA_Test;
import org.eclipse.swt.internal.widgets.menukit.MenuLCA_Test;
import org.eclipse.swt.internal.widgets.progressbarkit.ProgressBarLCA_Test;
import org.eclipse.swt.internal.widgets.sashkit.SashLCA_Test;
import org.eclipse.swt.internal.widgets.scalekit.ScaleLCA_Test;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA_Test;
import org.eclipse.swt.internal.widgets.sliderkit.SliderLCA_Test;
import org.eclipse.swt.internal.widgets.spinnerkit.SpinnerLCA_Test;
import org.eclipse.swt.internal.widgets.tabfolderkit.TabFolderLCA_Test;
import org.eclipse.swt.internal.widgets.tabitemkit.TabItemLCA_Test;
import org.eclipse.swt.internal.widgets.tablecolumnkit.TableColumnLCA_Test;
import org.eclipse.swt.internal.widgets.tableitemkit.TableItemLCA_Test;
import org.eclipse.swt.internal.widgets.tablekit.TableLCA_Test;
import org.eclipse.swt.internal.widgets.textkit.TextLCA_Test;
import org.eclipse.swt.internal.widgets.toolitemkit.ToolItemLCA_Test;
import org.eclipse.swt.internal.widgets.tooltipkit.ToolTipLCA_Test;
import org.eclipse.swt.internal.widgets.treecolumnkit.TreeColumnLCA_Test;
import org.eclipse.swt.internal.widgets.treeitemkit.TreeItemLCA_Test;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA_Test;


public class RWTQ07TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.eclipse.rap.rwt.q07" );

    // TODO [fappel]: Note that the following tests belong semantically to the
    //                RWT core tests, but there is still some refactoring
    //                necessary to achieve this, so they've been moved
    //                temporarily to the q07 fragment tests..
    suite.addTestSuite( PreserveWidgetsPhaseListener_Test.class );
    suite.addTestSuite( WidgetAdapter_Test.class );
    suite.addTestSuite( DuplicateRequest_Test.class );
    suite.addTestSuite( MouseEvent_Test.class );
    suite.addTestSuite( ControlEvent_Test.class );
    suite.addTestSuite( ActivateEvent_Test.class );
    suite.addTestSuite( FocusEvent_Test.class );
    suite.addTestSuite( TypedEvent_Test.class );
    suite.addTestSuite( UntypedEvents_Test.class );

    // TODO [fappel]: Check also which parts of the following tests belong
    //                to the host bundle testsuite
    // == LifeCycle ==
    suite.addTestSuite( LifeCycleAdapter_Test.class );
    suite.addTestSuite( LifeCycleAdapterUtil_Test.class );
    suite.addTestSuite( RenderDispose_Test.class );
    suite.addTestSuite( JSWriter_Test.class );
    suite.addTestSuite( WidgetLCAUtil_Test.class );
    suite.addTestSuite( ControlLCAUtil_Test.class );
    suite.addTestSuite( EventLCAUtil_Test.class );
    suite.addTestSuite( UITestUtil_Test.class );
    suite.addTestSuite( PhaseListenerManager_Test.class );
    suite.addTestSuite( SimpleLifeCycle_Test.class );
    suite.addTestSuite( RWTLifeCycle_Test.class );
    suite.addTestSuite( RWTLifeCycle2_Test.class );
    suite.addTestSuite( ThemeAdapter_Test.class );
    suite.addTestSuite( MeasurementHandler_Test.class );
    suite.addTestSuite( TextSizeDeterminationFacadeImpl_Test.class );
    suite.addTestSuite( DragSourceLCA_Test.class );

    // == Service ==
    suite.addTestSuite( StartupPage_Test.class );

    // == LCA ==
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
    suite.addTestSuite( CoolItemLCA_Test.class );
    suite.addTestSuite( LinkLCA_Test.class );
    suite.addTestSuite( ListLCA_Test.class );
    suite.addTestSuite( SpinnerLCA_Test.class );
    suite.addTestSuite( ComboLCA_Test.class );
    suite.addTestSuite( ToolItemLCA_Test.class );
    suite.addTestSuite( TabFolderLCA_Test.class );
    suite.addTestSuite( TabItemLCA_Test.class );
    suite.addTestSuite( ScrolledCompositeLCA_Test.class );
    suite.addTestSuite( BrowserLCA_Test.class );
    suite.addTestSuite( ScaleLCA_Test.class );
    suite.addTestSuite( DateTimeLCA_Test.class );
    suite.addTestSuite( ExpandBarLCA_Test.class );
    suite.addTestSuite( ExpandItemLCA_Test.class );
    suite.addTestSuite( SliderLCA_Test.class );
    suite.addTestSuite( CComboLCA_Test.class );
    suite.addTestSuite( CLabelLCA_Test.class );
    suite.addTestSuite( ProgressBarLCA_Test.class );
    suite.addTestSuite( ControlDecoratorLCA_Test.class );
    suite.addTestSuite( DNDSupport_Test.class );
    suite.addTestSuite( CanvasLCA_Test.class );
    suite.addTestSuite( GCOperationWriter_Test.class );
    suite.addTestSuite( ToolTipLCA_Test.class );
    suite.addTestSuite( KeyBindingUtil_Test.class );
    suite.addTestSuite( FileUploadLCA_Test.class );
    suite.addTestSuite( ClientResources_Test.class );
    return suite;
  }
}