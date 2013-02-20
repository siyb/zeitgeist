package org.geekosphere.zeitgeist.processor;

import org.geekosphere.zeitgeist.data.ZGItem;
import org.geekosphere.zeitgeist.data.ZGItem.ZGItemType;
import org.geekosphere.zeitgeist.data.ZGTag;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.diamonddogs.exception.ProcessorExeception;
import at.diamonddogs.service.processor.JSONProcessor;

public class ZGSingleItemProcessor extends JSONProcessor<ZGItem> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZGSingleItemProcessor.class);
	public static final int ID = 32540723;

	@Override
	protected ZGItem parse(JSONObject inputObject) {
		ZGItem ret = new ZGItem();
		try {
			LOGGER.debug("Reply: " + inputObject.toString());
			JSONObject o = inputObject.getJSONObject("item");

			ret.setId(o.getInt("id"));
			ret.setType(ZGItemType.valueOf(o.getString("type").toUpperCase()));

			if (ret.getType() == ZGItemType.IMAGE) {
				if (o.has("image")) {
					JSONObject image = o.getJSONObject("image");
					ret.setRelativeImagePath(image.getString("image"));
					ret.setRelativeThumbnailPath(image.getString("thumbnail"));
				}
			}

			ret.setSource(o.getString("source"));
			ret.setTitle(o.getString("title"));

			ret.setNsfw(o.getBoolean("nsfw"));
			if (ret.getType() == ZGItemType.IMAGE) {
				if (o.has("size")) {
					ret.setSizeInBytes(o.getInt("size"));
				}
			}
			ret.setMimeType(o.getString("mimetype"));
			ret.setCheckSum(o.getString("checksum"));

			if (ret.getType() == ZGItemType.IMAGE) {
				if (o.has("dimensions")) {
					String[] dimen = o.getString("dimensions").split("x");
					ret.setWidth(Integer.parseInt(dimen[0]));
					ret.setHeight(Integer.parseInt(dimen[1]));
				}
			}
			if (o.has("upvote_count")) {
				ret.setNoOfUpvotes(o.getInt("upvote_count"));
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

		} catch (Throwable tr) {
			LOGGER.error("Parser Exception", tr);
			throw new ProcessorExeception(tr);
		}
		return ret;
	}

	@Override
	public int getProcessorID() {
		return ID;
	}

}
