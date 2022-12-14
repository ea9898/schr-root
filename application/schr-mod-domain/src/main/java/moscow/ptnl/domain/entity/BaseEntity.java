package moscow.ptnl.domain.entity;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

/**
 * Базовый класс для сущностей.
 *
 * @author sorlov
 * @param <T> тип идентификатора
 */
public abstract class BaseEntity<T> implements Serializable {

    /**
     * Возвращает уникальный идентификатор сущности (ключ).
     *
     * @return id
     */
    public abstract T getId();

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ||
                //Надо учесть, что сущность может быть HibernateProxy
                (this instanceof HibernateProxy ? Hibernate.getClass(this) : this.getClass()) !=
                        (o instanceof HibernateProxy ? Hibernate.getClass(o) : o.getClass())) return false;
        moscow.ptnl.domain.entity.BaseEntity<T> that = (moscow.ptnl.domain.entity.BaseEntity<T>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
