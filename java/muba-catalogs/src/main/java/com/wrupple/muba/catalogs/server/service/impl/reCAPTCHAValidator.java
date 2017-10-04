package com.wrupple.muba.catalogs.server.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.event.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.server.annotations.CAPTCHA;

@Singleton
public class reCAPTCHAValidator implements ConstraintValidator<CAPTCHA, String> {

	/**
	 * The shared key between your site and ReCAPTCHA.
	 */
	private final String secret;
	private final ObjectMapper mapper;
	private final String encoding = "UTF-8";

	@Inject
	public reCAPTCHAValidator(@Named("reCAPTCHA.secret") String secret, ObjectMapper mapper) {
		super();
		this.secret = secret;
		this.mapper = mapper;
	}

	@Override
	public void initialize(CAPTCHA constraintAnnotation) {
		// statically initialize server keys

	}

	static class ResponseContract {
		boolean success;

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

	}

	@Override
	public boolean isValid(String response, ConstraintValidatorContext context) {
		// https://www.google.com/recaptcha/api.js?onload=[MYCALLBACK
		// HERE]&render=explicit
		try {
			URL url = new URL("https://www.google.com/recaptcha/api/siteverify");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("secret=");
			writer.write(URLEncoder.encode(secret, encoding));
			writer.write("&");
			writer.write("response=");

			writer.write(URLEncoder.encode(response, encoding));

			// writer.write("&");
			// remoteip = The user's IP address.
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				InputStream inputStream = connection.getInputStream();

				ResponseContract co = mapper.readValue(inputStream, ResponseContract.class);

				return co.isSuccess();
			} else {
				throw new IllegalAccessError(" Access Exception :( CODE:" + connection.getResponseCode());
			}

		} catch (IOException e) {
			throw new IllegalAccessError("unable to verify captcha " + e.getMessage());

		}

	}

}
