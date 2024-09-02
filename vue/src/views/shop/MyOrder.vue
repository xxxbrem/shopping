<template>
  <div style="width: 900px;margin: 25px">
    <div style="display: flex;margin: 20px">
      <!--    搜索区域-->
      <el-input v-model="search" placeholder="请输入你要搜索的商品名称" style="width: 30%;margin-left: 250px;margin-right: 20px" clearable></el-input>
      <el-button type="primary" icon="el-icon-search" @click="goSearch">搜索</el-button>
    </div>

    <!--    表格区域-->
    <el-table
        v-loading="loading"
        :data="tableData"
        stripe border
        :default-sort = "{prop: 'createTime', order: 'descending'}">

      <!--      商品预览图-->
      <el-table-column
          label="商品预览" width="130px">
        <template #default="scope">
          <el-image
              style="width: 100px; height: 100px"
              :src="scope.row.orderPicture"
              :preview-src-list="[scope.row.orderPicture]">
          </el-image>
        </template>
      </el-table-column>

      <el-table-column
          prop="orderName"
          label="商品名称">
      </el-table-column>
      <el-table-column
          prop="postFee"
          label="邮费">
      </el-table-column>
      <el-table-column
          prop="payPrice"
          label="总价">
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="创建时间" sortable width="140px">
      </el-table-column>
      <el-table-column
          prop="paymentTime"
          label="支付时间" sortable width="140px">
      </el-table-column>
      <el-table-column label="支付状态">
        <!--        这里应该是使用template 和default标签把那个整个data数据取出来，然后scope就是整个数据，每一行数据就是scope.row,tableData=scope-->
        <template #default="scope">
          <span v-if="scope.row.state === 2" style="color: orange">未支付</span>
          <span v-if="scope.row.state === 1" style="color: green">已支付</span>
        </template>
      </el-table-column>
<!--      操作栏-->
      <el-table-column fixed="right" label="操作" align = "center">
        <template #default="scope">
          <!--          这里scope.row传入直接是传入一行的数据对象，可以使用row.username取出用户名的-->
          <el-popconfirm title="确定要删除这个订单吗？" @confirm="deleteClick(scope.row.id)" style="margin-left: 10px">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!--    页码区域,在Vue2里面是不支持v-model:prop="data"的，所以我们要用v-bind:prop.sync="data"实现双向绑定-->
    <div style="margin: 20px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-sizes="[5,10,20]"
          :page-size.sync="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalNumber">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "MyOrder",
  created() {
    document.title = "我的订单";
    // 取出当前用户信息
    // 取出session的字符串,为了防止取到空值，||一个空对象
    let str = sessionStorage.getItem("user") || "{}";
    // 赋值
    this.user = JSON.parse(str);
    // 加载数据
    this.loadData();
  },
  data() {
    return {
      // 表示表格正在加载
      loading: true,
      // 取出的用户信息
      user: {},
      // 总数据数
      totalNumber: 400,
      // 当前页码
      currentPage: 1,
      // 一页多少个
      pageSize: 10,
      // 输入框的数据
      search: '',
      // 表格的数据
      tableData: []
    }
  },
  methods: {
    // 搜索功能
    goSearch:function (){
      this.loadData();
    },
    // 加载表格数据,Get请求，Get请求不能直接传对象，只有Post可以直接传对象进去
    loadData() {
      this.loading = true;
      request.get("/order/user_order", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search,
          userId: this.user.id,
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.totalNumber = res.data.total;
        this.loading = false;
      })
    },
    // 删除数据
    deleteClick(id) {
      request.delete('/order/'+id).then(res=>{
        if (res.code === '0') {
          this.$message({
            type: "warning",
            message: "删除成功",
          })
        }else {
          this.$message({
            type: "error",
            message: res.msg,
          })
        }
      });
      // 更新数据
      this.loadData();
    },
    // 表单每页大小的改变，因为有了双向绑定，直接更新一下数据就好了
    handleSizeChange() {
      this.loadData();
    },
    // 改变当前页码
    handleCurrentChange: function () {
      this.loadData();
    }
  },
}
</script>

<style scoped>

</style>