package com.guiwoo.stock_dividend.persist.repository;

import com.guiwoo.stock_dividend.model.Dividend;
import com.guiwoo.stock_dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity,Long> {
    List<DividendEntity> findAllByCompanyId(Long id);
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime time);

    @Transactional
    void deleteAllByCompanyId(Long id);
}
