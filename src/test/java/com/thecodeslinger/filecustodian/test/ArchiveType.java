package com.thecodeslinger.filecustodian.test;

import com.thecodeslinger.filecustodian.database.archive.Archive;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Objects;

/**
 * This class is the basis for the test-data "DSL", representing an {@link Archive}. Through the
 * concrete implementations {@link FullArchive} and {@link IncrementalArchive} and the {@code mk*}
 * methods the desired number of {@link Archive} instances can be created. The only limitation is
 * that a full archive cannot be used as a child to another archive. Other than that the hierarchies
 * can be built with any kind of flexibility.
 */
public abstract class ArchiveType {

    /**
     * Represents and creates an {@link Archive} of type
     * {@link com.thecodeslinger.filecustodian.database.archive.ArchiveType#FULL}.
     */
    public static class FullArchive extends ArchiveType {

        FullArchive(IncrementalArchive child) {
            super(child);
        }

        Archive create(Instant instant) {
            Archive archive = Archive.createFull(instant);
            return setAndReturnParent(child, archive);
        }
    }

    /**
     * Represents and creates an {@link Archive} of type
     * {@link com.thecodeslinger.filecustodian.database.archive.ArchiveType#INCREMENTAL}.
     */
    public static class IncrementalArchive extends ArchiveType {
        Archive parent = null;

        IncrementalArchive(IncrementalArchive child) {
            super(child);
        }

        Archive create(Instant instant) {
            assert null != parent : "Parent child relation messed up";

            Archive archive = Archive.createIncremental(instant, parent);
            return setAndReturnParent(child, archive);
        }
    }

    /**
     * Generic factory method. Creates any {@link ArchiveType} without a nested child.
     *
     * @param clazz
     *      Either {@link FullArchive} or {@link IncrementalArchive}.
     *
     * @param <T>
     *     Either {@link FullArchive} or {@link IncrementalArchive}.
     *
     * @return
     *      Returns an new object without a nested child.
     *
     * @throws ReflectiveOperationException
     *      The constructor does not exist (which it does).
     */
    public static <T extends ArchiveType> T mk(Class<T> clazz) throws ReflectiveOperationException {
        return mk(clazz, null);
    }

    /**
     * Generic factory method. Creates any {@link ArchiveType} with a nested child.
     *
     * @param clazz
     *      Either {@link FullArchive} or {@link IncrementalArchive}.
     *
     * @param child
     *      The nested {@link IncrementalArchive} child or {@code null}.
     *
     * @param <T>
     *      Either {@link FullArchive} or {@link IncrementalArchive}.
     *
     * @return
     *      Returns an new object with the given nested child.
     *
     * @throws ReflectiveOperationException
     *      The constructor does not exist (which it does).
     */
    public static <T> T mk(Class<T> clazz, IncrementalArchive child) throws ReflectiveOperationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor(IncrementalArchive.class);
        return constructor.newInstance(child);
    }

    /**
     * Shorter specific version. Creates a {@link FullArchive} object without a nested child.
     */
    public static FullArchive Full() {
        return Full(null);
    }

    /**
     * Shorter specific version. Creates a {@link FullArchive} object with the given nested child.
     */
    public static FullArchive Full(IncrementalArchive child) {
        return new FullArchive(child);
    }

    /**
     * Shorter specific version. Creates a {@link IncrementalArchive} object without a nested child.
     */
    public static IncrementalArchive Inc() {
        return Inc(null);
    }

    /**
     * Shorter specific version. Creates a {@link IncrementalArchive} object with the given nested child.
     */
    public static IncrementalArchive Inc(IncrementalArchive child) {
        return new IncrementalArchive(child);
    }

    /**
     * The nested archive. Can be {@code null}.
     */
    IncrementalArchive child;

    /**
     * A constructor, obviously.
     *
     * @param child
     *      A nested archive or {@code null}.
     */
    ArchiveType(IncrementalArchive child) {
        this.child = child;
    }

    /**
     * Creates the real {@link Archive} instance off the "DSL" object.
     *
     * @param instant
     *      The timestamp of when the archive shall have been created.
     *
     * @return
     *      Returns the new {@link Archive} instance.
     */
    abstract Archive create(Instant instant);

    /**
     * Internal hack in order to be able to create the correct relationship between parent and
     * child archives.
     */
    private static Archive setAndReturnParent(IncrementalArchive child, Archive parent) {
        if (Objects.nonNull(child)) {
            child.parent = parent;
        }
        return parent;
    }
}
