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

import org.eclipse.rap.e4.apache.jxpath.ri.Compiler;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class NodeTypeTest extends NodeTest {
    private int nodeType;

    /**
     * Create a new NodeTypeTest.
     * @param nodeType to match
     */
    public NodeTypeTest(int nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Get the nodeType.
     * @return int
     */
    public int getNodeType() {
        return nodeType;
    }

    public String toString() {
        return nodeTypeToString(nodeType) + "()";
    }

    /**
     * Render the given node type as a String.
     * @param code int
     * @return String
     */
    public static String nodeTypeToString(int code) {
        switch (code) {
            case Compiler.NODE_TYPE_NODE :
                return "node";
            case Compiler.NODE_TYPE_TEXT :
                return "text";
            case Compiler.NODE_TYPE_COMMENT :
                return "comment";
            case Compiler.NODE_TYPE_PI :
                return "processing-instruction";
            default:
                return "UNKNOWN";
        }
    }
}
