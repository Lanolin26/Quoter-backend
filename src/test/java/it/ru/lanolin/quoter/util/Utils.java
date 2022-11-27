package it.ru.lanolin.quoter.util;

public final class Utils {

	private Utils() {
	}

	public static final int MAX_USER_ENTITIES = 4;
	public static final int MAX_QUOTE_ENTITIES = 9;
	public static final int MAX_QUOTE_SOURCE_ENTITIES = 12;
	public static final int MAX_QUOTE_SOURCE_TYPE_ENTITIES = 6;

//	private static final Random rnd = new Random(123827489756L);
	public static final String[] ALL_LETTERS = {
			"Q","W","E","R","T","Y","U","I","O","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M",
			"q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m",
			"1","2","3","4","5","6","7","8","9","0",
			"Й","Ц","У","К","Е","Н","Г","Ш","Щ","З","Х","Ъ","Ф","Ы","В","А","П","Р","О","Л","Д","Ж","Э","Я","Ч","С",
			"М","И","Т","Ь","Б","Ю",
			"й","ц","у","к","е","н","г","ш","щ","з","х","ъ","ф","ы","в","а","п","р","о","л","д","ж","э","я","ч","с",
			"м","и","т","ь","б","ю"
	};

//	public static String randomStringWithLength(int len) {
//		List<String> chars = Arrays.asList(ALL_LETTERS);
//		Collections.shuffle(chars, rnd);
//		List<String> strings = chars.subList(0, len);
//		StringBuilder sb = new StringBuilder();
//		strings.forEach(sb::append);
//		return sb.toString();
//	}

}
