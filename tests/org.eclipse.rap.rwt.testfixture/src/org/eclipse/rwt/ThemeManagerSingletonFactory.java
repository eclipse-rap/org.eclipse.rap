/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.engine.ApplicationContext.InstanceTypeFactory;
import org.eclipse.rwt.internal.theme.*;


class ThemeManagerSingletonFactory implements InstanceTypeFactory {
  private static ThemeManagerHolder themeManagerHolder;
  static {
    ThemeManager.STANDARD_RESOURCE_LOADER = new TestResourceLoader();
  }
  
  static class TestThemeManagerHolder extends ThemeManagerHolder {
    public void resetInstance() {
      // ignore reset for test cases to improve performance
    }
    public void resetInstanceInTestCases() {
      super.resetInstance();
    }
  }

  // TODO [ApplicationContext]: Used as performance optimized solution for tests. At the time being 
  //      buffering speeds up RWTAllTestSuite about 10% on my machine. Think about a less intrusive 
  //      solution.
  private static class TestResourceLoader implements ResourceLoader {
    private ClassLoader classLoader = ThemeManager.class.getClassLoader();
    private Map resourceStreams = new HashMap();

    public InputStream getResourceAsStream( final String name )
      throws IOException
    {
      StreamBuffer result = ( StreamBuffer )resourceStreams.get( name );
      if( !hasStreamBuffer( result ) ) {
        result = bufferStream( name );
      } else {
        result.reset();
      }
      return result;
    }

    private StreamBuffer bufferStream( final String name ) {
      StreamBuffer result = null;
      InputStream in = classLoader.getResourceAsStream( name );
      if( in != null ) {
        result = new StreamBuffer( in );
        result.mark( Integer.MAX_VALUE );
        resourceStreams.put( name, result );
      }
      return result;
    }

    private boolean hasStreamBuffer( StreamBuffer result ) {
      return result != null;
    }
  }

  private static class StreamBuffer extends BufferedInputStream {
    private StreamBuffer( InputStream in ) {
      super( in );
    }
    public void close() throws IOException {}
  }


  public Object createInstance() {
    if( themeManagerHolder == null ) {
      themeManagerHolder = new TestThemeManagerHolder();
    }
    return themeManagerHolder;
  }

  public Class getInstanceType() {
    return ThemeManagerHolder.class;
  }
}
