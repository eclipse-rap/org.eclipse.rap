/*******************************************************************************
 * Copyright (c) 2016 arxes-tolina GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wojtek Polcwiartek <wojciech.polcwiartek@tolina.de> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import static org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration.DEFAULT_LANGUAGE;
import static org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration.LANGUAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Map;

import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings( "deprecation" )
public class RichTextEditorConfiguration_Test {

  private RichTextEditorConfiguration config;

  @Before
  public void setUp() {
    config = new RichTextEditorConfiguration();
  }

  @Test
  public void testGetAllOptions_withDefaultConfigurations() {
    Map<String, Object> customConfiguration = config.getAllOptions();
    assertEquals( Locale.getDefault().getLanguage(), customConfiguration.get( LANGUAGE ) );
    assertEquals( "en", customConfiguration.get( DEFAULT_LANGUAGE ) );
  }

  @Test
  public void testGetAllOptions_withCustomConfigurations() {
    config.setDefaultLanguage( Locale.GERMAN );
    config.setLanguage( Locale.FRANCE );
    config.setOption( "OTHER_KEY", "OTHER_VALUE" );
    Map<String, Object> customConfiguration = config.getAllOptions();
    assertEquals( "fr", customConfiguration.get( LANGUAGE ) );
    assertEquals( "de", customConfiguration.get( DEFAULT_LANGUAGE ) );
    assertEquals( "OTHER_VALUE", customConfiguration.get( "OTHER_KEY" ) );
  }

  @Test
  public void testToJason_withDefaultConfigurations() {
    assertDefaultConfiguration( config.toJson() );
  }

  @Test
  public void testDefaultConfiguration_fromToolbarConfiguration() {
    config = new RichTextEditorConfiguration( new ToolbarConfiguration() );

    assertDefaultConfiguration( config.toJson() );
  }

  @Test
  public void testToJason_withCustomConfiguration() {
    config.setDefaultLanguage( Locale.ITALIAN );
    config.setLanguage( Locale.JAPANESE );
    config.setRemoveStyles( false );
    config.setRemoveFormat( false );
    config.setRemovePasteFromWord( false );
    config.setRemovePasteText( false );
    config.setToolbarCollapsible( true );
    config.setToolbarInitialExpanded( false );

    JsonObject json = config.toJson();

    assertEquals( "it", json.get( "defaultLanguage" ).asString());
    assertEquals( "ja", json.get( "language" ).asString());
    assertFalse( json.get( "toolbarStartupExpanded" ).asBoolean() );
    assertTrue( json.get( "toolbarCanCollapse" ).asBoolean() );
    JsonValue toolbarGroupsValue = json.get( "toolbarGroups" );
    JsonArray toolbarGroupsArray = toolbarGroupsValue.asArray();
    assertEquals( 5, toolbarGroupsArray.size() );
    assertEquals( "", json.get( "removeButtons" ).asString());
  }

  @Test
  public void testToJason_withCustomConfiguration_numberOption() {
    config.setOption( "integer", Integer.valueOf( 10 ) );
    config.setOption( "long", Long.valueOf( 11 ) );
    config.setOption( "float", Float.valueOf( 12 ) );
    config.setOption( "double", Double.valueOf( 13 ) );

    JsonObject json = config.toJson();

    assertEquals( 10, json.get( "integer" ).asInt());
    assertEquals( 11, json.get( "long" ).asLong());
    assertEquals( 12, json.get( "float" ).asFloat(), 0.0001 );
    assertEquals( 13, json.get( "double" ).asDouble(), 0.0001);
  }

  private static void assertDefaultConfiguration( JsonObject json ) {
    assertEquals( "en", json.get( "defaultLanguage" ).asString());
    assertEquals( Locale.getDefault().getLanguage(), json.get( "language" ).asString());
    assertTrue( json.get( "toolbarStartupExpanded" ).asBoolean() );
    assertFalse( json.get( "toolbarCanCollapse" ).asBoolean() );
    JsonValue toolbarGroupsValue = json.get( "toolbarGroups" );
    JsonArray toolbarGroupsArray = toolbarGroupsValue.asArray();
    assertEquals( 5, toolbarGroupsArray.size() );
    JsonObject basicStylesGroup = toolbarGroupsArray.get( 0 ).asObject();
    assertEquals( "basicstyles", basicStylesGroup.get( "name" ).asString() );
    JsonArray basicStylesGroups = basicStylesGroup.get( "groups" ).asArray();
    assertEquals( 2, basicStylesGroups.size() );
    JsonObject paragraphGroup = toolbarGroupsArray.get( 1 ).asObject();
    assertEquals( "paragraph", paragraphGroup.get( "name" ).asString() );
    JsonArray paragraphGroups = paragraphGroup.get( "groups" ).asArray();
    assertEquals( 3, paragraphGroups.size() );
    assertEquals( "/", toolbarGroupsArray.get( 2 ).asString() );
    JsonObject styles = toolbarGroupsArray.get( 3 ).asObject();
    assertEquals( "styles", styles.get( "name" ).asString() );
    JsonObject colors = toolbarGroupsArray.get( 4 ).asObject();
    assertEquals( "colors", colors.get( "name" ).asString() );
    assertEquals( "PasteText,PasteFromWord,Styles,Format", json.get( "removeButtons" ).asString());
  }

}
