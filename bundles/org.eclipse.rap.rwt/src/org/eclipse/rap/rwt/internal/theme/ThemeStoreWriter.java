/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rap.rwt.service.ApplicationContext;


public final class ThemeStoreWriter {

  private final IThemeCssElement[] allThemeableWidgetElements;
  private final Theme theme;
  private final ApplicationContext applicationContext;

  public ThemeStoreWriter( ApplicationContext applicationContext,
                           Theme theme,
                           IThemeCssElement[] elements )
  {
    this.applicationContext = applicationContext;
    this.theme = theme;
    allThemeableWidgetElements = elements;
  }

  public String createJson() {
    CssType[] allValues = theme.getValuesMap().getAllValues();
    Map valuesMap = createValuesMap( allValues );
    JsonObject json = new JsonObject();
    json.add( "values", createJsonFromValuesMap( valuesMap ) );
    json.add( "theme", createThemeJson() );
    return json.toString();
  }

  private JsonObject createThemeJson() {
    JsonObject result = new JsonObject();
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    for( int i = 0; i < allThemeableWidgetElements.length; i++ ) {
      IThemeCssElement element = allThemeableWidgetElements[ i ];
      String elementName = element.getName();
      JsonObject elementObj = createThemeJsonForElement( valuesMap, element );
      result.add( elementName, elementObj );
    }
    return result;
  }

  private JsonObject createThemeJsonForElement( ThemeCssValuesMap valuesMap,
                                                IThemeCssElement element )
  {
    JsonObject result = new JsonObject();
    String[] properties = element.getProperties();
    ThemePropertyAdapterRegistry registry
      = ThemePropertyAdapterRegistry.getInstance( applicationContext );
    for( int i = 0; i < properties.length; i++ ) {
      String propertyName = properties[ i ];
      JsonArray valuesArray = new JsonArray();
      String elementName = element.getName();
      ConditionalValue[] values = valuesMap.getValues( elementName, propertyName );
      for( int j = 0; j < values.length; j++ ) {
        ConditionalValue conditionalValue = values[ j ];
        JsonArray array = new JsonArray();
        array.add( createJsonArray( conditionalValue.constraints ) );
        CssType value = conditionalValue.value;
        ThemePropertyAdapter adapter = registry.getPropertyAdapter( value.getClass() );
        String cssKey = adapter.getKey( value );
        array.add( cssKey );
        valuesArray.add( array );
      }
      result.add( propertyName, valuesArray );
    }
    return result;
  }

  private Map createValuesMap( CssType[] values ) {
    Map<String,JsonObject> result = new LinkedHashMap<String,JsonObject>();
    for( int i = 0; i < values.length; i++ ) {
      appendValueToMap( values[ i ], result );
    }
    return result;
  }

  private void appendValueToMap( CssType propertyValue, Map<String,JsonObject> valuesMap ) {
    ThemePropertyAdapterRegistry registry
      = ThemePropertyAdapterRegistry.getInstance( applicationContext );
    ThemePropertyAdapter adapter = registry.getPropertyAdapter( propertyValue.getClass() );
    if( adapter != null ) {
      String slot = adapter.getSlot( propertyValue );
      if( slot != null ) {
        String key = adapter.getKey( propertyValue );
        JsonValue value = adapter.getValue( propertyValue );
        if( value != null ) {
          JsonObject slotObject = getSlot( valuesMap, slot );
          slotObject.add( key, value );
        }
      }
    }
  }

  private static JsonValue createJsonFromValuesMap( Map valuesMap ) {
    JsonObject result = new JsonObject();
    Set entrySet = valuesMap.entrySet();
    Iterator keyIterator = entrySet.iterator();
    while( keyIterator.hasNext() ) {
      Entry entry = ( Entry )keyIterator.next();
      String key = ( String )entry.getKey();
      JsonValue value = ( JsonValue )entry.getValue();
      result.add( key, value );
    }
    return result;
  }

  private static JsonObject getSlot( Map<String,JsonObject> valuesMap, String name ) {
    JsonObject result = valuesMap.get( name );
    if( result == null ) {
      result = new JsonObject();
      valuesMap.put( name, result );
    }
    return result;
  }

}
