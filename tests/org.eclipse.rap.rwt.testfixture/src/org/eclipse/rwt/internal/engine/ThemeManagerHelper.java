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
package org.eclipse.rwt.internal.engine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.theme.*;


public class ThemeManagerHelper {
  private static ThemeManagerHolder themeManagerHolder;
  static {
    ThemeManager.STANDARD_RESOURCE_LOADER = new TestResourceLoader();
  }
  
  public static class TestThemeManagerHolder extends ThemeManagerHolder {
    boolean activated;
    
    public void deactivate() {
      // ignore reset for test cases to improve performance
    }
    
    public void activate() {
      if( !activated ) {
        super.activate();
        activated = true;
      }
    }
    
    public void resetInstanceInTestCases() {
      activated = false;
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
  
  private static ThemeManagerHolder getInstance() {
    if( themeManagerHolder == null ) {
      themeManagerHolder = new TestThemeManagerHolder();
    }
    return themeManagerHolder;
  }

  private static void doThemeManagerReset() {
    TestThemeManagerHolder themeManager = ( TestThemeManagerHolder )RWTFactory.getThemeManager();
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
      result = ThemeManager.getInstance();
    } catch( IllegalStateException noApplicationContextAvailable ) {
    } catch( IllegalArgumentException noThemeManagerRegisterd ) {
    }
    return result;
  }
}