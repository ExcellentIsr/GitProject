package telran.view;

import java.util.Scanner;

public class StandartInputOutput implements InputOutput {
	Scanner scanner = new Scanner(System.in);

	@Override
	public String readString(String promt) {
		writeLine(promt);
		return scanner.nextLine();
	}

	@Override
	public void writeString(Object obj) {
		System.out.print(obj);
	}
}