/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - bug 348056: Eliminate compiler warnings
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture.internal.engine;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ResourceLoader;


public class ThemeManagerHelper {
  private static ThemeManager themeManager;
  static {
    replaceStandardResourceLoader();
  }

  public static class TestThemeManager extends ThemeManager {
    boolean initialized;
    boolean activated;
    boolean deactivated;

    @Override
    public void deactivate() {
      // ignore reset for test cases to improve performance
      deactivated = true;
    }

    @Override
    public void initialize() {
      if( !initialized ) {
        // Register empty default theme. Execute tests against fall-back theme.
        registerTheme( new Theme( RWT.DEFAULT_THEME_ID, "RAP Default Theme", null ) );
        initialized = true;
      }
    }

    @Override
    public void activate() {
      if( !activated ) {
        super.activate();
        activated = true;
      } else {
        InputStream inputStream = new ByteArrayInputStream( "dummy".getBytes() );
        RWTFactory.getResourceManager().register( "dummy", inputStream );
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
      initialized = false;
      activated = false;
      super.deactivate();
    }
  }

  // TODO [ApplicationContext]: Used as performance optimized solution for tests. At the time being
  //      buffering speeds up RWTAllTestSuite about 10% on my machine. Think about a less intrusive
  //      solution.
  private static class TestResourceLoader implements ResourceLoader {
    private final ClassLoader classLoader = ThemeManager.class.getClassLoader();
    private final Map<String,StreamBuffer> resourceStreams = new HashMap<String,StreamBuffer>();

    public InputStream getResourceAsStream( String name )
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

    private StreamBuffer bufferStream( String name ) {
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
    @Override
    public void close() throws IOException {}
  }

  public static void adaptApplicationContext( Object toAdapt ) {
    if( toAdapt instanceof ApplicationContextImpl ) {
      ApplicationContextImpl context = ( ApplicationContextImpl )toAdapt;
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
    boolean result = isThemeManagerAvailable();
    if( result ) {
      List<String> registeredThemeIds = Arrays.asList( getThemeManager().getRegisteredThemeIds() );
      if( registeredThemeIds.size() == 2 ) {
        result =    !registeredThemeIds.contains( ThemeManager.FALLBACK_THEME_ID )
                 || !registeredThemeIds.contains( RWT.DEFAULT_THEME_ID );
      }
    }
    return result;
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