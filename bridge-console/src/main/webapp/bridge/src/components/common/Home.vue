<template>

  <div class="wrapper">

    <v-head></v-head>

    <v-sidebar></v-sidebar>

    <div class="content">

      <v-bread></v-bread>

      <transition name="move" mode="out-in">
        <router-view></router-view>
      </transition>

      <v-footer></v-footer>

    </div>


  </div>

</template>


<script>
  import vHead from './Header.vue';
  import vSidebar from './Sidebarz.vue';
  import vBread from './Breadcrumbs.vue';
  import vFooter from './Footer.vue';

  export default {
    components: {
      vSidebar, vHead, vBread, vFooter
    },
    data() {
      return {}
    },
    mounted() {
    }
  }


</script>
