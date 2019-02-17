package com.thecodeslinger.filecustodian.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class DataLocationTest {
    
    private static final String ROOT_PATH = "/";
    private static final String SOURCE_PATH = "/home/data/";
    private static final String DEST_PATH = "/mnt/drive/backup/";
    private static final String FILE_IN_SUBFOLDER = "documents/jokes.md";
    private static final String FILE_IN_ROOT = "jokes.md";
    private static final String FILE_IN_UNSUPPORTED = "/me/jokes.md";
    
    @Test
    public void valid_sourcefile_returns_destination_path() {
        // Given
        DataLocation location = new DataLocation(SOURCE_PATH, DEST_PATH);
        
        // Expect
        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_ROOT)))
                .isEqualTo(Paths.get(DEST_PATH + FILE_IN_ROOT));
        
        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_SUBFOLDER)))
                .isEqualTo(Paths.get(DEST_PATH + FILE_IN_SUBFOLDER));
    }
    
    @Test
    public void source_is_root_returns_destination_path() {
        // Given
        DataLocation location = new DataLocation(ROOT_PATH, DEST_PATH);
        
        // Expect
        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_ROOT)))
                .isEqualTo(Paths.get(DEST_PATH + SOURCE_PATH + FILE_IN_ROOT));

        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_SUBFOLDER)))
                .isEqualTo(Paths.get(DEST_PATH + SOURCE_PATH + FILE_IN_SUBFOLDER));
    }
    
    @Test
    public void destination_is_root_returns_destination_path() {
        // Given
        DataLocation location = new DataLocation(SOURCE_PATH, ROOT_PATH);
        
        // Expect
        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_ROOT)))
                .isEqualTo(Paths.get(ROOT_PATH + FILE_IN_ROOT));

        assertThat(location.getDestinationPath(Paths.get(SOURCE_PATH + FILE_IN_SUBFOLDER)))
                .isEqualTo(Paths.get(ROOT_PATH + FILE_IN_SUBFOLDER));
    }

    /* * * * * Exception Handling * * * * */
    
	@Test
	public void sourcefile_not_in_source_folder_throws_iae() {
		// Given
        DataLocation location = new DataLocation(SOURCE_PATH, DEST_PATH);
		
		// Expect
		assertThatIllegalArgumentException()
				.isThrownBy(() -> location.getDestinationPath(Paths.get(FILE_IN_UNSUPPORTED)));
	}
    
    @Test
    public void sourfile_relative_throws_iae() {
        // Given
        DataLocation location = new DataLocation(SOURCE_PATH, DEST_PATH);
        
        // Expect
        assertThatIllegalArgumentException()
                .isThrownBy(() -> location.getDestinationPath(Paths.get(FILE_IN_ROOT)));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> location.getDestinationPath(Paths.get(FILE_IN_SUBFOLDER)));
    }
}
