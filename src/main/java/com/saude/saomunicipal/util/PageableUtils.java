package com.saude.saomunicipal.util;

import com.saude.saomunicipal.exception.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Monta um {@link Pageable} validando o campo de ordenação contra uma
 * whitelist. Sem essa validação, um sortBy arbitrário do cliente chega
 * direto ao Spring Data, que lança PropertyReferenceException e cai no
 * handler genérico de exceções como erro 500.
 */
public final class PageableUtils {

    private PageableUtils() {
    }

    public static Pageable build(
            int page,
            int size,
            String sortBy,
            String direction,
            Set<String> camposPermitidos
    ) {
        if (!camposPermitidos.contains(sortBy)) {
            throw new BusinessException(
                    "Campo de ordenação inválido: '" + sortBy + "'. Valores aceitos: " + camposPermitidos + "."
            );
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}
