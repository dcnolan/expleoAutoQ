package com.expleoautomation.utils;



import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;


public class Encryption {
	  

	  public static String encrypt(String strToEncrypt) {
		      return Base64.getEncoder().encodeToString(strToEncrypt.getBytes(StandardCharsets.UTF_8));
	  }
	 
	  public static String decrypt(String strToDecrypt) {
	      return new String(Base64.getDecoder().decode(strToDecrypt));
	  }


	


	
	@Test
	public void testEncryptionDecryption()
	{

		test("welcome1");
		


	}
	
	public void test(String plainText)
	{
		String encryptedText = encrypt(plainText);
		String decryptedText = decrypt(encryptedText);
		Assert.assertEquals(plainText, decryptedText);
		System.out.println(encryptedText);
	}

}
