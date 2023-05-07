package telran.gitappl;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record Commit(CommitMessage commitMessage, Instant commitTime, 
		Map<String, FileParameters> fileParameters, Commit prevCommitName) implements Serializable {
}
