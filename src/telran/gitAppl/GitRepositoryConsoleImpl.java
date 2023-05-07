package telran.gitAppl;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import telran.gitRecords.Commit;
import telran.gitRecords.CommitMessage;
import telran.gitRecords.FileParameters;
import telran.gitRecords.FileState;

public class GitRepositoryConsoleImpl implements GitRepository {
	private static final long serialVersionUID = 1L;

	public HashMap<String, ArrayList<Commit>> branchAndCommits = new HashMap<>();
	private HashSet<String> ignoreFiles = new HashSet<>();

	private String branchHead = null;
	private String commitHead = null;
	String PATH_DIRECTORY = "D:\\Projects\\Git";

	@Override
	public String commit(String message) {
		String res = "Nothing to commit";
		boolean flag = false;
		List<FileState> fileStates = info();
		if (branchHead == null) {
			res = "First you need create the branch";
		} else {
			for (FileState fileState : fileStates) {
				if (fileState.condition() != CommitСonditions.COMMITED) {
					flag = true;
				}
			}

			if (flag) {
				Commit commit = newCommit(message, fileStates);
				branchAndCommits.get(branchHead).add(commit);
				// listCommits.add(commit);
				// branchAndCommits.replace(branchHead, listCommits);
				commitHead = commit.commitMessage().name();
				res = String.format("Commited. Name: %s with message \"%s\"", commit.commitMessage().name(),
						commit.commitMessage().message());
			}
		}

		return res;
	}

	private Commit newCommit(String message, List<FileState> fileStates) {
		CommitMessage commitMessage = new CommitMessage(message);
		Instant commitTime = Instant.now();
		Map<String, FileParameters> commitFileParameters = getFileParameters(fileStates);
		Commit prevCommit = checkPrev(fileStates);

		return new Commit(commitMessage, commitTime, commitFileParameters, prevCommit);
	}

	private Commit checkPrev(List<FileState> fileStates) {
//		boolean flag= false;
//		for (FileState fileState : fileStates) {
//			if (fileState.condition() != CommitСonditions.UNTRACKED) {
//				flag = true;
//			}
//		}
		ArrayList<Commit> list = branchAndCommits.get(branchHead);
		return commitHead == null ? null : list.get(list.size() - 1);
	}

	private Map<String, FileParameters> getFileParameters(List<FileState> fileStates) {
		Map<String, FileParameters> res = new HashMap<>();

		for (FileState fileState : fileStates) {
			if (fileState.condition() != CommitСonditions.COMMITED) {
				Path filePath = fileState.path();
				FileParameters fileParameters = new FileParameters(loadFileData(filePath),
						Instant.ofEpochMilli(filePath.toFile().lastModified()));
				res.put(filePath.toString(), fileParameters);
			}
		}

		return res;
	}

	private String[] loadFileData(Path path) {
		String[] res = null;

		try {
			res = Files.readAllLines(path).toArray(String[]::new);
		} catch (IOException e) {
			System.out.println("IOException: " + e.toString());
		}

		return res;
	}

	@Override
	public List<FileState> info() {
		List<FileState> list = null;

		try {
			list = Files.walk(Path.of(PATH_DIRECTORY), 1).filter(Files::isRegularFile)
					.filter(path -> matches(path.getFileName().toString()))
					.map(path -> new FileState(path, checkFileStatus(path, getCommit(commitHead)))).toList();
		} catch (IOException e) {
			System.out.println("IOException: " + e.toString());
		}

		return list;
	}

	private CommitСonditions checkFileStatus(Path path, Commit commit) {
		CommitСonditions res = null;

		if (commit == null) {
			res = CommitСonditions.UNTRACKED;
		} else if (!commit.fileParameters().containsKey(path.toString())) {
//			int indPrevCommit = branchAndCommits.get(branchHead).indexOf(commit);
//			Commit prevCommit = branchAndCommits.get(branchHead).get(indPrevCommit).prevCommitName();
			res = checkFileStatus(path, getCommit(commit.commitMessage().name()).prevCommitName());
		} else {
			Instant commitTime = commit.commitTime();
			Instant lastModified = Instant.ofEpochMilli(path.toFile().lastModified());
			res = commitTime.isAfter(lastModified) ? CommitСonditions.COMMITED : CommitСonditions.MODIFIED;
		}

		return res;
	}

	private boolean matches(String path) {
		return !ignoreFiles.stream().anyMatch(path::matches);
	}

	private Commit getCommit(String commitName) {
		return getCommit(commitName, branchHead);
	}

	private Commit getCommit(String commitName, String branchName) {
		ArrayList<Commit> list = branchAndCommits.get(branchName);
		Commit res = null;
		if (commitName != null && list != null) {
			boolean flag = false;
			Iterator<Commit> iterator = list.iterator();
			Commit next = null;
			while (!flag && iterator.hasNext()) {
				next = iterator.next();
				if (next.commitMessage().name().equals(commitName)) {
					res = next;
					flag = true;
				}
			}
		}
		return res;
	}

