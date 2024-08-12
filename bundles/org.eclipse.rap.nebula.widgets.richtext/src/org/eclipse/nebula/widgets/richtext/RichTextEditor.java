/*******************************************************************************
 * Copyright (c) 2011, 2024 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;


/**
 * Rich Text Editor control that wraps CKEditor, a web-based WYSIWYG/Rich-Text editor.
 *
 * @see <a href="http://ckeditor.com/">http://ckeditor.com/</a>
 *
 * @since 3.1
 */
@SuppressWarnings( "deprecation" )
public class RichTextEditor extends Composite {

  private static final String RESOURCES_PATH = "resources/";
  private static final String REGISTER_PATH = "ckeditor/";

  private static final String[] RESOURCE_FILES = {
    "ckeditor.js",
    "config.js",
    "styles.js",
    "contents.css",
    "skins/moono/editor.css",
    "skins/moono/editor_ie.css",
    "skins/moono/editor_gecko.css",
    "skins/moono/dialog.css",
    "skins/moono/dialog_ie.css",
    "skins/moono/icons.png",
    "skins/moono/icons_hidpi.png",
    "skins/moono/images/arrow.png",
    "skins/moono/images/close.png",
    "skins/moono/images/lock-open.png",
    "skins/moono/images/lock.png",
    "skins/moono/images/refresh.png",
    "skins/moono/images/hidpi/close.png",
    "skins/moono/images/hidpi/lock-open.png",
    "skins/moono/images/hidpi/lock.png",
    "skins/moono/images/hidpi/refresh.png",
    "RichTextEditor.js",
    "RichTextEditorHandler.js"
  };
  private static final String REMOTE_TYPE = "rwt.widgets.RichTextEditor";

  private String text = "";
  private boolean editable = true;
  private final RemoteObject remoteObject;
  private final OperationHandler operationHandler = new AbstractOperationHandler() {
    @Override
    public void handleSet( JsonObject properties ) {
      JsonValue textValue = properties.get( "text" );
      if( textValue != null ) {
        text = textValue.asString();
      }
    }
  };
  private final RichTextEditorConfiguration config;

  /**
   * Constructs a new instance of this class given its parent.
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public RichTextEditor( Composite parent ) {
    this( parent, SWT.NONE );
  }

  /**
   * Constructs a new instance of this class given its parent.
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public RichTextEditor( Composite parent, int style ) {
    this( parent, (RichTextEditorConfiguration) null, style );
  }

  /**
   * Constructs a new instance of this class given its parent.
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param toolbarConfig the
   *          {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration} to use or
   *          <code>null</code> for using the default
   *          {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration}
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   * @deprecated use constructors that take a {@link RichTextEditorConfiguration}
   */
  @Deprecated
  public RichTextEditor( Composite parent, ToolbarConfiguration toolbarConfig ) {
    this( parent, toolbarConfig, SWT.NONE );
  }

  /**
   * Constructs a new instance of this class given its parent, the given
   * {@link ToolbarConfiguration} and a style value describing its behavior and appearance.
   *
   * @param parent the parent composite where this rich text editor should be added to
   * @param toolbarConfig the
   *          {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration} to use or
   *          <code>null</code> for using the default
   *          {@link org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration}
   * @param style the style of widget to construct
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   * @deprecated use constructors that take a {@link RichTextEditorConfiguration}
   */
  @Deprecated
  public RichTextEditor( Composite parent, ToolbarConfiguration toolbarConfig, int style ) {
    this( parent, toolbarConfig != null ? new RichTextEditorConfiguration( toolbarConfig ) : null, style );
  }

  /**
   * Constructs a new instance of this class given its parent, the given
   * {@link ToolbarConfiguration} and a style value describing its behavior and appearance.
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param editorConfig the {@link RichTextEditorConfiguration} to use or <code>null</code> for
   *          using the default {@link RichTextEditorConfiguration}
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @since 3.2
   */
  public RichTextEditor( Composite parent, RichTextEditorConfiguration editorConfig ) {
    this( parent, editorConfig, SWT.NONE );
  }

  /**
   * Constructs a new instance of this class given its parent, the given
   * {@link ToolbarConfiguration} and a style value describing its behavior and appearance.
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param editorConfig the {@link RichTextEditorConfiguration} to use or <code>null</code> for
   *          using the default {@link RichTextEditorConfiguration}
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   * <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException
   * <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @since 3.2
   */
  public RichTextEditor( Composite parent, RichTextEditorConfiguration editorConfig, int style ) {
    super( parent, style );
    // init editor configuration
    if( editorConfig == null ) {
      config = new RichTextEditorConfiguration();
    } else {
      config = editorConfig;
    }
    registerResources();
    loadJavaScript();
    Connection connection = RWT.getUISession().getConnection();
    remoteObject = connection.createRemoteObject( REMOTE_TYPE );
    remoteObject.setHandler( operationHandler );
    remoteObject.set( "parent", getId( this ) );
    remoteObject.set( "config", config.toJson() );
  }

