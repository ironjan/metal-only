package com.codingspezis.android.metalonly.player.plan;

public class SectionItem extends Item {

	private final String title;

	public SectionItem(String title) {
		this.title = title;
		data = null;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public boolean isSection() {
		return true;
	}

}