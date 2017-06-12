package com.juvenxu.mvnbook.account.captcha;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AccountCaptchaServiceTest {
	private AccountCaptchaService service;

	@Before
	public void prepare() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("account-captcha.xml");
		service = (AccountCaptchaService) ctx.getBean("accountCaptchaService");
	}

	@Test
	public void testGenerateCaptcha() throws Exception {
		String captchaKey = service.generateCaptchaKey();
		assertNotNull(captchaKey);

		byte[] captchaImage = service.generateCaptchaImage(captchaKey);
		assertTrue(captchaImage.length > 0);

		createImage(captchaKey, captchaImage);

	}

	@Test
	public void testValidateCaptchaCorrect() throws Exception {
		List<String> preDefinedTexts = new ArrayList<String>();
		preDefinedTexts.add("12345");
		preDefinedTexts.add("abcde");
		service.setPreDefinedTexts(preDefinedTexts);

		String captchaKey = service.generateCaptchaKey();
		byte[] captchaImage = service.generateCaptchaImage(captchaKey);
		assertTrue(service.validateCaptcha(captchaKey, "12345"));

		createImage(captchaKey, captchaImage);

		captchaKey = service.generateCaptchaKey();
		captchaImage = service.generateCaptchaImage(captchaKey);
		assertTrue(service.validateCaptcha(captchaKey, "abcde"));

		createImage(captchaKey, captchaImage);
	}

	@Test
	public void testValidateCaptchaIncorrect() throws Exception {
		List<String> preDefinedTexts = new ArrayList<String>();
		preDefinedTexts.add("12345");
		service.setPreDefinedTexts(preDefinedTexts);

		String captchaKey = service.generateCaptchaKey();
		service.generateCaptchaImage(captchaKey);
		assertFalse(service.validateCaptcha(captchaKey, "67890"));
	}

	private void createImage(String captchaKey, byte[] captchaImage) throws Exception {
		File image = new File("target/" + captchaKey + ".jpg");
		OutputStream output = null;
		try {
			output = new FileOutputStream(image);
			output.write(captchaImage);
		} finally {
			if (output != null) {
				output.close();
			}
		}
		assertTrue(image.exists() && image.length() > 0);
	}
}
