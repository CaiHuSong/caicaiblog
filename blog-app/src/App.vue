<template>
  <div id="app">
    <router-view/>
    <go-top></go-top>
  </div>
</template>

<script>
import GoTop from '@/components/gotop/GoTop'
export default {
  name: 'App',
  components: { GoTop }
}
</script>

<style>
* { margin: 0; padding: 0; }

body {
	background-color: #7591AD;
	font-weight: 400;
	font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
	line-height: 1.5;
 
    /* width: 100vw;
    height: 100vh;
    background-color: var(--background-color);
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: 'Montserrat', sans-serif, Arial, 'Microsoft Yahei'; */
}
a {
    cursor: pointer;
    text-decoration: none;
    transition: none 86ms ease-out;
}
a:hover {
	color: #5FB878;
}
.me-area{
	background-color: #fff;
}
html{height:100%;}
body{min-height:100%;}
body{position:relative;}

.me-pull-right {
	float: right;
}
</style>
