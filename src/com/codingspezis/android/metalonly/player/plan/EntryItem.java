package com.codingspezis.android.metalonly.player.plan;

public class EntryItem extends Item {

	public EntryItem(PlanData data) {
		this.data = data;
	}

	@Override
	public boolean isSection() {
		return false;
	}

}