package com.personthecat.orestonevariants.properties;

import com.google.common.collect.ImmutableSet;
import com.personthecat.orestonevariants.Main;
import com.personthecat.orestonevariants.util.Lazy;
import javafx.util.Pair;

import java.util.*;

import static com.personthecat.orestonevariants.util.CommonMethods.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PropertyGroup {
    public final List<OreProperties> properties;
    public final Optional<String> mod;
    public final String name;

    /** Information containing all of the default PropertyGroups. */
    private static final GroupInfo[] DEFAULT_INFO = {
        new GroupInfo("minecraft", "coal", "diamond",
            "gold", "iron", "lapis", "redstone")
    };

    /** The group containing all registered PropertyGroups */
    public static final Lazy<PropertyGroup> ALL = new Lazy<>(
        PropertyGroup::getAllProperties
    );

    /** The group containing all of the default PropertyGroups. */
    public static final Lazy<PropertyGroup> DEFAULT = new Lazy<>(
        PropertyGroup::getDefaultProperties
    );

    /** Convenience constructor for custom groups. */
    public PropertyGroup(String name, List<OreProperties> properties) {
        this(name, properties, empty());
    }

    /** Primary constructor. */
    public PropertyGroup(String name, List<OreProperties> properties, Optional<String> mod) {
        this.name = name;
        this.properties = properties;
        this.mod = mod;
    }

    public static ImmutableSet<PropertyGroup> setupPropertyGroups() {
        return ImmutableSet.of();
    }

    /**
     * Attempts to locate a property group with the specified name. Else,
     * creates a new group containing only the respective OreProperties.
     */
    public static PropertyGroup findOrCreate(String name) {
        return find(Main.PROPERTY_GROUPS, g -> g.name.equals(name))
            .orElse(new PropertyGroup(name, Collections.singletonList(OreProperties.of(name)
            .orElseThrow(() -> runExF("No PropertyGroup or properties named \"{}\"", name)))));
    }

    /** Generates a group containing all registered OreProperties. */
    private static PropertyGroup getAllProperties() {
        return new PropertyGroup("all", new ArrayList<>(Main.ORE_PROPERTIES));
    }

    /** Generates a group containing all default OreProperties. */
    private static PropertyGroup getDefaultProperties() {
        final List<OreProperties> list = new ArrayList<>();
        // Find all groups with default names and reuse their blocks.
        for (GroupInfo info : DEFAULT_INFO) {
            final List<OreProperties> updated = find(Main.PROPERTY_GROUPS, g -> g.name.equals(info.getMod()))
                .map(group -> group.properties)
                .orElseThrow(() -> runExF("PropertyGroups were not registered in time."));
            list.addAll(updated);
        }
        return new PropertyGroup("default", list);
    }

    /** Used for neatly displaying info about default PropertyGroups. */
    private static class GroupInfo extends Pair<String, String[]> {
        private GroupInfo(String name, String... entries) {
            super(name, entries);
        }

        private String getMod() {
            return getKey();
        }

        /** Returns the formatted names in this group. */
        private String[] getNames() {
            final String[] names = new String[getValue().length];
            int index = 0;
            for (String name : getValue()) {
                names[index++] = getKey().equals("minecraft")
                    ? f("{}_ore", name)
                    : f("{}_{}_ore", getKey(), name);
            }
            return names;
        }
    }
}