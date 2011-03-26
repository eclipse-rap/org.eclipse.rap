package org.eclipse.rap.ui.tests.impl;

import org.eclipse.rap.ui.tests.ServiceHandlerExtensionTest;
import org.eclipse.rwt.service.IServiceHandler;


public class ServiceHandler2 implements IServiceHandler {

  public void service() {
    ServiceHandlerExtensionTest.log = this.getClass().getName();
  }
}