  private void registerResources() {
    ResourceManager resourceManager = RWT.getResourceManager();
    for( String fileName : RESOURCE_FILES ) {
      registerFileIfNeeded( resourceManager, fileName );
    }
    String lang = ( String )config.getOption( RichTextEditorConfiguration.LANGUAGE );
    String defaultLang = ( String )config.getOption( RichTextEditorConfiguration.DEFAULT_LANGUAGE );
    registerFileIfNeeded( resourceManager, "lang/" + lang + ".js" );
    registerFileIfNeeded( resourceManager, "lang/" + defaultLang + ".js" );
  }

  private static void registerFileIfNeeded( ResourceManager resourceManager, String fileName ) {
    boolean isRegistered = resourceManager.isRegistered( REGISTER_PATH + fileName );
    if( !isRegistered ) {
      try {
        register( resourceManager, fileName );
      } catch( IOException ioe ) {
        throw new IllegalArgumentException( "Failed to load resources", ioe );
      }
    }
  }

  private static void loadJavaScript() {
    ClientFileLoader loader = RWT.getClient().getService( ClientFileLoader.class );
    ResourceManager resourceManager = RWT.getResourceManager();
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "ckeditor.js" ) );
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "config.js" ) );
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "RichTextEditor.js" ) );
    loader.requireJs( resourceManager.getLocation( REGISTER_PATH + "RichTextEditorHandler.js" ) );
  }

  private static void register( ResourceManager resourceManager, String fileName ) throws IOException {
    ClassLoader classLoader = RichTextEditor.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( RESOURCES_PATH + fileName );
    try {
      resourceManager.register( REGISTER_PATH + fileName, inputStream );
    } finally {
      inputStream.close();
    }
  }

  /**
   * This method returns the {@link RichTextEditorConfiguration} that is used to configure this
   * {@link RichTextEditor}. It can be used to change some configurations at runtime.
   *
   * @return The {@link RichTextEditorConfiguration} used to configure this {@link RichTextEditor}.
   * @since 3.2
   */
  public RichTextEditorConfiguration getEditorConfiguration() {
    return config;
  }

  @Override
  public void setLayout( Layout layout ) {
    throw new UnsupportedOperationException( "Cannot change internal layout of RichTextEditor" );
  }

  @Override
  public void setFont( Font font ) {
    super.setFont( font );
    remoteObject.set( "font", getCssFont() );
  }

  @Override
  public void dispose() {
    if( !isDisposed() ) {
      remoteObject.destroy();
    }
    super.dispose();
  }

  /**
   * Set text to the editing area. Can contain HTML tags for styling.
   *
   * @param text The text to set to the editing area.
   */
  public void setText( String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    remoteObject.set( "text", text );
  }

  /**
   * Get the text from the editing area. Contains HTML tags for formatting.
   *
   * @return The text that is currently set in the editing area.
   */
  public String getText() {
    checkWidget();
    return text;
  }

  /**
   * Insert text to the editing area.  Appends the text in the current cursor position
   * Can contain HTML tags for styling.
   *
   * @param text The text to insert into the editing area.
   * @since 4.0
   */
  public void insertText( String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    JsonObject parameters = new JsonObject().add( "text", text );
    remoteObject.call( "insertText", parameters );
  }

  /**
   * Returns the editable state.
   *
   * @return whether or not the receiver is editable
   *
   */
  public boolean isEditable() {
    checkWidget();
    return editable;
  }

  /**
   * Sets the editable state.
   *
   * @param editable the new editable state
   */
  public void setEditable( boolean editable ) {
    checkWidget();
    if( this.editable != editable ) {
      this.editable = editable;
      remoteObject.set( "editable", editable );
    }
  }

  @Override
  public boolean isReparentable() {
    checkWidget();
    // CKEditor can't be reparented
    return false;
  }

  private String getCssFont() {
    StringBuilder result = new StringBuilder();
    if( getFont() != null ) {
      FontData data = getFont().getFontData()[ 0 ];
      result.append( data.getHeight() );
      result.append( "px " );
      result.append( data.getName() );
    }
    return result.toString();
  }

}
