package com.thecodeslinger.filecustodian.test;

import com.thecodeslinger.filecustodian.database.archive.Archive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Provides simple means to create arbitrary {@link Archive} instance of varying complexity
 * and access them. The timestamp of each archive is calculated with a basis of when a test
 * is started. For every item in the list a value of one day is added to the preceding
 * archive's created timestamp. The base timestamp can be retrieved by
 *
 * <p>Usage example:
 *
 * <pre><code>
 * class ExtensiveTest extends ArchiveTestBase {
 *     @Test
 *     void best_test_ever() {
 *         // Given
 *         create(Full(Inc(Inc())), Full());
 *
 *         // Expect
 *         Archive full = get(0);
 *         Archive inc1 = get(1);
 *
 *         assertThat(full.getIncremental().get()).isEqualTo(inc1);
 *     }
 * }
 * </code></pre>
 */
public abstract class ArchiveTestBase {
    private List<Archive> allArchives = new ArrayList<>();
    private Instant baseInstant;

    /**
     * Get the base timestamp.
     */
    @BeforeEach
    void setup() {
        baseInstant = Instant.now();
    }

    /**
     * Clear the archives for the next test.
     */
    @AfterEach
    void cleanup() {
        allArchives.clear();
    }

    /**
     * Returns the base instant upon which all {@link Archive#getCreated()} are based.
     */
    protected Instant getBaseInstant() {
        return baseInstant;
    }

    /**
     * Returns the total number of archives that have been created.
     */
    protected int getArchiveCount() {
        return allArchives.size();
    }

    /**
     * <p>Creates a list of {@link Archive} instances that can be used in the test.
     *
     * <p>Assumes one day difference between all {@link Archive#getCreated()} timestamps,
     * adding one day to the previous timestamp in the order they were given.
     */
    protected void create(ArchiveType... archives) {
        Arrays.stream(archives).forEach(this::createWithChildrenAndAddToAllArchives);
    }

    /**
     * Retrieve the {@link Archive} at the specified index.
     *
     * @param index
     *      The index refers to the position at which the archive was listed when calling
     *      {@link ArchiveTestBase#create(ArchiveType...)}.
     *
     * @return
     *      The requested {@link Archive}.
     */
    protected Archive get(int index) {
        assert index >= 0 && index < allArchives.size() : "Index out of bounds";
        return allArchives.get(index);
    }

    /**
     * Assert that the {@link Archive} at the given index matches the expected values.
     *
     * @param index
     *      The index of the {@link Archive}. See {@link ArchiveTestBase#get(int)}.
     *
     * @param type
     *      {@link com.thecodeslinger.filecustodian.database.archive.ArchiveType}.
     *
     * @param instant
     *      The expected {@link Archive#getCreated()} timestamp.
     *
     * @param incremental
     *      The expected child {@link Archive}. Can be {@code null} if this archive does
     *      not contain an incremental archive.
     */
    protected void assertArchiveAt(
            int index, com.thecodeslinger.filecustodian.database.archive.ArchiveType type,
            Instant instant, Archive incremental) {
        assertArchive(get(index), type, instant, incremental);
    }

    /**
     * Assert that the {@link Archive} at the given index matches the expected values.
     *
     * @param archive
     *      The {@link Archive} to assert.
     *
     * @param type
     *      {@link com.thecodeslinger.filecustodian.database.archive.ArchiveType}.
     *
     * @param instant
     *      The expected {@link Archive#getCreated()} timestamp.
     *
     * @param incremental
     *      The expected child {@link Archive}. Can be {@code null} if this archive does
     *      not contain an incremental archive.
     */
    protected void assertArchive(
            Archive archive, com.thecodeslinger.filecustodian.database.archive.ArchiveType type,
            Instant instant, Archive incremental) {
        assertThat(archive.getType()).isEqualTo(type);
        assertThat(archive.getCreated()).isEqualTo(instant);

        if (Objects.nonNull(incremental)) {
            var opt = archive.getIncremental();
            assertThat(opt).isNotEmpty();
            // Not necessary, but I don't want the IDE to highlight the call to opt.get()
            // as a warning.
            opt.ifPresent(v -> assertThat(v).isEqualTo(incremental));
        }
        else {
            assertThat(archive.getIncremental()).isEmpty();
        }
    }

    /**
     * Convert the "DSL" {@link ArchiveType} to the corresponding {@link Archive}. Recursively
     * calls itself until {@code archiveData} and all nested child archives are created and
     * added to {@link ArchiveTestBase#allArchives}.
     */
    private void createWithChildrenAndAddToAllArchives(ArchiveType archiveData) {
        if (Objects.nonNull(archiveData)) {
            Archive archive = archiveData.create(getNextInstant());
            allArchives.add(archive);

            createWithChildrenAndAddToAllArchives(archiveData.child);
        }
    }

    /**
     * Calculate and return the next instant.
     */
    private Instant getNextInstant() {
        return baseInstant.plus(allArchives.size(), ChronoUnit.DAYS);
    }
}
