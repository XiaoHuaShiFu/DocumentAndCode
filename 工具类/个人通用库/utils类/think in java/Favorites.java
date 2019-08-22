package util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

/**
 * 描述: 类型安全的异构容器
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-07-23 16:11
 */

public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(type, type.cast(instance));
    }

}
