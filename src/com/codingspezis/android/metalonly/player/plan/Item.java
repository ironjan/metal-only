package com.codingspezis.android.metalonly.player.plan;

public abstract class Item {

	public PlanData data;

	public PlanData getPlanData() {
		return data;
	}

	public abstract boolean isSection();

}