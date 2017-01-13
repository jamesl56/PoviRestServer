package com.antwish.povi.server.db.mysql;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SymmetricEncryptionUtility {

	private static final String keyString = "dJSuUa/u+yaEZ48qJSKiSg==";
	private static final String ivString = "SF6Esmy03hBP1WBMfp0hJA==";
	
	private static Key myKey = null;
	private static IvParameterSpec myIv = null;
	
	private static void init()
	{
		if(myKey == null)
		{
			byte[] keyBytes = Base64.decodeBase64(keyString);
			myKey = new SecretKeySpec(keyBytes, "AES");
		}
		
		if(myIv == null)
		{
			byte[] ivBytes = Base64.decodeBase64(ivString);
			myIv = new IvParameterSpec(ivBytes);
		}
	}
	
	public final static Key generateKey() throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		SecureRandom random = new SecureRandom();
		kg.init(random);
		return kg.generateKey();
	}

	public static final String encrypt(final String message) throws IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException {

		init();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, myKey, myIv);

		byte[] stringBytes = message.getBytes();

		byte[] raw = cipher.doFinal(stringBytes);

		return Base64.encodeBase64String(raw);
	}

	public static final String decrypt(final String encrypted) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, IOException,
			InvalidAlgorithmParameterException {

		init();
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, myKey, myIv);

		byte[] raw = Base64.decodeBase64(encrypted);

		byte[] stringBytes = cipher.doFinal(raw);

		String clearText = new String(stringBytes, "UTF8");
		return clearText;
	}
}
