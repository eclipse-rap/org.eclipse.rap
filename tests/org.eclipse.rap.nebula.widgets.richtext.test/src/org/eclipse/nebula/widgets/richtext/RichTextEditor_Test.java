/*******************************************************************************
 * Copyright (c) 2011, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class RichTextEditor_Test {

  private Display display;
  private Shell shell;
  private RichTextEditor editor;
  private Connection connection;
  private RemoteObject remoteObject;

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    context.replaceConnection( connection );
    editor = new RichTextEditor( shell, SWT.BORDER );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testSetLayout_fails() {
    editor.setLayout( new FillLayout() );
  }

  @Test
  public void testContructor_createsRemoteObjectWithCorrectType() {
    verify( connection ).createRemoteObject( eq( "rwt.widgets.RichTextEditor" ) );
  }

  @Test
  public void testContructor_setsParent() {
    verify( remoteObject ).set( "parent", WidgetUtil.getId( editor ) );
  }

  @Test
  public void testContructor_setsConfig() {
    verify( remoteObject ).set( eq( "config" ), any( JsonObject.class ) );
  }

  @Test
  public void testContructor_loadsJavaScriptFiles() {
    ClientFileLoader loader = mockClientFiletLoader();
    ResourceManager resourceManager = RWT.getResourceManager();

    new RichTextEditor( shell, SWT.BORDER );

    verify( loader ).requireJs( resourceManager.getLocation( "ckeditor/ckeditor.js" ) );
    verify( loader ).requireJs( resourceManager.getLocation( "ckeditor/config.js" ) );
    verify( loader ).requireJs( resourceManager.getLocation( "ckeditor/RichTextEditor.js" ) );
    verify( loader ).requireJs( resourceManager.getLocation( "ckeditor/RichTextEditorHandler.js" ) );
  }

  @Test
  public void testSetText_affectsGetText() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    assertEquals( text, editor.getText() );
  }

  @Test
  public void testSetText_rendersToClient() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    verify( remoteObject ).set( "text", text );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetText_failsWithNull() {
    editor.setText( null );
  }

  @Test
  public void testSetText_fromClient() {
    String text = "foo<span>bar</span>";

    remoteSet( remoteObject, "text", JsonValue.valueOf( text ) );

    assertEquals( text, editor.getText() );
  }

  @Test
  public void testSetFont_rendersToClient() {
    editor.setFont( new Font( display, "fantasy", 13, 0 ) );

    verify( remoteObject ).set( "font", "13px fantasy" );
  }

  @Test
  public void testSetEditable_rendersToClient() {
    editor.setEditable( false );

    verify( remoteObject ).set( "editable", false );
  }

  @Test
  public void testIsEditable() {
    editor.setEditable( false );

    assertFalse( editor.isEditable() );
  }

  @Test
  public void testGetVisible() {
    editor.setVisible( false );

    assertFalse( editor.getVisible() );
  }

  @Test
  public void testDispose_rendersDestroyToClient() {
    editor.dispose();

    verify( remoteObject ).destroy();
  }

  @Test
  public void testIsReparentable() {
    assertFalse( editor.isReparentable() );
  }

  @Test
  public void testSetParent() {
    Composite newParent = new Composite( shell, SWT.NONE );

    boolean success = editor.setParent( newParent );

    assertFalse( success );
    assertSame( shell, editor.getParent() );
  }

  private ClientFileLoader mockClientFiletLoader() {
    WebClient client = mock( WebClient.class );
    context.replaceClient( client );
    ClientFileLoader loader = mock( ClientFileLoader.class );
    when( client.getService( ClientFileLoader.class ) ).thenReturn( loader );
    return loader;
  }

  private static void remoteSet( RemoteObject remoteObjectMock, String proprety, JsonValue value ) {
    getHandler( remoteObjectMock ).handleSet( new JsonObject().add( proprety, value ) );
  }

  private static OperationHandler getHandler( RemoteObject remoteObjectMock ) {
    ArgumentCaptor<OperationHandler> captor = ArgumentCaptor.forClass( OperationHandler.class );
    verify( remoteObjectMock ).setHandler( captor.capture() );
    return captor.getValue();
  }

}
