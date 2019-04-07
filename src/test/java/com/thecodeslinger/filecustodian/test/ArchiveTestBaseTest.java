package com.thecodeslinger.filecustodian.test;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static com.thecodeslinger.filecustodian.database.archive.ArchiveType.FULL;
import static com.thecodeslinger.filecustodian.database.archive.ArchiveType.INCREMENTAL;
import static com.thecodeslinger.filecustodian.test.ArchiveType.Full;
import static com.thecodeslinger.filecustodian.test.ArchiveType.Inc;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the "DSL" to create {@link com.thecodeslinger.filecustodian.database.archive.Archive}s
 * works properly.
 */
class ArchiveTestBaseTest extends ArchiveTestBase {

    @Test
    void create_singe_full_archive() {
        // Given
        create(Full());

        // Expect
        assertThat(getArchiveCount()).isEqualTo(1);
        assertArchiveAt(0, FULL, getBaseInstant(), null);
    }

    @Test
    void create_two_consecutive_full() {
        // Given
        create(Full(), Full());

        // Expect
        assertThat(getArchiveCount()).isEqualTo(2);
        assertArchiveAt(0, FULL, getBaseInstant(), null);
        assertArchiveAt(1, FULL, getBaseInstant().plus(1, ChronoUnit.DAYS), null);
    }

    @Test
    void create_full_with_two_nested_incremental() {
        // Given
        create(Full(Inc(Inc())));

        // Expect
        assertThat(getArchiveCount()).isEqualTo(3);

        var full0 = get(0);
        var inc1 = get(1);
        var inc2 = get(2);

        assertArchive(full0, FULL, getBaseInstant(), inc1);
        assertArchive(inc1, INCREMENTAL, getBaseInstant().plus(1, ChronoUnit.DAYS), inc2);
        assertArchive(inc2, INCREMENTAL, getBaseInstant().plus(2, ChronoUnit.DAYS), null);
    }

    @Test
    void create_random_archives() {
        // Given
        create(
                Full(Inc()),
                Full(Inc()),
                Full(Inc(Inc(Inc(Inc()))))
        );
        var baseInstant = getBaseInstant();

        // Expect
        assertThat(getArchiveCount()).isEqualTo(9);

        var full0 = get(0);
        var inc1 = get(1);

        assertArchive(full0, FULL, baseInstant, inc1);
        assertArchive(inc1, INCREMENTAL, baseInstant.plus(1, ChronoUnit.DAYS), null);

        var full2 = get(2);
        var inc3 = get(3);

        assertArchive(full2, FULL, baseInstant.plus(2, ChronoUnit.DAYS), inc3);
        assertArchive(inc3, INCREMENTAL, baseInstant.plus(3, ChronoUnit.DAYS), null);

        var full4 = get(4);
        var inc5 = get(5);
        var inc6 = get(6);
        var inc7 = get(7);
        var inc8 = get(8);

        assertArchive(full4, FULL, baseInstant.plus(4, ChronoUnit.DAYS), inc5);
        assertArchive(inc5, INCREMENTAL, baseInstant.plus(5, ChronoUnit.DAYS), inc6);
        assertArchive(inc6, INCREMENTAL, baseInstant.plus(6, ChronoUnit.DAYS), inc7);
        assertArchive(inc7, INCREMENTAL, baseInstant.plus(7, ChronoUnit.DAYS), inc8);
        assertArchive(inc8, INCREMENTAL, baseInstant.plus(8, ChronoUnit.DAYS), null);
    }
}
