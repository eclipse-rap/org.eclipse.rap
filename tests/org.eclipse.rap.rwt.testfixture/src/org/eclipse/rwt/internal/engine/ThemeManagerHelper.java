/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - bug 348056: Eliminate compiler warnings
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.engine.ResourceLoader;
import org.eclipse.rwt.internal.theme.ThemeManager;


public class ThemeManagerHelper {
  private static ThemeManager themeManager;
  static {
    replaceStandardResourceLoader();
  }
  
  public static class TestThemeManager extends ThemeManager {
    boolean activated;
    boolean deactivated;
    
    public void deactivate() {
      // ignore reset for test cases to improve performance
      deactivated = true;
    }
    
    public void activate() {
      if( !activated ) {
        super.activate();
        activated = true;
      } else {
        RWTFactory.getResourceManager().register( "dummy" );
      }
      deactivated = false;
    }
    
    @Override
    public String[] getRegisteredThemeIds() {
      String[] result = new String[ 0 ];
      if( !deactivated ) {
        result = super.getRegisteredThemeIds();
      }
      return result;
    }
    
    public void resetInstanceInTestCases() {
      activated = false;
      super.deactivate();
    }
  }

  // TODO [ApplicationContext]: Used as performance optimized solution for tests. At the time being 
  //      buffering speeds up RWTAllTestSuite about 10% on my machine. Think about a less intrusive 
  //      solution.
  private static class TestResourceLoader implements ResourceLoader {
    private ClassLoader classLoader = ThemeManager.class.getClassLoader();
    private Map<String,StreamBuffer> resourceStreams = new HashMap<String,StreamBuffer>();

    public InputStream getResourceAsStream( final String name )
      throws IOException
    {
      StreamBuffer result = resourceStreams.get( name );
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

  public static void adaptApplicationContext( Object toAdapt ) {
    if( toAdapt instanceof ApplicationContext ) {
      ApplicationContext context = ( ApplicationContext )toAdapt;
      context.setThemeManager( getInstance() );
    }
  }

  public static void resetThemeManager() {
    if( isThemeManagerAvailable() ) {
      doThemeManagerReset();
    }
  }

  public static void resetThemeManagerIfNeeded() {
    if( isThemeManagerResetNeeded() ) {
      doThemeManagerReset();
    }
  }
  
  public static void replaceStandardResourceLoader() {
    ThemeManager.STANDARD_RESOURCE_LOADER = new TestResourceLoader();
  }
  
  private static ThemeManager getInstance() {
    if( themeManager == null ) {
      themeManager = new TestThemeManager();
    }
    return themeManager;
  }

  private static void doThemeManagerReset() {
    TestThemeManager themeManager = ( TestThemeManager )RWTFactory.getThemeManager();
    themeManager.resetInstanceInTestCases();
  }

  private static boolean isThemeManagerResetNeeded() {
    return    isThemeManagerAvailable()
           && getThemeManager().getRegisteredThemeIds().length != 1;
  }

  private static boolean isThemeManagerAvailable() {
    return getThemeManager() != null;
  }

  private static ThemeManager getThemeManager() {
    ThemeManager result = null;
    try {
      result = RWTFactory.getThemeManager();
    } catch( IllegalStateException noApplicationContextAvailable ) {
    } catch( IllegalArgumentException noThemeManagerRegisterd ) {
    }
    return result;
  }
}