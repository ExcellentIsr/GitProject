package telran.gitRecords;

import java.io.Serializable;
import java.nio.file.Path;

import telran.gitAppl.CommitСonditions;

public record FileState(Path path, CommitСonditions condition) implements Serializable{
}