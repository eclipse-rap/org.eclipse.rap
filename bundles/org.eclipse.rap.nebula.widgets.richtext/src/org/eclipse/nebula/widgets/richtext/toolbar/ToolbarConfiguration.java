/*****************************************************************************
 * Copyright (c) 2015, 2016 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *	  Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *    EclipseSource - ongoing development
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.toolbar;

import java.util.HashSet;
import java.util.Set;


/**
 * The toolbar configuration of the CKEditor toolbar. Contains the default toolbar configuration via
 * toolbar groups and gives the ability to dynamically add/remove custom buttons.
 * <p>
 * To customize the CKEditor buttons shown in the toolbar, you need to override
 * {@link #getToolbarGroupConfiguration()} and {@link #getRemoveButtonConfiguration()}
 * </p>
 *
 * @since 3.1
 */
public class ToolbarConfiguration {

  private static final String CKEDITOR_CONFIG_TOOLBAR_GROUPS = "CKEDITOR.config.toolbarGroups";
  private static final String CKEDITOR_CONFIG_REMOVE_BUTTONS = "CKEDITOR.config.removeButtons";

  // "clipboard", "pastefromword" and "pastetext" plugins are removed in config.js

  /**
   * Configure whether to remove the <i>paste text</i> button from the toolbar. Default is
   * <code>true</code>.
   */
  /*public*/ private final boolean removePasteText = true;

  /**
   * Configure whether to remove the <i>paste from word</i> button from the toolbar. Default is
   * <code>true</code>.
   */
  /*public*/ private final boolean removePasteFromWord = true;

  /**
   * Configure whether to remove the <i>styles</i> combo box from the toolbar. Default is
   * <code>true</code>.
   */
  public boolean removeStyles = true;

  /**
   * Configure whether to remove <i>format</i> combo box from the toolbar. Default is
   * <code>true</code>.
   */
  public boolean removeFormat = true;

  /**
   * Configure if the toolbar should be collapsible. Default is <code>false</code>.
   */
  public boolean toolbarCollapsible = false;

  /**
   * Configure if the toolbar should be initially expanded. Is only interpreted if
   * {@link #toolbarCollapsible} is set to <code>true</code>. Default is <code>true</code>.
   */
  public boolean toolbarInitialExpanded = true;

  private final Set<String> removedButtons = new HashSet<>();

  /**
   * Configure CKEditor toolbar button groups. To customize the CKEditor buttons shown in the
   * toolbar, you need to override this method. The returned string is a direct representation of
   * <code>CKEDITOR.config.toolbarGroups</code> configuration property. It must start with
   * "CKEDITOR.config.toolbarGroups" and end with a semicolon. The value is a JavaScript array as
   * defined in CKEditor Documentation.
   * <p>
   * Usage:
   *
   * <pre>
   * public class MyConfig extends ToolbarConfiguration {
   *
   *   &#64;Override
   *   protected String getToolbarGroupConfiguration() {
   *     return "CKEDITOR.config.toolbarGroups = [{\"name\":\"styles\"}];";
   *   }
   * }
   * </pre>
   * </p>
   *
   * @return The toolbar group configuration for the CKEditor toolbar.
   *
   * @see <a href="http://docs.ckeditor.com/#!/guide/dev_toolbar">CKEditor Toolbar Configuration</a>
   */
  protected String getToolbarGroupConfiguration() {
    StringBuilder builder = new StringBuilder( CKEDITOR_CONFIG_TOOLBAR_GROUPS );
    builder.append( " = [" );
    builder.append( "{\"name\":\"basicstyles\",\"groups\":[\"basicstyles\",\"cleanup\"]}," );
    builder.append( "{\"name\":\"paragraph\",\"groups\":[\"list\",\"indent\",\"align\"]}," );
    builder.append( "\"/\"," );
    builder.append( "{\"name\":\"styles\"}," );
    builder.append( "{\"name\":\"colors\" }" );
    builder.append( "];" );
    return builder.toString();
  }

