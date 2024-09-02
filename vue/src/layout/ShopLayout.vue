<template>
  <div style="background-color: rgba(234,153,192)">
    <!--    头部-->
    <ShopHeader style="background-color: rgba(245,245,245)"></ShopHeader>
    <!--    主体-->
    <!--      内容区域,因为搜索栏和下面的页面交互太多了，所以不适合单独分离出来-->
    <router-view/>
    <!--    网页脚-->
    <NavFooter style="margin-top: 25px"></NavFooter>
  </div>
</template>

<script>
import ShopHeader from "@/components/shop/ShopHeader.vue";
import NavFooter from "@/components/NavFooter.vue";

export default {
  name: "ShopLayout",
  components: {
    NavFooter,
    ShopHeader
  }
}
</script>

<style scoped>

</style>