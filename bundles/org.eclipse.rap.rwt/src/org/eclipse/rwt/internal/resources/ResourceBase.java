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
package org.eclipse.rwt.internal.resources;


/** <p>Base class for buffers, caches etc. which have to deal 
 *  with resources.</p>
 */ 
// TODO [rh] Can we push this class' members to ResourceManager and remove it?
public abstract class ResourceBase {
  
  ///////////////////////
  // constant definitions
  
  /** <p>denotes a mode in which resources are delivered: resources 
   *  are written to disk and delivered as static files.</p> */
  public static final String DELIVER_FROM_DISK  = "deliverFromDisk";
  /** <p>denotes a mode in which resources are delivered: resources 
    * libraries delivered by the Delegates servlet dynamically.</p> */
  public static final String DELIVER_BY_SERVLET = "deliverByServlet";
  /** <p>denotes a mode in which resources are delivered: resources 
   * libraries delivered by the Delegates servlet dynamically, but
   * user defined resources will be copied to the temporary directory
   * given by the system property value of 'java.io.tmpdir' plus 
   * '/w4toolkit/&lt;username&gt;', where <em>username<em> is the 
   * value of the system property 'user.name'.</p>
   * 
   * <p>For internal use only</p> */  
  public static final String DELIVER_BY_SERVLET_AND_TEMP_DIR 
    = "deliverByServletAndTempDir";

  /** the mode in which resource libraries are delivered. */
  private static String deliveryMode = DELIVER_BY_SERVLET;

  /** <p>returns whether the application runs in the mode specified by the 
    * passed String.</p> */
  public static boolean isDeliveryMode( final String deliveryMode ) {
    return getDeliveryMode().equals( deliveryMode );
  }
  
  /** <p>Sets which mode is used for delivering the resources at runtime.
   *  DELIVER_BY_SERVLET could be useful if running on a server which is
   *  permittedto to write to the webapp home.</p>
   *  @param newDeliveryMode <code>ResourceBase .DELIVER_BY_SERVLET</code> or
   *  <code>ResourceBase.DELIVER_FROM_DISK</code>
   */
  public static void setDeliveryMode( final String newDeliveryMode ) {
    if(    deliveryMode.equals( DELIVER_BY_SERVLET )
        || deliveryMode.equals( DELIVER_FROM_DISK )
        || deliveryMode.equals( DELIVER_BY_SERVLET_AND_TEMP_DIR ) )
    {
      deliveryMode = newDeliveryMode;
    }
  }
  
  /** <p>Returns which mode is used for delivering the resources at runtime.
   *  DELIVER_BY_SERVLET could be useful if running on a server which has
   *  no grant writing to webapp home.</p>
   */
  public static String getDeliveryMode() {
    return deliveryMode;
  }  
}