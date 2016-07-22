/*****************************************************************************
 * Copyright (c) 2016 Dirk Fauth.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *      Wojtek Polcwiartek <wojciech.polcwiartek@tolina.de> - RAP implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;

/**
 * Configuration class that is used for general configurations of the CKEditor instance.
 *
 * @since 3.2
 */
public class RichTextEditorConfiguration {

  /**
   * Key for the default language configuration.
   */
  public static final String DEFAULT_LANGUAGE = "defaultLanguage";

  /**
   * Key for the language configuration.
   */
  public static final String LANGUAGE = "language";

  /**
   * Key for toolbar groups configuration.
   */
  public static final String TOOLBAR_GROUPS = "toolbarGroups";

  /**
   * Key for toolbar buttons that should not be rendered.
   */
  public static final String REMOVE_BUTTONS = "removeButtons";
  /**
   *
   * Key to configure whether the toolbar can be collapsed by the user.
   */
  public static final String TOOLBAR_CAN_COLLAPSE = "toolbarCanCollapse";

  /**
   * Key to configure whether the toolbar must start expanded when the editor is loaded.
   */
  public static final String TOOLBAR_STARTUP_EXPANDED = "toolbarStartupExpanded";
  private static final String DEFAULT_TOOLBAR_GROUPS = createDefaultToolbarGroups();

  /**
   * Configure whether to remove the <i>paste text</i> button from the toolbar. Default is
   * <code>true</code>.
   */
  private boolean removePasteText = true;
  /**
   * Configure whether to remove the <i>paste from word</i> button from the toolbar. Default is
   * <code>true</code>.
   */
  private boolean removePasteFromWord = true;
  /**
   * Configure whether to remove the <i>styles</i> combo box from the toolbar. Default is
   * <code>true</code>.
   */
  private boolean removeStyles = true;
  /**
   * Configure whether to remove <i>format</i> combo box from the toolbar. Default is
   * <code>true</code>.
   */
  private boolean removeFormat = true;

  private final Map<String, Object> options = new HashMap<>();
  private final Set<String> removedButtons = new HashSet<>();


