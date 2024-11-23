package com.govzcode.sportevents.service

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

data class Filter(
    val field: String,
    val type: String,
    val value: Any
)

@Service
class FilterService {

    private val filterRegistry: MutableMap<String, Filter> = mutableMapOf()

    // Метод для добавления фильтра
    fun addFilter(filter: Filter) {
        filterRegistry[filter.field] = filter
    }

    // Метод для получения всех фильтров
    fun getFilters(): List<Filter> {
        return filterRegistry.values.toList()
    }

    // Метод для динамического применения фильтров к запросу
    fun <T> buildSpecification(params: Map<String, String>, clazz: Class<T>): Specification<T> {
        var spec: Specification<T> = Specification.where(null)

        params.forEach { (field, value) ->
            val filter = filterRegistry[field]
            if (filter != null) {
                spec = spec.and { root, query, criteriaBuilder ->
                    when (filter.type) {
                        "equals" -> {
                            // Обработка фильтра по равенству
                            when (val fieldType = root.get<Any>(field).javaType) {
                                String::class.java -> criteriaBuilder.equal(root.get<String>(field), value)
                                Integer::class.java -> criteriaBuilder.equal(root.get<Int>(field), value.toInt())
                                else -> criteriaBuilder.equal(root.get<Any>(field), value)
                            }
                        }
                        "like" -> {
                            // Обработка фильтра по шаблону
                            when (val fieldType = root.get<Any>(field).javaType) {
                                String::class.java -> criteriaBuilder.like(root.get<String>(field), "%$value%")
                                else -> criteriaBuilder.conjunction() // Для других типов не применяем "like"
                            }
                        }
                        "greater_than" -> {
                            // Обработка фильтра для больше чем
                            when (val fieldType = root.get<Any>(field).javaType) {
                                Integer::class.java -> criteriaBuilder.greaterThan(root.get<Int>(field), value.toInt())
                                else -> criteriaBuilder.conjunction() // Для других типов не применяем "greater_than"
                            }
                        }
                        "between" -> {
                            // Обработка фильтра для диапазона
                            val range = value.split("-")
                            if (range.size == 2) {
                                val min = range[0].toIntOrNull() ?: 0
                                val max = range[1].toIntOrNull() ?: Int.MAX_VALUE
                                when (val fieldType = root.get<Any>(field).javaType) {
                                    Integer::class.java -> criteriaBuilder.between(root.get<Int>(field), min, max)
                                    else -> criteriaBuilder.conjunction() // Для других типов не применяем "between"
                                }
                            } else {
                                criteriaBuilder.conjunction() // Если диапазон неправильный, возвращаем пустое условие
                            }
                        }
                        else -> criteriaBuilder.conjunction() // Для неизвестных типов фильтра
                    }
                }
            }
        }
        return spec
    }
}