package com.shicc.saas.tenant.service.mapper;


import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 */

public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    List<E> toEntity(List<D> dtoList);

    List<D> toDto(List<E> entityList);

//    default <T, K, V> List<DefaultKeyValue<K, V>> toMenuList(List<T> list, Function<T, K> key, Function<T, V> value) {
//        if (list == null) {
//            return Collections.emptyList();
//        }
//        return list.stream().map(o -> new DefaultKeyValue<>(key.apply(o), value.apply(o))).collect(Collectors.toList());
//    }
}