  /**
   * Creates a new instance for general configurations that are added to the created CKEditor
   * instance at initialization.
   */
  public RichTextEditorConfiguration() {
    options.put( DEFAULT_LANGUAGE, Locale.ENGLISH.getLanguage() );
    options.put( LANGUAGE, Locale.getDefault().getLanguage() );
    setToolbarCollapsible( false );
    setToolbarInitialExpanded( true );
    options.put( TOOLBAR_GROUPS, DEFAULT_TOOLBAR_GROUPS );
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  @SuppressWarnings( "deprecation" )
  RichTextEditorConfiguration( org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration config ) {
    this();
    removePasteText = config.removePasteText;
    removePasteFromWord = config.removePasteFromWord;
    removeStyles = config.removeStyles;
    removeFormat = config.removeFormat;
    removedButtons.addAll( config.getRemovedButtons() );
    setToolbarCollapsible( config.toolbarCollapsible );
    setToolbarInitialExpanded( config.toolbarInitialExpanded );
    JsonObject jsonConfig = JsonObject.readFrom( config.toString() );
    // set the option like this in case the method itself was overridden by subclassing
    options.put( TOOLBAR_GROUPS, jsonConfig.get( TOOLBAR_GROUPS ).toString() );
    // set the option like this in case the method itself was overridden by subclassing
    options.put( REMOVE_BUTTONS, jsonConfig.get( REMOVE_BUTTONS ).asString() );
  }

  private static String createDefaultToolbarGroups() {
    StringBuilder builder = new StringBuilder();
    builder.append( "[" );
    builder.append( "{\"name\":\"basicstyles\",\"groups\":[\"basicstyles\",\"cleanup\"]}," );
    builder.append( "{\"name\":\"paragraph\",\"groups\":[\"list\",\"indent\",\"align\"]}," );
    builder.append( "\"/\"," );
    builder.append( "{\"name\":\"styles\"}," );
    builder.append( "{\"name\":\"colors\" }" );
    builder.append( "]" );
    return builder.toString();
  }

  /**
   * Adds a new option to the configuration.
   *
   * @param key The configuration option key.
   * @param value The configuration option value.
   * @see <a href="http://docs.ckeditor.com/#!/api/CKEDITOR.config">CKEDITOR.config</a>
   */
  public void setOption( String key, Object value ) {
    options.put( key, value );
  }

  /**
   * Returns a configuration option set in this {@link RichTextEditorConfiguration}.
   *
   * @param key The configuration option key for which the value is requested.
   * @return The configuration option value for the given key or <code>null</code> in case there is
   *         nothing configured for that key.
   */
  public Object getOption( String key ) {
    return options.get( key );
  }

  /**
   * @return An unmodifiable map that contains all configuration option values.
   */
  public Map<String, Object> getAllOptions() {
    return Collections.unmodifiableMap( options );
  }

  // convenience methods
  /**
   * @param lang The user interface language localization to use. If left empty, the editor will
   *          automatically be localized to the user language. If the user language is not
   *          supported, the language specified in the <i>defaultLanguage</i> configuration setting
   *          is used.
   */
  public void setLanguage( String lang ) {
    options.put( LANGUAGE, lang );
  }

  /**
   * @param locale The user interface language localization to use. If left empty, the editor will
   *          automatically be localized to the user language. If the user language is not
   *          supported, the language specified in the <i>defaultLanguage</i> configuration setting
   *          is used.
   */
  public void setLanguage( Locale locale ) {
    setLanguage( locale.getLanguage() );
  }

  /**
   * @param lang The language to be used if the language setting is left empty and it is not
   *          possible to localize the editor to the user language.
   */
  public void setDefaultLanguage( String lang ) {
    options.put( DEFAULT_LANGUAGE, lang );
  }

  /**
   * @param locale The language to be used if the language setting is left empty and it is not
   *          possible to localize the editor to the user language.
   */
  public void setDefaultLanguage( Locale locale ) {
    setDefaultLanguage( locale.getLanguage() );
  }

  /**
   * @param removePasteText <code>true</code> to remove the <i>paste text</i> button from the
   *          toolbar.
   */
  public void setRemovePasteText( boolean removePasteText ) {
    this.removePasteText = removePasteText;
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * @param removePasteFromWord <code>true</code> to remove the <i>paste from word</i> button from
   *          the toolbar.
   */
  public void setRemovePasteFromWord( boolean removePasteFromWord ) {
    this.removePasteFromWord = removePasteFromWord;
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * @param removeStyles <code>true</code> to remove the <i>styles</i> combo box from the toolbar.
   */
  public void setRemoveStyles( boolean removeStyles ) {
    this.removeStyles = removeStyles;
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * @param removeFormat <code>true</code> to remove <i>format</i> combo box from the toolbar.
   */
  public void setRemoveFormat( boolean removeFormat ) {
    this.removeFormat = removeFormat;
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * Adds the CKEditor default button for the given name to the toolbar.
   * <p>
   * <i>Note: This works only for buttons that have been removed using
   * {@link #removeDefaultToolbarButton(String[])}</i>
   * </p>
   *
   * @param buttonNames The names of the CKEditor default button to add.
   */
  public void addDefaultToolbarButton( String... buttonNames ) {
    for( String buttonName : buttonNames ) {
      removedButtons.remove( buttonName );
    }
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * Removes the CKEditor default button for the given name from the toolbar.
   *
   * @param buttonNames The names of the CKEditor default button to remove.
   */
  public void removeDefaultToolbarButton( String... buttonNames ) {
    // remember the button that should be removed
    for( String buttonName : buttonNames ) {
      removedButtons.add( buttonName );
    }
    options.put( REMOVE_BUTTONS, getRemoveButtonConfiguration() );
  }

  /**
   * @return The configuration which default buttons should be removed from the toolbar.
   */
  private String getRemoveButtonConfiguration() {
    // Subscript and Superscript are not supported styling options for the
    // Rich Text Viewer
    StringBuilder builder = new StringBuilder();
    if( removePasteText ) {
      builder.append( ",PasteText" );
    }
    if( removePasteFromWord ) {
      builder.append( ",PasteFromWord" );
    }
    if( removeStyles ) {
      builder.append( ",Styles" );
    }
    if( removeFormat ) {
      builder.append( ",Format" );
    }
    for( String removed : this.removedButtons ) {
      builder.append( "," ).append( removed );
    }
    String removeButtons = builder.toString();
    if( removeButtons.startsWith( "," ) ) {
      return removeButtons.substring( 1 );
    }
    return removeButtons;
  }

  /**
   * Configure if the toolbar should be collapsible. Default is <code>false</code>.
   *
   * @param toolbarCollapsible <code>true</code> if the toolbar should be collapsible,
   *          <code>false</code> if not.
   */
  public void setToolbarCollapsible( boolean toolbarCollapsible ) {
    this.options.put( TOOLBAR_CAN_COLLAPSE, Boolean.valueOf( toolbarCollapsible ) );
  }

  /**
   * Configure if the toolbar should be initially expanded. Default is <code>true</code>.
   *
   * @param toolbarInitialExpanded <code>true</code> if the toolbar should be initially expanded,
   *          <code>false</code> if not.
   */
  public void setToolbarInitialExpanded( boolean toolbarInitialExpanded ) {
    this.options.put( TOOLBAR_STARTUP_EXPANDED, Boolean.valueOf( toolbarInitialExpanded ) );
  }

  JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    Map<String, Object> allOptions = getAllOptions();
    for( Entry<String, Object> entry : allOptions.entrySet() ) {
      String optionName = entry.getKey();
      Object optionValue = entry.getValue();
      JsonValue jsonValue = createJsonValue( optionValue );
      jsonObject.add( optionName, jsonValue );
    }
    return jsonObject;
  }

  private static JsonValue createJsonValue( Object value ) {
    if( value instanceof Boolean ) {
      Boolean bool = ( Boolean )value;
      return JsonValue.valueOf( bool.booleanValue() );
    }
    if( value instanceof String ) {
      String str = ( String )value;
      return processString( str );
    }
    if( value instanceof Integer ) {
      Integer num = ( Integer )value;
      return JsonValue.valueOf( num.intValue() );
    }
    if( value instanceof Long ) {
      Long num = ( Long )value;
      return JsonValue.valueOf( num.longValue() );
    }
    if( value instanceof Float ) {
      Float num = ( Float )value;
      return JsonValue.valueOf( num.floatValue() );
    }
    if( value instanceof Double ) {
      Double num = ( Double )value;
      return JsonValue.valueOf( num.doubleValue() );
    }
    System.out.println( value );
    String message = "Only a RichTextEditorConfiguration with Boolean, String, Integer, Long, "
                   + "Float and Double values is currently supported";
    throw new IllegalArgumentException( message );
  }

  private static JsonValue processString( String str ) {
    if( str == null || str.length() < 1 ) {
      return JsonValue.valueOf( "" );
    }
    if( str.charAt( 0 ) == '[' || str.charAt( 0 ) == '{' ) {
      return JsonValue.readFrom( str );
    }
    return JsonValue.valueOf( str );
  }

}