  /**
   * Configure CKEditor toolbar default buttons. To customize the CKEditor default buttons shown in
   * the toolbar, you could override this method. The returned string is a direct representation
   * of <code>CKEDITOR.config.removeButtons</code> configuration property. It must start with
   * "CKEDITOR.config.removeButtons" and end with a semicolon. The value is a comma-separated list
   * of default button names as defined in CKEditor Documentation.
   * <p>
   * Usage:
   *
   * <pre>
   * public class MyConfig extends ToolbarConfiguration {
   *
   *   &#64;Override
   *   protected String getRemoveButtonConfiguration() {
   *     return "CKEDITOR.config.removeButtons = \"Subscript,Superscript\";";
   *   }
   * }
   * </pre>
   * </p>
   *
   * @return The configuration which default buttons should be removed from the toolbar.
   *
   * @see <a href="http://docs.ckeditor.com/#!/guide/dev_toolbar">CKEditor Toolbar Configuration</a>
   */
  protected String getRemoveButtonConfiguration() {
    StringBuilder builder = new StringBuilder( "CKEDITOR.config.removeButtons = \"" );
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
    for( String removed : removedButtons ) {
      builder.append( "," ).append( removed );
    }
    builder.append( "\";" );
    return builder.toString();
  }

  /**
   * Adds the CKEditor default button for the given name to the toolbar.
   * <p>
   * <i>Note: This works only for buttons that have been removed using
   * {@link #removeDefaultToolbarButton(String)}</i>
   * </p>
   *
   * @param buttonName The name of the CKEditor default button to add.
   */
  public void addDefaultToolbarButton( String buttonName ) {
    this.removedButtons.remove( buttonName );
  }

  /**
   * Removes the CKEditor default button for the given name from the toolbar.
   *
   * @param buttonName The name of the CKEditor default button to remove.
   */
  public void removeDefaultToolbarButton( String buttonName ) {
    this.removedButtons.add( buttonName );
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder( "{" );
    builder.append( "\"toolbarGroups\":" ).append( extractToolbarGroupConfiguration() ).append( "," );
    builder.append( "\"removeButtons\":" ).append( extractRemoveButtonConfiguration() ).append( "," );
    builder.append( "\"toolbarCanCollapse\":" ).append( toolbarCollapsible ).append( "," );
    builder.append( "\"toolbarStartupExpanded\":" ).append( toolbarInitialExpanded );
    builder.append( "}" );
    return builder.toString();
  }

  private String extractToolbarGroupConfiguration() {
    String toolbarGroupConfiguration = getToolbarGroupConfiguration().trim();
    if(    !toolbarGroupConfiguration.startsWith( CKEDITOR_CONFIG_TOOLBAR_GROUPS )
        || !toolbarGroupConfiguration.endsWith( ";" ) ) {
      throw new RuntimeException( "Invalid CKEditor toolbarGroups configuration" );
    }
    int equalSignIndex = toolbarGroupConfiguration.indexOf( '=' );
    int lastSemicolonIndex = toolbarGroupConfiguration.lastIndexOf( ';' );
    return toolbarGroupConfiguration.substring( equalSignIndex + 1, lastSemicolonIndex ).trim();
  }

  private String extractRemoveButtonConfiguration() {
    String removeButtonConfiguration = getRemoveButtonConfiguration().trim();
    if(    !removeButtonConfiguration.startsWith( CKEDITOR_CONFIG_REMOVE_BUTTONS )
        || !removeButtonConfiguration.endsWith( ";" ) ) {
      throw new RuntimeException( "Invalid CKEditor removeButtons configuration" );
    }
    int equalSignIndex = removeButtonConfiguration.indexOf( '=' );
    int lastSemicolonIndex = removeButtonConfiguration.lastIndexOf( ';' );
    return removeButtonConfiguration.substring( equalSignIndex + 1, lastSemicolonIndex ).trim();
  }

}
