// Created on 20.10.2006
package org.eclipse.rwt.internal.lifecycle;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class Scope_Test extends TestCase {
  
  public void testScopes() throws Exception {
    List values = Scope.VALUES;
    assertNotNull( values );
    assertEquals( 2, values.size() );
    
    Object scope0 = values.get( 0 );
    assertSame( Scope.APPLICATION,scope0 );
    assertEquals( "APPLICATION", Scope.APPLICATION.toString() );
    
    Object scope1 = values.get( 1 );
    assertSame( Scope.SESSION, scope1 );
    assertEquals( "SESSION", Scope.SESSION.toString() );
    
    Object[] scopes = values.toArray();
    Arrays.sort( scopes );
    for( int i = 0; i < scopes.length; i++ ) {
      assertSame( values.get( i ), scopes[ i ] );
    }
    
    assertSame( Scope.APPLICATION, 
                values.get( Scope.APPLICATION.getOrdinal() ) );
    assertSame( Scope.SESSION, 
                values.get( Scope.SESSION.getOrdinal() ) );
  }
}
