package net.neostellar.astalisPermManager.rank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rank {

    private final String id;
    private final String prefix;
    private final String suffix;
    private final int weight;
    private final List<String> inheritance;
    private final List<String> permissions;
    private final Set<String> resolvedPermissions = new HashSet<>();


    public Rank(String id, String prefix, String suffix, int weight, List<String> inheritance, List<String> permissions) {
        this.id = id;
        this.prefix = prefix;
        this.suffix = suffix;
        this.weight = weight;
        this.inheritance = inheritance;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getWeight() {
        return weight;
    }

    public List<String> getInheritance() {
        return inheritance;
    }

    public List<String> getPermissions() {return permissions;}

    public Set<String> getResolvedPermissions() { return resolvedPermissions; }

}
