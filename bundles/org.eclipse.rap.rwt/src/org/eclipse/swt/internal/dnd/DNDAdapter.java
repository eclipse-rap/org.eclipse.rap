/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.widgets.Control;


public final class DNDAdapter implements IDNDAdapter {

  private static final String CANCEL = DNDAdapter.class.getName() + "#cancel";
  private static final String DETAIL_CHANGED_VALUE
    = DNDAdapter.class.getName() + "#detailChangedValue";
  private static final String DETAIL_CHANGED_CONTROL
    = DNDAdapter.class.getName() + "#detailChangedControl";
  private static final String FEEDBACK_CHANGED_VALUE
    = DNDAdapter.class.getName() + "#feedbackChangedValue";
  private static final String FEEDBACK_CHANGED_CONTROL
    = DNDAdapter.class.getName() + "#feedbackChangedControl";

  public void cancel() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CANCEL, Boolean.TRUE );
  }

  public boolean isCanceled() {
    return ContextProvider.getStateInfo().getAttribute( CANCEL ) != null;
  }

  public void setDetailChanged( final Control control, final int detail ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( DETAIL_CHANGED_VALUE, new Integer( detail ) );
    stateInfo.setAttribute( DETAIL_CHANGED_CONTROL, control );
  }

  public void cancelDetailChanged() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( DETAIL_CHANGED_VALUE, null );
    stateInfo.setAttribute( DETAIL_CHANGED_CONTROL, null );
  }

  public boolean hasDetailChanged() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Object value = stateInfo.getAttribute( DETAIL_CHANGED_VALUE );
    return value != null;
  }

  public int getDetailChangedValue() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Integer value = ( Integer )stateInfo.getAttribute( DETAIL_CHANGED_VALUE );
    return value.intValue();
  }

  public Control getDetailChangedControl() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Control )stateInfo.getAttribute( DETAIL_CHANGED_CONTROL );
  }

  public void setFeedbackChanged( Control control, int feedback ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( FEEDBACK_CHANGED_VALUE, new Integer( feedback ) );
    stateInfo.setAttribute( FEEDBACK_CHANGED_CONTROL, control );
  }

  public void cancelFeedbackChanged() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( FEEDBACK_CHANGED_VALUE, null );
    stateInfo.setAttribute( FEEDBACK_CHANGED_CONTROL, null );
  }

  public boolean hasFeedbackChanged() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Object value = stateInfo.getAttribute( FEEDBACK_CHANGED_VALUE );
    return value != null;
  }

  public int getFeedbackChangedValue() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Integer value = ( Integer )stateInfo.getAttribute( FEEDBACK_CHANGED_VALUE );
    return value.intValue();
  }

  public Control getFeedbackChangedControl() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Control )stateInfo.getAttribute( FEEDBACK_CHANGED_CONTROL );
  }

}