package org.geekosphere.zeitgeist.data;

import java.util.ArrayList;
import java.util.Date;

public class ZGItem {
	public enum ZGItemType {
		IMAGE, AUDIO, VIDEO;
	}

	private int id;
	private ZGItemType type;
	private String source;
	private String title;
	private boolean nsfw;
	private int sizeInBytes = -1;
	private String mimeType;
	private String checkSum;
	private int width = -1;
	private int height = -1;
	private int noOfUpvotes;
	private ArrayList<ZGTag> tags;
	private String relativeImagePath;
	private String relativeThumbnailPath;
	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ZGItemType getType() {
		return type;
	}

	public void setType(ZGItemType type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isNsfw() {
		return nsfw;
	}

	public void setNsfw(boolean nsfw) {
		this.nsfw = nsfw;
	}

	public int getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(int sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNoOfUpvotes() {
		return noOfUpvotes;
	}

	public void setNoOfUpvotes(int noOfUpvotes) {
		this.noOfUpvotes = noOfUpvotes;
	}

	public ArrayList<ZGTag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<ZGTag> tags) {
		this.tags = tags;
	}

	public String getRelativeImagePath() {
		return relativeImagePath;
	}

	public void setRelativeImagePath(String relativeImagePath) {
		this.relativeImagePath = relativeImagePath;
	}

	public String getRelativeThumbnailPath() {
		return relativeThumbnailPath;
	}

	public void setRelativeThumbnailPath(String relativeThumbnailPath) {
		this.relativeThumbnailPath = relativeThumbnailPath;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ZGItem [id=" + id + ", type=" + type + ", source=" + source + ", title=" + title + ", nsfw=" + nsfw + ", sizeInBytes="
				+ sizeInBytes + ", mimeType=" + mimeType + ", checkSum=" + checkSum + ", width=" + width + ", height=" + height
				+ ", noOfUpvotes=" + noOfUpvotes + ", tags=" + tags + ", relativeImagePath=" + relativeImagePath + ", relativeThumbnail="
				+ relativeThumbnailPath + ", date=" + date + "]";
	}

}
