/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.parse.Asn1ParseResult;

import java.io.IOException;

/**
 * To represent Asn1 End Of Content type
 */
public final class Asn1Eoc extends Asn1Simple<Object> {
    public static final Asn1Eoc INSTANCE = new Asn1Eoc();
    private static final byte[]  EMPTY_BYTES = new byte[0];

    private Asn1Eoc() {
        super(UniversalTag.EOC, null);
    }

    @Override
    protected byte[] encodeBody() {
        return EMPTY_BYTES;
    }

    @Override
    protected int encodingBodyLength() {
        return 0;
    }

    @Override
    protected void decodeBody(Asn1ParseResult parseResult) throws IOException {
        if (parseResult.getBodyLength() != 0) {
            throw new IOException("Unexpected bytes found for EOC");
        }
    }

    @Override
    public String toString() {
        return "EOC";
    }
}