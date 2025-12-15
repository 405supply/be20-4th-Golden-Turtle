package com.team.goldenturtle.stockinfo.query.mapper;

import com.team.goldenturtle.stockinfo.query.dto.response.StockListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockMapper {

    @Select("""
        SELECT
            stock_id AS stockId,
            ticker AS ticker,
            name AS name,
            market AS market,
            status AS status,
            created_at AS createdAt,
            updated_at AS updatedAt
        FROM tb_stock
        ORDER BY status
        LIMIT #{limit}
        OFFSET #{offset}
        """)
    List<StockListResponse> findAllStocks(
            @Param("limit")  int limit,
            @Param("offset") int offset);

    @Select("""
        SELECT
            stock_id AS stockId,
            ticker AS ticker,
            name AS name,
            market AS market,
            status AS status,
            created_at AS createdAt,
            updated_at AS updatedAt
        FROM tb_stock
        WHERE status = 'ACTIVE'
    """)
    List<StockListResponse> findActiveStocks();
}
