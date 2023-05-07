package telran.gitAppl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import telran.view.Item;
import telran.view.StandartInputOutput;

public class GitAppl {

	public static void main(String[] args) {
		StandartInputOutput io = new StandartInputOutput();
		GitRepositoryConsoleImpl gitRepository = getGitRepository();
		Item menu = new GitControllerMenu(gitRepository).menu();
		menu.perform(io);
	}
	
	public static GitRepositoryConsoleImpl getGitRepository() {
		File file = new File(GitRepository.GIT_FILE);
		GitRepositoryConsoleImpl gitRepository = new GitRepositoryConsoleImpl();
		if(file.exists()) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))){
				gitRepository = (GitRepositoryConsoleImpl) input.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFoundException: " + e.toString());
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException: " + e.toString());
			} catch (IOException e) {
				System.out.println("IOException: " + e.toString());
			}
		}
		return gitRepository;
	}
}