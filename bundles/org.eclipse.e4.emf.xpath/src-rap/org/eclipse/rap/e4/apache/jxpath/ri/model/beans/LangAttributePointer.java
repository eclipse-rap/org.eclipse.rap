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
package org.eclipse.rap.e4.apache.jxpath.ri.model.beans;

import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * A Pointer that points to the "lang" attribute of a JavaBean. The value
 * of the attribute is based on the locale supplied to it in the constructor.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652884 $ $Date: 2008-05-02 15:02:00 -0500 (Fri, 02 May 2008) $
 */
public class LangAttributePointer extends NodePointer {

    private static final long serialVersionUID = -8665319197100034134L;

    /**
     * Create a new LangAttributePointer.
     * @param parent parent pointer.
     */
    public LangAttributePointer(NodePointer parent) {
        super(parent);
    }

    public QName getName() {
        return new QName("xml", "lang");
    }

    public String getNamespaceURI() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public int getLength() {
        return 1;
    }

    public Object getBaseValue() {
        return parent.getLocale().toString().replace('_', '-');
    }

    public Object getImmediateNode() {
        return getBaseValue();
    }

    public boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Throws UnsupportedOperationException.
     * @param value Object
     */
    public void setValue(Object value) {
        throw new UnsupportedOperationException(
                "Cannot change locale using the 'lang' attribute");
    }

    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        if (parent != null) {
            buffer.append(parent.asPath());
            if (buffer.length() == 0
                || buffer.charAt(buffer.length() - 1) != '/') {
                buffer.append('/');
            }
        }
        buffer.append("@xml:lang");
        return buffer.toString();
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object object) {
        return object instanceof LangAttributePointer;
    }

    public boolean testNode(NodeTest test) {
        return false;
    }

    public int compareChildNodePointers(
        NodePointer pointer1,
        NodePointer pointer2) {
        // Won't happen - lang attributes don't have children
        return 0;
    }
}
