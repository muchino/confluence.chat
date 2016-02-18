package confluence.chat.utils;

import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class ChatUtils {

	public static Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static String listToString(List<String> list) {
		String string = "";
		for (int i = 0; i < list.size(); i++) {
			String entry = list.get(i);
			if (i == 0) {
				string = entry;
			} else {
				string += "," + entry;
			}
		}
		return string;
	}

	public static List<String> stringToList(String string) {
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(string)) {
			String[] split = string.split("\n");
			for (int i = 0; i < split.length; i++) {
				list.add(split[i].trim());
			}
		}
		return list;
	}

	/**
	 * Returns the username or the userkey in confluence 5.3
	 *
	 * @param username
	 * @return
	 */
	public static String getCorrectUserKey(String username) {
		if (UserCompatibilityHelper.isRenameUserImplemented()) {
			String stringKeyForUsername = UserCompatibilityHelper.getStringKeyForUsername(username);
			if (stringKeyForUsername != null) {
				return stringKeyForUsername;
			}
		}
		return username;
	}

	/**
	 * Returns the username
	 *
	 * @param usernameOrKey
	 * @return
	 */
	public static String getUserNameByKeyOrUserName(String usernameOrKey) {
		if (UserCompatibilityHelper.isRenameUserImplemented()) {
			User userForKey = UserCompatibilityHelper.getUserForKey(usernameOrKey);
			if (userForKey != null) {
				return userForKey.getName();
			}
		}
		return usernameOrKey;
	}
}
