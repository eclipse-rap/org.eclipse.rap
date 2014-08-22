/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.internal.textsize.MeasurementUtil;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.UISession;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


/**
 * JUnit rule for unit tests that need access to a RAP context including UISession and
 * ApplicationContext. To use this rule, add this line to a test case:
 *
 * <pre>
 * @Rule
 * public TestContext context = new TestContext();
 * </pre>
 *
 * @since 3.0
 */
@SuppressWarnings( "deprecation" )
public class TestContext implements TestRule {

  private ApplicationContextImpl applicationContext;
  private UISession uiSession;

  public Statement apply( final Statement base, final Description description ) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          setup( description );
          base.evaluate();
        } finally {
          tearDown();
        }
      }
    };
  }

  public UISession getUISession() {
    return uiSession;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  private void setup( Description description ) {
    ApplicationConfiguration config = createApplicationConfiguration();
    applicationContext = createApplicationContext( config );
    uiSession = new UISessionImpl( applicationContext, new TestSession() );
    ContextProvider.setContext( createServiceContext( applicationContext, uiSession ) );
    SingletonManager.install( uiSession );
    MeasurementUtil.installMeasurementOperator( uiSession );
    ThemeUtil.setCurrentThemeId( uiSession, RWT.DEFAULT_THEME_ID );
    ProtocolUtil.setClientMessage( createMessage() );
    CurrentPhase.set( PhaseId.PROCESS_ACTION );
  }

  private void tearDown() {
    uiSession = null;
    applicationContext = null;
    ContextProvider.disposeContext();
  }

  private static ApplicationConfiguration createApplicationConfiguration() {
    ContextProvider.disposeContext();
    return new ApplicationConfiguration() {
      public void configure( Application application ) {
      }
    };
  }

  private static ApplicationContextImpl createApplicationContext( ApplicationConfiguration config )
  {
    TestServletContext servletContext = new TestServletContext();
    ApplicationContextImpl appContext = new ApplicationContextImpl( config, servletContext );
    appContext.activate();
    return appContext;
  }

  private static ServiceContext createServiceContext( ApplicationContextImpl applicationContext,
                                                      UISession uiSession )
  {
    ServiceContext serviceContext = new ServiceContext( null, null, applicationContext );
    serviceContext.setServiceStore( new ServiceStore() );
    serviceContext.setUISession( uiSession );
    return serviceContext;
  }

  private static ClientMessage createMessage() {
    JsonObject json = new JsonObject()
      .add( "head", new JsonObject() )
      .add( "operations", new JsonArray() );
    return new ClientMessage( json );
  }

}
