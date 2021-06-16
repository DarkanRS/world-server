package com.rs.utils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
