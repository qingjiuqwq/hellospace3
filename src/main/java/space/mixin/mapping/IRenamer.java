/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有。
 */
package space.mixin.mapping;

public interface IRenamer {
    default String rename(IMappingFile.IPackage value) {
        return value.getMapped();
    }

    default String rename(IMappingFile.IClass value) {
        return value.getMapped();
    }

    default String rename(IMappingFile.IField value) {
        return value.getMapped();
    }

    default String rename(IMappingFile.IMethod value) {
        return value.getMapped();
    }

    default String rename(IMappingFile.IParameter value) {
        return value.getMapped();
    }

}
