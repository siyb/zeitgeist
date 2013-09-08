package org.geekosphere.zeitgeist.net;

import java.io.File;
import java.net.URL;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.geekosphere.zeitgeist.activity.ZGPreferenceActivity;
import org.geekosphere.zeitgeist.processor.ZGItemProcessor;
import org.geekosphere.zeitgeist.processor.ZGSingleItemProcessor;
import org.geekosphere.zeitgeist.processor.ZGTagProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import at.diamonddogs.data.dataobjects.WebRequest;
import at.diamonddogs.data.dataobjects.WebRequest.Type;

/**
 * TODO: add some sort of validation
 */
public class WebRequestBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestBuilder.class);

	private WebRequest wr;
	private String URL;
	private String email;
	private String apiSecret;

	public WebRequestBuilder(Context c) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
		URL = preferences.getString(ZGPreferenceActivity.KEY_HOST, "http://zeitgeist.li");
		email = preferences.getString(ZGPreferenceActivity.KEY_EMAIL, null);
		apiSecret = preferences.getString(ZGPreferenceActivity.KEY_API_SECRET, null);
	}

	public WebRequestBuilder getItems() {
		wr = createDefaultWebRequest();
		wr.setUrl(URL);
		wr.setRequestType(Type.GET);
		wr.setProcessorId(ZGItemProcessor.ID);
		return this;
	}

	public WebRequestBuilder getItemsByTag(String tag) {
		wr = createDefaultWebRequest();
		wr.setUrl(URL + "/show/tag/" + tag);
		wr.setRequestType(Type.GET);
		wr.setProcessorId(ZGItemProcessor.ID);
		return this;
	}

	public WebRequestBuilder getTags() {
		wr = createDefaultWebRequest();
		wr.setUrl(URL + "/list/tags");
		wr.setRequestType(Type.GET);
		wr.setProcessorId(ZGTagProcessor.ID);
		return this;
	}

	public WebRequestBuilder newItem() {
		wr = createDefaultWebRequest();
		wr.setUrl(URL + "/new");
		wr.setRequestType(Type.POST);
		wr.setProcessorId(ZGItemProcessor.ID);
		return this;
	}

	public WebRequestBuilder addUploadUrl(String[] urls) {
		MultipartEntity entity = getMultiPartEntity();
		for (String url : urls) {
			try {
				entity.addPart("remote_url[]", new StringBody(url));
			} catch (Throwable tr) {
				LOGGER.warn("Cannot announce", tr);
			}
		}
		return this;

	}

	public WebRequestBuilder addUploadFile(File[] files) {
		MultipartEntity entity = getMultiPartEntity();
		for (File file : files) {
			entity.addPart("image_upload[]", new FileBody(file));
		}
		wr.setHttpEntity(entity);
		return this;

	}

	public WebRequestBuilder addTags(String tags) {
		MultipartEntity entity = getMultiPartEntity();
		try {
			entity.addPart("tags", new StringBody(tags));
		} catch (Throwable tr) {
			LOGGER.warn("Cannot announce", tr);
		}
		return this;
	}

	public WebRequestBuilder announce() {
		MultipartEntity entity = getMultiPartEntity();
		try {
			entity.addPart("announce", new StringBody("true"));
		} catch (Throwable tr) {
			LOGGER.warn("Cannot announce", tr);
		}
		return this;
	}

	private MultipartEntity getMultiPartEntity() {
		MultipartEntity m = (MultipartEntity) wr.getHttpEntity();
		if (m == null) {
			m = new MultipartEntity();
			wr.setHttpEntity(m);
		}
		return m;
	}

	public WebRequestBuilder before(int before) {
		String url = addParameterToUrl(wr.getUrl(), "before", String.valueOf(before));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder after(int after) {
		String url = addParameterToUrl(wr.getUrl(), "after", String.valueOf(after));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder page(int page) {
		String url = addParameterToUrl(wr.getUrl(), "page", String.valueOf(page));
		wr.setUrl(url);
		return this;
	}

	public WebRequestBuilder withId(int id) {
		String url = appendPath(wr.getUrl(), String.valueOf(id));
		wr.setUrl(url);
		wr.setProcessorId(ZGSingleItemProcessor.ID);
		return this;
	}

	public WebRequest build() {
		return wr;
	}

	public void reset() {
		wr = null;
	}

	private WebRequest createDefaultWebRequest() {
		WebRequest wr = new WebRequest();
		wr.addHeaderField("Accept", "application/json");
		
		if (email != null && apiSecret != null && email.length() > 0 && apiSecret.length() > 0) {
			wr.addHeaderField("X-API-Auth", this.email + "|" + this.apiSecret);
		}
		return wr;
	}

	private String appendPath(URL url, String path) {
		Uri u = Uri.parse(url.toString());
		u = u.buildUpon().appendEncodedPath(path).build();
		try {
			return u.toString();
		} catch (Throwable tr) {
			LOGGER.error("Unable to create URL", tr);
			return null;
		}
	}

	private String addParameterToUrl(URL url, String key, String value) {
		Uri u = Uri.parse(url.toString());
		u = u.buildUpon().appendQueryParameter(key, value).build();
		try {
			return u.toString();
		} catch (Throwable tr) {
			LOGGER.error("Unable to create URL", tr);
			return null;
		}
	}
}
