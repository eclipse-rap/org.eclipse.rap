/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages.internal;

import org.eclipse.swt.graphics.Image;

public class Person {

  private final String firstName;
  private final String lastName;
  private final Image image;
  private final String phone;
  private final String mail;

  public Person( String firstName, String lastName, Image image, String phone, String mail ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.image = image;
    this.phone = phone;
    this.mail = mail;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Image getImage() {
    return image;
  }

  public String getPhone() {
    return phone;
  }

  public String getMail() {
    return mail;
  }

}
