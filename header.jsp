<script type="text/javascript">
var socket = null;
$(function(){
  function parseObj(StringData){
    return (new Function("return" + StringData))();
  }
  //����socket����
  socket = new WebSocket("ws://"+ window.location.host+"${pageContext.request.contextPath}/game");
  //���Ӵ��������
  socket.onopen = function() {
      console.log("open");
  }
  
  //���յ���������Ϣ�����
    socket.onmessage = function(message) {
        var data=parseObj(message.data);
        if(data.text > 0){
            $('#unreadMessage').addClass("unreadMessage");
        }else{
            $('#unreadMessage').removeClass("unreadMessage"); 
        }
    };

  //�ر����ӵ�ʱ�����
    socket.onclose = function(){
        console.log("close");
    }
  //����ʱ����
    socket.onerror = function() {
        console.log("error");
    }
  
});
</script>