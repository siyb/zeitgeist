package org.geekosphere.zeitgeist.data;

public class ZGTag {
	private int id;
	private String tagName;
	private int itemCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	@Override
	public String toString() {
		return "ZGTag [id=" + id + ", tagName=" + tagName + ", itemCount="
				+ itemCount + "]";
	}

}
