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
package org.eclipse.rap.e4.apache.jxpath.ri.compiler;

import org.eclipse.rap.e4.apache.jxpath.ri.QName;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class NodeNameTest extends NodeTest {
    private QName qname;
    private String namespaceURI;

    /**
     * Create a new NodeNameTest.
     * @param qname name to match
     */
    public NodeNameTest(QName qname) {
        this.qname = qname;
    }

    /**
     * Create a new NodeNameTest.
     * @param qname name to match
     * @param namespaceURI uri to match
     */
    public NodeNameTest(QName qname, String namespaceURI) {
        this.qname = qname;
        this.namespaceURI = namespaceURI;
    }

    /**
     * Get the node name.
     * @return QName
     */
    public QName getNodeName() {
        return qname;
    }

    /**
     * Get the ns URI.
     * @return String
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Learn whether this is a wildcard test.
     * @return <code>true</code> if the node name is "*".
     */
    public boolean isWildcard() {
        return qname.getName().equals("*");
    }

    public String toString() {
        return qname.toString();
    }
}
