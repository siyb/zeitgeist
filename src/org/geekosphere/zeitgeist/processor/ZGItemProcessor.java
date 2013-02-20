package org.geekosphere.zeitgeist.processor;

import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.data.ZGItem.ZGItemType;
import org.geekosphere.zeitgeist.data.ZGTag;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.os.Handler;
import at.diamonddogs.data.adapter.ReplyAdapter;
import at.diamonddogs.data.adapter.ReplyAdapter.Status;
import at.diamonddogs.data.dataobjects.Request;
import at.diamonddogs.exception.ProcessorExeception;
import at.diamonddogs.service.processor.JSONProcessor;
import at.diamonddogs.util.CacheManager.CachedObject;

public class ZGItemProcessor extends JSONProcessor<ZGItem[]> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGItemProcessor.class);
	public static final int ID = 21349051;

	@Override
	protected ZGItem[] parse(JSONObject inputObject) {
		ZGItem[] items = null;
		try {
			LOGGER.debug("Reply: " + inputObject.toString());
			JSONArray jsonItems = inputObject.getJSONArray("items");
			LOGGER.info("Number of items to parse: " + jsonItems.length());
			items = new ZGItem[jsonItems.length()];
			for (int i = 0; i < jsonItems.length(); i++) {

				JSONObject o = jsonItems.getJSONObject(i);
				items[i] = new ZGItem();
				items[i].setId(o.getInt("id"));
				items[i].setType(ZGItemType.valueOf(o.getString("type").toUpperCase()));

				if (items[i].getType() == ZGItemType.IMAGE) {
					if (o.has("image")) {
						JSONObject image = o.getJSONObject("image");
						items[i].setRelativeImagePath(image.getString("image"));
						items[i].setRelativeThumbnailPath(image.getString("thumbnail"));
					}
				}

				items[i].setSource(o.getString("source"));
				items[i].setTitle(o.getString("title"));

				items[i].setNsfw(o.getBoolean("nsfw"));
				if (items[i].getType() == ZGItemType.IMAGE) {
					if (o.has("size")) {
						items[i].setSizeInBytes(o.getInt("size"));
					}
				}
				items[i].setMimeType(o.getString("mimetype"));
				items[i].setCheckSum(o.getString("checksum"));

				if (items[i].getType() == ZGItemType.IMAGE) {
					if (o.has("dimensions")) {
						String[] dimen = o.getString("dimensions").split("x");
						items[i].setWidth(Integer.parseInt(dimen[0]));
						items[i].setHeight(Integer.parseInt(dimen[1]));
					}
				}
				if (o.has("upvote_count")) {
					items[i].setNoOfUpvotes(o.getInt("upvote_count"));
				}
				JSONArray a = o.getJSONArray("tags");
				ZGTag[] tags = new ZGTag[a.length()];
				for (int j = 0; j < a.length(); j++) {
					tags[j] = new ZGTag();
					JSONObject to = a.getJSONObject(j);
					tags[j].setId(to.getInt("id"));
					tags[j].setItemCount(to.getInt("count"));
					tags[j].setTagName(to.getString("tagname"));
				}

			}
		} catch (Throwable tr) {
			LOGGER.error("Parser Exception", tr);
			throw new ProcessorExeception(tr);
		}
		return items;
	}

	@Override
	public void processWebReply(Context c, ReplyAdapter r, Handler handler) {
		try {
			if (r.getStatus() == Status.OK) {
				handler.sendMessage(processData(r).returnMessage);
			} else {
				handler.sendMessage(createErrorMessage(r));
			}
		} catch (Throwable tr) {
			handler.sendMessage(createErrorMessage(tr, r));
		}
	}

	@Override
	public void processCachedObject(CachedObject cachedObject, Handler handler, Request request) {
		// No caching
	}

	@Override
	public int getProcessorID() {
		return ID;
	}

}
