package com.xia.springboot.controller;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xia.springboot.common.Result;
import com.xia.springboot.entity.Item;
import com.xia.springboot.entity.Order;
import com.xia.springboot.entity.User;
import com.xia.springboot.mapper.ItemMapper;
import com.xia.springboot.mapper.OrderMapper;
import com.xia.springboot.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    OrderMapper orderMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    ItemMapper itemMapper;

    /**
     * 新建一个订单
     * @param order
     * @return
     */
    @PostMapping
    public Result<?> save(@RequestBody Order order){
        orderMapper.insert(order);
        return Result.success();
    }

    //更新订单
    @PutMapping
    public Result<?> update(@RequestBody Order order){
        orderMapper.updateById(order);
        return Result.success();
    }

    //根据订单ID删除订单
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        orderMapper.deleteById(id);
        return Result.success();
    }

    //根据订单ID查询订单
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id){
        return Result.success(orderMapper.selectById(id));
    }

    //购买商品，新建一个订单，单独购买一个商品
    @GetMapping("/buy")
    public Result<?> buy(@RequestParam Long itemId,@RequestParam Integer userId){
        Item item = itemMapper.selectById(itemId);
        User user = userMapper.selectById(userId);
        String orderNo = IdUtil.getSnowflake().nextIdStr();
        String payUrl = "http://localhost:9090/alipay/pay?subject=" + item.getTitle() + "&traceNo=" + orderNo + "&totalAmount=" + item.getPrice();

        Order order = new Order();
        // 获取商品名称
        order.setOrderName(item.getTitle());
        // 设置订单号
        order.setOrderNo(orderNo);
        // 设置订单价格
        order.setPayPrice(item.getPrice());
        // 设置邮费
        order.setPostFee(BigDecimal.ZERO);
        // 设置支付状态
        order.setState(2);
        order.setUserId(user.getId());
        order.setUsername(user.getUsername());
        order.setOrderPicture(item.getImage());
        //构建创建时间
        Date currDate = new Date();
        order.setCreateTime(currDate);
        save(order);
        return Result.success(payUrl);
    }

    @PostMapping("/buyCart")
    public Result<?> buyCart(@RequestBody List<Order> orders,@RequestParam BigDecimal totalPrice){
        // 生成订单号
        String orderNo = IdUtil.getSnowflake().nextIdStr();
        // 生成订单名称
        String  orderName = orders.get(0).getOrderName() + "等多件商品";
        // 拼接支付URL
        String payUrl = "http://localhost:9090/alipay/pay?subject=" + orderName + "&traceNo=" + orderNo + "&totalAmount=" + totalPrice;
        // 对所以加入购物车的商品进行处理，添加订单号
        for (Order order:orders) {
            order.setOrderNo(orderNo);
            orderMapper.updateById(order);
        }
        // 返回支付URL
        return Result.success(payUrl);
    }

    /**
     *
     * @param itemId 要加入购物车的物品ID
     * @param userId 用户的ID
     * @return 返回成功消息就可以了
     */
    @GetMapping("/add")
    public Result<?> addToCart(@RequestParam Long itemId,@RequestParam Integer userId){
        Item item = itemMapper.selectById(itemId);
        User user = userMapper.selectById(userId);
        Order order = new Order();
        // 获取商品名称
        order.setOrderName(item.getTitle());
        // 设置订单价格
        order.setPayPrice(item.getPrice());
        // 设置邮费
        order.setPostFee(BigDecimal.ZERO);
        // 设置支付状态
        order.setState(2);
        // 设置用户的信息
        order.setUserId(user.getId());
        order.setUsername(user.getUsername());
        // 设置订单的预览图
        order.setOrderPicture(item.getImage());
        //构建创建时间
        Date currDate = new Date();
        order.setCreateTime(currDate);
        // 保存订单
        save(order);
        return Result.success();
    }

    /**
     * 查询全部的订单消息
     * @param pageNum 当前的页数
     * @param pageSize 一页的大小
     * @param search 搜索的关键词
     * @return 结果集
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        //新建分页对象
        Page<Order> page = new Page<>(pageNum, pageSize);
        //新建模糊查询对象,这里有个注意事项，你模糊项查询的对应项不能为null，为null就查不出来
        //LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().like(User::getUsername, search);
        LambdaQueryWrapper<Order> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(Order::getCreateTime);
        if (!search.equals(""))
            queryWrapper.like(Order::getOrderName,search);
        Page<Order> userPage = orderMapper.selectPage(page, queryWrapper);
        return Result.success(userPage);
    }

    /**
     * 查询对应用户的订单信息
     * @param pageNum 当前页码
     * @param pageSize 每一页的条数
     * @param search 搜索的商品名称
     * @param userId 传入的用户ID
     * @param unpaid 是否查询未支付的订单，0为不查询，1为查询
     * @return 返回分页好的数据表
     */
    @GetMapping("/user_order")
    public Result<?> findUserOrder(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String search, @RequestParam Integer userId,
                                   @RequestParam(defaultValue = "0") boolean unpaid) {
        //新建分页对象
        Page<Order> page = new Page<>(pageNum, pageSize);
        //新建模糊查询对象,这里有个注意事项，你模糊项查询的对应项不能为null，为null就查不出来
        //LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().like(User::getUsername, search);
        LambdaQueryWrapper<Order> queryWrapper = Wrappers.lambdaQuery();
        // 给查询对象加上ID限制和日期降序
        queryWrapper.eq(Order::getUserId,userId).orderByDesc(Order::getCreateTime);
        // 不为空就给查询对象加上搜索限制,例如是否查询未支付订单，或者模糊搜索
        if (unpaid)
            queryWrapper.eq(Order::getState,2);
        if (!search.equals(""))
            queryWrapper.like(Order::getOrderName,search);
        Page<Order> userPage = orderMapper.selectPage(page, queryWrapper);
        return Result.success(userPage);
    }
}
