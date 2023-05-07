package telran.gitAppl;

import java.io.Serializable;
import java.nio.file.Path;

public record FileState(Path path, CommitСonditions condition) implements Serializable{
}