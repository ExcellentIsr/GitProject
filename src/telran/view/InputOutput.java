package telran.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InputOutput {
	String readString(String promt);

	void writeString(Object obj);

	default void writeLine(Object obj) {
		writeString(obj.toString() + "\n");
	}

	default <R> R readObject(String promt, String errorPromt, Function<String, R> mapper) {
		boolean running = true;
		R res = null;
		while (running) {
			try {
				String str = readString(promt);
				res = mapper.apply(str);
				running = false;
			} catch (Exception e) {
				writeLine(errorPromt + " - " + e.getMessage());
			}
		}
		return res;
	}

	default String readStringPredicate(String promt, String errorPromt, Predicate<String> predicate) {
		return readObject(promt, errorPromt, s -> {
			if (!predicate.test(s)) {
				throw new RuntimeException("");
			}
			return s;
		});
	}

	default String readStringOptions(String promt, String errorPromt, Set<String> options) {
		return readStringPredicate(promt, errorPromt, options::contains);
	}

	default int readInt(String promt, String errorPromt) {
		return readInt(promt, errorPromt, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	default int readInt(String promt, String errorPromt, int min, int max) {
		return readObject(promt, errorPromt, d -> {
			try {
				int res = Integer.parseInt(d);
				checkRange(min, max, res);
				return res;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Must be a number " + max);
			}
		});
	}

	default void checkRange(double min, double max, double res) {
		if (res < min) {
			throw new RuntimeException("Must be great or equal " + min);
		}
		if (res > max) {
			throw new RuntimeException("Must be less or equal " + max);
		}
	}

	default long readLong(String promt, String errorPromt, long min, long max) {
		return readObject(promt, errorPromt, d -> {
			try {
				Long res = Long.parseLong(d);
				checkRange(min, max, res);
				return res;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Must be a number " + max);
			}
		});
	}

	default double readNumber(String promt, String errorPromt, double min, double max) {
		return readObject(promt, errorPromt, d -> {
			try {
				Double res = Double.parseDouble(d);
				checkRange(min, max, res);
				return res;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Must be a number " + max);
			}
		});
	}

	default LocalDate readDate(String promt, String errorPromt) {
		return readDate(promt, errorPromt, "yyyy-MM-dd", LocalDate.MIN, LocalDate.MAX);
	}

	default LocalDate readDate(String promt, String errorPromt, String format, LocalDate min, LocalDate max) {
		return readObject(promt, errorPromt, s -> {
			DateTimeFormatter dtf = null;
			try {
				dtf = DateTimeFormatter.ofPattern(format);
			} catch (Exception e) {
				throw new RuntimeException("Wrong date format " + format);
			}
			LocalDate res = null;
			try {
				res = LocalDate.parse(s, dtf);
			} catch (Exception e) {
				throw new RuntimeException("Must be date in format + " + format);
			}
			if (res.isBefore(min)) {
				throw new RuntimeException("Must not be before " + min.format(dtf));
			}
			if (res.isAfter(max)) {
				throw new RuntimeException("Must not be after " + max.format(dtf));
			}
			return res;
		});
	}
}