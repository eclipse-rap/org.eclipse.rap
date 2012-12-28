/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.swt.widgets.Event;


public class EventTestHelper {

  public static void assertFieldsEqual( TypedEvent typedEvent, Event untypedEvent ) {
    Field[] typedFields = typedEvent.getClass().getFields();
    for( Field typedField : typedFields ) {
      if( !Modifier.isStatic( typedField.getModifiers() ) ) {
        Field untypedField = getField( untypedEvent, typedField.getName() );
        Object typedEventValue = getFieldValue( typedEvent, typedField );
        Object untypedEventValue = getFieldValue( untypedEvent, untypedField );
        assertEquals( typedEventValue, untypedEventValue );
      }
    }
  }

  private static Field getField( Event object, String name ) {
    try {
      return object.getClass().getField( name );
    } catch( NoSuchFieldException nsfe ) {
      throw new RuntimeException( nsfe );
    }
  }

  private static Object getFieldValue( Object object, Field field ) {
    try {
      return field.get( object );
    } catch( IllegalAccessException iae ) {
      throw new RuntimeException( iae );
    }
  }
}
