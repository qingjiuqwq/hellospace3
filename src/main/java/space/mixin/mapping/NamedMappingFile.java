/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.mapping;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class NamedMappingFile implements INamedMappingFile, IMappingBuilder {
    private final List<String> names;
    private final Map<String, Package> packages = new HashMap<>();
    private final Map<String, Cls> classes = new HashMap<>();
    private final Map<String, String[]> classCache = new ConcurrentHashMap<>();
    private final Map<String, IMappingFile> mapCache = new ConcurrentHashMap<>();

    public NamedMappingFile(String... names) {
        if (names == null || names.length < 2) {
            throw new IllegalArgumentException("Can not create Mapping file with less then two names");
        }
        this.names = List.of(names);
    }

    private static void write(List<String> lines, IMappingFile.Format format, int[] indexes, InternalUtils.Element element, Map<String, String> meta, Named node) {
        String line = node.write(format, indexes);
        if (line != null) {
            lines.add(line);
            InternalUtils.writeMeta(format, lines, element, meta);
        }
    }

    private static <K, V> V retPut(Map<K, V> map, K key, V value) {
        map.put(key, value);
        return value;
    }

    private void ensureCount(String... names) {
        if (names == null) {
            throw new IllegalArgumentException("Names can not be null");
        }
        if (names.length != this.names.size()) {
            throw new IllegalArgumentException("Invalid number of names, expected " + this.names.size() + " got " + names.length);
        }
    }

    @Override
    public List<String> getNames() {
        return this.names;
    }

    @Override
    public IMappingFile getMap(final String from, final String to) {
        String key = from + "_to_" + to;
        return mapCache.computeIfAbsent(key, k -> {
            int fromI = this.names.indexOf(from);
            int toI = this.names.indexOf(to);
            if (fromI == -1 || toI == -1)
                throw new IllegalArgumentException("Could not find mapping names: " + from + " / " + to);
            return new MappingFile(this, fromI, toI);
        });
    }

    @Override
    public void write(Path path, IMappingFile.Format format, String... order) throws IOException {
        if (order == null || order.length == 1) {
            throw new IllegalArgumentException("Invalid order, you must specify atleast 2 names");
        }

        if (!format.hasNames() && order.length > 2) {
            throw new IllegalArgumentException("Can not write " + Arrays.toString(order) + " in " + format.name() + " format, it does not support headers");
        }

        int[] indexes = new int[order.length];
        for (int x = 0; x < order.length; x++) {
            indexes[x] = this.getNames().indexOf(order[x]);
            if (indexes[x] == -1) {
                throw new IllegalArgumentException("Invalid order: Missing \"" + order[x] + "\" name");
            }
        }


        List<String> lines = new ArrayList<>();
        Comparator<Named> sort = Comparator.comparing(a -> a.getName(indexes[0]));

        getPackages().sorted(sort).forEachOrdered(pkg ->
                write(lines, format, indexes, InternalUtils.Element.PACKAGE, pkg.meta, pkg)
        );
        getClasses().sorted(sort).forEachOrdered(cls -> {
            write(lines, format, indexes, InternalUtils.Element.CLASS, cls.meta, cls);

            cls.getFields().sorted(sort).forEachOrdered(fld ->
                    write(lines, format, indexes, InternalUtils.Element.FIELD, fld.meta, fld)
            );

            cls.getMethods().sorted(sort).forEachOrdered(mtd -> {
                write(lines, format, indexes, InternalUtils.Element.METHOD, mtd.meta, mtd);

                mtd.getParameters().sorted(Comparator.comparingInt(Cls.Method.Parameter::getIndex)).forEachOrdered(par ->
                        write(lines, format, indexes, InternalUtils.Element.PARAMETER, par.meta, par)
                );
            });
        });

        lines.removeIf(Objects::isNull);

        if (!format.isOrdered()) {
            lines.sort((format == IMappingFile.Format.SRG || format == IMappingFile.Format.XSRG) ? InternalUtils::compareLines : Comparator.naturalOrder());
        }

        if (format == IMappingFile.Format.TINY1 || format == IMappingFile.Format.TINY) {
            StringBuilder buf = new StringBuilder();
            buf.append(format == IMappingFile.Format.TINY ? "tiny\t2\t0" : "v1");
            for (String name : order) {
                buf.append('\t').append(name);
            }
            lines.add(0, buf.toString());
        } else if (format == IMappingFile.Format.TSRG2) {
            StringBuilder buf = new StringBuilder();
            buf.append("tsrg2");
            for (String name : order) {
                buf.append(' ').append(name);
            }
            lines.add(0, buf.toString());
        }

        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String line : lines) {
                writer.write(line);
                writer.write('\n');
            }
        }
    }

    private String remapClass(int index, String cls) {
        String[] ret = remapClass(cls);
        return ret[ret.length == 1 ? 0 : index];
    }

    private String[] remapClass(String cls) {
        String[] ret = classCache.get(cls);
        if (ret == null) {
            Cls _cls = classes.get(cls);
            if (_cls == null) {
                int idx = cls.lastIndexOf('$');
                if (idx != -1) {
                    String[] parent = remapClass(cls.substring(0, idx));
                    ret = new String[parent.length];
                    for (int x = 0; x < ret.length; x++) {
                        ret[x] = parent[x] + '$' + cls.substring(idx + 1);
                    }
                } else {
                    ret = new String[]{cls};
                }
            } else {
                ret = _cls.getNames();
            }
            classCache.put(cls, ret);
        }
        return ret;
    }

    private String remapDescriptor(int index, String desc) {
        Matcher matcher = MappingFile.DESC.matcher(desc);
        StringBuilder buf = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buf, Matcher.quoteReplacement("L" + remapClass(index, matcher.group("cls")) + ";"));
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    public Stream<Package> getPackages() {
        return this.packages.values().stream();
    }

    Stream<Cls> getClasses() {
        return this.classes.values().stream();
    }

    @Override
    public Package addPackage(String... names) {
        ensureCount(names);
        return retPut(this.packages, names[0], new Package(names));
    }

    @Override
    public Cls addClass(String... names) {
        ensureCount(names);
        return retPut(this.classes, names[0], new Cls(names));
    }

    @Override
    public INamedMappingFile build() {
        return this;
    }

    abstract static class Named {
        private final String[] names;

        Named(String... names) {
            this.names = names;
        }

        public String getName(int index) {
            return this.names[index];
        }

        String[] getNames() {
            return this.names;
        }

        protected String getNames(int... order) {
            StringBuilder ret = new StringBuilder();
            for (int index : order)
                ret.append('\t').append(getName(index));
            return ret.toString();
        }

        abstract String write(IMappingFile.Format format, int... order);
    }

    public class Package extends Named implements IPackage {
        final Map<String, String> meta = new LinkedHashMap<>();

        Package(String... names) {
            super(names);
        }

        @Override
        String write(IMappingFile.Format format, int... order) {
            return switch (format) {
                case SRG, XSRG -> "PK: " + getName(order[0]) + ' ' + getName(order[1]);
                case CSRG, TSRG -> getName(order[0]) + "/ " + getName(order[1]) + '/';
                case TSRG2 -> getTsrg2(order);
                case PG, TINY1, TINY -> null;
            };
        }

        private String getTsrg2(int... order) {
            StringBuilder ret = new StringBuilder();
            for (int x = 0; x < order.length; x++) {
                ret.append(getName(order[x])).append('/');
                if (x != order.length - 1)
                    ret.append(' ');
            }
            return ret.toString();
        }

        @Override
        public IPackage meta(String key, String value) {
            meta.put(key, value);
            return this;
        }

        @Override
        public IMappingBuilder build() {
            return NamedMappingFile.this;
        }
    }

    public class Cls extends Named implements IClass {
        final Map<String, String> meta = new LinkedHashMap<>();
        private final Map<String, Field> fields = new HashMap<>();
        private final Map<String, Method> methods = new HashMap<>();

        Cls(String... name) {
            super(name);
        }

        Stream<Field> getFields() {
            return this.fields.values().stream();
        }

        Stream<Method> getMethods() {
            return this.methods.values().stream();
        }

        @Override
        public Field field(String... names) {
            ensureCount(names);
            return retPut(this.fields, names[0], new Field(names));
        }

        @Override
        public Method method(String desc, String... names) {
            ensureCount(names);
            return retPut(this.methods, names[0] + desc, new Method(desc, names));
        }

        @Override
        public IClass meta(String key, String value) {
            this.meta.put(key, value);
            return this;
        }

        @Override
        public IMappingBuilder build() {
            return NamedMappingFile.this;
        }

        @Override
        String write(IMappingFile.Format format, int... order) {
            return switch (format) {
                case SRG, XSRG -> "CL: " + getName(order[0]) + ' ' + getName(order[1]);
                case CSRG, TSRG -> getName(order[0]) + ' ' + getName(order[1]);
                case TSRG2 -> getTsrg2(order);
                case PG -> getName(order[0]).replace('/', '.') + " -> " + getName(order[1]).replace('/', '.') + ':';
                case TINY1 -> "CLASS" + getNames(order);
                case TINY -> "c" + getNames(order);
            };
        }

        private String getTsrg2(int... order) {
            StringBuilder ret = new StringBuilder();
            for (int x = 0; x < order.length; x++) {
                ret.append(getName(order[x]));
                if (x != order.length - 1)
                    ret.append(' ');
            }
            return ret.toString();
        }

        public class Field extends Named implements IField {
            final Map<String, String> meta = new LinkedHashMap<>();
            @Nullable
            private String desc;

            Field(String... names) {
                super(names);
            }

            public String getDescriptor(int index) {
                return this.desc == null ? null : index == 0 ? this.desc : NamedMappingFile.this.remapDescriptor(index, this.desc);
            }

            @Override
            public IField descriptor(String value) {
                this.desc = value;
                return this;
            }

            @Override
            public IField meta(String key, String value) {
                this.meta.put(key, value);
                return this;
            }

            @Override
            public IClass build() {
                return Cls.this;
            }

            @Override
            String write(IMappingFile.Format format, int... order) {
                return switch (format) {
                    case SRG ->
                            "FD: " + Cls.this.getName(order[0]) + '/' + getName(order[0]) + ' ' + Cls.this.getName(order[1]) + '/' + getName(order[1]) + (this.desc == null ? "" : getDescriptor(order[0]) + ' ' + getDescriptor(order[1]));
                    case XSRG ->
                            "FD: " + Cls.this.getName(order[0]) + '/' + getName(order[0]) + (this.desc == null ? "" : getDescriptor(order[0])) + ' ' + Cls.this.getName(order[1]) + '/' + getName(order[1]) + (this.desc == null ? "" : getDescriptor(order[1]));
                    case CSRG -> Cls.this.getName(order[0]) + ' ' + getName(order[0]) + ' ' + getName(order[1]);
                    case TSRG -> '\t' + getName(order[0]) + ' ' + getName(order[1]);
                    case TSRG2 -> getTsrg2(order);
                    case PG ->
                            "    " + InternalUtils.toSource(getDescriptor(order[0])) + ' ' + getName(order[0]) + " -> " + getName(order[1]);
                    case TINY1 ->
                            "FIELD\t" + Cls.this.getName(order[0]) + '\t' + getDescriptor(order[0]) + getNames(order);
                    case TINY -> "\tf\t" + getDescriptor(order[0]) + getNames(order);
                };
            }

            private String getTsrg2(int... order) {
                StringBuilder ret = new StringBuilder().append('\t');
                for (int x = 0; x < order.length; x++) {
                    ret.append(getName(order[x]));
                    if (x == 0 && this.desc != null)
                        ret.append(' ').append(getDescriptor(order[x]));
                    if (x != order.length - 1)
                        ret.append(' ');
                }
                return ret.toString();
            }
        }

        public class Method extends Named implements IMethod {
            final Map<String, String> meta = new LinkedHashMap<>();
            private final String desc;
            private final Map<Integer, Parameter> params = new HashMap<>();

            Method(String desc, String... names) {
                super(names);
                this.desc = desc;
            }

            @Override
            public IParameter parameter(int index, String... names) {
                ensureCount(names);
                return retPut(this.params, index, new Parameter(index, names));
            }

            @Override
            public IMethod meta(String key, String value) {
                this.meta.put(key, value);
                return this;
            }

            @Override
            public IClass build() {
                return Cls.this;
            }

            public String getDescriptor(int index) {
                return index == 0 ? this.desc : NamedMappingFile.this.remapDescriptor(index, this.desc);
            }

            Stream<Parameter> getParameters() {
                return this.params.values().stream();
            }

            @Override
            String write(IMappingFile.Format format, int... order) {
                String oOwner = Cls.this.getName(order[0]);
                String oName = getName(order[0]);
                String mName = getName(order[1]);
                String oDesc = getDescriptor(order[0]);

                switch (format) {
                    case SRG:
                    case XSRG:
                        return "MD: " + oOwner + '/' + oName + ' ' + oDesc + ' ' + Cls.this.getName(order[1]) + '/' + mName + ' ' + getDescriptor(order[1]);
                    case CSRG:
                        return oOwner + ' ' + oName + ' ' + oDesc + ' ' + mName;
                    case TSRG:
                        return '\t' + oName + ' ' + oDesc + ' ' + mName;
                    case TSRG2:
                        return getTsrg2(order);
                    case TINY1:
                        return "METHOD\t" + oOwner + '\t' + oDesc + getNames(order);
                    case TINY:
                        return "\tm\t" + oDesc + getNames(order);
                    case PG:
                        int start = Integer.parseInt(meta.getOrDefault("start_line", "0"));
                        int end = Integer.parseInt(meta.getOrDefault("end_line", "0"));
                        return "    " + (start == 0 && end == 0 ? "" : start + ":" + end + ":") + InternalUtils.toSource(oName, oDesc) + " -> " + mName;
                    default:
                        throw new UnsupportedOperationException("Unknown format: " + format);
                }
            }

            private String getTsrg2(int... order) {
                StringBuilder ret = new StringBuilder().append('\t');
                for (int x = 0; x < order.length; x++) {
                    ret.append(getName(order[x]));
                    if (x == 0 && getDescriptor(order[x]) != null)
                        ret.append(' ').append(getDescriptor(order[x]));
                    if (x != order.length - 1)
                        ret.append(' ');
                }
                return ret.toString();
            }

            class Parameter extends Named implements IParameter {
                final Map<String, String> meta = new LinkedHashMap<>();
                private final int index;

                Parameter(int index, String... names) {
                    super(names);
                    this.index = index;
                }

                public int getIndex() {
                    return this.index;
                }

                @Override
                String write(IMappingFile.Format format, int... order) {
                    return switch (format) {
                        case SRG, XSRG, CSRG, TSRG, PG, TINY1 -> null;
                        case TINY -> "\t\tp\t" + getIndex() + getNames(order);
                        case TSRG2 -> getTsrg2(order);
                    };
                }

                private String getTsrg2(int... order) {
                    StringBuilder ret = new StringBuilder()
                            .append("\t\t").append(getIndex());
                    for (int i : order) {
                        ret.append(' ').append(getName(i));
                    }
                    return ret.toString();
                }

                @Override
                public IParameter meta(String key, String value) {
                    this.meta.put(key, value);
                    return this;
                }

                @Override
                public IMethod build() {
                    return Method.this;
                }
            }
        }
    }
}
