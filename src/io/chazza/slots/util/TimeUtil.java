package io.chazza.slots.util;

import org.apache.commons.lang.time.DurationFormatUtils;

public class TimeUtil {

	public static String toString(long ms) {
		String date = DurationFormatUtils.formatDuration(ms, "dd-H-mm-ss", false);
		String[] dateSpt = date.split("-");

		int day = Integer.valueOf(dateSpt[0]);
		int hour = Integer.valueOf(dateSpt[1]);
		int min = Integer.valueOf(dateSpt[2]);
		int sec = Integer.valueOf(dateSpt[3]);

		StringBuilder sb = new StringBuilder();
		if(day > 0) sb.append(day + "d ");
		if(hour > 0) sb.append(hour + "h ");
		if(min > 0) sb.append(min + "m ");
		sb.append(sec + "s");

		return sb.toString();
	}
}