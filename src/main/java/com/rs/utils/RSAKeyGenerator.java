// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeyGenerator {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair keypair = keyGen.genKeyPair();
		PrivateKey privateKey = keypair.getPrivate();
		PublicKey publicKey = keypair.getPublic();

		RSAPrivateKeySpec privSpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		RSAPublicKeySpec pubSpec = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);

		System.out.println("public static final BigInteger RSA_PRIVATE_MODULUS = new BigInteger(\"" + privSpec.getModulus().toString() + "\");");
		System.out.println("public static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(\"" + privSpec.getPrivateExponent().toString() + "\");");

		System.out.println("public static final BigInteger RSA_PUBLIC_MODULUS = new BigInteger(\"" + pubSpec.getModulus().toString() + "\");");
		System.out.println("public static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger(\"" + pubSpec.getPublicExponent().toString() + "\");");
	}

}
