package moscow.ptnl.app.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author sorlov
 */
@NoRepositoryBean
public interface BaseCrudRepository<T, K> extends CrudRepository<T, K>, JpaSpecificationExecutor<T> {

    Character ESCAPE = '\\';

    // Преобразование спец. символов для правильного выполнения условия LIKE
    default String buildLikeExpr(String value) {
        return value == null ? null : "%" + value.replace("_", "\\_").replace("%", "\\%") + "%";
    }

    default Predicate buildPredicate(List<Predicate> predicates, Function<Predicate[], Predicate> builder) {
        return builder.apply(predicates.stream().filter(Objects::nonNull).toArray(Predicate[]::new));
    }

    default <V> void addPredicate(List<Predicate> predicates, V value, Function<V, Predicate> builder) {
        if (value != null && (!(value instanceof Collection) || !((Collection<?>) value).isEmpty())) {
            predicates.add(builder.apply(value));
        }
    }
}
