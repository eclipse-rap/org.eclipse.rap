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

package org.eclipse.rwt.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class ControlLCAUtil_Test extends TestCase {

  public void testWriteBounds() throws Exception {
    // Ensure that bounds for an uninitialized widget are rendered
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( shell );
    // TODO [fappel]: check whether minWidth and minHeight is still needed -
    //                causes problems on FF with caching
    //String expected
    //  = w.setSpace( 0, 0, 0, 0 );w.setMinWidth( 0 );w.setMinHeight( 0 );";
    String expected = "w.setSpace( 0, 0, 0, 0 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Ensure that unchanged bound do not lead to markup
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ControlLCAUtil.writeBounds( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // Ensure that bounds-changes on an already initialized widgets are rendered
    Fixture.fakeResponseWriter();
    shell.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    ControlLCAUtil.writeBounds( shell );
    expected = "w.setSpace( 1, 3, 2, 4 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
  }

  public void testWriteTooolTip() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    // on a not yet initialized control: no tool tip -> no markup
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.setToolTipText( "" );
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // on a not yet initialized control: non-empty tool tip must be rendered
    Fixture.fakeResponseWriter();
    shell.setToolTipText( "abc" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "abc" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( null );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "abc" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "newTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "newTooltip" ) != -1 );
    // on an initialized control: change non-empty tooltip text
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "newToolTip" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "anotherTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "anotherTooltip" ) != -1 );
    // test actual markup - the next two lines fake situation that there is
    // already a widget reference (w)
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.newWidget( "Window" );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    String expected = "wm.setToolTip( w, \"anotherTooltip\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteImage() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label item = new Label( shell, SWT.NONE );

    // for an un-initialized control: no image -> no markup
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    ControlLCAUtil.writeImage( item, item.getImage() );
    assertEquals( "", Fixture.getAllMarkup() );

    // for an un-initialized control: render image, if any
    Fixture.fakeResponseWriter();
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    ControlLCAUtil.writeImage( item, item.getImage() );
    String expected = "w.setIcon( \""
                    + ResourceFactory.getImagePath( item.getImage() )
                    + "\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    // for an initialized control with change image: render it
    RWTFixture.markInitialized( item );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    item.setImage( null );
    ControlLCAUtil.writeImage( item, item.getImage() );
    assertTrue( Fixture.getAllMarkup().indexOf( "w.setIcon( \"\" );" ) != -1 );
  }

  public void testWriteActivateListener() throws IOException {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );

    // A non-initialized widget with no listener attached must not render
    // JavaScript code for adding activateListeners
    AbstractWidgetLCA labelLCA = WidgetUtil.getLCA( label );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    String markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.length() > 0 );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // A non-initialized widget with a listener attached must render JavaScript
    // code for adding activateListeners
    ActivateEvent.addListener( label, listener );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) != -1 );

    // An initialized widget with unchanged activateListeners must not render
    // JavaScript code for adding activateListeners
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( label );
    labelLCA.preserveValues( label );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // Removing an ActivateListener from an initialized widget must render
    // JavaScript code for removing activateListeners
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( label );
    labelLCA.preserveValues( label );
    ActivateEvent.removeListener( label, listener );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );
    assertTrue( markup.indexOf( "removeActivateListenerWidget" ) != -1 );
  }

  public void testProcessSelection() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected" );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( listener );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Test that requestParams like '...events.widgetSelected=w3' cause the
    // event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "widgetSelected", log.toString() );

    // Test that requestParams like '...events.widgetSelected=w3,0' cause the
    // event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "widgetSelected", log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );
  }

  public void testMaxZOrder() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    for( int i = 0; i < ControlLCAUtil.MAX_STATIC_ZORDER; i++ ) {
      new Button( shell, SWT.PUSH );
    }
    Control control = new Button( shell, SWT.PUSH );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    ControlLCAUtil.preserveValues( control );
    assertEquals( new Integer( 1 ), adapter.getPreserved( Props.Z_INDEX ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
