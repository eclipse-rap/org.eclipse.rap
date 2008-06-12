/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

/** 
 * <p>This interface provides the common W4Toolkit application parameter 
 * settings.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @see org.eclipse.rwt.internal.IConfiguration 
 */
public interface IInitialization {

  public static final String PARAM_LIFE_CYCLE = "lifecycle";

  public static final String LIFE_CYCLE_DEFAULT
    = "com.w4t.engine.lifecycle.standard.LifeCycle_Standard";
  
  public final static String NOSCRIPT_SUBMITTERS_NONE = "None";
  public final static String NOSCRIPT_SUBMITTERS_CREATE = "Create";
  public final static String NOSCRIPT_SUBMITTERS_USE = "Use";
  
  public static final String RESOURCES_DELIVER_FROM_DISK = "deliverFromDisk";
  public static final String RESOURCES_DELIVER_BY_SERVLET = "deliverByServlet";

  public static final String HANDLE_MISSING_I18N_RESOURCE_EMPTY = "Empty";
  public static final String HANDLE_MISSING_I18N_RESOURCE_FAIL = "Fail";
  public static final String HANDLE_MISSING_I18N_RESOURCE_EXPLICIT = "Explicit";


  /**
   * <p>Returns the fully qualified class name of the WebForm that will be 
   * displayed when the web application starts.</p> 
   */
  String getStartUpForm();

  /**
   * specifies the implementation class that manages the lifecycle of each
   * request.
   */
  String getLifeCycle();
  
  /**
   * <p>Returns the fully qualified class name of a WebForm that
   * displays Exceptions that broke the control flow within
   * the web application. If user-defined, this must be a subclass of
   * <code>WebForm</code> and must implement the 
   * <code>WebErrorForm</code> interface.</p>
   * @see org.eclipse.rwt.WebForm
   * @see org.eclipse.rwt.WebErrorForm
   */
  String getErrorPage();

  /**
   * <p>Returns the fully qualified class name of a WebForm that is used as
   * the entry point for an web-application providing administration
   * information about the main application.</p>
   */
  String getAdministrationStartupForm();

  /**
   * <p>Returns the fully qualified class name of a WebForm that
   * displays messages that were created within the web application.
   * If user-defined, this must be a subclass of
   * <code>WebForm</code> and must implement the
   * <code>MessageForm</code> interface.</p>
   * @see org.eclipse.rwt.WebForm
   * @see org.eclipse.rwt.MessageForm
   */
  String getMessagePage();

  /**
   * <p>Returns the path to a writeable directory, used for temporary files.</p>
   */
  String getWorkDirectory();

  /**
   * <p>Returns the maximum time till the closing of a WebForm on the
   * client is recognized at server side. Time interval, in ms.</p>
   */
  long getClosingTimeout();

  /**
   * <p>Returns the time interval between scans for closed WebForm at
   * the server side. This value should not be greater than
   * half of the closingTimeout value. Time interval, in ms.</p>
   */
  long getSkimmerFrequenzy();
  
  /**
   * <p>Returns whether the W4Toolkit administration pages are accessible
   * via the admin.html page (Shoud be 'false' for productive versions).</p>
   */
  boolean isDirectMonitoringAccess();

  /** 
   * <p>Returns whether the HTML output of the web appliction is sent
   * gzipped to browsers that support gzipped network communication
   * (Should be 'true' for productive versions to save network traffic 
   * and shorten loading time).</p>
   */
  boolean isCompression();

  /** 
   * <p>Returns whether the server-side processing time of the HTML
   * page is displayed (on the bottom of the page). This 
   * may be useful for application tuning (Should be 'false'
   * for productive versions).</p>
   */
  boolean isProcessTime();

  /**
   * <p>Returns whether special submitter images are used for browsers 
   * that have JavaScript disabled. Possible values are:
   * <ul>
   * <li><code>NOSCRIPT_SUBMITTERS_NONE<code><br>
   * If set to <code>NOSCRIPT_SUBMITTERS_NONE<code>, a standard submitter image 
   * is rendered in addition to the labels on link 
   * buttons, tree nodes etc.;</li>
   * <li><code>NOSCRIPT_SUBMITTERS_CREATE<code><br>
   * If set to <code>NOSCRIPT_SUBMITTERS_CREATE<code>, a special image is 
   * created automatically with the appropriate text and colors.
   * Images created only once and buffered on harddisk
   * in the webapplications image directory.
   * Setting this to 'create' requires an available X 
   * server on Unixes, however.</li>
   * <li><code>NOSCRIPT_SUBMITTERS_USE<code><br>
   * If set to <code>NOSCRIPT_SUBMITTERS_USE<code> earlier generated images 
   * are used but no new images are generated. If no image is
   * available from disk, a standard submitter image
   * is rendered in addition to the labels on link
   * buttons, tree nodes etc.;</li></ul></p>
   */
  String getNoscriptSubmitters();

  /**
   * <p>Returns whether static resources like JavaScript-libraries, images,
   * css-files etc. which are available on the applications
   * classpath are copied to disk and delivered as static files by
   * a web-server or delivered directly by the servlet engine.
   * Should be <code>RESOURCES_DELIVER_FROM_DISK</code> in most cases. 
   * Can be <code>RESOURCES_DELIVER_FROM_DISK</code> or 
   * <code>RESOURCES_DELIVER_BY_SERVLET</code>.
   */
  String getResources();

  /**
   * <p>This is a special option for certain environments, where
   * the gc algorithm comes too late to unload classes.
   * If set to a number > 0, this will enforce a gc after the
   * specified number of sessions has been invalidated.</p>
   */
  long getMaxSessionUnboundToForceGC();

  /**
   * <p>W4 Toolkit supports i18n by accepting values like 
   * 'property://someKey@some.package.SomePropertiesFile'
   * which are resolved on rendering, so that the specified
   * property is displayed in the HTML output that the user sees.</p>
   * 
   * <p>This attribute specifies the behaviour of the resolver
   * when the specifed resource could not be found as expected.
   * (For development, it may be convenient to set this to
   * <code>HANDLE_MISSING_I18N_RESOURCE_EMPTY</code>, whereas 
   * probably in productive environments the most sensible setting 
   * would be <code>HANDLE_MISSING_I18N_RESOURCE_EMPTY</code> here.)</p>
   * 
   * <p>Possible values are:
   * <ul>
   * <li><code>HANDLE_MISSING_I18N_RESOURCE_FAIL</code><br>
   * behaves like a failed assertion, that is a runtime exception is fired.</li>
   * <li><code>HANDLE_MISSING_I18N_RESOURCE_EMPTY</code><br>
   * does nothing and renders an empty String into the component's output.</li>
   * <li><code>HANDLE_MISSING_I18N_RESOURCE_EXPLICIT</code><br>
   * does nothing and renders the property URI literally into the 
   * component's output.</li></ul>
   * </p>
   */
  String getHandleMissingI18NResource();
}
