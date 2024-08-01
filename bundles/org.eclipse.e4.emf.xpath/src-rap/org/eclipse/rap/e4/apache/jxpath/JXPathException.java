/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath;

/**
 * Thrown in various situations by JXPath; may contain a nested exception.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 618149 $ $Date: 2008-02-03 20:04:13 -0600 (Sun, 03 Feb 2008) $
 */

public class JXPathException extends RuntimeException {
    private static final long serialVersionUID = 4306409701468017766L;

    /** @serial */
    private Throwable exception;

    /**
     * Create a new <code>JXPathException</code> with no
     * detail mesage.
     */

     public JXPathException() {
         super();
         this.exception = null;
     }

    /**
     * Create a new <code>JXPathException</code> with
     * the <code>String </code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public JXPathException(String msg) {
        super(msg);
        this.exception = null;
    }


    /**
     * Create a new <code>JXPathException</code> with a
     * given <code>Throwable</code> base cause of the error.
     *
     * @param e The exception to be encapsulated in a
     * JXPathException.
     */
    public JXPathException(Throwable e) {
        super(e.toString());
        this.exception = e;
    }

    /**
     * Create a new <code>JXPathException</code> with the
     * given <code>Exception</code> base cause and detail message.
     *
     * @param msg The detail message.
     * @param e The exception to be encapsulated in a JXPathException
     */
    public JXPathException(String msg, Throwable e) {
        super(msg);
        this.exception = e;
    }


    /**
     * Return the message (if any) for this error . If there is no
     * message for the exception and there is an encapsulated
     * exception then the message of that exception will be returned.
     *
     * @return The error message.
     */
    public String getMessage() {
        String message = super.getMessage();
        if (exception == null) {
            return message;
        }
        StringBuffer buf = new StringBuffer();
        if (message != null) {
            buf.append(message).append("; ");
        }
        String eMsg = exception.getMessage();
        buf.append(eMsg == null ? exception.getClass().getName() : eMsg);
        return buf.toString();
    }

    /**
     * Return the actual exception (if any) that caused this exception to
     * be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Same as {@link #getException() getException()}
     * @return The encapsulated exception, or null if there is none.
     */
    public Throwable getCause() {
        return exception;
    }

}
