package telran.gitRecords;

import java.io.Serializable;
import java.time.Instant;

public record FileParameters(String[] fileData, Instant timeLastModified) implements Serializable {
}