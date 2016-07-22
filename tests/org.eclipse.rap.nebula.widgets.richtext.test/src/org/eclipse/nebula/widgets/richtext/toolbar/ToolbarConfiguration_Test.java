/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.richtext.toolbar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings( "deprecation" )
public class ToolbarConfiguration_Test {

  private ToolbarConfiguration toolbarConfig;

  @Before
  public void setUp() {
    toolbarConfig = new ToolbarConfiguration();
  }

  @Test
  public void testToString_withDefaultConfigurations() {
    JsonObject config = ( JsonObject )JsonValue.readFrom( toolbarConfig.toString() );

    assertNotNull( config.get( "toolbarGroups" ) );
    assertNotNull( config.get( "removeButtons" ) );
    assertNotNull( config.get( "toolbarCanCollapse" ) );
    assertNotNull( config.get( "toolbarStartupExpanded" ) );
  }

  @Test
  public void testToString_withCustomConfigurations() {
    toolbarConfig = new ToolbarConfiguration() {
      @Override
      protected String getToolbarGroupConfiguration() {
        return "CKEDITOR.config.toolbarGroups = [];";
      }
      @Override
      protected String getRemoveButtonConfiguration() {
        return "CKEDITOR.config.removeButtons = \"Subscript\";";
      }
    };

    JsonObject config = ( JsonObject )JsonValue.readFrom( toolbarConfig.toString() );

    assertNotNull( config.get( "toolbarGroups" ) );
    assertNotNull( config.get( "removeButtons" ) );
    assertNotNull( config.get( "toolbarCanCollapse" ) );
    assertNotNull( config.get( "toolbarStartupExpanded" ) );
  }

  @Test( expected = RuntimeException.class )
  public void testToString_throwsWithInvalidToolbarGroupsConfig() {
    toolbarConfig = new ToolbarConfiguration() {
      @Override
      protected String getToolbarGroupConfiguration() {
        return "invalid";
      }
    };

    toolbarConfig.toString();
  }

  @Test( expected = RuntimeException.class )
  public void testToString_throwsWithInvalidRemoveButtonsConfig() {
    toolbarConfig = new ToolbarConfiguration() {
      @Override
      protected String getRemoveButtonConfiguration() {
        return "invalid";
      }
    };

    toolbarConfig.toString();
  }

  @Test
  public void testRemoveDefaultToolbarButton() {
    toolbarConfig.removeDefaultToolbarButton( "Subscript" );

    JsonObject config = ( JsonObject )JsonValue.readFrom( toolbarConfig.toString() );
    assertTrue( config.get( "removeButtons" ).asString().contains( "Subscript" ) );
  }

  @Test
  public void testAddDefaultToolbarButton() {
    toolbarConfig.removeDefaultToolbarButton( "Subscript" );

    toolbarConfig.addDefaultToolbarButton( "Subscript" );

    JsonObject config = ( JsonObject )JsonValue.readFrom( toolbarConfig.toString() );
    assertFalse( config.get( "removeButtons" ).asString().contains( "Subscript" ) );
  }

}
