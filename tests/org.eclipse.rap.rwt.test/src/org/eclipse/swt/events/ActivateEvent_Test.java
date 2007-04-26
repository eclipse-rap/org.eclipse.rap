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

package org.eclipse.swt.events;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.requests.RequestParams;


public class ActivateEvent_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testListenerOnControl() {
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    ActivateEvent.addListener( label, new ActivateListener() {
      public void activated( final ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( final ActivateEvent event ) {
        deactivated[ deactivatedCount[ 0 ] ] = ( Widget )event.getSource();
        deactivatedCount[ 0 ]++;
      }
    } );

    String displayId = DisplayUtil.getId( display );
    String labelId = WidgetUtil.getId( label );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertEquals( 1, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
  }

  public void testListenerOnComposite() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Widget[] activated = new Widget[ 10 ];
    final int[] activatedCount = { 0 };
    final Widget[] deactivated = new Widget[ 10 ];
    final int[] deactivatedCount = { 0 };
    ActivateListener listener = new ActivateListener() {
      public void activated( final ActivateEvent event ) {
        activated[ activatedCount[ 0 ] ] = ( Widget )event.getSource();
        activatedCount[ 0 ]++;
      }
      public void deactivated( final ActivateEvent event ) {
        deactivated[ deactivatedCount[ 0 ] ] = ( Widget )event.getSource();
        deactivatedCount[ 0 ]++;
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );
    Composite otherComposite = new Composite( shell, SWT.NONE );
    Label otherLabel = new Label( otherComposite, SWT.NONE );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( otherLabel );
    ActivateEvent.addListener( composite, listener );
    ActivateEvent.addListener( label, listener );
    ActivateEvent.addListener( otherComposite, listener );
    ActivateEvent.addListener( otherLabel, listener );
    
    String displayId = DisplayUtil.getId( display );
    String labelId = WidgetUtil.getId( label );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertEquals( 2, activatedCount[ 0 ] );
    assertSame( label, activated[ 0 ] );
    assertSame( composite, activated[ 1 ] );
    assertEquals( 2, deactivatedCount[ 0 ] );
    assertSame( otherLabel, deactivated[ 0 ] );
    assertSame( otherComposite, deactivated[ 1 ] );
  }
}