	@Override
	public String createBranch(String branchName) {
		String res = null;
		if (branchAndCommits.containsKey(branchName)) {
			res = "This branch already exists";
		} else {
			List<FileState> fileStates = info();
			for (FileState state : fileStates) {
				if ((state.condition() != CommitСonditions.COMMITED)) {
					res = "Current commit was not committed";
				}
			}
			if (branchHead == null) {
				res = null;
			}
			if (res == null) {
				branchAndCommits.put(branchName, new ArrayList<>());
				branchHead = branchName;
				commitHead = null;
				res = "Branch was created";
			}
		}
		return res;
	}

	@Override
	public String renameBranch(String oldName, String newName) {
		String res = null;
		if (!branchAndCommits.containsKey(oldName)) {
			res = "Branch " + oldName + " does not exists";
		} else if (branchAndCommits.containsKey(newName)) {
			res = "Branch " + newName + " already exists";
		} else {
			ArrayList<Commit> list = branchAndCommits.remove(oldName);
			branchAndCommits.put(newName, list);
			res = "Renamed";
			if (oldName.equals(branchHead)) {
				branchHead = newName;
			}
		}
		return res;
	}

	@Override
	public String deleteBranch(String name) {
		String res = null;
		if (name.equals(branchHead)) {
			res = "Branch " + name + " is current";
		} else if (!branchAndCommits.containsKey(name)) {
			res = "Branch " + name + " does not exists";
		} else {
			branchAndCommits.remove(name);
			res = "Deleted";
		}
		return res;
	}

	@Override
	public List<CommitMessage> log() {
		List<CommitMessage> res = new ArrayList<>();
		Commit commit = getCommit(commitHead);
		while (commit != null) {
			res.add(commit.commitMessage());
			commit = commit.prevCommitName();
		}
		return res;
	}

	@Override
	public List<String> branches() {
		return branchAndCommits.keySet().stream()
				.map(branch -> branch.equals(branchHead) ? branch + "(current)" : branch).toList();
	}

	@Override
	public List<Path> commitContent(String commitName) {
		Commit currentCommit = getCommit(commitName);
		return currentCommit == null ? Arrays.asList()
				: currentCommit.fileParameters().keySet().stream().map(Path::of).toList();
	}

	@Override
	public String switchTo(String branchName) {
		String res = null;
		List<FileState> fileStates = info();
		for (FileState state : fileStates) {
			if ((state.condition() != CommitСonditions.COMMITED)) {
				res = "Must be commited";
			}
		}
		if (!branchAndCommits.containsKey(branchName)) {
			res = "Branch " + branchName + " does not exists";
		}
		if (res == null) {
			clear();
			ArrayList<Commit> list = branchAndCommits.get(branchName);
			commitHead = list.get(list.size() - 1).commitMessage().name();
			load(commitHead, branchName);
			branchHead = branchName;
			res = "Switched";
		}
		return res;
	}

	private void clear() {
		try {
			Files.walk(Path.of(PATH_DIRECTORY)).filter(Files::isRegularFile).forEach(file -> {
				try {
					Files.delete(file);
				} catch (IOException e) {
					System.out.println("IOException: " + e.toString());
				}
			});
		} catch (IOException e) {
			System.out.println("IOException: " + e.toString());
		}
	}

	private void load(String commitName, String branchName) {
		Commit commit = getCommit(commitName, branchName);
		Map<String, FileParameters> fileParameters = commit.fileParameters();

		for (Entry<String, FileParameters> item : fileParameters.entrySet()) {
			Path path = Path.of(item.getKey());
			String[] fileData = item.getValue().fileData();
			Instant timeLastModified = item.getValue().timeLastModified();

			loadFiles(path, fileData, timeLastModified);
		}
	}

	private void loadFiles(Path path, String[] fileData, Instant timeLastModified) {
		try {
			Files.createFile(path);
			Files.write(path, Arrays.asList(fileData));
			Files.setLastModifiedTime(path, FileTime.from(timeLastModified));
		} catch (IOException e) {
			System.out.println("IOException: " + e.toString());
		}
	}

	@Override
	public String getHead() {
		String res = branchHead == null ? "No one commits" : "Branch " + branchHead;
		res += commitHead == null ? "" : " & Commit " + commitHead;
		return res;
	}

	@Override
	public void save() {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(GIT_FILE))) {
			output.writeObject(this);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.toString());
		} catch (IOException e) {
			System.out.println("IOException: " + e.toString());
		}
	}

	@Override
	public String addIgnoredFileNameExp(String regex) {
		ignoreFiles.add(regex);

		return String.format("File " + regex + " will be ignored");
	}
}