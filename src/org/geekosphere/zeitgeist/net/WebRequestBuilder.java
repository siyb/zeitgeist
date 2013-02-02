package org.geekosphere.zeitgeist.net;

import java.net.URL;

import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.net.Uri;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.data.dataobjects.WebRequest.Type;

public class WebRequestBuilder {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebRequestBuilder.class);

	private WebRequest wr;
	public static final String URL = "http://zeitgeist.li/";

	public WebRequestBuilder getItems() {
		wr = createDefaultWebRequest();
		wr.setUrl(URL);
		wr.setRequestType(Type.GET);
		wr.setProcessorId(ZGItemProcessor.ID);
		return this;
	}

	public WebRequestBuilder before(int before) {
		URL url = addParameterToUrl(wr.getUrl(),"before",String.valueOf(before));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder after(int after) {
		URL url = addParameterToUrl(wr.getUrl(),"after",String.valueOf(after));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder page(int page) {
		URL url = addParameterToUrl(wr.getUrl(),"page",String.valueOf(page));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder withId(int id) {
		URL url = appendPath(wr.getUrl(), String.valueOf(id));
		wr.setUrl(url);
		return this;
	}
	
	public WebRequest build() {
		return wr;
	}

	private WebRequest createDefaultWebRequest() {
		WebRequest wr = new WebRequest();
		wr.addHeaderField("Accept", "application/json");
		return wr;
	}

	private URL appendPath(URL url, String path) {
		Uri u = Uri.parse(url.toString());
		u.buildUpon().appendEncodedPath(path).build();
		try {
			return new URL(u.toString());
		} catch (Throwable tr) {
			LOGGER.error("Unable to create URL", tr);
			return null;
		}
	}
	
	private URL addParameterToUrl(URL url, String key, String value) {
		Uri u = Uri.parse(url.toString());
		u.buildUpon().appendQueryParameter(key, value).build();
		try {
			return new URL(u.toString());
		} catch (Throwable tr) {
			LOGGER.error("Unable to create URL", tr);
			return null;
		}
	}
}
