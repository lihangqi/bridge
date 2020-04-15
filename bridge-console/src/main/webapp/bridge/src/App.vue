<template>
  <div id="app">
    <transition name="slide-fade">
      <router-view></router-view>
    </transition>
  </div>
</template>
<style>
  @import "../static/css/main.css";
  @import "../static/css/color-dark.css"; /*深色主题*/
  #nprogress .bar {
    background: #FF4949 !important;
  }
</style>

<style scoped>
  .slide-fade-enter-active {
    transition: all .3s ease;
    -webkit-transition: all .3s ease;
    -moz-transition: all .3s ease;
    -ms-transition: all .3s ease;
    -o-transition: all .3s ease;
  }
  .slide-fade-leave-active {
    transition: all .5s ease;
    -webkit-transition: all .5s ease;
    -moz-transition: all .5s ease;
    -ms-transition: all .5s ease;
    -o-transition: all .5s ease;
  }
  .slide-fade-enter{
    transform: translateX(0px);
    -webkit-transform: translateX(0px);
    -moz-transform: translateX(0px);
    -ms-transform: translateX(0px);
    -o-transform: translateX(0px);
    opacity: 0;
  }
  .slide-fade-leave-active {
    opacity: 0;
  }
</style>
