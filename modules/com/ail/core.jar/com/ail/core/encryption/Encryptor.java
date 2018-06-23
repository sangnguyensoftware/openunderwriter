/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.core.encryption;

import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;

public class Encryptor {

    private static final String ENCODING = "UTF-8";
    private static final String IBM_PROVIDER_CLASS = "com.ibm.crypto.provider.IBMJCE";
    private static final String SUN_PROVIDER_CLASS = "com.sun.crypto.provider.SunJCE";
    private static final String PROVIDER_CLASS = SUN_PROVIDER_CLASS;

    private static Map<String, Cipher> _encryptCipherMap = new ConcurrentHashMap<String, Cipher>(1, 1f, 1);

    public static String encrypt(Key key, String plainText) throws EncryptorException {

        if (key == null) {
            return plainText;
        }

        byte[] encryptedBytes = encryptUnencoded(key, plainText);

        return encode(encryptedBytes);
    }

    public static byte[] encryptUnencoded(Key key, byte[] plainBytes) throws EncryptorException {

        String algorithm = key.getAlgorithm();

        String cacheKey = algorithm.concat("#").concat(key.toString());

        Cipher cipher = _encryptCipherMap.get(cacheKey);

        try {
            if (cipher == null) {
                Security.addProvider(getProvider());

                cipher = Cipher.getInstance(algorithm);

                cipher.init(Cipher.ENCRYPT_MODE, key);

                _encryptCipherMap.put(cacheKey, cipher);
            }

            synchronized (cipher) {
                return cipher.doFinal(plainBytes);
            }
        } catch (Exception e) {
            throw new EncryptorException("Encription failed", e);
        }
    }

    private static byte[] encryptUnencoded(Key key, String plainText) throws EncryptorException {

        try {
            byte[] decryptedBytes = plainText.getBytes(ENCODING);

            return encryptUnencoded(key, decryptedBytes);
        } catch (Exception e) {
            throw new EncryptorException("UUEncoding failed", e);
        }
    }

    private static Provider getProvider() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Class<?> providerClass = null;

        try {
            providerClass = Class.forName(PROVIDER_CLASS);
        } catch (ClassNotFoundException cnfe) {
            if (System.getProperty("java.vm.vendor").equals("IBM Corporation")) {
                providerClass = Class.forName(IBM_PROVIDER_CLASS);
            } else {
                throw cnfe;
            }
        }

        return (Provider) providerClass.newInstance();
    }
    
    private static String encode(byte[] raw) {
        return encode(raw, 0, raw.length);
    }

    private static String encode(byte[] raw, int offset, int length) {
        int lastIndex = Math.min(raw.length, offset + length);

        StringBuilder sb = new StringBuilder(
            ((lastIndex - offset) / 3 + 1) * 4);

        for (int i = offset; i < lastIndex; i += 3) {
            sb.append(encodeBlock(raw, i, lastIndex));
        }

        return sb.toString();
    }
    
    private static char[] encodeBlock(byte[] raw, int offset, int lastIndex) {
        int block = 0;
        int slack = lastIndex - offset - 1;
        int end = slack < 2 ? slack : 2;

        for (int i = 0; i <= end; i++) {
            byte b = raw[offset + i];

            int neuter = b >= 0 ? ((int) (b)) : b + 256;
            block += neuter << 8 * (2 - i);
        }

        char[] base64 = new char[4];

        for (int i = 0; i < 4; i++) {
            int sixbit = block >>> 6 * (3 - i) & 0x3f;
            base64[i] = getChar(sixbit);
        }

        if (slack < 1) {
            base64[2] = '=';
        }

        if (slack < 2) {
            base64[3] = '=';
        }

        return base64;
    }

    private static char getChar(int sixbit) {
        if ((sixbit >= 0) && (sixbit <= 25)) {
            return (char)(65 + sixbit);
        }

        if ((sixbit >= 26) && (sixbit <= 51)) {
            return (char)(97 + (sixbit - 26));
        }

        if ((sixbit >= 52) && (sixbit <= 61)) {
            return (char)(48 + (sixbit - 52));
        }

        if (sixbit == 62) {
            return '+';
        }

        return sixbit != 63 ? '?' : '/';
    }
}