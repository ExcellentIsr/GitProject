package telran.gitAppl;

import java.nio.file.Path;
import java.util.List;

import telran.gitRecords.CommitMessage;
import telran.gitRecords.FileState;
import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;

public class GitControllerMenu {
	GitRepositoryConsoleImpl gitRepository;
	
	public GitControllerMenu(GitRepositoryConsoleImpl gitRepository) {
		super();
		this.gitRepository = gitRepository;
	}
	
	public Menu menu() {
		return new Menu("Git",
			Item.of("commit", 				 io -> commit(io)),
			Item.of("info", 				 io -> info(io)),
			Item.of("createBranch",			 io -> createBranch(io)),
			Item.of("renameBranch", 		 io -> renameBranch(io)),
			Item.of("deleteBranch", 		 io -> deleteBranch(io)),
			Item.of("log", 					 io -> log(io)),
			Item.of("branches", 			 io -> branches(io)),
			Item.of("commitContent", 		 io -> commitContent(io)),
			Item.of("switchTo", 		 	 io -> switchTo(io)),
			Item.of("getHead", 				 io -> getHead(io)),
//			Item.of("save", 				 io -> save(io)),
			Item.of("addIgnoredFileNameExp", io -> addIgnoredFileNameExp(io)),
			Item.of("exit", io -> {
				gitRepository.save();
				io.writeLine("");
			}, true));
	}

	private void addIgnoredFileNameExp(InputOutput io) {
		String fileName = io.readString("Enter file name to ignoring:");
		io.writeLine(gitRepository.addIgnoredFileNameExp(fileName));
	}

	@SuppressWarnings("unused")
	private void save(InputOutput io) {
			gitRepository.save();
	}

	private void getHead(InputOutput io) {
		io.writeLine(gitRepository.getHead());
	}

	private void switchTo(InputOutput io) {
		String name = io.readString("Enter branch name to switch");
		io.writeLine(gitRepository.switchTo(name));
	}

	private void commitContent(InputOutput io) {
		String name = io.readString("Enter commit name:");
		List<Path> res = gitRepository.commitContent(name);
		res.forEach(item -> io.writeLine(item));
	}

	private void branches(InputOutput io) {
		List<String> list = gitRepository.branches();
		list.forEach(item -> io.writeLine(item));
	}

	private void log(InputOutput io) {
		List<CommitMessage> list = gitRepository.log();
		list.forEach(item -> io.writeLine(item));
	}
	
	private void deleteBranch(InputOutput io) {
		String name = io.readString("Enter deleting branch name:");
		io.writeLine(gitRepository.deleteBranch(name));
	}

	private void renameBranch(InputOutput io) {
		String oldName = io.readString("Enter old branch name");
		String newName = io.readString("Enter new branch name");
		io.writeLine(gitRepository.renameBranch(oldName, newName));
	}

	private void createBranch(InputOutput io) {
		String name = io.readString("Enter new branch name:");
		io.writeLine(gitRepository.createBranch(name));
	}

	private void info(InputOutput io) {
		List<FileState> list = gitRepository.info();
		list.forEach((item) -> io.writeLine(item));
	}

	private void commit(InputOutput io) {
		String message = io.readString("Enter your commit message:");
		io.writeLine(gitRepository.commit(message));
	}
}
