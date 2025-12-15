package com.team.goldenturtle.repo;

import com.team.goldenturtle.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExTradeRepository extends JpaRepository<Trade, Long> {

}
