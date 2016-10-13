<script type="text/javascript">
var socket = null;
$(function(){
  function parseObj(StringData){
    return (new Function("return" + StringData))();
  }
  //创建socket对象
  socket = new WebSocket("ws://"+ window.location.host+"${pageContext.request.contextPath}/game");
  //连接创建后调用
  socket.onopen = function() {
      console.log("open");
  }
  
  //接收到服务器消息后调用
    socket.onmessage = function(message) {
        var data=parseObj(message.data);
        if(data.text > 0){
            $('#unreadMessage').addClass("unreadMessage");
        }else{
            $('#unreadMessage').removeClass("unreadMessage"); 
        }
    };

  //关闭连接的时候调用
    socket.onclose = function(){
        console.log("close");
    }
  //出错时调用
    socket.onerror = function() {
        console.log("error");
    }
  
});
</script>