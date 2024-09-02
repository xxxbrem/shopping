package com.xia.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xia.springboot.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper extends BaseMapper<Order> {

    @Update("update t_order set state = #{state},payment_time=#{payTime} where order_no=#{tradeNo}")
    int updateState(@Param("tradeNo") String tradeNo,@Param("state") int state,@Param("payTime") String payTime);
}
