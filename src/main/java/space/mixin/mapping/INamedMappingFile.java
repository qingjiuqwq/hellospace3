/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface INamedMappingFile {
    static INamedMappingFile load(File path) throws IOException {
        try (InputStream in = new FileInputStream(path)) {
            return load(in);
        }
    }

    static INamedMappingFile load(InputStream in) throws IOException {
        return InternalUtils.loadNamed(in);
    }

    List<String> getNames();

    IMappingFile getMap(String from, String to);

    default void write(Path path, IMappingFile.Format format) throws IOException {
        write(path, format, getNames().toArray(new String[0]));
    }

    void write(Path path, IMappingFile.Format format, String... order) throws IOException;
}
