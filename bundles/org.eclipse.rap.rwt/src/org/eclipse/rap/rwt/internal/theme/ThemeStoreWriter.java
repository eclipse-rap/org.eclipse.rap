/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;


public final class ThemeStoreWriter {

  private final IThemeCssElement[] allThemeableWidgetElements;
  private final Theme theme;

  public ThemeStoreWriter( Theme theme, IThemeCssElement[] elements ) {
    this.theme = theme;
    allThemeableWidgetElements = elements;
  }

  public String createJson() {
    QxType[] allValues = theme.getValuesMap().getAllValues();
    Map valuesMap = createValuesMap( allValues );
    JsonObject json = new JsonObject();
    json.append( "values", createJsonFromValuesMap( valuesMap ) );
    json.append( "theme", createThemeJson() );
    return json.toString();
  }

  private JsonObject createThemeJson() {
    JsonObject result = new JsonObject();
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    for( int i = 0; i < allThemeableWidgetElements.length; i++ ) {
      IThemeCssElement element = allThemeableWidgetElements[ i ];
      String elementName = element.getName();
      JsonObject elementObj = createThemeJsonForElement( valuesMap, element );
      result.append( elementName, elementObj );
    }
    return result;
  }

  private JsonObject createThemeJsonForElement( ThemeCssValuesMap valuesMap,
                                                IThemeCssElement element )
  {
    JsonObject result = new JsonObject();
    String[] properties = element.getProperties();
    ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
    for( int i = 0; i < properties.length; i++ ) {
      String propertyName = properties[ i ];
      JsonArray valuesArray = new JsonArray();
      String elementName = element.getName();
      ConditionalValue[] values = valuesMap.getValues( elementName, propertyName );
      for( int j = 0; j < values.length; j++ ) {
        ConditionalValue conditionalValue = values[ j ];
        JsonArray array = new JsonArray();
        array.append( JsonArray.valueOf( conditionalValue.constraints ) );
        QxType value = conditionalValue.value;
        ThemePropertyAdapter adapter = registry.getPropertyAdapter( value.getClass() );
        String cssKey = adapter.getKey( value );
        array.append( cssKey );
        valuesArray.append( array );
      }
      result.append( propertyName, valuesArray );
    }
    return result;
  }

  private static Map createValuesMap( QxType[] values ) {
    Map<String,JsonObject> result = new LinkedHashMap<String,JsonObject>();
    for( int i = 0; i < values.length; i++ ) {
      appendValueToMap( values[ i ], result );
    }
    return result;
  }

  private static void appendValueToMap( QxType propertyValue, Map<String,JsonObject> valuesMap ) {
    ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
    ThemePropertyAdapter adapter = registry.getPropertyAdapter( propertyValue.getClass() );
    if( adapter != null ) {
      String slot = adapter.getSlot( propertyValue );
      if( slot != null ) {
        String key = adapter.getKey( propertyValue );
        JsonValue value = adapter.getValue( propertyValue );
        if( value != null ) {
          JsonObject slotObject = getSlot( valuesMap, slot );
          slotObject.append( key, value );
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
      result.append( key, value );
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
