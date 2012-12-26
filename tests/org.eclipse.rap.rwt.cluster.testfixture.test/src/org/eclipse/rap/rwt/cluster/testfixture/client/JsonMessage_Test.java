/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class JsonMessage_Test {

  @Test
  public void createsEmtpyMessage() {
    JsonMessage message = new JsonMessage();

    String json = message.toString();

    assertEquals( "{\"head\":{},\"operations\":[]}", json );
  }

  @Test
  public void createsInitHeader() {
    JsonMessage message = new JsonMessage();

    message.setInitialize( true );
    String json = message.toString();

    assertEquals( "{\"head\":{\"rwt_initialize\":true},\"operations\":[]}", json );
  }

  @Test
  public void createsRequestCounter() {
    JsonMessage message = new JsonMessage();

    message.setRequestCounter( 23 );
    String json = message.toString();

    assertEquals( "{\"head\":{\"requestCounter\":23},\"operations\":[]}", json );
  }

  @Test
  public void createsOperations() {
    JsonMessage message = new JsonMessage();

    message.addOperation( "foo" );
    message.addOperation( "bar" );
    String json = message.toString();

    assertEquals( "{\"head\":{},\"operations\":[foo,bar]}", json );
  }

  @Test
  public void createsFullMessage() {
    JsonMessage message = new JsonMessage();

    message.addOperation( "[\"set\",\"w4\",{\"width\":42}]" );
    message.addOperation( "[\"call\",\"w3\",\"foo\",{}]" );
    message.setRequestCounter( 23 );
    String json = message.toString();

    String expected = "{\"head\":{\"requestCounter\":23},\"operations\":["
                      + "[\"set\",\"w4\",{\"width\":42}],"
                      + "[\"call\",\"w3\",\"foo\",{}]]}";
    assertEquals( expected, json );
  }

}
