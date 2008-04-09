package org.eclipse.rwt.internal;

import java.text.MessageFormat;

public class FacadesInitializer {

  public static Object load( final Class facade ) {
    String name = facade.getName();
    Object result = null;
    try {
      ClassLoader loader = facade.getClassLoader();
      result = loader.loadClass( name + "Impl" ).newInstance();
    } catch( Throwable throwable ) {
      String txt = "Could not load facade for {0}";
      String msg = MessageFormat.format( txt, new Object[] { name } );
      throw new RuntimeException( msg, throwable );
    }
    return result;
  }
}
