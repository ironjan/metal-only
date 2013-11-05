package com.codingspezis.android.metalonly.player.utils.jsonapi;

/**
 * 
 * <pre>
 * {
 *     "moderated": false,
 *     "moderator": "MetalHead",
 *     "sendung": "Keine Gruesse und Wuensche moeglich. (Mixed Metal)",
 *     "wunschvoll": "1",
 *     "grussvoll": "1",
 *     "wunschlimit": "0",
 *     "grusslimit": "0"
 * }
 * </pre>
 */
public class Stats {
	private static final String WISH_GREET_FULL = "1";
	private String moderator, sendung, wunschvoll, grussvoll, wunschlimit,
			grusslimit;
	private boolean moderated;
	private String genre;

	public void setModerator(String moderator) {
		this.moderator = moderator;
	}

	public void setSendung(String sendung) {
		this.sendung = sendung;
	}

	public void setWunschvoll(String wunschvoll) {
		this.wunschvoll = wunschvoll;
	}

	public void setGrussvoll(String grussvoll) {
		this.grussvoll = grussvoll;
	}

	public void setWunschlimit(String wunschlimit) {
		this.wunschlimit = wunschlimit;
	}

	public void setGrusslimit(String grusslimit) {
		this.grusslimit = grusslimit;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	public String getModerator() {
		return moderator;
	}

	public String getSendung() {
		return sendung;
	}

	public boolean canWish() {
		return !(WISH_GREET_FULL.equals(wunschvoll));
	}

	public boolean canGreet() {
		return !(WISH_GREET_FULL.equals(grussvoll));
	}

	public int getWunschlimit() {
		try {
			return Integer.parseInt(wunschlimit);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getGrusslimit() {
		try {
			return Integer.parseInt(grusslimit);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getGenre(){
		if(null == genre){
			int startGenre = sendung.indexOf("(") + 1;
			int endGenre = sendung.indexOf(")");
			genre = sendung.substring(startGenre, endGenre);
		}
		return genre;
	}
	public boolean isModerated() {
		return moderated;
	}

}
