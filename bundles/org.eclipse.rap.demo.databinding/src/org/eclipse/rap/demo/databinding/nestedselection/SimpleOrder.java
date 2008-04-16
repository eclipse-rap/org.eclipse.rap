/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: The
 * Pampered Chef - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.databinding.nestedselection;

import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @since 1.0
 */
public class SimpleOrder {

  private int orderNumber;
  private Date date;

  /**
   * @return Returns the date.
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param date The date to set.
   */
  public void setDate( final Date date ) {
    this.date = date;
  }

  /**
   * @return Returns the orderNumber.
   */
  public int getOrderNumber() {
    return orderNumber;
  }

  /**
   * @param orderNumber The orderNumber to set.
   */
  public void setOrderNumber( final int orderNumber ) {
    this.orderNumber = orderNumber;
  }

  /**
   * @param orderNumber
   * @param date
   */
  public SimpleOrder( final int orderNumber, final Date date ) {
    this.orderNumber = orderNumber;
    this.date = date;
  }

  public void addPropertyChangeListener( final PropertyChangeListener lsnr ) {
    // avoid log messages
  }

  public void removePropertyChangeListener( final PropertyChangeListener lsnr ){
    // avoid log messages
  }
}
