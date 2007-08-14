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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class JSListenerType_Test extends TestCase {
  
  public void testJSListenerTypes() throws Exception {
    List values = JSListenerType.VALUES;
    assertNotNull( values );
    assertEquals( 2, values.size() );
    
    Object jsListenerType0 = values.get( 0 );
    assertSame( JSListenerType.ACTION, jsListenerType0 );
    assertEquals( "ACTION", JSListenerType.ACTION.toString() );
    
    Object jsListenerType1 = values.get( 1 );
    assertSame( JSListenerType.STATE_AND_ACTION, jsListenerType1 );
    assertEquals( "STATE_AND_ACTION", 
                  JSListenerType.STATE_AND_ACTION.toString() );
    
    Object[] jsListenerTypes = values.toArray();
    Arrays.sort( jsListenerTypes );
    for( int i = 0; i < jsListenerTypes.length; i++ ) {
      assertSame( values.get( i ), jsListenerTypes[ i ] );
    }
    
    assertSame( JSListenerType.ACTION, 
                values.get( JSListenerType.ACTION.getOrdinal() ) );
    assertSame( JSListenerType.STATE_AND_ACTION, 
                values.get( JSListenerType.STATE_AND_ACTION.getOrdinal() ) );
  }
}
